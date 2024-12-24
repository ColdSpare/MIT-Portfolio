/*package com.coldspare.zana.farming;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class HoeUpgradeGUI {
    private final Inventory inv;
    private final Hoe hoe;

    public HoeUpgradeGUI(Hoe hoe) {
        this.hoe = hoe;
        inv = Bukkit.createInventory(null, 9, ChatColor.GOLD + "Hoe Upgrade");

        initializeItems();
    }

    public void initializeItems() {
        List<String> categories = Arrays.asList("Speedy", "Lootbag", "Crop Bloom", "Crate Buster", "Hacked");
        List<Integer> levels = Arrays.asList(hoe.getSpeedy(), hoe.getLootbag(), hoe.getCropBloom(), hoe.getCrateBuster(), hoe.getHacked());

        for (int i = 0; i < categories.size(); i++) {
            inv.setItem(i, createGuiItem(Material.DIAMOND_HOE, categories.get(i), "Current level: " + levels.get(i)));
        }
    }

    protected ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(name);

        // Set the lore of the item
        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);

        return item;
    }

    public void openInventory(final Player p) {
        p.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(inv.getTitle())) {
            event.setCancelled(true);
            String clickedItemName = event.getCurrentItem().getItemMeta().getDisplayName();

            switch (clickedItemName) {
                case "Speedy":
                    hoe.setSpeedy(Math.min(hoe.getSpeedy() + 1, 5));
                    break;
                case "Lootbag":
                    hoe.setLootbag(Math.min(hoe.getLootbag() + 1, 5));
                    break;
                case "Crop Bloom":
                    hoe.setCropBloom(Math.min(hoe.getCropBloom() + 1, 5));
                    break;
                case "Crate Buster":
                    hoe.setCrateBuster(Math.min(hoe.getCrateBuster() + 1, 5));
                    break;
                case "Hacked":
                    hoe.setHacked(Math.min(hoe.getHacked() + 1, 5));
                    break;
            }

            initializeItems();
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().equals(inv.getTitle())) {
            hoe.setItemMeta((ItemStack) event.getInventory().getHolder());
        }
    }
}*/