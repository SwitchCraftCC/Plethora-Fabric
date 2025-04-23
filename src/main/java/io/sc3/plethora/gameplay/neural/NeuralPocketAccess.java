package io.sc3.plethora.gameplay.neural;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.upgrades.UpgradeData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Proxy IPocketAccess for neural interfaces.
 */
public class NeuralPocketAccess implements IPocketAccess {
    private final NeuralComputer neural;
    private UpgradeData<IPocketUpgrade> upgradeData;

    public NeuralPocketAccess(NeuralComputer neural) {
        this.neural = neural;
    }

  /**
   * Get the level in which the pocket computer exists.
   *
   * @return The pocket computer's level.
   */
  @Override
  public ServerWorld getLevel() {
    return this.neural.getLevel();
  }

  /**
   * Get the position of the pocket computer.
   *
   * @return The pocket computer's position.
   */
  @Override
  public Vec3d getPosition() {
    return this.neural.getPosition().toCenterPos();
  }

  @Nullable
    @Override
    public Entity getEntity() {
        WeakReference<LivingEntity> ref = neural.getEntity();
        return ref != null ? ref.get() : null;
    }

    @Override
    public int getColour() {
        return -1;
    }

    @Override
    public void setColour(int colour) {}

    @Override
    public int getLight() {
        return -1;
    }

    @Override
    public void setLight(int colour) {}

  /**
   * Get the currently equipped upgrade.
   *
   * @return The currently equipped upgrade.
   * @see #getUpgradeNBTData()
   * @see #setUpgrade(UpgradeData)
   */
  @Override
  @Nullable
  public UpgradeData<IPocketUpgrade> getUpgrade() {
    return this.upgradeData;
  }

  /**
   * Set the upgrade for this pocket computer, also updating the item stack.
   * <p>
   * Note this method is not thread safe - it must be called from the server thread.
   *
   * @param upgrade The new upgrade to set it to, may be {@code null}.
   * @see #getUpgrade()
   */
  @Override
  public void setUpgrade(@Nullable UpgradeData<IPocketUpgrade> upgrade) {
    upgradeData = upgrade;
  }

  @Nonnull
    @Override
    public NbtCompound getUpgradeNBTData() {
        return new NbtCompound(); // TODO: Necessary to do anything with this?
    }

    @Override
    public void updateUpgradeNBTData() {

    }

    @Override
    public void invalidatePeripheral() {

    }

  /**
   * Get a list of all upgrades for the pocket computer.
   *
   * @return A collection of all upgrade names.
   * @deprecated This is a relic of a previous API, which no longer makes sense with newer versions of ComputerCraft.
   */
  @Deprecated
  public Map<Identifier, IPeripheral> getUpgrades() {
    return Map.of();
  }
}
