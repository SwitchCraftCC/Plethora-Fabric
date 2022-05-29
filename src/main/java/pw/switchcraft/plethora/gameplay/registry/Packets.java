package pw.switchcraft.plethora.gameplay.registry;

import net.minecraft.util.Identifier;

import static pw.switchcraft.plethora.Plethora.MOD_ID;

public final class Packets {
    public static final Identifier SPAWN_PACKET_ID = new Identifier(MOD_ID, "spawn_packet");

    public static final Identifier CANVAS_ADD_PACKET_ID = new Identifier(MOD_ID, "canvas_add_packet");
    public static final Identifier CANVAS_REMOVE_PACKET_ID = new Identifier(MOD_ID, "canvas_remove_packet");
    public static final Identifier CANVAS_UPDATE_PACKET_ID = new Identifier(MOD_ID, "canvas_update_packet");
}
