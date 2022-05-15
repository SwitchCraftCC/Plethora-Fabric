package pw.switchcraft.plethora.gameplay.modules.laser;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import pw.switchcraft.plethora.api.IPlayerOwnable;
import pw.switchcraft.plethora.gameplay.PlethoraFakePlayer;
import pw.switchcraft.plethora.mixin.TntBlockInvoker;
import pw.switchcraft.plethora.util.EntitySpawnPacket;
import pw.switchcraft.plethora.util.PlayerHelpers;
import pw.switchcraft.plethora.util.WorldPosition;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static pw.switchcraft.plethora.gameplay.registry.Registration.LASER_ENTITY;
import static pw.switchcraft.plethora.util.config.Config.Laser.damage;
import static pw.switchcraft.plethora.util.config.Config.Laser.lifetime;
import static pw.switchcraft.plethora.util.EntitySpawnPacket.SPAWN_PACKET_ID;

public class LaserEntity extends Entity implements IPlayerOwnable {
    private static final Random rand = new Random();

    @Nullable
    private Entity shooter;
    @Nullable
    private PlayerEntity shooterPlayer;
    @Nullable
    private GameProfile shooterOwner;

    @Nullable
    private WorldPosition shooterPos;

    private float potency = 0.0f;

    public LaserEntity(EntityType<? extends LaserEntity> entityType, World world) {
        super(entityType, world);
    }

    public LaserEntity(World world, @Nonnull Entity shooter,
                       float inaccuracy, float potency) {
        super(LASER_ENTITY, world);

        this.potency = potency;
        setShooter(shooter, PlayerHelpers.getProfile(shooter));

        float yaw = shooter.getYaw();
        float pitch = shooter.getPitch();

        Vec3d pos = shooter.getEyePos();
        setPosition(pos.subtract(
            MathHelper.cos(yaw / 180.0f * (float) Math.PI) * 0.16f,
            0.1f,
            MathHelper.sin(yaw / 180.0f * (float) Math.PI) * 0.16f
        ));

        Vec3d vel = new Vec3d(
            -MathHelper.sin(yaw / 180.0f * (float) Math.PI) * MathHelper.cos(pitch / 180.0f * (float) Math.PI),
            -MathHelper.sin(pitch / 180.0f * (float) Math.PI),
            MathHelper.cos(yaw / 180.0f * (float) Math.PI) * MathHelper.cos(pitch / 180.0f * (float) Math.PI)
        );
        setVelocity(vel);
        shoot(vel.x, vel.y, vel.z, 1.5f, inaccuracy);
    }

    public LaserEntity(World world, Vec3d shooterPos) {
        this(LASER_ENTITY, world);
        this.shooterPos = new WorldPosition(world, shooterPos);
    }

    public void setShooter(@Nullable Entity shooter, @Nullable GameProfile profile) {
        this.shooter = shooter;
        this.shooterOwner = profile;
    }

    public void setPotency(float potency) {
        this.potency = potency;
    }

    @Override
    protected void initDataTracker() {
        // TODO: ?
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return EntitySpawnPacket.create(this, SPAWN_PACKET_ID);
    }

    public void shoot(double vx, double vy, double vz, float velocity, float inaccuracy) {
        Vec3d vec3d = new Vec3d(vx, vy, vz)
            .normalize()
            .add(
                this.random.nextGaussian() * (double) 0.0075f * (double) inaccuracy,
                this.random.nextGaussian() * (double) 0.0075f * (double) inaccuracy,
                this.random.nextGaussian() * (double) 0.0075f * (double) inaccuracy
            )
            .multiply(velocity);

        setVelocity(vec3d);
        double d = vec3d.horizontalLength();
        setYaw((float)(MathHelper.atan2(vec3d.x, vec3d.z) * 180 / Math.PI));
        setPitch((float)(MathHelper.atan2(vec3d.y, d) * 180 / Math.PI));
        prevYaw = getYaw();
        prevPitch = getPitch();
    }

    @Override
    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
        this.setPosition(x, y, z);
        this.setRotation(yaw, pitch);
    }

    @Override
    public void setVelocityClient(double x, double y, double z) {
        setVelocity(x, y, z);
        if (prevPitch == 0.0f && prevYaw == 0.0f) {
            double d = Math.sqrt(x * x + z * z);
            setPitch((float)(MathHelper.atan2(y, d) * 180 / Math.PI));
            setYaw((float)(MathHelper.atan2(x, z) * 180 / Math.PI));
            prevPitch = getPitch();
            prevYaw = getYaw();
            refreshPositionAndAngles(getX(), getY(), getZ(), getYaw(), getPitch());
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        // TODO: implement PlayerHelpers and write shooterOwner
        if (shooterPos != null) nbt.put("shooterPos", shooterPos.serializeNbt());
        nbt.putFloat("potency", potency);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        shooter = null;
        shooterPlayer = null;
        shooterOwner = null; // TODO: implement PlayerHelpers and read shooterOwner

        if (nbt.contains("shooterPos", NbtType.COMPOUND)) {
            shooterPos = WorldPosition.deserializeNbt(nbt.getCompound("shooterPos"));
        }

        potency = nbt.getFloat("potency");
    }

    @Override
    public void tick() {
        prevX = getX();
        prevY = getY();
        prevZ = getZ();

        super.tick();

        World worldObj = getWorld();
        if (!worldObj.isClient) {
            double remaining = 1;
            int ticks = 5; // Maximum of 5 steps. This limit should never be reached but you never know.

            // Raytrace to the next collision and set our position to there
            while (remaining >= 1e-2 && potency > 0 && --ticks >= 0) {
                Vec3d pos = getPos();
                Vec3d vel = getVelocity();
                Vec3d nextPos = new Vec3d(
                    pos.x + vel.x * remaining,
                    pos.y + vel.y * remaining,
                    pos.z + vel.z * remaining
                );

                HitResult collision = world.raycast(new RaycastContext(pos, nextPos,
                    RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
                if (collision != null && collision.getType() != HitResult.Type.MISS)
                    nextPos = collision.getPos();

                List<Entity> collisions = worldObj
                    .getOtherEntities(this, getBoundingBox()
                        .offset(vel.x * remaining, vel.y * remaining, vel.z * remaining)
                        .expand(1));
                Entity shooter = getShooter();

                double closestDistance = nextPos.squaredDistanceTo(pos);
                LivingEntity closestEntity = null;

                for (Entity other : collisions) {
                    // TODO: isCollidable is false for everything except boats and shulkers - is there something else
                    //       that should be used?
                    if (/* other.isCollidable() && */ (other != shooter || age >= 5)
                        && other instanceof LivingEntity otherLiving) {
                        if (other instanceof PlayerEntity otherPlayer
                            && shooter instanceof PlayerEntity shooterPlayer
                            && !shooterPlayer.shouldDamagePlayer(otherPlayer)) {
                            continue;
                        }

                        float size = 0.3f;
                        Box singleCollision = other.getBoundingBox().expand(size);
                        Optional<Vec3d> optional = singleCollision.raycast(pos, nextPos);

                        if (optional.isPresent()) {
                            Vec3d hit = optional.get();
                            double distanceSq = hit.squaredDistanceTo(pos);
                            if (distanceSq < closestDistance) {
                                closestEntity = otherLiving;
                                closestDistance = distanceSq;
                                nextPos = hit;
                            }
                        }
                    }
                }

                if (closestEntity != null) {
                    collision = new EntityHitResult(closestEntity);
                }

                remaining -= pos.distanceTo(nextPos) / Math.sqrt(vel.lengthSquared());

                // Set position
                setPosition(nextPos);
                syncPositions(false);

                // Handle collision
                if (collision != null && collision.getType() != HitResult.Type.MISS) {
                    BlockPos blockPos = new BlockPos(collision.getPos());
                    if (collision.getType() == HitResult.Type.BLOCK
                        && worldObj.getBlockState(blockPos) == Blocks.NETHER_PORTAL.getDefaultState()) {
                        setInNetherPortal(blockPos);
                    } else {
                        onImpact(collision);
                    }
                }
            }
        } else {
            // Set position
            Vec3d vel = getVelocity();
            Vec3d newPos = getPos().add(vel);
            setPosition(newPos);
        }

        if (!worldObj.isClient && (potency <= 0 || age > lifetime)){
            kill();
        }
    }

    private void onImpact(HitResult hitResult) {
        World world = getEntityWorld();
        if (world.isClient) return;

        switch (hitResult.getType()) {
            case BLOCK -> {
                if (!(hitResult instanceof BlockHitResult col)) return;
                BlockPos position = new BlockPos(col.getPos());

                BlockState blockState = world.getBlockState(position);
                Block block = blockState.getBlock();

                if (!blockState.isAir() && !blockState.getMaterial().isLiquid()) {
                    float hardness = blockState.getHardness(world, position);

                    PlayerEntity player = getShooterPlayer();
                    if (player == null) return;

                    // Ensure the player is setup correctly
                    syncPositions(true);

                    if (!world.canPlayerModifyAt(player, position)) {
                        potency = -1;
                        return;
                    }

                    // TODO: Post a block break event here

                    if (block == Blocks.TNT) {
                        potency -= hardness;

                        // Ignite TNT blocks
                        Entity shooter = getShooter();
                        TntBlockInvoker.invokePrimeTnt(
                            world, position,
                            shooter instanceof LivingEntity ? (LivingEntity) shooter : getShooterPlayer()
                        );
                        world.removeBlock(position, false);
                    } else if (block == Blocks.OBSIDIAN) {
                        potency -= hardness;

                        // Attempt to light obsidian blocks, creating a portal
                        BlockPos offset = position.offset(col.getSide());
                        BlockState offsetState = world.getBlockState(offset);

                        if (!offsetState.isAir()) return;

                        if (world instanceof ServerWorld serverWorld) {
                            // TODO: Verify this check is sufficient
                            if (!serverWorld.getServer().isSpawnProtected(serverWorld, offset, player)) {
                                world.playSound(null, offset, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0f, rand.nextFloat() * 0.4f + 0.8f);
                                world.setBlockState(offset, Blocks.FIRE.getDefaultState());
                            }
                        }
                    } else if (hardness > -1 && hardness <= potency) {
                        potency -= hardness;

                        // TODO: Check if breakBlock is the correct method here.
                        if (world.breakBlock(position, true, player)) {
                            block.onBroken(world, position, blockState);
                        }
                    } else {
                        potency = -1;
                    }
                }
            }
            case ENTITY -> {
                if (!(hitResult instanceof EntityHitResult col)) return;

                Entity entity = col.getEntity();
                if (entity instanceof LivingEntity) {
                    // Ensure the player is setup correctly
                    syncPositions(true);

                    Entity shooter = getShooter();
                    DamageSource source = shooter == null
                        ? new EntityDamageSource("laser", this)
                        : new ProjectileDamageSource("laser", this, shooter);

                    source.setProjectile();

                    entity.setFireTicks(5);
                    entity.damage(source, (float) (potency * damage));
                    potency = -1;
                }
            }
        }
    }

    @Nullable
    private Entity getShooter() {
        if (shooter == null) return shooter;

        World worldObj = getEntityWorld();
        if (!(worldObj instanceof ServerWorld world)) return null;
        return shooter = shooterPlayer = new PlethoraFakePlayer(world, null, shooterOwner);
    }

    @Nullable
    private PlayerEntity getShooterPlayer() {
        if (shooterPlayer == null) return shooterPlayer;

        Entity shooter = getShooter();
        if (shooter instanceof PlayerEntity p) return shooterPlayer = p;

        World worldObj = getEntityWorld();
        if (!(worldObj instanceof ServerWorld world)) return null;
        return shooterPlayer = new PlethoraFakePlayer(world, shooter, shooterOwner);
    }

    private void syncPositions(boolean force) {
        PlayerEntity fakePlayer = shooterPlayer;
        Entity shooter = this.shooter;
        if (!(fakePlayer instanceof PlethoraFakePlayer)) return;

        if (shooter != null && shooter != fakePlayer) {
            syncFromEntity(fakePlayer, shooter);
        } else if (shooterPos != null) {
            World current = fakePlayer.getEntityWorld();

            if (current == null || !current.getRegistryKey().equals(shooterPos.getWorldKey())) {
                // Don't load another world unless we have to
                World replace = force ? shooterPos.getWorld(getEntityWorld().getServer()) : shooterPos.getWorld();

                if (replace == null) {
                    syncFromEntity(fakePlayer, this);
                } else {
                    syncFromPos(fakePlayer, replace, shooterPos.getPos(), getYaw(), getPitch());
                }
            } else {
                syncFromPos(fakePlayer, current, shooterPos.getPos(), getYaw(), getPitch());
            }
        } else {
            syncFromEntity(fakePlayer, this);
        }
    }

    private static void syncFromEntity(PlayerEntity player, Entity from) {
        Vec3d fromPos = from.getPos();
        player.moveToWorld((ServerWorld) from.getEntityWorld());
        player.updatePositionAndAngles(fromPos.x, fromPos.y, fromPos.z, from.getYaw(), from.getPitch());
    }

    private static void syncFromPos(PlayerEntity player, @Nonnull World world, Vec3d pos, float yaw, float pitch) {
        player.moveToWorld((ServerWorld) world);
        player.updatePositionAndAngles(pos.x, pos.y, pos.z, yaw, pitch);
    }

    @Nullable
    public GameProfile getOwningProfile() {
        return shooterOwner;
    }
}
