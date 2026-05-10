package com.example.goathorndetector;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.logging.Level;

public class GoatHornDetector extends JavaPlugin implements Listener {
    
    // Configuration variables
    private boolean enabled;
    private String targetInstrument;
    private List<String> commands;
    
    @Override
    public void onEnable() {
        // Save default config if it doesn't exist
        saveDefaultConfig();
        
        // Load configuration
        loadConfiguration();
        
        // Register event listener
        getServer().getPluginManager().registerEvents(this, this);
        
        getLogger().info("GoatHornDetector plugin has been enabled!");
        getLogger().info("Target instrument: " + targetInstrument);
        getLogger().info("Plugin enabled: " + enabled);
    }
    
    @Override
    public void onDisable() {
        getLogger().info("GoatHornDetector plugin has been disabled!");
    }
    
    private void loadConfiguration() {
        FileConfiguration config = getConfig();
        
        // Set default values if they don't exist
        config.addDefault("enabled", true);
        config.addDefault("instrument", "minecraft:dieu_cay");
        config.addDefault("commands", List.of(
            "say %player% used the special goat horn!",
            "give %player% diamond 1",
            "title %player% title {\"text\":\"Special Horn Used!\",\"color\":\"gold\"}"
        ));
        
        // Save defaults to file
        config.options().copyDefaults(true);
        saveConfig();
        
        // Load values
        enabled = config.getBoolean("enabled");
        targetInstrument = config.getString("instrument");
        commands = config.getStringList("commands");
        
        getLogger().info("Configuration loaded - Enabled: " + enabled + ", Target: " + targetInstrument);
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Check if plugin is enabled
        if (!enabled) {
            return;
        }
        
        // Check if it's a right-click action
        if (!event.getAction().toString().contains("RIGHT_CLICK")) {
            return;
        }
        
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        // Check if player is holding an item
        if (item == null) {
            return;
        }
        
        // Check if the item is a goat horn
        if (item.getType() != Material.GOAT_HORN) {
            return;
        }
        
        // Get the instrument NBT data
        String instrument = getGoatHornInstrument(item);
        
        // Debug message - always print when a goat horn is used
        getLogger().info("Player " + player.getName() + " used goat horn with instrument: " + 
                        (instrument != null ? instrument : "null/unknown"));
        
        // Check if the instrument matches our target
        if (instrument != null && instrument.equals(targetInstrument)) {
            getLogger().info("Instrument match found! Executing commands for player: " + player.getName());
            
            // Execute all configured commands
            executeCommands(player);
        } else {
            getLogger().info("Instrument '" + instrument + "' does not match target '" + targetInstrument + "'");
        }
    }
    
    private String getGoatHornInstrument(ItemStack item) {
        if (item == null || item.getType() != Material.GOAT_HORN) {
            return null;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        
        // Debug: Print all available persistent data keys
        getLogger().info("Debug: Checking persistent data keys for goat horn:");
        for (NamespacedKey key : meta.getPersistentDataContainer().getKeys()) {
            getLogger().info("  Found key: " + key.toString());
        }
        
        // Try multiple possible NBT key variations
        String[] possibleKeys = {
            "instrument",
            "goat_horn_instrument", 
            "Instrument",
            "minecraft:instrument"
        };
        
        for (String keyStr : possibleKeys) {
            try {
                NamespacedKey key = NamespacedKey.fromString(keyStr);
                if (key != null && meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                    String instrument = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                    getLogger().info("Debug: Found instrument via key '" + keyStr + "': " + instrument);
                    return instrument;
                }
            } catch (Exception e) {
                getLogger().info("Debug: Failed to check key '" + keyStr + "': " + e.getMessage());
            }
        }
        
        // Try to get instrument from MusicInstrument meta (Paper specific)
        try {
            if (meta instanceof org.bukkit.inventory.meta.MusicInstrumentMeta) {
                org.bukkit.inventory.meta.MusicInstrumentMeta musicMeta = 
                    (org.bukkit.inventory.meta.MusicInstrumentMeta) meta;
                
                if (musicMeta.getInstrument() != null) {
                    String instrument = musicMeta.getInstrument().getKey().toString();
                    getLogger().info("Debug: Found instrument via MusicInstrumentMeta: " + instrument);
                    return instrument;
                }
            }
        } catch (Exception e) {
            getLogger().info("Debug: MusicInstrumentMeta method failed: " + e.getMessage());
        }
        
        // Check custom model data as fallback
        if (meta.hasCustomModelData()) {
            int customModelData = meta.getCustomModelData();
            getLogger().info("Debug: Goat horn has custom model data: " + customModelData);
            String mapped = mapCustomModelDataToInstrument(customModelData);
            if (mapped != null) {
                getLogger().info("Debug: Mapped custom model data to: " + mapped);
                return mapped;
            }
        }
        
        // Try to access NBT data directly using Paper's NBT API
        try {
            // Get the item's NBT compound
            var nbtItem = item.asOne(); // Paper method to get single item
            // This requires more advanced NBT access - let's try a different approach
            
            getLogger().info("Debug: Item display name: " + 
                           (meta.hasDisplayName() ? meta.getDisplayName() : "none"));
            getLogger().info("Debug: Item lore: " + 
                           (meta.hasLore() ? meta.getLore().toString() : "none"));
            
        } catch (Exception e) {
            getLogger().info("Debug: Direct NBT access failed: " + e.getMessage());
        }
        
        getLogger().info("Debug: Could not determine instrument for goat horn");
        return null;
    }
    
    private String mapCustomModelDataToInstrument(int customModelData) {
        // Updated mapping for Minecraft 1.21+ goat horn instruments
        // These are the default goat horn variants in Minecraft
        switch (customModelData) {
            case 0: return "minecraft:ponder_goat_horn";
            case 1: return "minecraft:sing_goat_horn";
            case 2: return "minecraft:seek_goat_horn";
            case 3: return "minecraft:feel_goat_horn";
            case 4: return "minecraft:admire_goat_horn";
            case 5: return "minecraft:call_goat_horn";
            case 6: return "minecraft:yearn_goat_horn";
            case 7: return "minecraft:dream_goat_horn";
            // Add your custom instrument mapping here
            case 999: return "minecraft:dieu_cay"; // Example custom mapping
            default: 
                getLogger().info("Debug: Unknown custom model data: " + customModelData + 
                               " - you may need to add mapping for this value");
                return "minecraft:unknown_horn_" + customModelData;
        }
    }
    
    private void executeCommands(Player player) {
        if (commands == null || commands.isEmpty()) {
            getLogger().warning("No commands configured to execute!");
            return;
        }
        
        getLogger().info("Commands will execute in 6.9 seconds for player: " + player.getName());
        
        // Execute commands after 6.9 second delay (138 ticks = 6.9 seconds)
        Bukkit.getScheduler().runTaskLater(this, () -> {
            getLogger().info("Executing delayed commands for player: " + player.getName());
            
            for (String command : commands) {
                // Replace %player% placeholder with actual player name
                String processedCommand = command.replace("%player%", player.getName());
                
                getLogger().info("Executing command: " + processedCommand);
                
                // Execute command as console
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processedCommand);
            }
        }, 138L); // 138 ticks = 6.9 seconds (20 ticks per second)
    }
    
    // Command to reload configuration
    @Override
    public boolean onCommand(org.bukkit.command.CommandSender sender, 
                           org.bukkit.command.Command command, 
                           String label, 
                           String[] args) {
        if (command.getName().equalsIgnoreCase("goathorn")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("goathorndetector.reload")) {
                    reloadConfig();
                    loadConfiguration();
                    sender.sendMessage("§aGoatHornDetector configuration reloaded!");
                    return true;
                } else {
                    sender.sendMessage("§cYou don't have permission to reload the configuration!");
                    return true;
                }
            }
        }
        return false;
    }
}

/* 
 * Config.yml file structure:
 * 
 * # GoatHornDetector Configuration
 * enabled: true
 * 
 * # The instrument NBT string to detect
 * instrument: "minecraft:dieu_cay"
 * 
 * # Commands to execute when the target horn is used
 * # Use %player% as placeholder for player name
 * commands:
 *   - "say %player% used the special goat horn!"
 *   - "give %player% diamond 1"
 *   - "title %player% title {\"text\":\"Special Horn Used!\",\"color\":\"gold\"}"
 *   - "particle heart ~ ~1 ~ 0.5 0.5 0.5 0.1 10"
 *   - "playsound minecraft:entity.player.levelup master %player% ~ ~ ~ 1 1"
 * 
 * Plugin.yml file structure:
 * 
 * name: GoatHornDetector
 * version: 1.0
 * main: com.example.goathorndetector.GoatHornDetector
 * api-version: 1.21
 * author: YourName
 * description: Detects specific goat horn usage and executes commands
 * 
 * commands:
 *   goathorn:
 *     description: GoatHornDetector commands
 *     usage: /goathorn reload
 *     permission: goathorndetector.use
 * 
 * permissions:
 *   goathorndetector.use:
 *     description: Basic plugin usage
 *     default: op
 *   goathorndetector.reload:
 *     description: Reload plugin configuration
 *     default: op
 */