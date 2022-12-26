package io.sc3.plethora.util;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Random;

public final class Helpers {
    public static final Random RANDOM = new Random();

    public static int hashStack(@Nonnull ItemStack stack) {
        return Objects.hash(stack.getItem(), stack.getNbt());
    }

    public static int hashStacks(Iterable<ItemStack> stacks) {
        int hash = 0;
        for (ItemStack stack : stacks) {
            hash *= 31;
            if (!stack.isEmpty()) hash += hashStack(stack);
        }
        return hash;
    }

    /**
     * Take modulo for double numbers according to lua math, and return a double result.
     *
     * @param lhs Left-hand-side of the modulo.
     * @param rhs Right-hand-side of the modulo.
     * @return double value for the result of the modulo,
     * using lua's rules for modulo
     */
    public static double mod(double lhs, double rhs) {
        double mod = lhs % rhs;
        return mod * rhs < 0 ? mod + rhs : mod;
    }

    /**
     * Normalise an angle between -180 and 180.
     *
     * @param angle The angle to normalise.
     * @return The normalised angle.
     */
    public static double normaliseAngle(double angle) {
        angle = mod(angle, 360);
        if (angle > 180) angle -= 360;
        return angle;
    }
}
