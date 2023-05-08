package io.sc3.plethora.util;

import net.minecraft.util.math.Vec3d;

public interface VelocityDeterminable {
    void storePrevPos();
    Vec3d getMotion();
}
