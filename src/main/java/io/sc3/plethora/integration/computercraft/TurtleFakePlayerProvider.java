package io.sc3.plethora.integration.computercraft;

import com.mojang.authlib.GameProfile;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.shared.turtle.TurtleUtil;
import dan200.computercraft.shared.util.DirectionUtil;
import dan200.computercraft.shared.util.InventoryUtil;
import dan200.computercraft.shared.util.WorldUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import io.sc3.plethora.api.IPlayerOwnable;
import io.sc3.plethora.gameplay.PlethoraFakePlayer;

import java.util.WeakHashMap;

public class TurtleFakePlayerProvider {
    private static final WeakHashMap<ITurtleAccess, PlethoraFakePlayer> registeredPlayers = new WeakHashMap<>();

    public static PlethoraFakePlayer getPlayer(ITurtleAccess entity, IPlayerOwnable ownable) {
        return getPlayer(entity, ownable == null ? null : ownable.getOwningProfile());
    }

    public static PlethoraFakePlayer getPlayer(ITurtleAccess turtle, GameProfile profile) {
        PlethoraFakePlayer fake = registeredPlayers.get(turtle);
        if (fake == null) {
            fake = new PlethoraFakePlayer((ServerWorld) turtle.getLevel(), null, profile);
            registeredPlayers.put(turtle, fake);
        }

        return fake;
    }

    public static void load(PlethoraFakePlayer player, ITurtleAccess turtle, Direction direction) {
        player.setWorld((ServerWorld) turtle.getLevel());

        BlockPos position = turtle.getPosition();
        player.updatePositionAndAngles(
            position.getX() + 0.5 + 0.51 * direction.getOffsetX(),
            position.getY() + 0.5 + 0.51 * direction.getOffsetY(),
            position.getZ() + 0.5 + 0.51 * direction.getOffsetZ(),
            (direction.getAxis() != Direction.Axis.Y ? direction : turtle.getDirection()).asRotation(),
            direction.getAxis() != Direction.Axis.Y ? 0 : DirectionUtil.toPitchAngle(direction)
        );
        player.setHeadYaw(player.getYaw());

        player.setSneaking(false);

        PlayerInventory playerInv = player.getInventory();
        playerInv.selectedSlot = 0;

        // Copy primary items into player inventory and empty the rest
        Inventory turtleInv = turtle.getInventory();
        int size = turtleInv.size();
        int largerSize = playerInv.size();
        playerInv.selectedSlot = turtle.getSelectedSlot();
        for (int i = 0; i < size; i++) {
            playerInv.setStack(i, turtleInv.getStack(i));
        }
        for (int i = size; i < largerSize; i++) {
            playerInv.setStack(i, ItemStack.EMPTY);
        }

        playerInv.markDirty();

        // Add properties
        ItemStack activeStack = player.getStackInHand(Hand.MAIN_HAND);
        if (!activeStack.isEmpty()) {
            player.getAttributes().addTemporaryModifiers(activeStack.getAttributeModifiers(EquipmentSlot.MAINHAND));
        }
    }

    public static void unload(PlethoraFakePlayer player, ITurtleAccess turtle) {
        PlayerInventory playerInv = player.getInventory();
        playerInv.selectedSlot = 0;

        // Remove properties
        ItemStack activeStack = player.getStackInHand(Hand.MAIN_HAND);
        if (!activeStack.isEmpty()) {
            player.getAttributes().removeModifiers(activeStack.getAttributeModifiers(EquipmentSlot.MAINHAND));
        }

        // Copy primary items into turtle playerInv and then insert/drop the rest
        Inventory turtleInv = turtle.getInventory();
        int size = turtleInv.size();
        int largerSize = playerInv.size();
        playerInv.selectedSlot = turtle.getSelectedSlot();
        for (int i = 0; i < size; i++) {
            turtleInv.setStack(i, playerInv.getStack(i));
            playerInv.setStack(i, ItemStack.EMPTY);
        }

        for (int i = size; i < largerSize; i++) {
          TurtleUtil.storeItemOrDrop(turtle, playerInv.getStack(i));

            playerInv.setStack(i, ItemStack.EMPTY);
        }

        playerInv.markDirty();
    }
}
