package com.coldspare.zana.tokens;

import com.coldspare.zana.Zana;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class RemoveTokensCommand implements CommandExecutor, TabCompleter {
    private final Zana plugin;

    public RemoveTokensCommand(Zana plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("zana.token")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /removetokens <player> <amount>");
            return false;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "Player not found");
            return false;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
            if (amount < 0) {
                sender.sendMessage(ChatColor.RED + "Invalid amount");
                return false;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid amount");
            return false;
        }

        plugin.getTokenManager().removeTokens(targetPlayer, amount);
        sender.sendMessage(ChatColor.GREEN + "Removed " + amount + " tokens from " + targetPlayer.getName());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return null; // Returns a list of online players
        }
        return ImmutableList.of(); // Returns an empty list
    }
}
