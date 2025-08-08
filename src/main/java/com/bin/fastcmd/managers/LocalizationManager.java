package com.bin.fastcmd.managers;

import com.bin.fastcmd.FastCMD;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Manages localization with UTF-8 support for proper display on Windows CMD
 */
public class LocalizationManager {
    
    private final FastCMD plugin;
    private FileConfiguration langConfig;
    private String currentLanguage;
    
    public LocalizationManager(FastCMD plugin) {
        this.plugin = plugin;
    }
    
    public void loadLanguage() {
        currentLanguage = plugin.getConfigManager().getLanguage();
        
        // Create lang folder if it doesn't exist
        File langFolder = new File(plugin.getDataFolder(), "lang");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }
        
        // Save default language files if they don't exist
        File enFile = new File(langFolder, "en.yml");
        File ruFile = new File(langFolder, "ru.yml");
        
        if (!enFile.exists()) {
            plugin.saveResource("lang/en.yml", false);
        }
        if (!ruFile.exists()) {
            plugin.saveResource("lang/ru.yml", false);
        }
        
        // Load the current language file
        File langFile = new File(langFolder, currentLanguage + ".yml");
        if (!langFile.exists()) {
            plugin.getLogger().warning("Language file " + currentLanguage + ".yml not found, falling back to English");
            langFile = enFile;
            currentLanguage = "en";
        }
        
        try {
            // Load with UTF-8 encoding
            String content = new String(Files.readAllBytes(langFile.toPath()), StandardCharsets.UTF_8);
            langConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(
                new java.io.ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)), 
                StandardCharsets.UTF_8
            ));
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to load language file: " + e.getMessage());
            // Fallback to default configuration
            InputStream resource = plugin.getResource("lang/en.yml");
            if (resource != null) {
                langConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(resource, StandardCharsets.UTF_8));
            } else {
                langConfig = new YamlConfiguration();
            }
        }
    }
    
    public String getMessage(String path) {
        String message = langConfig.getString(path, "Missing message: " + path);
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    public String getMessage(String path, String... replacements) {
        String message = getMessage(path);
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace(replacements[i], replacements[i + 1]);
            }
        }
        return message;
    }
    
    public String getPrefix() {
        return getMessage("prefix");
    }
    
    public String getCurrentLanguage() {
        return currentLanguage;
    }
    
    public void setLanguage(String language) {
        plugin.getConfigManager().setLanguage(language);
        loadLanguage();
    }
    
    public String[] getAvailableLanguages() {
        File langFolder = new File(plugin.getDataFolder(), "lang");
        if (!langFolder.exists()) {
            return new String[]{"en", "ru"};
        }
        
        File[] files = langFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) {
            return new String[]{"en", "ru"};
        }
        
        String[] languages = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            languages[i] = files[i].getName().replace(".yml", "");
        }
        return languages;
    }
}
