/*package com.coldspare.zana;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class MainMenu implements Shop, Listener {
    private Inventory inv;

    @Override
    public void openInventory(final Player player) {
        player.openInventory(inv);
        //Zana.getInstance().openShop(player, this);
    }

    public MainMenu() {
        inv = Bukkit.createInventory(null, 27, ChatColor.translateAlternateColorCodes('&', "&aShop"));
        initializeItems();
    }

    public void initializeItems() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.translateAlternateColorCodes('&', "&a▶ &fClick to open"));
        lore.add("");
        inv.setItem(11, createGuiItem(Material.GRASS_BLOCK, "&aBlock Shop", lore));
        inv.setItem(13, createGuiItem(Material.WHITE_WOOL, "&aGen Shop", lore));
        inv.setItem(15, createGuiItem(Material.DIAMOND_PICKAXE, "&aMiscellaneous Shop", lore));
        inv.setItem(26, createGuiItem(Material.BARRIER, "&aClose Shop", lore));
    }

    protected ItemStack createGuiItem(final Material material, final String name, final ArrayList<String> lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (e.getInventory() != inv) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        final Player player = (Player) e.getWhoClicked();
        if(clickedItem.getType() == Material.BARRIER) {
            player.closeInventory();
            Zana.getInstance().closeShop(player);
        } else if (clickedItem.getType() == Material.GRASS_BLOCK) {
            Zana.getInstance().openShop(player, new BlockShop());
        } else if (clickedItem.getType() == Material.DIAMOND_PICKAXE) {
            Zana.getInstance().openShop(player, new MiscShop());
        }
    }
}
*/