package pw.switchcraft.plethora.util;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.Map;
import java.util.Set;

import static java.util.Map.entry;

@ConfigSerializable
public final class Config {
    public Laser laser = new Laser();
    public Kinetic kinetic = new Kinetic();
    public Scanner scanner = new Scanner();
    public Sensor sensor = new Sensor();
    public NeuralInterface neuralInterface = new NeuralInterface();

    @ConfigSerializable
    public static class Laser {
        @Comment("The minimum power of a laser.")
        public static double minimumPotency = 0.5;

        @Comment("The maximum power of a laser.")
        public static double maximumPotency = 5;

        @Comment("The damage done to an entity by a laser per potency.")
        public static double damage = 4;

        @Comment("The maximum time in ticks a laser can exist for before it'll despawn.")
        public static int lifetime = 5 * 20;
    }

    @ConfigSerializable
    public static class Kinetic {
        @Comment("The maximum velocity the kinetic manipulator can apply to you.")
        public static int launchMax = 4;

        @Comment("The value to scale the y velocity by, helps limit how high the player can go.")
        public static double launchYScale = 0.5;

        @Comment("Whether to scale the fall distance after launching.\n\n"
            + "This means that the player will not die from fall damage if they launch themselves upwards in order to "
            + "cancel out their negative velocity. This may not work correctly with mods which provide custom gravity, "
            + "such as Galacticraft.")
        public static boolean launchFallReset = true;

        @Comment("Whether to reset the floating timer after launching.\n\n"
            + "This means the player will not be kicked for flying after using the kinetic augment a lot.")
        public static boolean launchFloatReset = true;

        @Comment("The value to scale the velocity by when flying, helps limit how fast the player can go.")
        public static double launchElytraScale = 0.4;
    }

    @ConfigSerializable
    public static class Scanner {
        @Comment("The radius scanners can get blocks in.")
        public static int radius = 8;

        @Comment("The radius a fully upgraded scanner can get blocks in.")
        public static int maxRadius = 16;

        @Comment("The additional cost each level incurs for scan().")
        public static int scanLevelCost = 50;

        @Comment("Custom colour mapping for ore blocks.\n\n"
            + "The key is the full ore block ID (e.g. `minecraft:redstone_ore`) and the value is the hex of the colour "
            + "to use in #RRGGBB format (e.g. `#ff0000`).")
        public static Map<String, String> oreColours = Map.ofEntries(
            entry("minecraft:coal_ore", "#252525"),
            entry("minecraft:copper_ore", "#4fba98"),
            entry("minecraft:diamond_ore", "#1ed0d6"),
            entry("minecraft:emerald_ore", "#41f384"),
            entry("minecraft:gold_ore", "#fcee4b"),
            entry("minecraft:iron_ore", "#d8af93"),
            entry("minecraft:lapis_ore", "#446fdc"),
            entry("minecraft:redstone_ore", "#ff0000"),
            entry("minecraft:nether_gold_ore", "#fcee4b"),
            entry("minecraft:nether_quartz_ore", "#eae5de"),
            entry("minecraft:deepslate_coal_ore", "#252525"),
            entry("minecraft:deepslate_copper_ore", "#4fba98"),
            entry("minecraft:deepslate_diamond_ore", "#1ed0d6"),
            entry("minecraft:deepslate_emerald_ore", "#41f384"),
            entry("minecraft:deepslate_gold_ore", "#fcee4b"),
            entry("minecraft:deepslate_iron_ore", "#d8af93"),
            entry("minecraft:deepslate_lapis_ore", "#446fdc"),
            entry("minecraft:deepslate_redstone_ore", "#ff0000"),
            entry("minecraft:raw_iron_block", "#d8af93"),
            entry("minecraft:raw_copper_block", "#4fba98"),
            entry("minecraft:raw_gold_block", "#fcee4b")
        );
    }

    @ConfigSerializable
    public static class Sensor {
        @Comment("The radius sensors can get entities in.")
        public static int radius = 16;

        @Comment("The radius a fully upgraded sensor can get entities in.")
        public static int maxRadius = 32;

        @Comment("The additional cost each level incurs for sense().")
        public static int senseLevelCost = 40;

        @Comment("Custom colour mapping for entities.\n\n"
            + "The key is the full entity ID (e.g. `minecraft:creeper`) and the value is the hex of the colour "
            + "to use in #RRGGBB format (e.g. `#ff0000`).")
        public static Map<String, String> entityColours = Map.ofEntries(); // TODO: Add default colours for entities
    }

    @ConfigSerializable
    public static class NeuralInterface {
        @Comment("List of peripheral item IDs that can be used in neural interface. Edit with caution.") // TODO
        public static Set<String> peripheralItemIds = Set.of( // Configurate will make this a HashSet
            "computercraft:speaker",
            "computercraft:wireless_modem_normal",
            "computercraft:wireless_modem_advanced"
        );
    }
}
