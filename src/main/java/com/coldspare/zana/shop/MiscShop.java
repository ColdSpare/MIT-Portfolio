/*package com.coldspare.zana;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class MiscShop implements Shop, Listener {
    private final Map<Material, Double> items = new LinkedHashMap<>();

    public void registerEvents(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private void setBorder(Inventory inventory) {
        int[] blackSlots = new int[]{0,2,4,6,8,18,26,36,38,40,42,44};
        int[] graySlots = new int[]{1,3,5,7,9,17,27,35,37,39,41,43};

        for (int slot : blackSlots) {
            inventory.setItem(slot, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        }

        for (int slot : graySlots) {
            inventory.setItem(slot, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }
    }

    public MiscShop() {

        items.put(Material.DIAMOND_SHOVEL, 2500.0);
        items.put(Material.DIAMOND_PICKAXE, 2500.0);
        items.put(Material.DIAMOND_AXE, 2500.0);
        items.put(Material.DIAMOND_HOE, 2500.0);
        items.put(Material.NETHERITE_SHOVEL, 5000.0);
        items.put(Material.NETHERITE_PICKAXE, 5000.0);
        items.put(Material.NETHERITE_AXE, 5000.0);
        items.put(Material.NETHERITE_HOE, 5000.0);
        items.put(Material.WATER_BUCKET, 50.0);
        items.put(Material.HOPPER, 2500.0);
        items.put(Material.CHEST, 250.0);
        items.put(Material.ITEM_FRAME, 250.0);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTitle().equals("Miscellaneous Shop")) {
            event.setCancelled(true);
        }
    }

    private Inventory createPage(int pageNumber) {
        Inventory inv = Bukkit.createInventory(this, 54, "Miscellaneous Items - Page " + (pageNumber + 1));
        int index = pageNumber * 21;

        for (int i = 10; i < 44 && index < items.size(); i++) {
            if ((i + 1) % 9 == 0 || i % 9 == 0) {  // Skip the first and last slots in each row
                i++;
                continue;
            }

            Material material = (Material) items.keySet().toArray()[index];
            ItemStack itemStack = new ItemStack(material);
            ItemMeta meta = itemStack.getItemMeta();

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Price: $" + items.get(material));
            meta.setLore(lore);
            itemStack.setItemMeta(meta);

            inv.setItem(i, itemStack);
            index++;
        }

        if (pageNumber > 0) {
            ItemStack previousPage = new ItemStack(Material.ARROW);
            ItemMeta previousPageMeta = previousPage.getItemMeta();
            previousPageMeta.setDisplayName(ChatColor.GOLD + "Previous Page");
            previousPage.setItemMeta(previousPageMeta);
            inv.setItem(48, previousPage);
        }

        if (index < items.size()) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta nextPageMeta = nextPage.getItemMeta();
            nextPageMeta.setDisplayName(ChatColor.GOLD + "Next Page");
            nextPage.setItemMeta(nextPageMeta);
            inv.setItem(50, nextPage);
        } else if (pageNumber > 0) {
            ItemStack noMorePages = new ItemStack(Material.RED_DYE);
            ItemMeta noMorePagesMeta = noMorePages.getItemMeta();
            noMorePagesMeta.setDisplayName(ChatColor.RED + "No more pages");
            noMorePages.setItemMeta(noMorePagesMeta);
            inv.setItem(50, noMorePages);
        }

        setBorder(inv);
        return inv;
    }


    protected ItemStack createGuiItem(final Material material, final String name, final ArrayList<String> lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    public void openInventory(final Player player) {
        player.openInventory(createPage(0));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals("Miscellaneous Shop")) {
            return;
        }

        e.setCancelled(true);

        ItemStack clickedItem = e.getCurrentItem();
        Player player = (Player) e.getWhoClicked();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        // This is a shop item
        int currentPageNumber = Integer.parseInt(e.getInventory().getTitle().split(" ")[3]) - 1;

        if (ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName()).equals("Next Page")) {
            player.openInventory(createPage(currentPageNumber + 1));
        } else if (ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName()).equals("Previous Page")) {
            player.openInventory(createPage(currentPageNumber - 1));
        } else {
            Economy economy = Zana.getEconomy();

            if (items.containsKey(clickedItem.getType())) {
                double priceDouble = items.get(clickedItem.getType());
                int price = (int) priceDouble;

                // Check if the player has enough money
                if (economy.getBalance(player) >= price) {
                    // Deduct the money and give the item
                    economy.withdrawPlayer(player, price);
                    player.getInventory().addItem(new ItemStack(clickedItem.getType(), 1));
                    player.sendMessage(ChatColor.GREEN + "You bought a " + clickedItem.getType().toString() + " for " + price + " dollars!");
                } else {
                    player.sendMessage(ChatColor.RED + "You don't have enough money to buy this item!");
                }
            }
        }
    }
}
*/