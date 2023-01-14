package io.sc3.plethora.gameplay.redstone

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.api.peripheral.IPeripheral
import io.sc3.plethora.api.method.ArgumentHelper
import net.minecraft.util.math.Direction

class RedstoneIntegratorPeripheral(private val be: RedstoneIntegratorBlockEntity) : IPeripheral {
  override fun getType() = "redstone_integrator"

  @LuaFunction
  fun getSides(): Array<String> =
    Direction.values().map { it.name }.toTypedArray()

  @LuaFunction
  fun setOutput(args: IArguments) {
    val side = args.getSideIndex(0)
    val power = if (args.getBoolean(1)) 15.toByte() else 0
    be.outputs[side] = power
    be.enqueueOutputTick()
  }

  @LuaFunction
  fun getOutput(args: IArguments): Boolean {
    val side = args.getSideIndex(0)
    return be.outputs[side] > 0
  }

  @LuaFunction
  fun getInput(args: IArguments): Boolean {
    val side = args.getSideIndex(0)
    return be.inputs[side] > 0
  }

  @LuaFunction
  fun setBundledOutput(args: IArguments) {
    val side = args.getSideIndex(0)
    val power = args.getInt(1)
    be.bundledOutputs[side] = power
    be.enqueueOutputTick()
  }

  @LuaFunction
  fun getBundledOutput(args: IArguments): Int {
    val side = args.getSideIndex(0)
    return be.bundledOutputs[side]
  }

  @LuaFunction
  fun getBundledInput(args: IArguments): Int {
    val side = args.getSideIndex(0)
    return be.bundledInputs[side]
  }

  @LuaFunction
  fun testBundledInput(args: IArguments): Boolean {
    val side = args.getSideIndex(0)
    val power = args.getInt(1)
    return be.bundledInputs[side] and power == power
  }

  @LuaFunction("setAnalogOutput", "setAnalogueOutput")
  fun setAnalogOutput(args: IArguments) {
    val side = args.getSideIndex(0)
    val power = ArgumentHelper.assertBetween(args.getInt(1), 0, 15, "Power out of range (%s)")
    be.outputs[side] = power.toByte()
    be.enqueueOutputTick()
  }

  @LuaFunction("getAnalogOutput", "getAnalogueOutput")
  fun getAnalogOutput(args: IArguments): Int {
    val side = args.getSideIndex(0)
    return be.outputs[side].toInt()
  }

  @LuaFunction("getAnalogInput", "getAnalogueInput")
  fun getAnalogInput(args: IArguments): Int {
    val side = args.getSideIndex(0)
    return be.inputs[side].toInt()
  }

  override fun attach(computer: IComputerAccess) {
    be.computers.add(computer)
  }

  override fun detach(computer: IComputerAccess) {
    be.computers.remove(computer)
  }

  override fun equals(other: IPeripheral?): Boolean {
    return this === other
  }

  private fun IArguments.getSide(index: Int): Direction =
    when (val value = getString(index).lowercase()) {
      "bottom" -> Direction.DOWN
      "top" -> Direction.UP
      else -> Direction.byName(value) ?: throw LuaException("Bad name '$value' for argument ${index + 1}")
    }

  private fun IArguments.getSideIndex(index: Int): Int =
    getSide(index).ordinal
}
