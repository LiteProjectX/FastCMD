package com.bin.fastcmd.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for parsing command arguments with flags
 */
public class ArgumentParser {
    
    private final Map<String, String> flags;
    private String command;
    
    public ArgumentParser(String[] args, int startIndex) {
        this.flags = new HashMap<>();
        parseArguments(args, startIndex);
    }
    
    private void parseArguments(String[] args, int startIndex) {
        StringBuilder commandBuilder = new StringBuilder();
        boolean inCommand = false;
        
        for (int i = startIndex; i < args.length; i++) {
            String arg = args[i];
            
            // Check if it's a command in backticks
            if (arg.startsWith("`") && arg.endsWith("`") && arg.length() > 2) {
                // Single arg command
                this.command = arg.substring(1, arg.length() - 1);
                continue;
            } else if (arg.startsWith("`")) {
                // Start of multi-word command
                inCommand = true;
                commandBuilder.append(arg.substring(1)).append(" ");
                continue;
            } else if (arg.endsWith("`") && inCommand) {
                // End of multi-word command
                commandBuilder.append(arg.substring(0, arg.length() - 1));
                this.command = commandBuilder.toString();
                inCommand = false;
                continue;
            } else if (inCommand) {
                // Middle of multi-word command
                commandBuilder.append(arg).append(" ");
                continue;
            }
            
            // Check if it's a flag
            if (arg.startsWith("--") && arg.contains(":")) {
                String[] flagParts = arg.substring(2).split(":", 2);
                if (flagParts.length == 2) {
                    flags.put(flagParts[0], flagParts[1]);
                }
            }
        }
    }
    
    public String getCommand() {
        return command;
    }
    
    public String getFlag(String flagName) {
        return flags.get(flagName);
    }
    
    public String getFlag(String flagName, String defaultValue) {
        return flags.getOrDefault(flagName, defaultValue);
    }
    
    public boolean hasFlag(String flagName) {
        return flags.containsKey(flagName);
    }
    
    public Long getLongFlag(String flagName, Long defaultValue) {
        String value = getFlag(flagName);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}