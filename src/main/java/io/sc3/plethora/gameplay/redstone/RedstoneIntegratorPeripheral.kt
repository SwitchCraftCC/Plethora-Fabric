package io.sc3.plethora.gameplay.redstone;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.util.math.Direction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static io.sc3.plethora.api.method.ArgumentHelper.assertBetween;

public class RedstoneIntegratorPeripheral implements IPeripheral {
    private final RedstoneIntegratorBlockEntity be;

    public RedstoneIntegratorPeripheral(RedstoneIntegratorBlockEntity be) {
        this.be = be;
    }

    @Nonnull
    @Override
    public String getType() {
        return "redstone_integrator";
    }

    @LuaFunction
    public final String[] getSides() {
        Map<Integer, String> result = new HashMap<>();

        for (int i = 0; i < Direction.values().length; i++) {
            result.put(i + 1, Direction.values()[i].getName());
        }

        return result.values().toArray(new String[0]);
    }

    @LuaFunction
    public final void setOutput(IArguments args) throws LuaException {
        int side = getFacing(args, 0).ordinal();
        byte power = args.getBoolean(1) ? (byte) 15 : 0;

        be.outputs[side] = power;
        be.enqueueOutputTick();
    }

    @LuaFunction
    public final boolean getOutput(IArguments args) throws LuaException {
        int side = getFacing(args, 0).ordinal();
        return be.outputs[side] > 0;
    }

    @LuaFunction
    public final boolean getInput(IArguments args) throws LuaException {
        int side = getFacing(args, 0).ordinal();
        return be.inputs[side] > 0;
    }

    @LuaFunction
    public final void setBundledOutput(IArguments args) throws LuaException {
        int side = getFacing(args, 0).ordinal();
        int power = args.getInt(1);

        be.bundledOutputs[side] = power;
        be.enqueueOutputTick();
    }

    @LuaFunction
    public final int getBundledOutput(IArguments args) throws LuaException {
        int side = getFacing(args, 0).ordinal();
        return be.bundledOutputs[side];
    }

    @LuaFunction
    public final int getBundledInput(IArguments args) throws LuaException {
        int side = getFacing(args, 0).ordinal();
        return be.bundledInputs[side];
    }

    @LuaFunction
    public final boolean testBundledInput(IArguments args) throws LuaException {
        int side = getFacing(args, 0).ordinal();
        int power = args.getInt(1);
        return (be.bundledInputs[side] & power) == power;
    }

    @LuaFunction({ "setAnalogOutput", "setAnalogueOutput" })
    public final void setAnalogOutput(IArguments args) throws LuaException {
        int side = getFacing(args, 0).ordinal();
        int power = assertBetween(args.getInt(1), 0, 15, "Power out of range (%s)");

        be.outputs[side] = (byte) power;
        be.enqueueOutputTick();
    }

    @LuaFunction({ "getAnalogOutput", "getAnalogueOutput" })
    public final int getAnalogOutput(IArguments args) throws LuaException {
        int side = getFacing(args, 0).ordinal();
        return be.outputs[side];
    }

    @LuaFunction({ "getAnalogInput", "getAnalogueInput" })
    public final int getAnalogInput(IArguments args) throws LuaException {
        int side = getFacing(args, 0).ordinal();
        return be.inputs[side];
    }

    @Override
    public void attach(@Nonnull IComputerAccess computer) {
        be.computers.add(computer);
    }

    @Override
    public void detach(@Nonnull IComputerAccess computer) {
        be.computers.remove(computer);
    }

    private static Direction getFacing(IArguments args, int index) throws LuaException {
        String value = args.getString(index);
        if (value.equalsIgnoreCase("bottom")) return Direction.DOWN;
        if (value.equalsIgnoreCase("top")) return Direction.UP;

        Direction facing = Direction.byName(value);
        if (facing == null) {
            throw new LuaException("Bad name '" + value.toLowerCase(Locale.ENGLISH) + "' for argument " + (index + 1));
        }

        return facing;
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return this == other;
    }
}
