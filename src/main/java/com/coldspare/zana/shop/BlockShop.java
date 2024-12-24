/*package com.coldspare.zana;

import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockShop implements Shop, Listener {
    private Inventory inv;
    private final Map<Material, Integer> items = new HashMap<>();
    private final Map<Player, Integer> playerPages = new HashMap<>();
    private int currentPageNumber;

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

    public BlockShop() {
        currentPageNumber = 0;

        items.put(Material.COBBLESTONE, 40);
        items.put(Material.DARK_PRISMARINE, 40);
        items.put(Material.STONE, 80);
        items.put(Material.GRASS_BLOCK, 80);
        items.put(Material.SAND, 80);
        items.put(Material.PACKED_ICE, 80);
        items.put(Material.ICE, 80);
        items.put(Material.DIRT, 80);
        items.put(Material.FARMLAND, 80);
        items.put(Material.OAK_LOG, 160);
        items.put(Material.SPRUCE_LOG, 160);
        items.put(Material.BIRCH_LOG, 160);
        items.put(Material.JUNGLE_LOG, 160);
        items.put(Material.ACACIA_LOG, 160);
        items.put(Material.DARK_OAK_LOG, 160);
        items.put(Material.OAK_PLANKS, 40);
        items.put(Material.SPRUCE_PLANKS, 40);
        items.put(Material.BIRCH_PLANKS, 40);
        items.put(Material.JUNGLE_PLANKS, 40);
        items.put(Material.ACACIA_PLANKS, 40);
        items.put(Material.DARK_OAK_PLANKS, 40);
        items.put(Material.OAK_LEAVES, 80);
        items.put(Material.SPRUCE_LEAVES, 80);
        items.put(Material.BIRCH_LEAVES, 80);
        items.put(Material.JUNGLE_LEAVES, 80);
        items.put(Material.ACACIA_LEAVES, 80);
        items.put(Material.DARK_OAK_LEAVES, 80);
        items.put(Material.AZALEA_LEAVES, 80);
        items.put(Material.GLASS, 80);
        items.put(Material.WHITE_WOOL, 80);
        items.put(Material.RED_WOOL, 80);
        items.put(Material.BLACK_WOOL, 80);
        items.put(Material.BROWN_WOOL, 80);
        items.put(Material.LIME_WOOL, 80);
        items.put(Material.GREEN_WOOL, 80);
        items.put(Material.CYAN_WOOL, 80);
        items.put(Material.LIGHT_BLUE_WOOL, 80);
        items.put(Material.PINK_WOOL, 80);
        items.put(Material.GRAY_WOOL, 80);
        items.put(Material.MAGENTA_WOOL, 80);
        items.put(Material.PURPLE_WOOL, 80);
        items.put(Material.WHITE_CONCRETE_POWDER, 80);
        items.put(Material.RED_CONCRETE_POWDER, 80);
        items.put(Material.BLACK_CONCRETE_POWDER, 80);
        items.put(Material.BROWN_CONCRETE_POWDER, 80);
        items.put(Material.LIME_CONCRETE_POWDER, 80);
        items.put(Material.GREEN_CONCRETE_POWDER, 80);
        items.put(Material.LIGHT_BLUE_CONCRETE_POWDER, 80);
        items.put(Material.PINK_CONCRETE_POWDER, 80);
        items.put(Material.GRAY_CONCRETE_POWDER, 80);
        items.put(Material.PURPLE_CONCRETE_POWDER, 80);
        items.put(Material.WHITE_STAINED_GLASS, 80);
        items.put(Material.MAGENTA_STAINED_GLASS, 80);
        items.put(Material.LIGHT_BLUE_STAINED_GLASS, 80);
        items.put(Material.YELLOW_STAINED_GLASS, 80);
        items.put(Material.LIME_STAINED_GLASS, 80);
        items.put(Material.PINK_STAINED_GLASS, 80);
        items.put(Material.LIGHT_GRAY_STAINED_GLASS, 80);
        items.put(Material.CYAN_STAINED_GLASS, 80);
        items.put(Material.PURPLE_STAINED_GLASS, 80);
        items.put(Material.BROWN_STAINED_GLASS, 80);
        items.put(Material.GREEN_STAINED_GLASS, 80);
        items.put(Material.RED_STAINED_GLASS, 80);
        items.put(Material.BLACK_STAINED_GLASS, 80);
        items.put(Material.LADDER, 40);
        items.put(Material.SOUL_SAND, 80);
        items.put(Material.MAGMA_BLOCK, 80);
        items.put(Material.GLOWSTONE, 80);

        // Initialize the first page
        inv = createPage(0);
    }

    public void initializeItems(Inventory page, int start, int end) {
        ArrayList<String> lore = new ArrayList<>();
        int slot = 10; // Start slot
        for (int i = start; i < end && i < items.size() && slot <= 26; i++) {
            // If the slot number is the last of the row, skip to the next row
            if (slot % 9 == 7) {
                slot += 2;
            }
            Material material = (Material) items.keySet().toArray()[i];
            int price = items.get(material);

            lore.clear();
            lore.add("");
            lore.add(ChatColor.translateAlternateColorCodes('&', "&a▶ &fCost: &2$&a" + price));
            lore.add("");

            page.setItem(slot, createGuiItem(material, "#1fff5e" + material.name(), lore, 16));
            slot++;
        }

        for (int i = 0; i < page.getSize(); i++) {
            if (page.getItem(i) == null) {
                // Set all empty slots to stone buttons
                page.setItem(i, createGuiItem(Material.STONE_BUTTON, "&7", new ArrayList<>(), 1));
            }
        }
        this.inv = page;  // Assign the page to inv after it's completely initialized
    }

    public Inventory createPage(int pageNumber) {
        Inventory page = Bukkit.createInventory(null, 54, "Block Shop");
        int index = pageNumber * 21;

        int placePosition = 0;
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

            page.setItem(i, itemStack);
            placePosition++;
            index++;
        }

        if (pageNumber > 0) {
            ItemStack previousPage = new ItemStack(Material.ARROW);
            ItemMeta previousPageMeta = previousPage.getItemMeta();
            previousPageMeta.setDisplayName(ChatColor.GOLD + "Previous Page");
            previousPage.setItemMeta(previousPageMeta);
            page.setItem(45, previousPage);
        }

        if (index < items.size()) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta nextPageMeta = nextPage.getItemMeta();
            nextPageMeta.setDisplayName(ChatColor.GOLD + "Next Page");
            nextPage.setItemMeta(nextPageMeta);
            page.setItem(53, nextPage);
        } else if (pageNumber > 0) {
            ItemStack noMorePages = new ItemStack(Material.RED_DYE);
            ItemMeta noMorePagesMeta = noMorePages.getItemMeta();
            noMorePagesMeta.setDisplayName(ChatColor.RED + "No more pages");
            noMorePages.setItemMeta(noMorePagesMeta);
            page.setItem(53, noMorePages);
        }

        setBorder(page);
        currentPageNumber = pageNumber;
        return page;
    }

    protected ItemStack createGuiItem(final Material material, final String name, final ArrayList<String> lore, int amount) {
        final ItemStack item = new ItemStack(material, amount);
        final ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a" + material.name()));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public void openInventory(final Player player) {
        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals("Block Shop")) {
            return;
        }

        e.setCancelled(true);

        ItemStack clickedItem = e.getCurrentItem();
        Player player = (Player) e.getWhoClicked();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        // This is a shop item
        if (clickedItem.getType() == Material.ARROW) {
            if (ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName()).equals("Next Page")) {
                player.openInventory(createPage(currentPageNumber + 1));
            } else if (ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName()).equals("Previous Page")) {
                player.openInventory(createPage(currentPageNumber - 1));
            }
        } else {
            Economy economy = Zana.getEconomy();

            if (items.containsKey(clickedItem.getType())) {
                int price = items.get(clickedItem.getType());

                // Check if the player has enough money
                if (economy.getBalance(player) >= price) {
                    // Deduct the money and give the item
                    economy.withdrawPlayer(player, price);
                    player.getInventory().addItem(new ItemStack(clickedItem.getType(), 16));
                    player.sendMessage(ChatColor.GREEN + "You bought 16 " + clickedItem.getType().toString() + " for " + price + " dollars!");
                } else {
                    player.sendMessage(ChatColor.RED + "You don't have enough money to buy this item!");
                }
            }
        }
    }
}*/