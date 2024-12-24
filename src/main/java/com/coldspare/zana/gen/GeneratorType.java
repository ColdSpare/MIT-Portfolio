package com.coldspare.zana.gen;

import com.coldspare.zana.Zana; // Add this import
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public enum GeneratorType {
    WHITE_TERRACOTTA(Material.WHITE_TERRACOTTA, 250, "&f&lWHITE GENERATOR", Material.WHITE_DYE, "WHITE_GEN_TAG"),
    GRAY_TERRACOTTA(Material.GRAY_TERRACOTTA, 500, "&7&lGRAY GENERATOR", Material.GRAY_DYE, "GRAY_GEN_TAG"),
    BLACK_TERRACOTTA(Material.BLACK_TERRACOTTA, 1000, "&8&lBLACK GENERATOR", Material.BLACK_DYE, "BLACK_GEN_TAG"),
    BROWN_TERRACOTTA(Material.BROWN_TERRACOTTA, 5000, "&6&lBROWN GENERATOR", Material.BROWN_DYE, "BROWN_GEN_TAG"),
    RED_TERRACOTTA(Material.RED_TERRACOTTA, 25000, "&6&lRED GENERATOR", Material.RED_DYE, "RED_GEN_TAG"),
    ORANGE_TERRACOTTA(Material.ORANGE_TERRACOTTA, 50000, "&6&lORANGE GENERATOR", Material.ORANGE_DYE, "ORANGE_GEN_TAG"),
    YELLOW_TERRACOTTA(Material.YELLOW_TERRACOTTA, 100000, "&e&lYELLOW GENERATOR", Material.YELLOW_DYE, "YELLOW_GEN_TAG"),
    LIME_TERRACOTTA(Material.LIME_TERRACOTTA, 250000, "&a&lLIME GENERATOR", Material.LIME_DYE, "LIME_GEN_TAG"),
    GREEN_TERRACOTTA(Material.GREEN_TERRACOTTA, 500000, "&2&lGREEN GENERATOR", Material.GREEN_DYE, "GREEN_GEN_TAG"),
    CYAN_TERRACOTTA(Material.CYAN_TERRACOTTA, 750000, "&3&lCYAN GENERATOR", Material.CYAN_DYE, "CYAN_GEN_TAG"),
    LIGHT_BLUE_TERRACOTTA(Material.LIGHT_BLUE_TERRACOTTA, 1000000, "&b&lLIGHT BLUE GENERATOR", Material.LIGHT_BLUE_DYE, "LIGHT_BLUE_GEN_TAG"),
    BLUE_TERRACOTTA(Material.BLUE_TERRACOTTA, 1250000, "&1&lBLUE GENERATOR", Material.BLUE_DYE, "BLUE_GEN_TAG"),
    PURPLE_TERRACOTTA(Material.PURPLE_TERRACOTTA, 1500000, "&5&lPURPLE GENERATOR", Material.PURPLE_DYE, "PURPLE_GEN_TAG"),
    MAGENTA_TERRACOTTA(Material.MAGENTA_TERRACOTTA, 2000000, "&d&lMAGENTA GENERATOR", Material.MAGENTA_DYE, "MAGENTA_GEN_TAG"),
    PINK_TERRACOTTA(Material.PINK_TERRACOTTA, 2500000, "&d&lPINK GENERATOR", Material.PINK_DYE, "PINK_GEN_TAG"),
    WHITE_GLAZED_TERRACOTTA(Material.WHITE_GLAZED_TERRACOTTA, 3500000, "&f&lWHITE GLAZED GENERATOR", Material.WHITE_CANDLE, "WHITE_GLAZED_GEN_TAG"),
    GRAY_GLAZED_TERRACOTTA(Material.GRAY_GLAZED_TERRACOTTA, 5000000, "&7&lGRAY GLAZED GENERATOR", Material.GRAY_CANDLE, "GRAY_GLAZED_GEN_TAG"),
    BLACK_GLAZED_TERRACOTTA(Material.BLACK_GLAZED_TERRACOTTA, 7500000, "&8&lBLACK GLAZED GENERATOR", Material.BLACK_CANDLE, "BLACK_GLAZED_GEN_TAG"),
    BROWN_GLAZED_TERRACOTTA(Material.BROWN_GLAZED_TERRACOTTA, 10000000, "&6&lBROWN GLAZED GENERATOR", Material.BROWN_CANDLE, "BROWN_GLAZED_GEN_TAG"),
    RED_GLAZED_TERRACOTTA(Material.RED_GLAZED_TERRACOTTA, 25000000, "&c&lRED GLAZED GENERATOR", Material.RED_CANDLE, "RED_GLAZED_GEN_TAG"),
    ORANGE_GLAZED_TERRACOTTA(Material.ORANGE_GLAZED_TERRACOTTA, 50000000, "&6&lORANGE GLAZED GENERATOR", Material.ORANGE_CANDLE, "ORANGE_GLAZED_GEN_TAG"),
    YELLOW_GLAZED_TERRACOTTA(Material.YELLOW_GLAZED_TERRACOTTA, 100000000, "&e&lYELLOW GLAZED GENERATOR", Material.YELLOW_CANDLE, "YELLOW_GLAZED_GEN_TAG"),
    LIME_GLAZED_TERRACOTTA(Material.LIME_GLAZED_TERRACOTTA, 250000000, "&a&lLIME GLAZED GENERATOR", Material.LIME_CANDLE, "LIME_GLAZED_GEN_TAG"),
    GREEN_GLAZED_TERRACOTTA(Material.GREEN_GLAZED_TERRACOTTA, 500000000, "&2&lGREEN GLAZED GENERATOR", Material.GREEN_CANDLE, "GREEN_GLAZED_GEN_TAG"),
    CYAN_GLAZED_TERRACOTTA(Material.CYAN_GLAZED_TERRACOTTA, 750000000, "&3&lCYAN GLAZED GENERATOR", Material.CYAN_CANDLE, "CYAN_GLAZED_GEN_TAG"),
    LIGHT_BLUE_GLAZED_TERRACOTTA(Material.LIGHT_BLUE_GLAZED_TERRACOTTA, 1000000000, "&b&lLIGHT BLUE GLAZED GENERATOR", Material.LIGHT_BLUE_CANDLE, "LIGHT_BLUE_GLAZED_GEN_TAG"),
    BLUE_GLAZED_TERRACOTTA(Material.BLUE_GLAZED_TERRACOTTA, 2000000000, "&1&lBLUE GLAZED GENERATOR", Material.BLUE_CANDLE, "BLUE_GLAZED_GEN_TAG"),
    PURPLE_GLAZED_TERRACOTTA(Material.PURPLE_GLAZED_TERRACOTTA, 0, "&5&lPURPLE GLAZED GENERATOR", Material.PURPLE_CANDLE, "PURPLE_GLAZED_GEN_TAG");

    private final Material blockMaterial;
    private final int price;
    private final String itemName;
    private final Material itemType;
    private final String tag;

    GeneratorType(Material blockMaterial, int price, String itemName, Material itemType, String tag) {
        this.blockMaterial = blockMaterial;
        this.price = price;
        this.itemName = itemName;
        this.itemType = itemType;
        this.tag = tag;
    }

    public static GeneratorType fromMaterial(Material material) {
        for (GeneratorType type : values()) {
            if (type.getBlockMaterial() == material) {
                return type;
            }
        }
        return null;
    }

    public Material getBlockMaterial() {
        return blockMaterial;
    }

    public int getPrice() {
        return price;
    }

    public Material getItemType() {
        return itemType;
    }

    public ItemStack toItemStack() {
        ItemStack itemStack = new ItemStack(blockMaterial);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemName));

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GRAY + "Gen Factory");
        lore.add("");
        lore.add(ChatColor.GREEN + "Description");
        lore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "▎ " + ChatColor.WHITE + "Place me down to generate");
        lore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "▎ " + ChatColor.WHITE + "items worth money!");
        lore.add("");
        lore.add(ChatColor.GREEN + "Information");
        lore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "▎ " + ChatColor.WHITE + "Upgrade: " + ChatColor.GREEN + "$" + ChatColor.GREEN + (getNextType() != null ? String.format("%,d", getNextType().getPrice()) : "Max Level"));
        lore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "▎ " + ChatColor.WHITE + "Tier: " + ChatColor.GREEN + (ordinal() + 1));
        lore.add("");
        lore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "> " + ChatColor.LIGHT_PURPLE + "Place at your plot" + ChatColor.GREEN + "" + ChatColor.BOLD + " <");

        meta.setLore(lore);

        // Set a custom tag to identify the generator block
        NamespacedKey key = new NamespacedKey(Zana.getInstance(), tag);
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, tag);
        itemStack.setItemMeta(meta);
        return itemStack;
    }


    public GeneratorType getNextType() {
        GeneratorType[] types = values();
        int ordinal = ordinal();
        return (ordinal < types.length - 1) ? types[ordinal + 1] : null;
    }

    public String getTag() {
        return tag;
    }
}