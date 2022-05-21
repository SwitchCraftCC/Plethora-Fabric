package pw.switchcraft.plethora.gameplay.modules.laser;

import com.mojang.authlib.GameProfile;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.turtle.ITurtleAccess;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import pw.switchcraft.plethora.api.IPlayerOwnable;
import pw.switchcraft.plethora.api.IWorldLocation;
import pw.switchcraft.plethora.api.method.*;
import pw.switchcraft.plethora.api.module.IModuleContainer;
import pw.switchcraft.plethora.api.module.SubtargetedModuleMethod;
import pw.switchcraft.plethora.util.PlayerHelpers;
import pw.switchcraft.plethora.util.config.Config;

import javax.annotation.Nonnull;

import static pw.switchcraft.plethora.util.Helpers.normaliseAngle;
import static pw.switchcraft.plethora.util.config.Config.Laser.maximumPotency;
import static pw.switchcraft.plethora.util.config.Config.Laser.minimumPotency;

public class LaserMethods {
    private static final Identifier MODULE_ID = LaserModuleItem.MODULE_ID;

    public static final SubtargetedModuleMethod<IWorldLocation> FIRE = SubtargetedModuleMethod.of(
        "fire", MODULE_ID, IWorldLocation.class,
        "function(yaw:number, pitch:number, potency:number) -- Fire a laser in a set direction",
        LaserMethods::fire
    );
    private static FutureMethodResult fire(@Nonnull final IUnbakedContext<IModuleContainer> unbaked,
                                           @Nonnull IArguments args) throws LuaException {
        double yaw = normaliseAngle(args.getFiniteDouble(0));
        double pitch = normaliseAngle(args.getFiniteDouble(1));
        final float potency = (float) args.getFiniteDouble(2);

        ArgumentHelper.assertBetween(potency, minimumPotency, maximumPotency, "Potency out of range (%s).");

        final double motionX = -Math.sin(yaw / 180.0f * (float) Math.PI) * Math.cos(pitch / 180.0f * (float) Math.PI);
        final double motionZ = Math.cos(yaw / 180.0f * (float) Math.PI) * Math.cos(pitch / 180.0f * (float) Math.PI);
        final double motionY = -Math.sin(pitch / 180.0f * (float) Math.PI);

        return unbaked.getCostHandler().await(potency * Config.Laser.cost, FutureMethodResult.nextTick(() -> {
            IContext<IModuleContainer> context = unbaked.bake();
            IWorldLocation location = context.getContext(ContextKeys.ORIGIN, IWorldLocation.class);
            Vec3d pos = location.getLoc();

            LaserEntity laser = new LaserEntity(location.getWorld(), pos);
            {
                IPlayerOwnable ownable = context.getContext(ContextKeys.ORIGIN, IPlayerOwnable.class);
                Entity entity = context.getContext(ContextKeys.ORIGIN, Entity.class);

                GameProfile profile = null;
                if (ownable != null) profile = ownable.getOwningProfile();
                if (profile == null) profile = PlayerHelpers.getProfile(entity);

                laser.setShooter(entity, profile);
            }

            if (context.hasContext(BlockEntity.class) || context.hasContext(ITurtleAccess.class)) {
                double vOff = 0.3; // The laser is 0.25 high, so we add a little more.

                // Offset positions to be around the edge of the manipulator. Avoids breaking the manipulator and the
                // block below/above in most cases.
                // Also offset to be just above/below the manipulator, depending on the pitch.

                Vec3d offset;
                if (pitch < -60) {
                    offset = new Vec3d(0, 0.5 + vOff, 0);
                } else if (pitch > 60) {
                    offset = new Vec3d(0, -0.5 - vOff, 0);
                } else {
                    // The laser is 0.25 wide, the offset from the centre is 0.5.
                    double hOff = 0.9;
                    double length = Math.sqrt(motionX * motionX + motionZ * motionZ);
                    offset = new Vec3d(motionX / length * hOff, 0, motionZ / length * hOff);
                }

                laser.setPosition(pos.add(offset));
            } else if (context.hasContext(Entity.class)) {
                Entity entity = context.getContext(Entity.class);
                Vec3d vector = entity.getPos();
                double offset = entity.getWidth() + 0.2;
                double length = Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);

                // Offset positions to be around the edge of the entity. Avoids damaging the entity.
                laser.setPosition(vector.add(
                    motionX / length * offset,
                    entity.getStandingEyeHeight() + motionY / length * offset,
                    motionZ / length * offset
                ));
            } else {
                laser.setPosition(pos);
            }

            laser.setPotency(potency);
            laser.shoot(motionX, motionY, motionZ, 1.5f, 0.0f);

            location.getWorld().spawnEntity(laser);

            return FutureMethodResult.empty();
        }));
    }
}
