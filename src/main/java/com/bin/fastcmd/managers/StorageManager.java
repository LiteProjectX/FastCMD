package com.bin.fastcmd.managers;

import com.bin.fastcmd.FastCMD;
import com.bin.fastcmd.models.CommandBlockType;
import com.bin.fastcmd.models.VirtualCommandBlock;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Manages storage of virtual command blocks with UTF-8 support
 */
public class StorageManager {
    
    private final FastCMD plugin;
    private File storageFile;
    private FileConfiguration storageConfig;
    private Map<String, VirtualCommandBlock> commandBlocks;
    
    public StorageManager(FastCMD plugin) {
        this.plugin = plugin;
        this.commandBlocks = new HashMap<>();
        this.storageFile = new File(plugin.getDataFolder(), "storage.yml");
    }
    
    public void loadStorage() {
        // Create data folder if it doesn't exist
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        
        // Create storage file from template if it doesn't exist
        if (!storageFile.exists()) {
            plugin.saveResource("storage.yml", false);
        }
        
        try {
            // Load with UTF-8 encoding
            String content = new String(Files.readAllBytes(storageFile.toPath()), StandardCharsets.UTF_8);
            storageConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(
                new java.io.ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)), 
                StandardCharsets.UTF_8
            ));
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to load storage.yml: " + e.getMessage());
            storageConfig = new YamlConfiguration();
        }
        
        // Load command blocks from storage
        ConfigurationSection blocksSection = storageConfig.getConfigurationSection("virtual-command-blocks");
        if (blocksSection != null) {
            for (String name : blocksSection.getKeys(false)) {
                ConfigurationSection blockSection = blocksSection.getConfigurationSection(name);
                if (blockSection != null) {
                    try {
                        VirtualCommandBlock block = VirtualCommandBlock.fromMap(name, blockSection.getValues(false));
                        commandBlocks.put(name, block);
                    } catch (Exception e) {
                        plugin.getLogger().warning("Failed to load command block '" + name + "': " + e.getMessage());
                    }
                }
            }
        }
        
        plugin.getLogger().info("Loaded " + commandBlocks.size() + " virtual command blocks");
    }
    
    public void saveStorage() {
        try {
            // Clear existing data
            storageConfig.set("virtual-command-blocks", null);
            
            // Save all command blocks
            ConfigurationSection blocksSection = storageConfig.createSection("virtual-command-blocks");
            for (Map.Entry<String, VirtualCommandBlock> entry : commandBlocks.entrySet()) {
                ConfigurationSection blockSection = blocksSection.createSection(entry.getKey());
                Map<String, Object> blockData = entry.getValue().toMap();
                for (Map.Entry<String, Object> dataEntry : blockData.entrySet()) {
                    blockSection.set(dataEntry.getKey(), dataEntry.getValue());
                }
            }
            
            // Save with UTF-8 encoding
            String yamlContent = storageConfig.saveToString();
            Files.write(storageFile.toPath(), yamlContent.getBytes(StandardCharsets.UTF_8));
            
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save storage.yml: " + e.getMessage());
        }
    }
    
    public void addCommandBlock(VirtualCommandBlock block) {
        commandBlocks.put(block.getName(), block);
        saveStorage();
    }
    
    public void removeCommandBlock(String name) {
        commandBlocks.remove(name);
        saveStorage();
    }
    
    public VirtualCommandBlock getCommandBlock(String name) {
        return commandBlocks.get(name);
    }
    
    public boolean hasCommandBlock(String name) {
        return commandBlocks.containsKey(name);
    }
    
    public Set<String> getCommandBlockNames() {
        return commandBlocks.keySet();
    }
    
    public Map<String, VirtualCommandBlock> getAllCommandBlocks() {
        return new HashMap<>(commandBlocks);
    }
    
    public void updateCommandBlock(VirtualCommandBlock block) {
        commandBlocks.put(block.getName(), block);
        saveStorage();
    }
}
