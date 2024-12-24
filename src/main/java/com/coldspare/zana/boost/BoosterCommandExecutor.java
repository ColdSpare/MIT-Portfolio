package com.coldspare.zana.boost;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BoosterCommandExecutor implements CommandExecutor, TabCompleter {
    private final BoosterManager boosterManager;

    public BoosterCommandExecutor(BoosterManager boosterManager) {
        this.boosterManager = boosterManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("zana.booster")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        if (command.getName().equalsIgnoreCase("boosteradd")) {
            if (args.length != 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /boosteradd <player> <boosterType> <multiplier>");
                return false;
            }

            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found!");
                return false;
            }

            try {
                BoosterManager.BoosterType type = BoosterManager.BoosterType.valueOf(args[1].toUpperCase());
                double multiplier = Double.parseDouble(args[2]);

                boosterManager.addBooster(target.getUniqueId(), type, multiplier);
                sender.sendMessage(ChatColor.GREEN + "Added " + type + " booster for " + target.getName() + " with a multiplier of " + multiplier + ".");
            } catch (IllegalArgumentException e) {
                sender.sendMessage(ChatColor.RED + "Invalid booster type or multiplier. Try XP or SELL for booster type and a number for multiplier.");
            }

            return true;
        } else if (command.getName().equalsIgnoreCase("boosterclear")) {
            if (args.length != 1) {
                sender.sendMessage(ChatColor.RED + "Usage: /boosterclear <player>");
                return false;
            }

            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found!");
                return false;
            }

            boosterManager.clearBoosters(target.getUniqueId());
            sender.sendMessage(ChatColor.GREEN + "Boosters for " + target.getName() + " have been cleared.");
            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("boosteradd") || command.getName().equalsIgnoreCase("boosterclear")) {
            if (args.length == 1) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            } else if (command.getName().equalsIgnoreCase("boosteradd") && args.length == 2) {
                return Arrays.stream(BoosterManager.BoosterType.values()).map(Enum::name).collect(Collectors.toList());
            }
        }

        return null;
    }
}
