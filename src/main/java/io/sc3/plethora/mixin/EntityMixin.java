package io.sc3.plethora.mixin;

import io.sc3.plethora.util.VelocityDeterminable;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entity.class)
public class EntityMixin implements VelocityDeterminable {
  @Unique
  private Vec3d prevPos = Vec3d.ZERO;
  @Unique
  private Vec3d deltaPos = Vec3d.ZERO;
  @Unique
  private RegistryKey<World> prevWorld;

  @Override
  public void storePrevPos() {
    var entity = (Entity) (Object) this;
    var pos = entity.getPos();
    var world = entity.getWorld().getRegistryKey();

    // Avoid velocity spikes when changing dimensions
    if (prevWorld != world) {
      prevWorld = world;
      deltaPos = Vec3d.ZERO;
    } else {
      deltaPos = pos.subtract(prevPos);
    }

    prevPos = pos;
  }

  @Override
  public Vec3d getDeltaPos() {
    return deltaPos;
  }
}
