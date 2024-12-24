package com.coldspare.zana.level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class LevelCommands implements CommandExecutor {
    private final PlayerLevelManager playerLevelManager;
    private boolean confirmReset = false;  // Will be used in /resetlevels command

    public LevelCommands(PlayerLevelManager playerLevelManager) {
        this.playerLevelManager = playerLevelManager;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (command.getName().equalsIgnoreCase("leveltop")) {
                Map<UUID, Integer> sortedLevels = playerLevelManager.getAllPlayerLevels().entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .limit(10)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

                player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "GLOBAL LEVEL TOP");
                int rank = 1;
                for (Map.Entry<UUID, Integer> entry : sortedLevels.entrySet()) {
                    String playerName = playerLevelManager.getPlayerName(entry.getKey());
                    if (playerName != null) {
                        player.sendMessage(ChatColor.WHITE + "#" + rank + " " + playerName + " " + ChatColor.GRAY + "-" + ChatColor.DARK_AQUA + " ☯" + entry.getValue());
                        rank++;
                    }
                }
                return true;
            } else if (sender instanceof Player) {
                if (!player.hasPermission("zana.levels")) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                    return true;
                }
                if (command.getName().equalsIgnoreCase("resetlevels")) {
                    if (args.length == 1 && args[0].equalsIgnoreCase("confirm")) {
                        if (confirmReset) {
                            playerLevelManager.clearAllPlayerXP();
                            playerLevelManager.clearAllPlayerLevels();
                            playerLevelManager.savePlayerData(true);
                            player.sendMessage(ChatColor.GREEN + "All level data has been wiped.");
                            confirmReset = false;
                        } else {
                            confirmReset = true;
                            player.sendMessage(ChatColor.YELLOW + "Are you reallyyyy sure!!? If yes, do /resetlevels confirm again");
                        }
                    } else {
                        confirmReset = false;
                        player.sendMessage(ChatColor.YELLOW + "Are you sure you want to wipe all level data? Run /resetlevels confirm to confirm");
                    }
                    return true;
                }
                if (command.getName().equalsIgnoreCase("xpremove") || command.getName().equalsIgnoreCase("xpadd")
                        || command.getName().equalsIgnoreCase("leveladd") || command.getName().equalsIgnoreCase("levelremove")
                        || command.getName().equalsIgnoreCase("levelset")) {
                    if (args.length != 2) {
                        player.sendMessage(ChatColor.RED + "Incorrect usage. Correct usage: /" + command.getName() + " <player> <amount>");
                        return true;
                    }
                    Player targetPlayer = Bukkit.getPlayer(args[0]);
                    if (targetPlayer == null) {
                        player.sendMessage(ChatColor.RED + "Player not found.");
                        return true;
                    }
                    double amount;
                    try {
                        amount = Double.parseDouble(args[1]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "Invalid amount. Please enter a number.");
                        return true;
                    }

                    UUID targetId = targetPlayer.getUniqueId();
                    if (command.getName().equalsIgnoreCase("xpremove")) {
                        double currentXP = playerLevelManager.getPlayerXP(targetId);
                        playerLevelManager.setPlayerXP(targetId, Math.max(0, currentXP - amount));
                        player.sendMessage(ChatColor.GREEN + "Removed " + amount + " XP from " + targetPlayer.getName() + ".");
                    } else if (command.getName().equalsIgnoreCase("xpadd")) {
                        playerLevelManager.giveXP(targetPlayer, amount);
                        player.sendMessage(ChatColor.GREEN + "Added " + amount + " XP to " + targetPlayer.getName() + ".");
                    } else if (command.getName().equalsIgnoreCase("leveladd")) {
                        int currentLevel = playerLevelManager.getPlayerLevel(targetId);
                        playerLevelManager.setPlayerLevel(targetId, currentLevel + (int) amount);
                        player.sendMessage(ChatColor.GREEN + "Added " + (int) amount + " levels to " + targetPlayer.getName() + ".");
                    } else if (command.getName().equalsIgnoreCase("levelremove")) {
                        int currentLevel = playerLevelManager.getPlayerLevel(targetId);
                        playerLevelManager.setPlayerLevel(targetId, Math.max(0, currentLevel - (int) amount));
                        player.sendMessage(ChatColor.GREEN + "Removed " + (int) amount + " levels from " + targetPlayer.getName() + ".");
                    } else if (command.getName().equalsIgnoreCase("levelset")) {
                        playerLevelManager.setPlayerLevel(targetId, (int) amount);
                        player.sendMessage(ChatColor.GREEN + "Set " + targetPlayer.getName() + "'s level to " + (int) amount + ".");
                    }
                    playerLevelManager.savePlayerData(true);
                    return true;
                }
            }
            return false;
        }
    return false;
    }
}
