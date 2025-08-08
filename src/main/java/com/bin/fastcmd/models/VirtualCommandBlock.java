package com.bin.fastcmd.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a virtual command block with all its properties
 */
public class VirtualCommandBlock {
    
    private String name;
    private String command;
    private CommandBlockType type;
    private long delay;
    private long disableTime;
    private String connectedBlock;
    private boolean isRunning;
    private long startTime;
    
    public VirtualCommandBlock(String name, String command, CommandBlockType type, long delay, long disableTime, String connectedBlock) {
        this.name = name;
        this.command = command;
        this.type = type;
        this.delay = delay;
        this.disableTime = disableTime;
        this.connectedBlock = connectedBlock;
        this.isRunning = false;
        this.startTime = 0;
    }
    
    // Getters and setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getCommand() {
        return command;
    }
    
    public void setCommand(String command) {
        this.command = command;
    }
    
    public CommandBlockType getType() {
        return type;
    }
    
    public void setType(CommandBlockType type) {
        this.type = type;
    }
    
    public long getDelay() {
        return delay;
    }
    
    public void setDelay(long delay) {
        this.delay = delay;
    }
    
    public long getDisableTime() {
        return disableTime;
    }
    
    public void setDisableTime(long disableTime) {
        this.disableTime = disableTime;
    }
    
    public String getConnectedBlock() {
        return connectedBlock;
    }
    
    public void setConnectedBlock(String connectedBlock) {
        this.connectedBlock = connectedBlock;
    }
    
    public boolean isRunning() {
        return isRunning;
    }
    
    public void setRunning(boolean running) {
        this.isRunning = running;
        if (running) {
            this.startTime = System.currentTimeMillis();
        } else {
            this.startTime = 0;
        }
    }
    
    public long getStartTime() {
        return startTime;
    }
    
    /**
     * Convert the command block to a map for YAML serialization
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("command", command);
        map.put("type", type.name());
        map.put("delay", delay);
        map.put("disable-time", disableTime);
        if (connectedBlock != null && !connectedBlock.isEmpty()) {
            map.put("connected", connectedBlock);
        }
        return map;
    }
    
    /**
     * Create a command block from a map (YAML deserialization)
     */
    public static VirtualCommandBlock fromMap(String name, Map<String, Object> map) {
        String command = (String) map.get("command");
        CommandBlockType type = CommandBlockType.valueOf((String) map.get("type"));
        long delay = getLongFromMap(map, "delay", 0);
        long disableTime = getLongFromMap(map, "disable-time", 0);
        String connectedBlock = (String) map.get("connected");
        
        return new VirtualCommandBlock(name, command, type, delay, disableTime, connectedBlock);
    }
    
    private static long getLongFromMap(Map<String, Object> map, String key, long defaultValue) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return defaultValue;
    }
    
    /**
     * Check if the command block should be disabled based on disable time
     */
    public boolean shouldBeDisabled() {
        if (disableTime <= 0 || !isRunning) {
            return false;
        }
        return System.currentTimeMillis() - startTime >= disableTime;
    }
}
