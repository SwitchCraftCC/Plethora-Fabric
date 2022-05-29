package pw.switchcraft.plethora.gameplay.modules.glasses;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import net.minecraft.util.math.Vec3d;
import pw.switchcraft.plethora.util.Vec2d;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

import static dan200.computercraft.api.lua.LuaValues.getType;

public class ArgumentPointHelper {
    public static Vec2d getVec2d(@Nonnull IArguments args, int index) throws LuaException {
        return getVec2d(args.getTable(index));
    }

    private static Vec2d getVec2d(Map<?, ?> point) throws LuaException {
        Object xObj, yObj;
        if (point.containsKey("x")) {
            xObj = point.get("x");
            yObj = point.get("y");

            if (!(xObj instanceof Number)) throw badKey(xObj, "x", "number");
            if (!(yObj instanceof Number)) throw badKey(yObj, "y", "number");
        } else {
            xObj = point.get(1.0);
            yObj = point.get(2.0);

            if (!(xObj instanceof Number)) throw badKey(xObj, "1", "number");
            if (!(yObj instanceof Number)) throw badKey(yObj, "2", "number");
        }

        return new Vec2d(((Number) xObj).doubleValue(), ((Number) yObj).doubleValue());
    }

    private static Vec3d getVec3d(Map<?, ?> point) throws LuaException {
        Object xObj, yObj, zObj;
        if (point.containsKey("x")) {
            xObj = point.get("x");
            yObj = point.get("y");
            zObj = point.get("z");

            if (!(xObj instanceof Number)) throw badKey(xObj, "x", "number");
            if (!(yObj instanceof Number)) throw badKey(yObj, "y", "number");
            if (!(zObj instanceof Number)) throw badKey(yObj, "z", "number");
        } else {
            xObj = point.get(1.0);
            yObj = point.get(2.0);
            zObj = point.get(3.0);

            if (!(xObj instanceof Number)) throw badKey(xObj, "1", "number");
            if (!(yObj instanceof Number)) throw badKey(yObj, "2", "number");
            if (!(zObj instanceof Number)) throw badKey(yObj, "3", "number");
        }

        return new Vec3d(
            ((Number) xObj).doubleValue(),
            ((Number) yObj).doubleValue(),
            ((Number) zObj).doubleValue()
        );
    }

    @Nonnull
    private static LuaException badKey(@Nullable Object object, @Nonnull String key, @Nonnull String expected) {
        return new LuaException("Expected " + expected + " for key " + key + ", got " + getType(object));
    }
}
