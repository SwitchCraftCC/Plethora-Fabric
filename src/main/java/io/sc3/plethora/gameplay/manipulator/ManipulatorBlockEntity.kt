package io.sc3.plethora.gameplay.manipulator

import com.mojang.authlib.GameProfile
import io.sc3.plethora.api.IPlayerOwnable
import io.sc3.plethora.api.module.IModuleHandler
import io.sc3.plethora.core.executor.TaskRunner
import io.sc3.plethora.gameplay.BaseBlockEntity
import io.sc3.plethora.gameplay.manipulator.ManipulatorBlock.Companion.BOX_EXPAND
import io.sc3.plethora.gameplay.manipulator.ManipulatorBlock.Companion.OFFSET
import io.sc3.plethora.util.Helpers
import io.sc3.plethora.util.PlayerHelpers
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.ItemScatterer
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class ManipulatorBlockEntity(
  type: BlockEntityType<*>,
  pos: BlockPos,
  state: BlockState,
  initialType: ManipulatorType
) : BaseBlockEntity(type, pos, state), IPlayerOwnable {
  var manipulatorType: ManipulatorType = initialType
    set(value) {
      stacks = value.defaultStacks()
      field = value
    }

  private val moduleData = mutableMapOf<Identifier, NbtCompound>()
  private var stacks = manipulatorType.defaultStacks()
  var stackHash = 0
    private set

  private var profile: GameProfile? = null

  val runner = TaskRunner()

  // Lazily loaded render options
  private var offset = -1f
  private var rotation = 0f

  val facing: Direction
    get() {
      val state = world!!.getBlockState(getPos())
      return if (state.block is ManipulatorBlock) {
        state.get(ManipulatorBlock.FACING)
      } else {
        Direction.DOWN
      }
    }

  fun getStack(slot: Int) = stacks[slot]

  fun getModuleData(id: Identifier): NbtCompound = moduleData[id]
    ?: NbtCompound().also { moduleData[id] = it }

  fun markModuleDataDirty() {
    markDirty()
    val world = world ?: return
    val pos = getPos()
    val state = world.getBlockState(pos)
    world.updateListeners(pos, state, state, Block.NOTIFY_ALL)
  }

  override fun readNbt(nbt: NbtCompound) {
    super.readNbt(nbt)
    readDescription(nbt)
  }

  override fun writeNbt(nbt: NbtCompound) {
    super.writeNbt(nbt)
    writeDescription(nbt)
  }

  override fun writeDescription(nbt: NbtCompound) {
    super.writeDescription(nbt)

    // Manipulator type (Mark I, Mark II)
    nbt.putInt("type", manipulatorType.ordinal)

    // Serialise the owner of the manipulator, used for tracking who fired a laser etc.
    PlayerHelpers.writeProfile(nbt, profile)

    // Manipulator's stored modules
    for (i in stacks.indices) {
      val stack = stacks[i]
      if (!stack.isEmpty) {
        nbt.put("stack$i", stack.writeNbt(NbtCompound()))
      } else {
        nbt.remove("stack$i")
      }
    }

    // Any additional data stored by the modules in the manipulator
    if (moduleData.isEmpty()) {
      nbt.remove("data")
    } else {
      val data = NbtCompound()
      for ((key, value) in moduleData) {
        data.put(key.toString(), value)
      }
    }
  }

  override fun readDescription(nbt: NbtCompound) {
    super.readDescription(nbt)

    if (nbt.contains("type", NbtElement.INT_TYPE.toInt())) {
      val meta = nbt.getInt("type")
      manipulatorType = ManipulatorType.values()[meta and 1]
    }

    profile = PlayerHelpers.readProfile(nbt)
    for (i in stacks.indices) {
      stacks[i] = if (nbt.contains("stack$i")) {
        ItemStack.fromNbt(nbt.getCompound("stack$i"))
      } else {
        ItemStack.EMPTY
      }
    }

    stackHash = Helpers.hashStacks(stacks)

    val data = nbt.getCompound("data")
    for (key in data.keys) {
      moduleData[Identifier(key)] = data.getCompound(key)
    }
  }

  override fun onUse(player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
    if (!player.canModifyBlocks()) return ActionResult.PASS
    if (player.entityWorld.isClient) return ActionResult.SUCCESS
    
    val world = world ?: return ActionResult.SUCCESS
    val blockPos = getPos()
    val hitPos = hit.pos.subtract(Vec3d.of(blockPos))
    
    val heldStack = player.getStackInHand(hand)
    val boxes = manipulatorType.boxesFor(facing.opposite)

    boxes.forEachIndexed { i, box ->
      if (box.expand(BOX_EXPAND).contains(hitPos)) {
        val stack = stacks[i]

        if (heldStack.isEmpty && !stack.isEmpty) {
          // Remove a module from the manipulator
          if (!player.isCreative) {
            val offset = Vec3d.of(pos)
              .add(Vec3d(facing.opposite.unitVector).multiply(OFFSET))
            ItemScatterer.spawn(world, offset.x, offset.y, offset.z, stack)
          }
          
          stacks[i] = ItemStack.EMPTY
          stackHash = Helpers.hashStacks(stacks)
          markForUpdate()
          
          return ActionResult.SUCCESS
        } else if (stack.isEmpty && !heldStack.isEmpty && heldStack.item is IModuleHandler) {
          // Insert a module into the manipulator
          stacks[i] = heldStack.copy()
          stacks[i].count = 1
          stackHash = Helpers.hashStacks(stacks)
          
          if (!player.isCreative) {
            heldStack.decrement(1)
            // TODO: Sufficient to just decrement without setting the inventory stack to empty? Should be.
          }
          
          markForUpdate()
          
          return ActionResult.SUCCESS
        }
      }
    }
    
    return ActionResult.PASS
  }

  override fun unload() {
    super.unload()
    runner.reset() // TODO: Verify this is sufficient, survives clearRemoved, etc
  }

  override fun broken() {
    super.broken()
    ItemScatterer.spawn(world, pos, stacks) // Will already filter out empty ItemStacks in spawn check
    stacks.clear()
    stackHash = 0
  }

  fun incrementRotation(tickDelta: Float): Float {
    if (offset < 0) offset = (Helpers.RANDOM.nextDouble() * 360).toFloat()
    rotation += tickDelta
    return rotation + offset
  }

  override fun getOwningProfile() = profile

  fun setOwningProfile(profile: GameProfile?) {
    this.profile = profile
  }

  companion object {
    fun tick(world: World, pos: BlockPos, state: BlockState, be: ManipulatorBlockEntity) {
      be.runner.update()
    }
  }
}
