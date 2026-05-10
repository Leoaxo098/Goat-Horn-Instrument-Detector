# GoatHornDetector

A Paper 1.21.3+ Minecraft plugin that detects when a player uses a specific goat horn instrument and executes configurable commands in response.

## Features

- 🎯 **Target specific goat horn instruments** by their NBT instrument identifier
- ⚡ **Execute multiple commands** when the target horn is used
- ⏱️ **Delayed execution** — commands run 6.9 seconds after the horn is used
- 🔧 **Fully configurable** via `config.yml`
- 🎮 **Player placeholder** — use `%player%` in commands to reference the triggering player
- 📋 **Reload command** — `/goathorn reload` to apply config changes without restarting

## Installation

1. Download `GoatHornDetector-1.0.jar` from the [releases page](https://github.com/YOUR_USERNAME/GoatHornDetector/releases) or build it yourself (see [Building](#building))
2. Place the JAR file in your Paper server's `plugins/` folder
3. Restart your server or run `/reload confirm`
4. Configure the plugin in `plugins/GoatHornDetector/config.yml`

## Configuration

### config.yml

```yaml
# GoatHornDetector Configuration
enabled: true

# The instrument NBT string to detect
instrument: "minecraft:dieu_cay"

# Commands to execute when the target horn is used
# Use %player% as placeholder for player name
commands:
  - "say %player% used the special goat horn!"
  - "give %player% diamond 1"
  - "title %player% title {\"text\":\"Special Horn Used!\",\"color\":\"gold\"}"
  - "particle heart ~ ~1 ~ 0.5 0.5 0.5 0.1 10"
  - "playsound minecraft:entity.player.levelup master %player% ~ ~ ~ 1 1"
```

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `enabled` | boolean | `true` | Enable or disable the plugin |
| `instrument` | string | `"minecraft:dieu_cay"` | The goat horn instrument NBT string to detect |
| `commands` | list of strings | _(see above)_ | Commands executed when the target horn is used |

> **Note on `instrument`:** This should be the **instrument identifier** stored in the goat horn's NBT data (e.g., `minecraft:ponder_goat_horn`, `minecraft:sing_goat_horn`, etc.). If you're using a custom instrument, match the identifier assigned to it.

## Commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/goathorn reload` | `goathorndetector.reload` | Reload the plugin configuration |

## Permissions

| Permission | Default | Description |
|------------|---------|-------------|
| `goathorndetector.use` | `op` | Basic plugin usage |
| `goathorndetector.reload` | `op` | Reload plugin configuration |

## Default Goat Horn Instruments

| Instrument | Description |
|------------|-------------|
| `minecraft:ponder_goat_horn` | Ponder |
| `minecraft:sing_goat_horn` | Sing |
| `minecraft:seek_goat_horn` | Seek |
| `minecraft:feel_goat_horn` | Feel |
| `minecraft:admire_goat_horn` | Admire |
| `minecraft:call_goat_horn` | Call |
| `minecraft:yearn_goat_horn` | Yearn |
| `minecraft:dream_goat_horn` | Dream |

## How It Works

1. A player right-clicks while holding a goat horn
2. The plugin reads the horn's instrument NBT data
3. If the instrument matches the configured `instrument` value, the scheduled commands execute
4. Commands are dispatched **6.9 seconds** (138 ticks) after the horn is used
5. `%player%` in each command is replaced with the triggering player's name

## Building

### Prerequisites

- Java 21 (JDK)
- Internet connection (for downloading dependencies)

### Steps

```bash
# Clone the repository
git clone https://github.com/YOUR_USERNAME/GoatHornDetector.git
cd GoatHornDetector

# Build the plugin
./gradlew build
```

The compiled JAR will be at `build/libs/GoatHornDetector-1.0.jar`.

## Requirements

- **Paper** 1.21.3 or higher
- **Java** 21 or higher

## License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.