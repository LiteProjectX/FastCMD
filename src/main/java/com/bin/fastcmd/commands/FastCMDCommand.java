package com.bin.fastcmd.commands;

import com.bin.fastcmd.FastCMD;
import com.bin.fastcmd.managers.LocalizationManager;
import com.bin.fastcmd.managers.StorageManager;
import com.bin.fastcmd.models.CommandBlockType;
import com.bin.fastcmd.models.VirtualCommandBlock;
import com.bin.fastcmd.utils.ArgumentParser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Main command executor for FastCMD plugin
 */
public class FastCMDCommand implements CommandExecutor {
    
    private final FastCMD plugin;
    private final LocalizationManager lang;
    private final StorageManager storage;
    
    public FastCMDCommand(FastCMD plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLocalizationManager();
        this.storage = plugin.getStorageManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check permission
        if (!sender.hasPermission("fastcmd.use")) {
            sender.sendMessage(lang.getPrefix() + lang.getMessage("errors.no-permission"));
            return true;
        }
        
        // Show help if no arguments
        if (args.length == 0) {
            showHelp(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "create":
                if (sender.hasPermission("fastcmd.create")) {
                    handleCreate(sender, args);
                } else {
                    sender.sendMessage(lang.getPrefix() + lang.getMessage("errors.no-permission"));
                }
                break;
            case "run":
                if (sender.hasPermission("fastcmd.run")) {
                    handleRun(sender, args);
                } else {
                    sender.sendMessage(lang.getPrefix() + lang.getMessage("errors.no-permission"));
                }
                break;
            case "delete":
                if (sender.hasPermission("fastcmd.delete")) {
                    handleDelete(sender, args);
                } else {
                    sender.sendMessage(lang.getPrefix() + lang.getMessage("errors.no-permission"));
                }
                break;
            case "change":
                if (sender.hasPermission("fastcmd.change")) {
                    handleChange(sender, args);
                } else {
                    sender.sendMessage(lang.getPrefix() + lang.getMessage("errors.no-permission"));
                }
                break;
            case "stop":
                if (sender.hasPermission("fastcmd.stop")) {
                    handleStop(sender, args);
                } else {
                    sender.sendMessage(lang.getPrefix() + lang.getMessage("errors.no-permission"));
                }
                break;
            case "help":
                if (sender.hasPermission("fastcmd.help")) {
                    showHelp(sender);
                } else {
                    sender.sendMessage(lang.getPrefix() + lang.getMessage("errors.no-permission"));
                }
                break;
            case "lang":
                if (sender.hasPermission("fastcmd.lang")) {
                    handleLanguage(sender, args);
                } else {
                    sender.sendMessage(lang.getPrefix() + lang.getMessage("errors.no-permission"));
                }
                break;
            default:
                sender.sendMessage(lang.getPrefix() + lang.getMessage("errors.invalid-syntax"));
                break;
        }
        
        return true;
    }
    
    private void handleCreate(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(lang.getPrefix() + lang.getMessage("errors.invalid-syntax"));
            return;
        }
        
        ArgumentParser parser = new ArgumentParser(args, 1);
        
        String commandStr = parser.getCommand();
        if (commandStr == null || commandStr.trim().isEmpty()) {
            sender.sendMessage(lang.getPrefix() + lang.getMessage("commands.create.no-command"));
            return;
        }
        
        // Get or generate name
        String name = parser.getFlag("name");
        if (name == null) {
            name = generateName();
        }
        
        // Check if name already exists
        if (storage.hasCommandBlock(name)) {
            sender.sendMessage(lang.getPrefix() + lang.getMessage("commands.create.exists", "{name}", name));
            return;
        }
        
        // Parse command block type
        String typeStr = parser.getFlag("type", "Normal");
        CommandBlockType type = CommandBlockType.fromString(typeStr);
        if (type == null) {
            sender.sendMessage(lang.getPrefix() + lang.getMessage("commands.create.invalid-type"));
            return;
        }
        
        // Parse optional parameters
        long delay = parser.getLongFlag("delay", plugin.getConfigManager().getDefaultDelay());
        long disableTime = parser.getLongFlag("disable", plugin.getConfigManager().getDefaultDisableTime());
        
        // Validate delay
        if (delay < 0 || delay > plugin.getConfigManager().getMaxDelay()) {
            sender.sendMessage(lang.getPrefix() + lang.getMessage("commands.create.invalid-delay", 
                "{max}", String.valueOf(plugin.getConfigManager().getMaxDelay())));
            return;
        }
        
        // Validate disable time
        if (disableTime < 0 || disableTime > plugin.getConfigManager().getMaxDisableTime()) {
            sender.sendMessage(lang.getPrefix() + lang.getMessage("commands.create.invalid-disable-time",
                "{max}", String.valueOf(plugin.getConfigManager().getMaxDisableTime())));
            return;
        }
        
        // Check connected block
        String connectedBlock = parser.getFlag("connected");
        if (connectedBlock != null && !storage.hasCommandBlock(connectedBlock)) {
            sender.sendMessage(lang.getPrefix() + lang.getMessage("commands.create.connected-not-found",
                "{name}", connectedBlock));
            return;
        }
        
        // Create and save command block
        VirtualCommandBlock commandBlock = new VirtualCommandBlock(name, commandStr, type, delay, disableTime, connectedBlock);
        storage.addCommandBlock(commandBlock);
        
        sender.sendMessage(lang.getPrefix() + lang.getMessage("commands.create.success", "{name}", name));
    }
    
    private void handleRun(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(lang.getPrefix() + lang.getMessage("errors.invalid-syntax"));
            return;
        }
        
        ArgumentParser parser = new ArgumentParser(args, 1);
        String name = parser.getFlag("name");
        
        // Support old syntax for backward compatibility
        if (name == null && args.length >= 2) {
            name = args[1];
        }
        
        if (name == null) {
            sender.sendMessage(lang.getPrefix() + lang.getMessage("commands.run.no-name"));
            return;
        }
        
        VirtualCommandBlock commandBlock = storage.getCommandBlock(name);
        
        if (commandBlock == null) {
            sender.sendMessage(lang.getPrefix() + lang.getMessage("commands.run.not-found", "{name}", name));
            return;
        }
        
        if (commandBlock.isRunning()) {
            sender.sendMessage(lang.getPrefix() + lang.getMessage("commands.run.already-running", "{name}", name));
            return;
        }
        
        plugin.getTaskManager().executeCommandBlock(commandBlock);
        sender.sendMessage(lang.getPrefix() + lang.getMessage("commands.run.success", "{name}", name));
    }
    
    private void handleDelete(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(lang.getPrefix() + lang.getMessage("errors.invalid-syntax"));
            return;
        }
        
        ArgumentParser parser = new ArgumentParser(args, 1);
        String name = parser.getFlag("name");
        
        // Support old syntax for backward compatibility
        if (name == null && args.length >= 2) {
            name = args[1];
        }
        
        if (name == null) {
            sender.sendMessage(lang.getPrefix() + lang.getMessage("commands.delete.no-name"));
            return;
        }
        
        if (!storage.hasCommandBlock(name)) {
            sender.sendMessage(lang.getPrefix() + lang.getMessage("commands.delete.not-found", "{name}", name));
            return;
        }
        
        // Stop task if running
        plugin.getTaskManager().stopTask(name);
        
        storage.removeCommandBlock(name);
        sender.sendMessage(lang.getPrefix() + lang.getMessage("commands.delete.success", "{name}", name));
    }
    
    private void handleChange(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(lang.getPrefix() + lang.getMessage("errors.invalid-syntax"));
            return;
        }
        
        ArgumentParser parser = new ArgumentParser(args, 1);
        String name = parser.getFlag("name");
        
        // Support old syntax for backward compatibility  
        if (name == null && args.length >= 2) {
            name = args[1];
        }
        
        if (name == null) {
            sender.sendMessage(lang.getPrefix() + lang.getMessage("commands.change.no-name"));
            return;
        }
        
        VirtualCommandBlock commandBlock = storage.getCommandBlock(name);
        
        if (commandBlock == null) {
            sender.sendMessage(lang.getPrefix() + lang.getMessage("commands.change.not-found", "{name}", name));
            return;
        }
        
        // Update command if provided
        String newCommand = parser.getCommand();
        if (newCommand != null && !newCommand.trim().isEmpty()) {
            commandBlock.setCommand(newCommand);
        }
        
        // Update type if provided
        String typeStr = parser.getFlag("type");
        if (typeStr != null) {
            CommandBlockType type = CommandBlockType.fromString(typeStr);
            if (type == null) {
                sender.sendMessage(lang.getPrefix() + lang.getMessage("commands.change.invalid-type"));
                return;
            }
            commandBlock.setType(type);
        }
        
        // Update delay if provided
        if (parser.hasFlag("delay")) {
            Long delay = parser.getLongFlag("delay", null);
            if (delay == null) {
                sender.sendMessage(lang.getPrefix() + lang.getMessage("commands.change.invalid-delay",
                    "{max}", String.valueOf(plugin.getConfigManager().getMaxDelay())));
                return;
            }
            if (delay < 0 || delay > plugin.getConfigManager().getMaxDelay()) {
                sender.sendMessage(lang.getPrefix() + lang.getMessage("commands.change.invalid-delay",
                    "{max}", String.valueOf(plugin.getConfigManager().getMaxDelay())));
                return;
            }
            commandBlock.setDelay(delay);
        }
        
        // Update disable time if provided
        if (parser.hasFlag("disable")) {
            Long disableTime = parser.getLongFlag("disable", null);
            if (disableTime == null) {
                sender.sendMessage(lang.getPrefix() + lang.getMessage("commands.change.invalid-disable-time",
                    "{max}", String.valueOf(plugin.getConfigManager().getMaxDisableTime())));
                return;
            }
            if (disableTime < 0 || disableTime > plugin.getConfigManager().getMaxDisableTime()) {
                sender.sendMessage(lang.getPrefix() + lang.getMessage("commands.change.invalid-disable-time",
                    "{max}", String.valueOf(plugin.getConfigManager().getMaxDisableTime())));
                return;
            }
            commandBlock.setDisableTime(disableTime);
        }
        
        // Update connected block if provided
        String connectedBlock = parser.getFlag("connected");
        if (connectedBlock != null) {
            if (!storage.hasCommandBlock(connectedBlock)) {
                sender.sendMessage(lang.getPrefix() + lang.getMessage("commands.change.connected-not-found",
                    "{name}", connectedBlock));
                return;
            }
            commandBlock.setConnectedBlock(connectedBlock);
        }
        
        storage.updateCommandBlock(commandBlock);
        sender.sendMessage(lang.getPrefix() + lang.getMessage("commands.change.success", "{name}", name));
    }
    
    private void handleStop(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(lang.getPrefix() + lang.getMessage("errors.invalid-syntax"));
            return;
        }
        
        ArgumentParser parser = new ArgumentParser(args, 1);
        String name = parser.getFlag("name");
        
        // Support old syntax for backward compatibility
        if (name == null && args.length >= 2) {
            name = args[1];
        }
        
        if (name == null) {
            sender.sendMessage(lang.getPrefix() + lang.getMessage("commands.stop.no-name"));
            return;
        }
        
        VirtualCommandBlock commandBlock = storage.getCommandBlock(name);
        
        if (commandBlock == null) {
            sender.sendMessage(lang.getPrefix() + lang.getMessage("commands.stop.not-found", "{name}", name));
            return;
        }
        
        if (!commandBlock.isRunning()) {
            sender.sendMessage(lang.getPrefix() + lang.getMessage("commands.stop.not-running", "{name}", name));
            return;
        }
        
        plugin.getTaskManager().stopTask(name);
        sender.sendMessage(lang.getPrefix() + lang.getMessage("commands.stop.success", "{name}", name));
    }
    
    private void handleLanguage(CommandSender sender, String[] args) {
        if (args.length < 2) {
            // Toggle between en and ru
            String currentLang = lang.getCurrentLanguage();
            String newLang = "en".equals(currentLang) ? "ru" : "en";
            lang.setLanguage(newLang);
            sender.sendMessage(lang.getPrefix() + lang.getMessage("commands.lang.changed"));
        } else {
            String newLang = args[1].toLowerCase();
            if ("en".equals(newLang) || "ru".equals(newLang)) {
                lang.setLanguage(newLang);
                sender.sendMessage(lang.getPrefix() + lang.getMessage("commands.lang.changed"));
            } else {
                sender.sendMessage(lang.getPrefix() + lang.getMessage("errors.invalid-syntax"));
            }
        }
    }
    
    private void showHelp(CommandSender sender) {
        sender.sendMessage(lang.getMessage("commands.help.title"));
        sender.sendMessage(lang.getMessage("commands.help.create"));
        sender.sendMessage(lang.getMessage("commands.help.run"));
        sender.sendMessage(lang.getMessage("commands.help.delete"));
        sender.sendMessage(lang.getMessage("commands.help.change"));
        sender.sendMessage(lang.getMessage("commands.help.stop"));
        sender.sendMessage(lang.getMessage("commands.help.lang"));
        sender.sendMessage(lang.getMessage("commands.help.help"));
        sender.sendMessage(lang.getMessage("commands.help.types"));
    }
    
    private String generateName() {
        int counter = 1;
        String baseName = "cmdblock";
        String name = baseName + counter;
        
        while (storage.hasCommandBlock(name)) {
            counter++;
            name = baseName + counter;
        }
        
        return name;
    }
}
