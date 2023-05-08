package io.sc3.plethora.mixin;

import io.sc3.plethora.util.VelocityDeterminable;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entity.class)
public class EntityMixin implements VelocityDeterminable {
    @Unique
    private Vec3d prevPos = Vec3d.ZERO;
    @Unique
    private Vec3d motion = Vec3d.ZERO;

    @Override
    public void storePrevPos() {
        Vec3d pos = ((Entity)((Object)this)).getPos();
        motion = pos.subtract(prevPos);
        prevPos = pos;
    }

    @Override
    public Vec3d getMotion() {
        return motion;
    }
}
