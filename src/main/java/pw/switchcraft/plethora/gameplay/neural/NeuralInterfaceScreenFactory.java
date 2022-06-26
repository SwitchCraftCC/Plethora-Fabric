package pw.switchcraft.plethora.gameplay.neural;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import pw.switchcraft.plethora.Plethora;

import java.util.Optional;

public class NeuralInterfaceScreenFactory implements ExtendedScreenHandlerFactory {
    public enum TargetType {
        PLAYER, ENTITY
    }

    private final TargetType targetType;
    private final int entityId;

    public NeuralInterfaceScreenFactory(TargetType targetType, int entityId) {
        this.targetType = targetType;
        this.entityId = entityId;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("gui.plethora.neuralInterface.title");
    }

    @Nullable
    @Override
    public NeuralInterfaceScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        LivingEntity parent = getEntity(inv.player, inv.player.world, targetType, entityId);
        if (parent == null) return null;

        Optional<ItemStack> optStack = NeuralHelpers.getStack(parent);
        if (optStack.isEmpty()) return null;

        return new NeuralInterfaceScreenHandler(syncId, inv, parent, optStack.get());
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeEnumConstant(targetType);
        buf.writeInt(entityId);
    }

    private static LivingEntity getEntity(PlayerEntity player, World world, TargetType targetType, int entityId) {
        switch (targetType) {
            case PLAYER:
                return player;
            case ENTITY:
                Entity entity = world.getEntityById(entityId);
                return entity instanceof LivingEntity ? (LivingEntity) entity : null;
            default:
                Plethora.LOG.error("Unknown neural gui target type: " + targetType);
                return null;
        }
    }

    public static NeuralInterfaceScreenHandler fromPacket(int syncId, PlayerInventory inv, PacketByteBuf buf) {
        var factory = new NeuralInterfaceScreenFactory(buf.readEnumConstant(TargetType.class), buf.readInt());
        return factory.createMenu(syncId, inv, inv.player);
    }
}
