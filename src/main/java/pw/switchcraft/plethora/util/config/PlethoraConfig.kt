package pw.switchcraft.plethora.util.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment

@ConfigSerializable
class PlethoraConfig {
  @JvmField var laser = Laser()
  @JvmField var kinetic = Kinetic()
  @JvmField var scanner = Scanner()
  @JvmField var sensor = Sensor()
  @JvmField var neuralInterface = NeuralInterface()

  @Comment("Some methods have a particular cost: they consume a set amount of energy from their owner. This level " +
    "regenerates over time.\n\nNote: These values only apply to the default handler. Other mods may add custom " +
    "handlers.")
  @JvmField var costSystem = CostSystem()

  @ConfigSerializable
  class Laser {
    @JvmField
    @Comment("The minimum power of a laser.")
    var minimumPotency = 0.5

    @JvmField
    @Comment("The maximum power of a laser.")
    var maximumPotency = 5.0

    @JvmField
    @Comment("The damage done to an entity by a laser per potency.")
    var damage = 4.0

    @JvmField
    @Comment("The energy cost per potency for a laser.")
    var cost = 10.0

    @JvmField
    @Comment("The maximum time in ticks a laser can exist for before it'll despawn.")
    var lifetime = 5 * 20
  }

  @ConfigSerializable
  class Kinetic {
    @JvmField
    @Comment("The maximum velocity the kinetic manipulator can apply to you.")
    var launchMax = 4

    @JvmField
    @Comment("The cost per launch power.")
    var launchCost = 0

    @JvmField
    @Comment("The value to scale the y velocity by, helps limit how high the player can go.")
    var launchYScale = 0.5

    @JvmField
    @Comment("Whether to scale the fall distance after launching.\n\nThis means that the player will not die from fall"
      + " damage if they launch themselves upwards in order to cancel out their negative velocity. This may not work " +
      " correctly with mods which provide custom gravity, such as Galacticraft.")
    var launchFallReset = true

    @JvmField
    @Comment("Whether to reset the floating timer after launching.\n\nThis means the player will not be kicked for " +
      "flying after using the kinetic augment a lot.")
    var launchFloatReset = true

    @JvmField
    @Comment("The value to scale the velocity by when flying, helps limit how fast the player can go.")
    var launchElytraScale = 0.4
  }

  @ConfigSerializable
  class Scanner {
    @JvmField
    @Comment("The radius scanners can get blocks in.")
    var radius = 8

    @JvmField
    @Comment("The radius a fully upgraded scanner can get blocks in.")
    var maxRadius = 16

    @JvmField
    @Comment("The additional cost each level incurs for scan().")
    var scanLevelCost = 50

    @JvmField
    @Comment("Custom colour mapping for ore blocks.\n\nThe key is the full ore block ID (e.g. `minecraft:redstone_ore`)"
      + " and the value is the hex of the colour to use in #RRGGBB format (e.g. `#ff0000`).")
    var oreColours = defaultOreColors.toMutableMap()

    companion object {
      val defaultOreColors = mapOf(
        "minecraft:coal_ore"               to "#252525",
        "minecraft:copper_ore"             to "#4fba98",
        "minecraft:diamond_ore"            to "#1ed0d6",
        "minecraft:emerald_ore"            to "#41f384",
        "minecraft:gold_ore"               to "#fcee4b",
        "minecraft:iron_ore"               to "#d8af93",
        "minecraft:lapis_ore"              to "#446fdc",
        "minecraft:redstone_ore"           to "#ff0000",
        "minecraft:nether_gold_ore"        to "#fcee4b",
        "minecraft:nether_quartz_ore"      to "#eae5de",
        "minecraft:deepslate_coal_ore"     to "#252525",
        "minecraft:deepslate_copper_ore"   to "#4fba98",
        "minecraft:deepslate_diamond_ore"  to "#1ed0d6",
        "minecraft:deepslate_emerald_ore"  to "#41f384",
        "minecraft:deepslate_gold_ore"     to "#fcee4b",
        "minecraft:deepslate_iron_ore"     to "#d8af93",
        "minecraft:deepslate_lapis_ore"    to "#446fdc",
        "minecraft:deepslate_redstone_ore" to "#ff0000",
        "minecraft:raw_iron_block"         to "#d8af93",
        "minecraft:raw_copper_block"       to "#4fba98",
        "minecraft:raw_gold_block"         to "#fcee4b"
      )
    }
  }

  @ConfigSerializable
  class Sensor {
    @JvmField
    @Comment("The radius sensors can get entities in.")
    var radius = 16

    @JvmField
    @Comment("The radius a fully upgraded sensor can get entities in.")
    var maxRadius = 32

    @JvmField
    @Comment("The additional cost each level incurs for sense().")
    var senseLevelCost = 40

    @JvmField
    @Comment("Custom colour mapping for entities.\n\nThe key is the full entity ID (e.g. `minecraft:creeper`) and the "
      + "value is the hex of the colour to use in #RRGGBB format (e.g. `#ff0000`).")
    var entityColours = mutableMapOf<String, String>() // TODO: Add default colours for entities
  }

  @ConfigSerializable
  class NeuralInterface {
    @JvmField
    @Comment("List of pocket upgrade item IDs that can be used in neural interface as peripherals.")
    var peripheralItemIds = mutableSetOf(
      "computercraft:speaker",
      "computercraft:wireless_modem_normal",
      "computercraft:wireless_modem_advanced"
    )
  }

  @ConfigSerializable
  class CostSystem {
    @JvmField
    @Comment("The energy level all systems start at.")
    var initial = 100.0

    @JvmField
    @Comment("The amount of energy regenerated each tick.")
    var regen = 10.0

    @JvmField
    @Comment("The maximum energy level an item can have.")
    var limit = 100.0

    @JvmField
    @Comment("Allows costs to go into the negative. Methods will fail when there is negative energy. This allows you "
      + "to use costs higher than the allocated buffer and so have a traditional rate-limiting system.")
    var allowNegative = false

    @JvmField
    @Comment("Wait for the system to get sufficient energy instead of throwing an error.")
    var awaitRegen = true

    var baseCosts = mutableMapOf<String, Int>()
  }
}
