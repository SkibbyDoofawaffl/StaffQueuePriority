package com.skibby.staffqueue;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaffQueuePriority extends JavaPlugin implements Listener {

    private Map<Player, Integer> priorityMap = new HashMap<>();
    private List<String> priorityGroups;
    private int queuePriority;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfigValues();
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("StaffQueuePriority enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("StaffQueuePriority disabled!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Temporary group check until LuckPerms is added
        String group = player.hasPermission("staff.priority") ? "staff" : "default";

        if (priorityGroups.contains(group)) {
            priorityMap.put(player, queuePriority);
            player.sendMessage(ChatColor.GREEN + "You have staff queue priority!");
            getLogger().info(player.getName() + " assigned queue priority " + queuePriority);
        } else {
            player.sendMessage(ChatColor.YELLOW + "You have standard queue priority.");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("queuepriority")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("queuepriority.reload")) {
                    sender.sendMessage(ChatColor.RED + "You don’t have permission to do that.");
                    return true;
                }

                reloadConfig();
                reloadConfigValues();
                sender.sendMessage(ChatColor.GREEN + "Config reloaded!");
                return true;
            }
            sender.sendMessage(ChatColor.YELLOW + "Usage: /queuepriority reload");
            return true;
        }
        return false;
    }

    private void reloadConfigValues() {
        FileConfiguration config = getConfig();
        priorityGroups = config.getStringList("priority-groups");
        queuePriority = config.getInt("queue-value", 100);
    }
}
