package pw.switchcraft.plethora.gameplay.manipulator;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import pw.switchcraft.plethora.api.IPlayerOwnable;
import pw.switchcraft.plethora.api.module.IModuleHandler;
import pw.switchcraft.plethora.core.executor.TaskRunner;
import pw.switchcraft.plethora.gameplay.BaseBlockEntity;
import pw.switchcraft.plethora.gameplay.registry.Registration.ModBlocks;
import pw.switchcraft.plethora.util.Helpers;
import pw.switchcraft.plethora.util.PlayerHelpers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static pw.switchcraft.plethora.gameplay.manipulator.ManipulatorBlock.BOX_EXPAND;
import static pw.switchcraft.plethora.gameplay.manipulator.ManipulatorBlock.OFFSET;
import static pw.switchcraft.plethora.gameplay.manipulator.ManipulatorType.MARK_1;
import static pw.switchcraft.plethora.gameplay.manipulator.ManipulatorType.MARK_2;

public class ManipulatorBlockEntity extends BaseBlockEntity implements IPlayerOwnable {
    private ManipulatorType type;
    private DefaultedList<ItemStack> stacks;
    private GameProfile profile;
    private int stackHash;

    private final Map<Identifier, NbtCompound> moduleData = new HashMap<>();

    private final TaskRunner runner = new TaskRunner();

    // Lazily loaded render options
    private float offset = -1;
    private float rotation;

    public ManipulatorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, ManipulatorType manipulatorType) {
        super(type, pos, state);
        setType(manipulatorType);
    }

    private void setType(ManipulatorType type) {
        if (this.type != null) return;

        this.type = type;
        stacks = DefaultedList.ofSize(type.size(), ItemStack.EMPTY);
    }

    public ManipulatorType getManipulatorType() {
        return type;
    }

    public Direction getFacing() {
        BlockState state = Objects.requireNonNull(getWorld()).getBlockState(getPos());
        return state.getBlock() instanceof ManipulatorBlock
            ? state.get(ManipulatorBlock.FACING)
            : Direction.DOWN;
    }

    @Nonnull
    public ItemStack getStack(int slot) {
        return stacks.get(slot);
    }

    public int getStackHash() {
        return stackHash;
    }

    public NbtCompound getModuleData(Identifier id) {
        NbtCompound nbt = moduleData.get(id);
        if (nbt == null) moduleData.put(id, nbt = new NbtCompound());
        return nbt;
    }

    public void markModuleDataDirty() {
        markDirty();
        BlockPos pos = getPos();
        World world = Objects.requireNonNull(getWorld());
        BlockState state = world.getBlockState(pos);
        world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        readDescription(nbt);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        writeDescription(nbt);
    }

    @Override
    protected void writeDescription(NbtCompound nbt) {
        super.writeDescription(nbt);

        // Manipulator type (Mark I, Mark II)
        nbt.putInt("type", type.ordinal());

        // Serialise the owner of the manipulator, used for tracking who fired a laser etc.
        PlayerHelpers.writeProfile(nbt, profile);

        // Manipulator's stored modules
        for (int i = 0; i < stacks.size(); i++) {
            ItemStack stack = stacks.get(i);
            if (!stack.isEmpty()) {
                nbt.put("stack" + i, stack.writeNbt(new NbtCompound()));
            } else {
                nbt.remove("stack" + i);
            }
        }

        // Any additional data stored by the modules in the manipulator
        if (moduleData.isEmpty()) {
            nbt.remove("data");
        } else {
            NbtCompound data = new NbtCompound();
            for (Map.Entry<Identifier, NbtCompound> entry : moduleData.entrySet()) {
                data.put(entry.getKey().toString(), entry.getValue());
            }
        }
    }

    @Override
    protected void readDescription(NbtCompound nbt) {
        super.readDescription(nbt);

        if (nbt.contains("type", NbtElement.INT_TYPE) && type != null) {
            int meta = nbt.getInt("type");
            setType(ManipulatorType.values()[meta & 1]);
        }

        if (type == null) return;

        profile = PlayerHelpers.readProfile(nbt);

        for (int i = 0; i < stacks.size(); i++) {
            stacks.set(i, nbt.contains("stack" + i)
                ? ItemStack.fromNbt(nbt.getCompound("stack" + i))
                : ItemStack.EMPTY);
        }

        stackHash = Helpers.hashStacks(stacks);

        NbtCompound data = nbt.getCompound("data");
        for (String key : data.getKeys()) {
            moduleData.put(new Identifier(key), data.getCompound(key));
        }
    }

    @Nonnull
    @Override
    public ActionResult onUse(PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (player.getEntityWorld().isClient) return ActionResult.SUCCESS;

        World world = Objects.requireNonNull(getWorld());
        BlockPos blockPos = getPos();
        Vec3d hitPos = hit.getPos().subtract(blockPos.getX(), blockPos.getY(), blockPos.getZ());

        if (type == null) {
            Block block = world.getBlockState(blockPos).getBlock();
            setType(block == ModBlocks.MANIPULATOR_MARK_1 ? MARK_1 : MARK_2);
        }

        ItemStack heldStack = player.getStackInHand(hand);
        Box[] boxes = type.boxesFor(getFacing());
        for (int i = 0; i < type.size(); i++) {
            Box box = boxes[i];
            if (box.expand(BOX_EXPAND).contains(hitPos)) {
                final ItemStack stack = stacks.get(i);
                if (heldStack.isEmpty() && !stack.isEmpty()) {
                    // Remove a module from the manipulator
                    if (!player.isCreative()) {
                        Vec3d offset = new Vec3d(pos.getX(), pos.getY(), pos.getZ())
                            .add(new Vec3d(getFacing().getOpposite().getUnitVector()).multiply(OFFSET));
                        ItemScatterer.spawn(world, offset.x, offset.y, offset.z, stack);
                    }

                    stacks.set(i, ItemStack.EMPTY);
                    stackHash = Helpers.hashStacks(stacks);
                    markForUpdate();

                    return ActionResult.SUCCESS;
                } else if (stack.isEmpty() && !heldStack.isEmpty() && heldStack.getItem() instanceof IModuleHandler) {
                    // Insert a module into the manipulator
                    stacks.set(i, heldStack.copy());
                    stacks.get(i).setCount(1);
                    stackHash = Helpers.hashStacks(stacks);

                    if (!player.isCreative()) {
                        heldStack.decrement(1);
                        // TODO: Sufficient to just decrement without setting the inventory stack to empty? Should be.
                    }

                    markForUpdate();

                    return ActionResult.SUCCESS;
                }
            }
        }

        return ActionResult.PASS;
    }

    @Override
    protected void unload() {
        super.unload();
        runner.reset(); // TODO: Verify this is sufficient, survives clearRemoved, etc
    }

    @Override
    public void broken() {
        super.broken();

        if (stacks == null) return;

        ItemScatterer.spawn(world, pos, stacks); // Will already filter out empty ItemStacks in spawn check

        stacks.clear();
        stackHash = 0;
    }

    public float incrementRotation(float tickDelta) {
        if (offset < 0) offset = (float) (Helpers.RANDOM.nextDouble() * (2 * Math.PI));
        rotation += tickDelta;
        return rotation + offset;
    }

    public static void tick(World world, BlockPos pos, BlockState state, ManipulatorBlockEntity be) {
        be.runner.update();
    }

    public TaskRunner getRunner() {
        return runner;
    }

    @Nullable
    @Override
    public GameProfile getOwningProfile() {
        return profile;
    }

    public void setOwningProfile(GameProfile profile) {
        this.profile = profile;
    }
}
