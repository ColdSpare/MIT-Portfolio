package com.coldspare.zana.sell;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SellWandGiveCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("sellwandgive")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command");
            return true;
        }

        if (args.length == 3) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                double multiplier;
                int amount;
                try {
                    multiplier = Double.parseDouble(args[1]);
                    amount = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid number format");
                    return true;
                }
                ItemStack sellWand = new ItemStack(Material.BLAZE_ROD, amount);
                ItemMeta meta = sellWand.getItemMeta();
                meta.setDisplayName(ChatColor.GOLD + "Sell Wand");
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.DARK_GRAY + "Sell Item");
                lore.add("");
                lore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "▎ " + ChatColor.WHITE + "Sell chests with a multiplier!");
                lore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "▎ " + ChatColor.WHITE + "Multiplier: " + ChatColor.GREEN + ChatColor.UNDERLINE + multiplier + "x");
                lore.add("");
                meta.setLore(lore);
                sellWand.setItemMeta(meta);
                target.getInventory().addItem(sellWand);
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "Player not found");
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /sellwandgive <player> <multiplier> <amount>");
            return true;
        }
    }
}