# OldMC

> Restore the classic Minecraft experience.

OldMC is a client-side Fabric mod that aims to bring the classic Minecraft experience to newer versions(1.21.9+).

## Features

- **Dirt background** - Restores the dirt background from Minecraft 1.20.4 and earlier, replacing the transparent background used since 1.20.5.
- **Classic F3 layout** - Restores the F3 layout from earlier versions in Minecraft 1.21.9 and later.
- **World loading percentage** - Restores the world loading screen from before Minecraft 1.21.9 by removing the progress bar and bringing back the percentage.
- **Classic loading grid size** - Restores the 29×29 chunk grid used before Minecraft 1.21.9.
- **Restore baby mob appearances (26.1+)** - Restores the look of baby mobs from before 26.1.
- **Restore baby mob sounds (26.1+)** - Restores the sounds baby mobs made before 26.1.
- **Classic world creation screen** - Restores the Create New World screen from Minecraft 1.19.3 and earlier.
- **Classic title screen button layout** - Restores the title screen button layout from earlier versions in Minecraft 26.2 and later.
- **Text-only pause menu buttons** - Restores the pause menu button layout from earlier versions in Minecraft 26.2 and later.
- **Pause menu Friends button position** - Choose where the Friends button appears on the pause menu, or hide it.
- **Title screen Friends button position** - Choose where the Friends button appears on the title screen, or hide it. Requires “Classic title screen button layout”.
- **Remove “Report Bugs” and “Give Feedback”** - Removes both buttons from the pause menu.
- **Remove “Player Reporting”** - Removes the “Player Reporting” button from the pause menu.
- **Classic version numbers** - Displays the game version number in the format you choose under “Version number format” below.
- **Version number format** - Choose between “Major update format”, “Year-based update format”, and “1.21.x format”. See the examples below.

| Actual version | Major update format | Year-based update format | 1.21.x format |
| --- | --- | --- | --- |
| 26.1 | 1.22 | 1.26 | 1.21.12 |
| 26.1.1 | 1.22.1 | 1.26.1 | 1.21.13 |
| 26.1.2 | 1.22.2 | 1.26.2 | 1.21.14 |
| 26.2 | 1.23 | 1.26.3 | 1.21.15 |
| 26.3 | 1.24 | 1.26.4 | 1.21.16 |

## Requirements
- [Fabric Loader](https://github.com/FabricMC/fabric-loader) `>=0.17.3`
- [Fabric API](https://github.com/FabricMC/fabric-api)
- Minecraft `>=1.21.9`

## Build from source

```bash
git clone https://github.com/ismudkipx/oldmc.git
cd oldmc
./gradlew build
```

The build produces two files in the `build/libs` folder, one for Minecraft 1.21.9–1.21.11 and one for Minecraft 26.1–26.3.
