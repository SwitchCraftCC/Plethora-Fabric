package io.sc3.plethora.gameplay.modules.laser

import com.mojang.authlib.GameProfile
import io.sc3.plethora.Plethora
import io.sc3.plethora.Plethora.config
import io.sc3.plethora.api.IPlayerOwnable
import io.sc3.plethora.gameplay.PlethoraBlockTags.LASER_DONT_DROP
import io.sc3.plethora.gameplay.PlethoraEntityTags
import io.sc3.plethora.gameplay.PlethoraFakePlayer
import io.sc3.plethora.gameplay.registry.Registration
import io.sc3.plethora.gameplay.registry.Registration.ModDamageSources
import io.sc3.plethora.mixin.TntBlockInvoker
import io.sc3.plethora.util.PlayerHelpers
import io.sc3.plethora.util.WorldPosition
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.util.NbtType
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.block.FluidBlock
import net.minecraft.block.OperatorBlock
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents.ITEM_FLINTANDSTEEL_USE
import net.minecraft.util.Identifier
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.RaycastContext
import net.minecraft.world.World
import java.lang.Math.PI
import java.util.*
import javax.annotation.Nonnull
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class LaserEntity : Entity, IPlayerOwnable {
  private var shooter: Entity? = null
  private var shooterPlayer: PlayerEntity? = null
  private var shooterOwner: GameProfile? = null

  private var shooterPos: WorldPosition? = null

  var potency = 0.0f
  var spawnTime = world.time

  constructor(entityType: EntityType<out LaserEntity>, world: World) : super(entityType, world)

  constructor(world: World, @Nonnull shooter: Entity,
              inaccuracy: Float, potency: Float) : super(Registration.LASER_ENTITY, world) {
    this.potency = potency
    setShooter(shooter, PlayerHelpers.getProfile(shooter))

    val yaw = shooter.yaw.toDouble()
    val pitch = shooter.pitch

    val pos = shooter.eyePos
    setPosition(pos.subtract(
      cos(yaw / 180.0 * PI) * 0.16,
      0.1,
      sin(yaw / 180.0 * PI) * 0.16
    ))

    val vel = Vec3d(
      -sin(yaw / 180.0 * PI) * cos(pitch / 180.0 * PI),
      -sin(pitch / 180.0 * PI),
      cos(yaw / 180.0 * PI) * cos(pitch / 180.0 * PI)
    ).also { velocity = it }

    shoot(vel.x, vel.y, vel.z, 1.5f, inaccuracy)
  }

  constructor(world: World, shooterPos: Vec3d) : this(Registration.LASER_ENTITY, world) {
    this.shooterPos = WorldPosition(world, shooterPos)
  }

  fun setShooter(shooter: Entity?, profile: GameProfile?) {
    this.shooter = shooter
    shooterOwner = profile
  }

  override fun initDataTracker() {
    // TODO: ?
  }

  fun shoot(vx: Double, vy: Double, vz: Double, velocity: Float, inaccuracy: Float) {
    val vec3d = Vec3d(vx, vy, vz)
      .normalize()
      .add(
        random.nextGaussian() * 0.0075 * inaccuracy.toDouble(),
        random.nextGaussian() * 0.0075 * inaccuracy.toDouble(),
        random.nextGaussian() * 0.0075 * inaccuracy.toDouble()
      )
      .multiply(velocity.toDouble())

    setVelocity(vec3d)
    yaw = (MathHelper.atan2(vec3d.x, vec3d.z) * 180 / PI).toFloat()
    pitch = (MathHelper.atan2(vec3d.y, vec3d.horizontalLength()) * 180 / PI).toFloat()
    prevYaw = yaw
    prevPitch = pitch
  }

  override fun updateTrackedPositionAndAngles(x: Double, y: Double, z: Double, yaw: Float, pitch: Float,
                                              interpolationSteps: Int, interpolate: Boolean) {
    setPosition(x, y, z)
    setRotation(yaw, pitch)
  }

  override fun setVelocityClient(x: Double, y: Double, z: Double) {
    setVelocity(x, y, z)
    if (prevPitch == 0.0f && prevYaw == 0.0f) {
      pitch = (MathHelper.atan2(y, sqrt(x * x + z * z)) * 180 / PI).toFloat()
      yaw = (MathHelper.atan2(x, z) * 180 / PI).toFloat()
      prevPitch = pitch
      prevYaw = yaw
      refreshPositionAndAngles(getX(), getY(), getZ(), yaw, pitch)
    }
  }

  public override fun writeCustomDataToNbt(nbt: NbtCompound) {
    PlayerHelpers.writeProfile(nbt, shooterOwner)
    shooterPos?.let { nbt.put("shooterPos", it.serializeNbt()) }
    nbt.putFloat("potency", potency)
    nbt.putLong("spawn", spawnTime)
  }

  public override fun readCustomDataFromNbt(nbt: NbtCompound) {
    shooter = null
    shooterPlayer = null
    shooterOwner = PlayerHelpers.readProfile(nbt)

    if (nbt.contains("shooterPos", NbtType.COMPOUND)) {
      shooterPos = WorldPosition.deserializeNbt(nbt.getCompound("shooterPos"))
    }

    potency = nbt.getFloat("potency")
    spawnTime = nbt.getLong("spawn")
  }

  override fun tick() {
    val world = world
    prevX = x
    prevY = y
    prevZ = z

    super.tick()

    if (!world.isClient) {
      var remaining = 1.0
      var ticks = 5 // Maximum of 5 steps. This limit should never be reached, but you never know.

      // Raytrace to the next collision and set our position to there
      while (remaining >= 0.01 && potency > 0 && --ticks >= 0) {
        val pos = pos
        val vel = velocity
        var nextPos = Vec3d(
          pos.x + vel.x * remaining,
          pos.y + vel.y * remaining,
          pos.z + vel.z * remaining
        )

        var collision: HitResult? = world.raycast(RaycastContext(pos, nextPos,
          RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this))
        if (collision != null && collision.type != HitResult.Type.MISS) {
          nextPos = collision.pos
        }

        val collisions = world.getOtherEntities(this, boundingBox
          .offset(vel.x * remaining, vel.y * remaining, vel.z * remaining)
          .expand(1.0))
        val shooter = getShooter()

        var closestDistance = nextPos.squaredDistanceTo(pos)
        var closestEntity: LivingEntity? = null

        for (other in collisions) {
          // TODO: isCollidable is false for everything except boats and shulkers - is there something else
          //       that should be used?
          if (/* other.isCollidable() && */(other != shooter || age >= 5) && other is LivingEntity) {
            if (other is PlayerEntity && shooter is PlayerEntity && !shooter.shouldDamagePlayer(other)) {
              continue
            }

            val size = 0.3f
            val singleCollision = other.getBoundingBox().expand(size.toDouble())
            val optional = singleCollision.raycast(pos, nextPos)

            if (optional.isPresent) {
              val hit = optional.get()
              val distanceSq = hit.squaredDistanceTo(pos)
              if (distanceSq < closestDistance) {
                closestEntity = other
                closestDistance = distanceSq
                nextPos = hit
              }
            }
          }
        }

        if (closestEntity != null) {
          collision = EntityHitResult(closestEntity)
        }

        remaining -= pos.distanceTo(nextPos) / sqrt(vel.lengthSquared())

        // Set position
        setPosition(nextPos)
        // syncPositions(false) // TODO: Verify this is no longer needed

        // Handle collision
        if (collision != null && collision.type != HitResult.Type.MISS) {
          val blockPos = BlockPos.ofFloored(collision.pos)
          if (collision.type == HitResult.Type.BLOCK && world.getBlockState(blockPos).isOf(Blocks.NETHER_PORTAL)) {
            setInNetherPortal(blockPos)
          } else {
            onImpact(collision)
          }
        }
      }
    } else {
      // Set position
      val vel = velocity
      val newPos = pos.add(vel)
      setPosition(newPos)
    }

    if (!world.isClient && (potency <= 0 || age > lifetime)) {
      kill()
    }
  }

  private fun onImpact(hitResult: HitResult) {
    if (world.isClient) return
    val world = world as? ServerWorld ?: return

    if (hitResult.type == HitResult.Type.BLOCK) {
      if (hitResult !is BlockHitResult) return

      val position = BlockPos.ofFloored(hitResult.getPos())
      val blockState = world.getBlockState(position)
      val block = blockState.block

      if (!blockState.isAir && blockState.block !is FluidBlock) {
        val hardness = blockState.getHardness(world, position)
        val player = getShooterPlayer() ?: return

        // Ensure the player is set up correctly
        syncPositions(true)

        if (!world.canPlayerModifyAt(player, position)) {
          potency = -1f
          return
        }

        // TODO: Post a block break event here
        if (block === Blocks.TNT) {
          potency -= hardness

          // Ignite TNT blocks
          val shooter = getShooter()
          TntBlockInvoker.invokePrimeTnt(
            world, position,
            if (shooter is LivingEntity) shooter else getShooterPlayer()
          )
          world.removeBlock(position, false)
        } else if (block === Blocks.OBSIDIAN) {
          potency -= hardness

          // Attempt to light obsidian blocks, creating a portal
          val offset = position.offset(hitResult.side)
          val offsetState = world.getBlockState(offset)
          if (!offsetState.isAir) return

          if (world is ServerWorld) {
            // TODO: Verify this check is sufficient
            if (!world.server.isSpawnProtected(world, offset, player)) {
              world.playSound(null, offset, ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0f,
                rand.nextFloat() * 0.4f + 0.8f)
              world.setBlockState(offset, Blocks.FIRE.defaultState)
            }
          }
        } else if (hardness > -1 && hardness <= potency) {
          potency -= hardness

          // Mimic the behavior of ServerPlayerInteractionManager.tryBreakBlock.
          // Permission check first: (isSpawnProtected, or ClaimKit)
          if (canBreakBlock(world, position, false, player) && block !is OperatorBlock) {
            // Get the block entity before breaking the block, as we need it in dropStacks before it's removed from the
            // world.
            val blockEntity = world.getBlockEntity(position)

            // Don't do the drops in tryBreakBlock, as onBreak might try to do them itself. For the other cases, call
            // dropStacks after.
            block.onBreak(world, position, blockState, player)

            val broken = tryBreakBlock(world, position, false, player)
            if (broken) {
              block.onBroken(world, position, blockState)

              // ServerPlayerInteractionManager only calls dropStacks if the user is not in creative mode. This results
              // in interesting behavior, such as shulker boxes calling dropStacks in onBreak for creative mode, and in
              // getDroppedStacks for survival mode. Since that behavior checks `player`, we need to do it here too.
              // Note that this results in most normal blocks not dropping if a creative player fires a laser. Blocks
              // with special behavior in onBreak (shulkers, computers, chest contents) will still drop.
              if (!player.isCreative && !Registries.BLOCK.getEntry(block).isIn(LASER_DONT_DROP)) {
                // ServerPlayerInteractionManager calls dropStacks via afterBreak, but we don't want to increment
                // exhaustion, so call dropStacks directly instead.
                Block.dropStacks(blockState, world, position, blockEntity, player, ItemStack.EMPTY)
              }
            }
          }
        } else {
          potency = -1f
        }
      }
    } else if (hitResult.type == HitResult.Type.ENTITY) {
      if (hitResult !is EntityHitResult) return

      val entity: Entity = hitResult.entity
      if (entity is LivingEntity && canDamageEntity(entity)) {
        // Ensure the player is set up correctly
        syncPositions(true)

        val shooter = getShooter()

        if (entity.type.isIn(PlethoraEntityTags.LASERS_PROVIDE_ENERGY)) {
          // When shooting blazes, apply a strength effect and heal them instead.
          val effect = StatusEffectInstance(StatusEffects.STRENGTH, (20 * potency).toInt())
          entity.addStatusEffect(effect, shooter)
          entity.heal((potency * config.laser.damage).toFloat())
        } else {
          val damageType = world.registryManager.get(RegistryKeys.DAMAGE_TYPE).entryOf(ModDamageSources.LASER)
          val source = DamageSource(damageType, this, shooter)
          entity.setFireTicks(5)
          entity.damage(source, (potency * config.laser.damage).toFloat())
        }

        potency = -1f
      }
    }
  }

  private fun getShooter(): Entity? {
    if (shooter != null) return shooter

    val world = world as? ServerWorld ?: return null
    return PlethoraFakePlayer(world, null, shooterOwner)
      .also { shooterPlayer = it; shooter = it }
  }

  private fun getShooterPlayer(): PlayerEntity? {
    if (shooterPlayer != null) return shooterPlayer

    val shooter = getShooter()
    if (shooter is PlayerEntity) return shooter.also { shooterPlayer = it }

    val world = world as? ServerWorld ?: return null
    return PlethoraFakePlayer(world, shooter, shooterOwner)
      .also { shooterPlayer = it }
  }

  private fun syncPositions(force: Boolean) {
    val fakePlayer = shooterPlayer as? PlethoraFakePlayer ?: return
    val shooter = shooter

    if (shooter != null && shooter !== fakePlayer) {
      syncFromEntity(fakePlayer, shooter)
    } else if (shooterPos != null) {
      val current = fakePlayer.entityWorld

      if (current == null || current.registryKey != shooterPos!!.worldKey) {
        // Don't load another world unless we have to
        val replace = if (force) shooterPos!!.getWorld(entityWorld.server) else shooterPos!!.world

        if (replace == null) {
          syncFromEntity(fakePlayer, this)
        } else {
          syncFromPos(fakePlayer, replace, shooterPos!!.pos, yaw, pitch)
        }
      } else {
        syncFromPos(fakePlayer, current, shooterPos!!.pos, yaw, pitch)
      }
    } else {
      syncFromEntity(fakePlayer, this)
    }
  }

  override fun getOwningProfile(): GameProfile? {
    return shooterOwner
  }

  private fun canBreakBlock(world: World, pos: BlockPos, drop: Boolean, player: PlayerEntity): Boolean {
    // Injection point for ClaimKit
    return world.canPlayerModifyAt(player, pos)
  }

  private fun tryBreakBlock(world: World, pos: BlockPos, drop: Boolean, player: PlayerEntity): Boolean {
    // Injection point for ClaimKit
    return world.breakBlock(pos, drop, player)
  }

  private fun canDamageEntity(entity: Entity): Boolean {
    // Injection point for ClaimKit
    return true
  }

  companion object {
    private val rand = Random()

    private val lifetime = config.laser.lifetime
    private val trackedLasers = mutableSetOf<LaserEntity>()
    private val laserCleanupInterval = lifetime * 2 // Default 5 * 2 seconds

    private fun syncFromEntity(player: PlayerEntity, from: Entity) {
      val fromPos = from.pos
      val fromWorld = from.entityWorld

      if (player.world != fromWorld && fromWorld is ServerWorld) {
        player.moveToWorld(fromWorld)
      }

      player.updatePositionAndAngles(fromPos.x, fromPos.y, fromPos.z, from.yaw, from.pitch)
    }

    private fun syncFromPos(player: PlayerEntity, @Nonnull world: World, pos: Vec3d, yaw: Float, pitch: Float) {
      if (player.world != world && world is ServerWorld) {
        player.moveToWorld(world)
      }

      player.updatePositionAndAngles(pos.x, pos.y, pos.z, yaw, pitch)
    }

    @JvmStatic
    fun initLaserTracker() {
      // If a chunk is loaded with a level greater than 31, lasers will not be ticked and thus will not be removed
      // from the world when their age exceeds the lifetime. Keep track of lasers and remove them manually.
      ServerEntityEvents.ENTITY_LOAD.register { entity, _ ->
        if (entity is LaserEntity) {
          trackedLasers.add(entity)
        }
      }

      ServerEntityEvents.ENTITY_UNLOAD.register { entity, _ ->
        if (entity is LaserEntity) {
          trackedLasers.remove(entity)
        }
      }

      ServerTickEvents.END_SERVER_TICK.register(ServerTickEvents.EndTick { server ->
        // Check for lasers that should've been removed every 10 seconds
        val time = server.overworld.time
        if (time % laserCleanupInterval == 0L) {
          cleanupLasers(time - lifetime)
        }
      })
    }

    private fun cleanupLasers(expireThreshold: Long) {
      val toRemove = trackedLasers.filter { it.spawnTime < expireThreshold }

      if (toRemove.isNotEmpty()) {
        val worstChunk = if (toRemove.size > 10) findWorstLaserChunk(toRemove) else null
        val worstChunkStr = worstChunk?.let { (world, pos, count) -> " ($count in chunk $pos in $world)" } ?: ""
        Plethora.log.info("Removing {} expired lasers{}", toRemove.size, worstChunkStr)

        toRemove.forEach { it.kill() }
      }
    }

    private fun findWorstLaserChunk(lasers: List<LaserEntity>): Triple<Identifier, ChunkPos, Int>? {
      val laserChunks = lasers.groupBy { Pair(it.world.registryKey.value, it.chunkPos) }
      val worst = laserChunks.maxByOrNull { it.value.size } ?: return null
      return Triple(worst.key.first, worst.key.second, worst.value.size)
    }
  }
}
