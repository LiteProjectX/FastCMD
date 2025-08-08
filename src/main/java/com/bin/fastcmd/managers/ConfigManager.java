package com.bin.fastcmd.managers;

import com.bin.fastcmd.FastCMD;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Manages plugin configuration with UTF-8 support
 */
public class ConfigManager {
    
    private final FastCMD plugin;
    private FileConfiguration config;
    
    public ConfigManager(FastCMD plugin) {
        this.plugin = plugin;
    }
    
    public void loadConfig() {
        // Save default config if it doesn't exist
        if (!new File(plugin.getDataFolder(), "config.yml").exists()) {
            plugin.saveDefaultConfig();
        }
        
        plugin.reloadConfig();
        config = plugin.getConfig();
        
        // Set UTF-8 encoding
        System.setProperty("file.encoding", "UTF-8");
    }
    
    public String getLanguage() {
        return config.getString("language", "en");
    }
    
    public void setLanguage(String language) {
        config.set("language", language);
        plugin.saveConfig();
    }
    
    public boolean isTabCompletionEnabled() {
        return config.getBoolean("tab-completion.enabled", true);
    }
    
    public List<String> getCommandBlockTypes() {
        return config.getStringList("tab-completion.command-block-types");
    }
    
    public List<String> getCommonDelays() {
        return config.getStringList("tab-completion.common-delays");
    }
    
    public List<String> getCommonDisableTimes() {
        return config.getStringList("tab-completion.common-disable-times");
    }
    
    public long getMaxDelay() {
        return config.getLong("commands.max-delay", 72000);
    }
    
    public long getMaxDisableTime() {
        return config.getLong("commands.max-disable-time", 72000);
    }
    
    public long getDefaultDelay() {
        return config.getLong("commands.default-delay", 0);
    }
    
    public long getDefaultDisableTime() {
        return config.getLong("commands.default-disable-time", 0);
    }
    
    public boolean isDebugMode() {
        return config.getBoolean("debug", false);
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
}
