# Plethora-Fabric

Plethora is a ComputerCraft/CC:Tweaked peripheral provider for Minecraft 1.20.1. Plethora-Fabric is a port of 
[Plethora](https://github.com/SquidDev-CC/plethora) for Fabric 1.20.1. It aims to provide both metadata and
peripherals for vanilla Minecraft and (*TODO*) mainstream mods.

Plethora also adds a series of "modules" to the game. These modules can be used by the player with varying success.
They really come in to their element when put in a manipulator, providing a series of methods which allow
interacting with your environment. This includes:
- Introspection: investigating the current player's inventory (and ender chest)
- Scanner: scans blocks in an area, gathering metadata about them.
- Sensor: scans entities in an area. Like the scanner this allows getting metadata.
- Frickin' laser beam. It fires lasers.
- Kinetic augment: allows remote access to your muscles, making them even stronger than before.

If you've ever wanted to embed a computer in your skull then today is your lucky day. Plethora provides a neural
interface which can be attached to your head, or some unsuspecting animal or monster. Right-clicking the entity with a
neural controller allows you to interact with it. You can insert modules (which will be wrapped as peripherals) and
manipulate them with the built-in computer. Building a cyborg army has never been so easy.

You can also add a kinetic augment to the neural interface. This allows controlling the host entity in various ways.

## Port status
The port to Fabric is still ongoing. SwitchCraft's needs were prioritised in the port, but we eventually aim to support
as much of the original mod as possible. For a detailed list of differences to the original mod, and information on how
to migrate your code, see the changes list at [docs.sc3.io](https://docs.sc3.io/whats-new/plethora.html).

## Requirements
- Minecraft 1.20.1
- [Fabric](https://fabricmc.net/use/installer)
- [Fabric API](https://modrinth.com/mod/fabric-api)
- [CC: Tweaked](https://modrinth.com/mod/cc-tweaked)
- [Trinkets](https://modrinth.com/mod/trinkets)

## Documentation
There is pretty comprehensive documentation on [the Plethora website](https://squiddev-cc.github.io/plethora/). This
contains tutorials, explanations of several fundamental concepts and thoroughly explained examples. For a detailed list 
of differences to the 1.12.2 mod, and information on how to migrate your code, see the changes list at 
[docs.sc3.io](https://docs.sc3.io/whats-new/plethora.html).

## Images
![](https://squiddev-cc.github.io/plethora/images/squids-laser.png)

> You know, I have one simple request. And that is to have ~~sharks~~ squid with frickin' laser beams attached to their heads!

![](https://squiddev-cc.github.io/plethora/images/modules.png)

> Various modules available

## Modpacks

Modpack use: **allowed**

Please note that each custom SwitchCraft mod has its own license, so check the license of each mod before using it in
your modpack.

## License

This mod and its source code is licensed under the 
[MIT license](https://github.com/SwitchCraftCC/Plethora-Fabric/blob/HEAD/LICENSE).
