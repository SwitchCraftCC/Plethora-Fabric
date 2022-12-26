package io.sc3.plethora.gameplay.neural;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.IPocketAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;

/**
 * Proxy IPocketAccess for neural interfaces.
 */
public class NeuralPocketAccess implements IPocketAccess {
    private final NeuralComputer neural;

    public NeuralPocketAccess(NeuralComputer neural) {
        this.neural = neural;
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

    @Nonnull
    @Override
    public Map<Identifier, IPeripheral> getUpgrades() {
        return Collections.emptyMap(); // TODO: Necessary to do anything with this?
    }
}
