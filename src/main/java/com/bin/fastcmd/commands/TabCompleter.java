package com.bin.fastcmd.commands;

import com.bin.fastcmd.FastCMD;
import com.bin.fastcmd.models.CommandBlockType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tab completion handler for FastCMD commands
 */
public class TabCompleter implements org.bukkit.command.TabCompleter {
    
    private final FastCMD plugin;
    private final List<String> subCommands = Arrays.asList("create", "run", "delete", "change", "stop", "help", "lang");
    private final List<String> languages = Arrays.asList("en", "ru");
    private final List<String> flags = Arrays.asList("--name:", "--type:", "--delay:", "--disable:", "--connected:");
    
    public TabCompleter(FastCMD plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!plugin.getConfigManager().isTabCompletionEnabled()) {
            return new ArrayList<>();
        }
        
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // Complete subcommands
            completions.addAll(subCommands.stream()
                .filter(sub -> sub.toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList()));
        } else if (args.length > 1) {
            String subCommand = args[0].toLowerCase();
            
            switch (subCommand) {
                case "create":
                    completions.addAll(handleCreateTabComplete(args));
                    break;
                case "run":
                case "delete":
                case "stop":
                    completions.addAll(handleNameFlagTabComplete(args));
                    break;
                case "change":
                    completions.addAll(handleChangeTabComplete(args));
                    break;
                case "lang":
                    if (args.length == 2) {
                        completions.addAll(languages.stream()
                            .filter(lang -> lang.toLowerCase().startsWith(args[1].toLowerCase()))
                            .collect(Collectors.toList()));
                    }
                    break;
            }
        }
        
        return completions;
    }
    
    private List<String> handleCreateTabComplete(String[] args) {
        List<String> completions = new ArrayList<>();
        String currentLang = plugin.getLocalizationManager().getCurrentLanguage();
        String lastArg = args[args.length - 1];
        
        // If currently typing a flag value
        if (lastArg.startsWith("--")) {
            if (lastArg.startsWith("--type:")) {
                String partial = lastArg.substring(7); // Remove --type:
                for (String type : CommandBlockType.getLocalizedNames(currentLang)) {
                    if (type.toLowerCase().startsWith(partial.toLowerCase())) {
                        completions.add("--type:" + type);
                    }
                }
            } else if (lastArg.startsWith("--delay:")) {
                String partial = lastArg.substring(8); // Remove --delay:
                for (String delay : plugin.getConfigManager().getCommonDelays()) {
                    if (delay.startsWith(partial)) {
                        completions.add("--delay:" + delay);
                    }
                }
            } else if (lastArg.startsWith("--disable:")) {
                String partial = lastArg.substring(10); // Remove --disable:
                for (String disable : plugin.getConfigManager().getCommonDisableTimes()) {
                    if (disable.startsWith(partial)) {
                        completions.add("--disable:" + disable);
                    }
                }
            } else if (lastArg.startsWith("--connected:")) {
                String partial = lastArg.substring(12); // Remove --connected:
                for (String name : getCommandBlockNames("")) {
                    if (name.toLowerCase().startsWith(partial.toLowerCase())) {
                        completions.add("--connected:" + name);
                    }
                }
            } else if (lastArg.startsWith("--name:")) {
                // Don't auto-complete names, let user type their own
                completions.add(lastArg + "myblock");
            } else {
                // Show available flags
                for (String flag : flags) {
                    if (flag.toLowerCase().startsWith(lastArg.toLowerCase())) {
                        completions.add(flag);
                    }
                }
            }
        } else {
            // Show flags as options
            completions.addAll(flags);
            // Also support backtick completion for command
            if (lastArg.isEmpty()) {
                completions.add("`command here`");
            }
        }
        
        return completions;
    }
    
    private List<String> handleChangeTabComplete(String[] args) {
        List<String> completions = new ArrayList<>();
        String currentLang = plugin.getLocalizationManager().getCurrentLanguage();
        String lastArg = args[args.length - 1];
        
        // Similar to create but with different logic
        if (lastArg.startsWith("--")) {
            if (lastArg.startsWith("--name:")) {
                String partial = lastArg.substring(7); // Remove --name:
                for (String name : getCommandBlockNames("")) {
                    if (name.toLowerCase().startsWith(partial.toLowerCase())) {
                        completions.add("--name:" + name);
                    }
                }
            } else if (lastArg.startsWith("--type:")) {
                String partial = lastArg.substring(7);
                for (String type : CommandBlockType.getLocalizedNames(currentLang)) {
                    if (type.toLowerCase().startsWith(partial.toLowerCase())) {
                        completions.add("--type:" + type);
                    }
                }
            } else if (lastArg.startsWith("--delay:")) {
                String partial = lastArg.substring(8);
                for (String delay : plugin.getConfigManager().getCommonDelays()) {
                    if (delay.startsWith(partial)) {
                        completions.add("--delay:" + delay);
                    }
                }
            } else if (lastArg.startsWith("--disable:")) {
                String partial = lastArg.substring(10);
                for (String disable : plugin.getConfigManager().getCommonDisableTimes()) {
                    if (disable.startsWith(partial)) {
                        completions.add("--disable:" + disable);
                    }
                }
            } else if (lastArg.startsWith("--connected:")) {
                String partial = lastArg.substring(12);
                for (String name : getCommandBlockNames("")) {
                    if (name.toLowerCase().startsWith(partial.toLowerCase())) {
                        completions.add("--connected:" + name);
                    }
                }
            } else {
                // Show available flags
                for (String flag : flags) {
                    if (flag.toLowerCase().startsWith(lastArg.toLowerCase())) {
                        completions.add(flag);
                    }
                }
            }
        } else {
            // Show flags and backtick option
            completions.addAll(flags);
            if (lastArg.isEmpty()) {
                completions.add("`new command`");
            }
        }
        
        return completions;
    }
    
    private List<String> handleNameFlagTabComplete(String[] args) {
        List<String> completions = new ArrayList<>();
        String lastArg = args[args.length - 1];
        
        if (lastArg.startsWith("--name:")) {
            String partial = lastArg.substring(7); // Remove --name:
            for (String name : getCommandBlockNames("")) {
                if (name.toLowerCase().startsWith(partial.toLowerCase())) {
                    completions.add("--name:" + name);
                }
            }
        } else if (lastArg.startsWith("--")) {
            if ("--name:".startsWith(lastArg)) {
                completions.add("--name:");
            }
        } else {
            // Show both old syntax (direct name) and new syntax (--name:)
            completions.add("--name:");
            completions.addAll(getCommandBlockNames(lastArg));
        }
        
        return completions;
    }
    
    private List<String> getCommandBlockNames(String partial) {
        return plugin.getStorageManager().getCommandBlockNames().stream()
            .filter(name -> name.toLowerCase().startsWith(partial.toLowerCase()))
            .collect(Collectors.toList());
    }
}
