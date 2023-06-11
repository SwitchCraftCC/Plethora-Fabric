package io.sc3.plethora.gameplay.manipulator

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.peripheral.IPeripheral
import io.sc3.plethora.api.IWorldLocation
import io.sc3.plethora.api.WorldLocation
import io.sc3.plethora.api.method.ContextKeys
import io.sc3.plethora.api.method.CostHelpers
import io.sc3.plethora.api.module.BasicModuleContainer
import io.sc3.plethora.api.module.IModuleAccess
import io.sc3.plethora.api.module.IModuleContainer
import io.sc3.plethora.api.module.IModuleHandler
import io.sc3.plethora.api.reference.ConstantReference
import io.sc3.plethora.api.reference.Reference
import io.sc3.plethora.core.AttachableWrapperPeripheral
import io.sc3.plethora.core.ContextFactory
import io.sc3.plethora.core.MethodRegistry
import io.sc3.plethora.gameplay.modules.ModulePeripheral
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.MinecraftServer
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction

object ManipulatorPeripheral {
  @JvmStatic
  fun getPeripheral(manipulator: ManipulatorBlockEntity, direction: Direction?): IPeripheral? {
    val manipulatorType = manipulator.manipulatorType
    val size = manipulatorType.size()

    val stackHash = manipulator.stackHash
    val stacks = arrayOfNulls<ItemStack>(size)

    val modules = mutableSetOf<Identifier>()
    val moduleHandlers = mutableSetOf<Pair<IModuleHandler, ItemStack>>()

    for (i in 0 until size) {
      var stack = manipulator.getStack(i)
      val item = stack.item as? IModuleHandler ?: continue
      if (stack.isEmpty) continue

      stack = stack.copy().also { stacks[i] = it }

      modules.add(item.module) // TODO: Check module blacklist
      moduleHandlers.add(item to stack)
    }

    if (modules.isEmpty()) {
      return null
    }

    val container = BasicModuleContainer(modules)
    val accessMap = mutableMapOf<Identifier, ManipulatorAccess>()

    val containerRef = object : ConstantReference<IModuleContainer> {
      override fun get(): IModuleContainer {
        if (manipulator.isRemoved) {
          throw LuaException("Manipulator is no longer there")
        }

        for (i in 0 until size) {
          val oldStack = stacks[i]
          val newStack = manipulator.getStack(i)
          if (oldStack != null && !ItemStack.areItemsEqual(oldStack, newStack)) {
            val moduleHandler = oldStack.item as IModuleHandler
            throw LuaException("The ${moduleHandler.module} module has been removed")
          }
        }

        return container
      }

      override fun safeGet(): IModuleContainer {
        if (manipulator.isRemoved) throw LuaException("Manipulator is no longer there")
        if (stackHash != manipulator.stackHash) throw LuaException("A module has changed")
        return container
      }
    }

    val factory = ContextFactory.of(container, containerRef)
      .withCostHandler(CostHelpers.getCostHandler(manipulator))
      .withModules(container, containerRef)
      .addContext(ContextKeys.ORIGIN, manipulator, Reference.blockEntity(manipulator))
      .addContext(ContextKeys.ORIGIN, WorldLocation(manipulator.world!!, manipulator.pos))

    for (handler in moduleHandlers) {
      val module = handler.first.module
      val access = accessMap[module]
        ?: ManipulatorAccess(manipulator, handler.first, container)
          .also { accessMap[module] = it }

      handler.first.getAdditionalContext(handler.second, access, factory)
    }

    val paired = MethodRegistry.instance
      .getMethodsPaired(factory.baked)
    if (paired.left.isEmpty()) return null

    val peripheral = ModulePeripheral(
      "manipulator", manipulator, paired, manipulator.runner,
      factory.attachments, stackHash
    )
    accessMap.values.forEach { a -> a.wrapper = peripheral }
    return peripheral
  }

  private class ManipulatorAccess(
    private val manipulator: ManipulatorBlockEntity,
    module: IModuleHandler,
    private val container: IModuleContainer
  ) : IModuleAccess {
    var wrapper: AttachableWrapperPeripheral? = null
    private val location: IWorldLocation
    private val module: Identifier

    init {
      location = WorldLocation(manipulator.world!!, manipulator.pos)
      this.module = module.module
    }

    override fun getOwner(): Any = manipulator
    override fun getLocation(): IWorldLocation = location
    override fun getContainer(): IModuleContainer = container
    override fun getData(): NbtCompound = manipulator.getModuleData(module)
    override fun getServer(): MinecraftServer = manipulator.world!!.server!!

    override fun markDataDirty() {
      manipulator.markModuleDataDirty()
    }

    override fun queueEvent(event: String, vararg args: Any?) {
      wrapper?.queueEvent(event, *args)
    }
  }
}
