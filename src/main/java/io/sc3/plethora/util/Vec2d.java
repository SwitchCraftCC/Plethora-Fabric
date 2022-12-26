package pw.switchcraft.plethora.util;

public record Vec2d(double x, double y) {
    public static final Vec2d ZERO = new Vec2d(0.0, 0.0);
}
