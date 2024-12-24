/*package com.coldspare.zana.farming;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Hoe {
    private int speedy;
    private int lootbag;
    private int cropBloom;
    private int crateBuster;
    private int hacked;

    public static final String METADATA_KEY = "Zana.Hoe";
    public static final List<String> UPGRADE_NAMES = Arrays.asList("Speedy", "Lootbag", "Crop Bloom", "Crate Buster", "Hacked");
    public static final String LORE_FORMAT = "%s: %d";


    public Hoe() {
        this.speedy = 0;
        this.lootbag = 0;
        this.cropBloom = 0;
        this.crateBuster = 0;
        this.hacked = 0;
    }

    public int getSpeedy() {
        return speedy;
    }

    public void setSpeedy(int speedy) {
        this.speedy = speedy;
    }

    public int getLootbag() {
        return lootbag;
    }

    public void setLootbag(int lootbag) {
        this.lootbag = lootbag;
    }

    public int getCropBloom() {
        return cropBloom;
    }

    public void setCropBloom(int cropBloom) {
        this.cropBloom = cropBloom;
    }

    public int getCrateBuster() {
        return crateBuster;
    }

    public void setCrateBuster(int crateBuster) {
        this.crateBuster = crateBuster;
    }

    public int getHacked() {
        return hacked;
    }

    public void setHacked(int hacked) {
        this.hacked = hacked;
    }

    public static Hoe fromItemStack(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta() || !itemStack.getItemMeta().hasDisplayName()) {
            return null;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        List<String> lore = itemMeta.getLore();

        if (lore.size() < UPGRADE_NAMES.size()) {
            return null;
        }

        Hoe hoe = new Hoe();

        for (int i = 0; i < UPGRADE_NAMES.size(); i++) {
            String line = lore.get(i);
            String[] parts = line.split(": ");
            if (parts.length < 2) {
                return null;
            }

            int level = Integer.parseInt(parts[1]);

            switch (parts[0]) {
                case "Speedy":
                    hoe.setSpeedy(level);
                    break;
                case "Lootbag":
                    hoe.setLootbag(level);
                    break;
                case "Crop Bloom":
                    hoe.setCropBloom(level);
                    break;
                case "Crate Buster":
                    hoe.setCrateBuster(level);
                    break;
                case "Hacked":
                    hoe.setHacked(level);
                    break;
            }
        }

        return hoe;
    }

    public void setItemMeta(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>();

        lore.add(String.format(LORE_FORMAT, "Speedy", getSpeedy()));
        lore.add(String.format(LORE_FORMAT, "Lootbag", getLootbag()));
        lore.add(String.format(LORE_FORMAT, "Crop Bloom", getCropBloom()));
        lore.add(String.format(LORE_FORMAT, "Crate Buster", getCrateBuster()));
        lore.add(String.format(LORE_FORMAT, "Hacked", getHacked()));

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
    }

    public boolean shouldApplyEffect(String name) {
        int level;
        switch (name) {
            case "Speedy":
                level = getSpeedy();
                break;
            case "Lootbag":
                level = getLootbag();
                break;
            case "Crop Bloom":
                level = getCropBloom();
                break;
            case "Crate Buster":
                level = getCrateBuster();
                break;
            case "Hacked":
                level = getHacked();
                break;
            default:
                return false;
        }

        return Math.random() < (level * 0.02);
    }
}*/