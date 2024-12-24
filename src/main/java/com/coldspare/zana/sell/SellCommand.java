package com.coldspare.zana.sell;

import com.coldspare.zana.Zana;
import com.coldspare.zana.boost.BoosterManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SellCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        Inventory inventory = player.getInventory();

        // Check if the player has any sellable items
        boolean hasSellableItems = false;
        for (ItemStack item : inventory.getContents()) {
            if (item != null && SellManager.isSellable(item.getType())) {
                hasSellableItems = true;
                break;
            }
        }

        // If the player has no sellable items, send a message and return
        if (!hasSellableItems) {
            player.sendMessage(ChatColor.RED + "You have no sellable items.");
            return true;
        }

        // Get instance of SellManager here
        SellManager sellManager = Zana.getInstance().getSellManager();

        // If the player has sellable items, sell them
        double totalValue = 0;
        double totalItems = 0;
        double multiplier = Zana.getInstance().getBoosterManager().getBooster(player.getUniqueId(), BoosterManager.BoosterType.SELL);
        for (ItemStack item : inventory.getContents()) {
            if (item != null && sellManager.isSellable(item.getType())) {
                // Check if the item isn't in the offhand slot and not in armor slots
                if(!item.equals(player.getInventory().getItemInOffHand()) &&
                        !item.equals(player.getInventory().getHelmet()) &&
                        !item.equals(player.getInventory().getChestplate()) &&
                        !item.equals(player.getInventory().getLeggings()) &&
                        !item.equals(player.getInventory().getBoots())) {
                    // sell the item and get its value
                    double soldValue = sellManager.sellItemWithMultiplier(player, item.getType(), item.getAmount());
                    if(soldValue > 0){
                        totalItems += item.getAmount();
                        totalValue += soldValue;
                        inventory.remove(item);
                    }
                }
            }
        }

        player.sendMessage(ChatColor.GREEN + "You sold " + String.format("%,.2f", totalItems) + " items with a " + String.format("%,.2f", multiplier) + "x multiplier for a total of $" + String.format("%,.2f", totalValue));
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.0f);

        return true;
    }
}