package com.bin.fastcmd.utils;

import com.bin.fastcmd.FastCMD;
import com.bin.fastcmd.models.CommandBlockType;
import com.bin.fastcmd.models.VirtualCommandBlock;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages task execution for virtual command blocks
 */
public class TaskManager {
    
    private final FastCMD plugin;
    private final Map<String, BukkitTask> runningTasks;
    
    public TaskManager(FastCMD plugin) {
        this.plugin = plugin;
        this.runningTasks = new HashMap<>();
    }
    
    public void executeCommandBlock(VirtualCommandBlock commandBlock) {
        String name = commandBlock.getName();
        
        // Stop existing task if running
        stopTask(name);
        
        // Mark as running
        commandBlock.setRunning(true);
        
        switch (commandBlock.getType()) {
            case NORMAL:
                executeNormalCommand(commandBlock);
                break;
            case CHAIN:
                executeChainCommand(commandBlock);
                break;
            case REPEATING:
                executeRepeatingCommand(commandBlock);
                break;
        }
    }
    
    private void executeNormalCommand(VirtualCommandBlock commandBlock) {
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                // Execute the command
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandBlock.getCommand());
                
                // Mark as not running
                commandBlock.setRunning(false);
                runningTasks.remove(commandBlock.getName());
                
                // Execute connected command block if specified
                executeConnectedBlock(commandBlock);
            }
        }.runTaskLater(plugin, commandBlock.getDelay());
        
        runningTasks.put(commandBlock.getName(), task);
    }
    
    private void executeChainCommand(VirtualCommandBlock commandBlock) {
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                // Execute the command
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandBlock.getCommand());
                
                // Mark as not running
                commandBlock.setRunning(false);
                runningTasks.remove(commandBlock.getName());
                
                // Execute connected command block immediately for chain type
                executeConnectedBlock(commandBlock);
            }
        }.runTaskLater(plugin, commandBlock.getDelay());
        
        runningTasks.put(commandBlock.getName(), task);
    }
    
    private void executeRepeatingCommand(VirtualCommandBlock commandBlock) {
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                // Check if should be disabled
                if (commandBlock.shouldBeDisabled()) {
                    commandBlock.setRunning(false);
                    runningTasks.remove(commandBlock.getName());
                    cancel();
                    return;
                }
                
                // Execute the command
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandBlock.getCommand());
                
                // Execute connected command block if specified
                executeConnectedBlock(commandBlock);
            }
        }.runTaskTimer(plugin, commandBlock.getDelay(), Math.max(commandBlock.getDelay(), 1));
        
        runningTasks.put(commandBlock.getName(), task);
    }
    
    private void executeConnectedBlock(VirtualCommandBlock commandBlock) {
        String connectedName = commandBlock.getConnectedBlock();
        if (connectedName != null && !connectedName.isEmpty()) {
            VirtualCommandBlock connectedBlock = plugin.getStorageManager().getCommandBlock(connectedName);
            if (connectedBlock != null && !connectedBlock.isRunning()) {
                executeCommandBlock(connectedBlock);
            }
        }
    }
    
    public void stopTask(String name) {
        BukkitTask task = runningTasks.remove(name);
        if (task != null) {
            task.cancel();
        }
        
        // Update command block status
        VirtualCommandBlock commandBlock = plugin.getStorageManager().getCommandBlock(name);
        if (commandBlock != null) {
            commandBlock.setRunning(false);
        }
    }
    
    public boolean isTaskRunning(String name) {
        return runningTasks.containsKey(name);
    }
    
    public void stopAllTasks() {
        for (BukkitTask task : runningTasks.values()) {
            task.cancel();
        }
        runningTasks.clear();
        
        // Update all command block statuses
        for (VirtualCommandBlock commandBlock : plugin.getStorageManager().getAllCommandBlocks().values()) {
            commandBlock.setRunning(false);
        }
    }
}
