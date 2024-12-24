package com.coldspare.zana.sell;

import com.coldspare.zana.Zana;
import com.coldspare.zana.boost.BoosterManager;
import com.plotsquared.core.PlotSquared;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import com.plotsquared.core.location.Location;
import com.plotsquared.core.plot.PlotArea;
import com.plotsquared.core.plot.Plot;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SellWandListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        // Check if the item in hand is a sell wand
        if (itemInHand.getType() == Material.BLAZE_ROD && itemInHand.hasItemMeta() && itemInHand.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Sell Wand")) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Block block = event.getClickedBlock();
                event.setCancelled(true);  // Cancel the event to not open the chest
                if (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST) {
                    Chest chest = (Chest) block.getState();
                    Inventory chestInventory = chest.getInventory(); // Get the inventory from the Chest object
                    SellManager sellManager = Zana.getInstance().getSellManager();
                    org.bukkit.Location bukkitLocation = block.getLocation();
                    com.plotsquared.core.location.Location plotLocation = com.plotsquared.core.location.Location.at(bukkitLocation.getWorld().getName(), bukkitLocation.getBlockX(), bukkitLocation.getBlockY(), bukkitLocation.getBlockZ());

                    // Get the plot area using the world name
                    PlotArea plotArea = PlotSquared.get().getPlotAreaManager().getPlotArea(bukkitLocation.getWorld().getName(), "");

                    Plot plot = null;
                    if (plotArea != null) {
                        plot = plotArea.getPlot(plotLocation);
                    }

                    if (plot == null || !plot.isAdded(player.getUniqueId())) {
                        player.sendMessage(ChatColor.RED + "You cannot sell items from a chest that you can't open.");
                        event.setCancelled(true);
                        return;
                    }

                    double total = 0.0;
                    double wandMultiplier = 1.0;
                    if (itemInHand.getItemMeta().hasLore()) {
                        List<String> lore = itemInHand.getItemMeta().getLore();
                        for (String line : lore) {
                            if (line.startsWith(ChatColor.GREEN + "" + ChatColor.BOLD + "▎ " + ChatColor.WHITE + "Multiplier: " + ChatColor.GREEN + ChatColor.UNDERLINE)) {
                                try {
                                    String strippedLine = ChatColor.stripColor(line);
                                    String[] parts = strippedLine.split(": ");
                                    if (parts.length > 1) {
                                        wandMultiplier = Double.parseDouble(parts[1].replace("x", ""));
                                    }
                                } catch (NumberFormatException ignored) {
                                }
                            }
                        }
                    }

                    boolean foundSellableItem = false;
                    for (ItemStack item : chestInventory.getContents()) {
                        if (item != null && SellManager.isSellable(item.getType())) {
                            foundSellableItem = true;
                            double soldPrice = sellManager.sellItemWithWandAndBooster(player, item.getType(), item.getAmount(), wandMultiplier);
                            if(soldPrice > 0) {
                                total += soldPrice;
                                chestInventory.remove(item);
                            }
                        }
                    }

                    if (!foundSellableItem) {
                        player.sendMessage(ChatColor.RED + "No sellable items were found in the chest.");
                    }
                    else if (total > 0.0) {
                        player.sendMessage(ChatColor.GREEN + "You sold items for " + ChatColor.GOLD + "$" + total);
                    }
                }
            }
        }
    }
}