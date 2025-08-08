package com.bin.fastcmd;

import com.bin.fastcmd.commands.FastCMDCommand;
import com.bin.fastcmd.commands.TabCompleter;
import com.bin.fastcmd.managers.ConfigManager;
import com.bin.fastcmd.managers.LocalizationManager;
import com.bin.fastcmd.managers.StorageManager;
import com.bin.fastcmd.utils.TaskManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.charset.StandardCharsets;

/**
 * Main plugin class for FastCMD
 * Manages virtual command blocks with localization support
 */
public class FastCMD extends JavaPlugin {
    
    private static FastCMD instance;
    private ConfigManager configManager;
    private StorageManager storageManager;
    private LocalizationManager localizationManager;
    private TaskManager taskManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Set UTF-8 encoding for proper display on Windows CMD
        System.setProperty("file.encoding", "UTF-8");
        
        // Initialize managers
        configManager = new ConfigManager(this);
        storageManager = new StorageManager(this);
        localizationManager = new LocalizationManager(this);
        taskManager = new TaskManager(this);
        
        // Load configurations
        configManager.loadConfig();
        storageManager.loadStorage();
        localizationManager.loadLanguage();
        
        // Register commands
        FastCMDCommand commandExecutor = new FastCMDCommand(this);
        getCommand("fastcmd").setExecutor(commandExecutor);
        getCommand("fastcmd").setTabCompleter(new TabCompleter(this));
        
        getLogger().info("FastCMD plugin has been enabled!");
        getLogger().info("Language: " + localizationManager.getCurrentLanguage());
    }
    
    @Override
    public void onDisable() {
        // Stop all running tasks
        if (taskManager != null) {
            taskManager.stopAllTasks();
        }
        
        // Save storage
        if (storageManager != null) {
            storageManager.saveStorage();
        }
        
        getLogger().info("FastCMD plugin has been disabled!");
    }
    
    public static FastCMD getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public StorageManager getStorageManager() {
        return storageManager;
    }
    
    public LocalizationManager getLocalizationManager() {
        return localizationManager;
    }
    
    public TaskManager getTaskManager() {
        return taskManager;
    }
}
