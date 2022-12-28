package io.sc3.plethora.gameplay.neural

import dan200.computercraft.core.computer.ComputerSide
import dan200.computercraft.impl.PocketUpgrades
import dan200.computercraft.shared.computer.core.ComputerFamily.ADVANCED
import dan200.computercraft.shared.computer.core.ServerComputer
import io.sc3.plethora.Plethora
import io.sc3.plethora.core.executor.TaskRunner
import io.sc3.plethora.gameplay.neural.NeuralComputerHandler.HEIGHT
import io.sc3.plethora.gameplay.neural.NeuralComputerHandler.WIDTH
import io.sc3.plethora.gameplay.neural.NeuralHelpers.INV_SIZE
import io.sc3.plethora.util.Helpers
import net.minecraft.entity.LivingEntity
import net.minecraft.inventory.Inventories
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import java.lang.ref.WeakReference
import javax.annotation.Nonnull

class NeuralComputer(
  world: ServerWorld,
  pos: BlockPos,
  computerId: Int,
  label: String?
) : ServerComputer(world, pos, computerId, label, ADVANCED, WIDTH, HEIGHT) {
  var entity: WeakReference<LivingEntity>? = null
    private set

  private val stacks = DefaultedList.ofSize(INV_SIZE, ItemStack.EMPTY)
  var moduleHash = 0
    private set

  private val moduleData: MutableMap<Identifier, NbtCompound> = HashMap()
  private var moduleDataDirty = false

  val executor = TaskRunner()
  private val access: NeuralPocketAccess = NeuralPocketAccess(this)

  fun readModuleData(nbt: NbtCompound) {
    for (key in nbt.keys) {
      moduleData[Identifier(key)] = nbt.getCompound(key)
    }
  }

  fun getModuleData(id: Identifier): NbtCompound? {
    var nbt = moduleData[id]
    if (nbt == null) moduleData[id] = NbtCompound().also { nbt = it }
    return nbt
  }

  fun markModuleDataDirty() {
    moduleDataDirty = true
  }

  /**
   * Update an sync peripherals
   *
   * @param owner The owner of the current peripherals
   */
  fun update(@Nonnull owner: LivingEntity, @Nonnull neuralStack: ItemStack, dirtyStatus: Int): Boolean {
    var dirty = dirtyStatus

    val existing = if (entity == null) null else entity!!.get()
    if (existing !== owner) {
      dirty = -1
      entity = if (owner.isAlive) WeakReference(owner) else null
    }

    level = owner.entityWorld as ServerWorld
    position = owner.blockPos

    // Sync changed slots
    if (dirty != 0) {
      stacks.clear()
      Inventories.readNbt(neuralStack.orCreateNbt, stacks)
      moduleHash = Helpers.hashStacks(stacks.subList(NeuralHelpers.PERIPHERAL_SIZE, INV_SIZE))
    }

    // Update peripherals
    for (slot in 0 until NeuralHelpers.PERIPHERAL_SIZE) {
      val stack = stacks[slot]
      if (stack.isEmpty) continue

      val upgrade = PocketUpgrades.instance()[stack] ?: continue
      val side = ComputerSide.valueOf(if (slot < NeuralHelpers.BACK) slot else slot + 1)
      val peripheral = getPeripheral(side) ?: continue
      upgrade.update(access, peripheral)
    }

    if (dirty != 0) {
      for (slot in 0 until NeuralHelpers.PERIPHERAL_SIZE) {
        if (dirty and (1 shl slot) == 1 shl slot) {
          // We skip the "back" slot
          try {
            val newPeripheral = NeuralHelpers.buildPeripheral(access, stacks[slot])
            setPeripheral(ComputerSide.valueOf(if (slot < NeuralHelpers.BACK) slot else slot + 1), newPeripheral)
          } catch (e: Exception) {
            Plethora.log.error("Failed to build peripheral for slot $slot", e)
          }
        }
      }

      // If the modules have changed.
      if (dirty shr NeuralHelpers.PERIPHERAL_SIZE != 0) {
        try {
          setPeripheral(ComputerSide.BACK, NeuralHelpers.buildModules(this, stacks, owner))
        } catch (e: Exception) {
          Plethora.log.error("Failed to build peripheral for modules", e)
        }
      }
    }

    executor.update()

    if (moduleDataDirty) {
      moduleDataDirty = false

      val nbt = NbtCompound()
      for ((key, value) in moduleData) {
        nbt.put(key.toString(), value)
      }

      neuralStack.orCreateNbt.put(NeuralComputerHandler.MODULE_DATA, nbt)
      return true
    }

    return false
  }
}
