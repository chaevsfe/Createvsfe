# Create-UfoPort
An unofficial port of [Create](https://modrinth.com/mod/create-fabric) for Minecraft 1.21.1 (Fabric), targeting feature parity with Create NeoForge 6.0.9.

## Features

All of Create's core features plus the new Create 6.0 High Logistics system:

- **High Logistics** (new in Create 6.0): Stock Ticker, Package entity/item, Packager, Frogport/Postbox, Factory Panel, Packager Link wireless networking, Table Cloth shop system, Redstone Requester
- All kinetics (gearboxes, shafts, belts, conveyors, chain conveyors, waterwheels, windmills, steam engines)
- Contraptions (mechanical pistons, bearings, gantries, elevators, trains)
- Fluid handling (pipes, pumps, tanks, spouts)
- Processing (crushing wheels, millstones, mixer, press, depot, deployer)
- Schematics and Ponder system
- ComputerCraft integration
- JEI/EMI recipe integration
- Train map integration (Xaero, JourneyMap, FTB Chunks)
- Flywheel 1.0.6 (external, replaces bundled 0.6.x — fixes Sodium/Iris conflicts)

## Requirements

- Minecraft 1.21.1
- [Fabric Loader](https://fabricmc.net/use/installer/) 0.16.0+
- [Fabric API](https://modrinth.com/mod/fabric-api) 0.112.0+1.21.1+
- [Forge Config API Port](https://modrinth.com/mod/forge-config-api-port) 21.0.5+
- [Flywheel](https://modrinth.com/mod/flywheel) 1.0.6+1.21.1 (bundled in jar)

## Compatibility

**Compatible:**
- [Cobblemon](https://modrinth.com/mod/cobblemon) — entity mixin hardened for Cobblemon entity initialization
- [Origins](https://modrinth.com/mod/origins) — getAttributes() null guard prevents crash on entity spawn
- [Farmer's Delight Refabricated](https://modrinth.com/mod/farmers-delight-refabricated) 3.0.0+
- Sodium 0.5.12+, Iris 1.8.0+

**Incompatible:**
- Any mod that embeds or depends on the official [Porting Lib](https://github.com/Fabricators-of-Create/Porting-Lib) modules (e.g. Sophisticated Backpacks/Storage). Create-UfoPort bundles its own `porting_lib_ufo` which conflicts with official porting_lib modules.

If you find a mod incompatibility, feel free to open an issue or send a pull request.

## Building from source

**Prerequisites:** JDK 21 (Microsoft or Temurin recommended)

```bash
git clone https://github.com/vlad250906/Create-UfoPort
cd Create-UfoPort
./gradlew build
```

Output jar: `build/libs/create-fabric-ufoport-<version>+mc1.21-1.21.1.jar`

## Credits

- [vlad250906](https://github.com/vlad250906) — original Create-UfoPort port
- [Create](https://github.com/Creators-of-Create/Create) — NeoForge source
- [Porting Lib](https://github.com/Fabricators-of-Create/Porting-Lib)
- [Flywheel](https://github.com/Engine-Room/Flywheel)
- [Milk Lib](https://github.com/TropheusJ/milk-lib)
- [Registrate Refabricated](https://github.com/Fabricators-of-Create/Registrate-Refabricated)
