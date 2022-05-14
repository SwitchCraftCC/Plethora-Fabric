package pw.switchcraft.plethora.api.module;

import pw.switchcraft.plethora.api.IWorldLocation;

import javax.annotation.Nonnull;

/**
 * A container for modules, allowing interaction with the outside world
 */
public interface IModuleAccess {
    /**
     * The owner of this module container. This is probably a {@link net.minecraft.block.entity.BlockEntity} or
     * {@link net.minecraft.entity.LivingEntity}.
     *
     * @return The module's owner. This is constant for the lifetime of the module access.
     */
    @Nonnull
    Object getOwner();

    /**
     * Get the position of this owner
     *
     * @return The owners' position
     */
    @Nonnull
    IWorldLocation getLocation();
}
