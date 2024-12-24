package com.coldspare.zana.sell;

import com.coldspare.zana.Zana;
import com.coldspare.zana.boost.BoosterManager;
import com.coldspare.zana.level.PlayerLevelManager;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SellManager {

    private static final Map<Material, Double> sellItems = new HashMap<>();
    private static PlayerLevelManager playerLevelManager;
    private BoosterManager boosterManager;

    public SellManager(PlayerLevelManager playerLevelManager, BoosterManager boosterManager) {
        this.playerLevelManager = playerLevelManager;
        this.boosterManager = boosterManager;
    }

    static {
        sellItems.put(Material.WHITE_DYE, 10.0);
        sellItems.put(Material.GRAY_DYE, 15.0);
        sellItems.put(Material.BLACK_DYE, 25.0);
        sellItems.put(Material.BROWN_DYE, 35.0);
        sellItems.put(Material.RED_DYE, 50.0);
        sellItems.put(Material.ORANGE_DYE, 75.0);
        sellItems.put(Material.YELLOW_DYE, 100.0);
        sellItems.put(Material.LIME_DYE, 120.0);
        sellItems.put(Material.GREEN_DYE, 150.0);
        sellItems.put(Material.CYAN_DYE, 175.0);
        sellItems.put(Material.LIGHT_BLUE_DYE, 200.0);
        sellItems.put(Material.BLUE_DYE, 210.0);
        sellItems.put(Material.PURPLE_DYE, 220.0);
        sellItems.put(Material.MAGENTA_DYE, 230.0);
        sellItems.put(Material.PINK_DYE, 240.0);
        sellItems.put(Material.WHITE_CANDLE, 250.0);
        sellItems.put(Material.GRAY_CANDLE, 260.0);
        sellItems.put(Material.BLACK_CANDLE, 270.0);
        sellItems.put(Material.BROWN_CANDLE, 280.0);
        sellItems.put(Material.RED_CANDLE, 290.0);
        sellItems.put(Material.ORANGE_CANDLE, 300.0);
        sellItems.put(Material.YELLOW_CANDLE, 310.0);
        sellItems.put(Material.LIME_CANDLE, 320.0);
        sellItems.put(Material.GREEN_CANDLE, 330.0);
        sellItems.put(Material.CYAN_CANDLE, 340.0);
        sellItems.put(Material.LIGHT_BLUE_CANDLE, 350.0);
        sellItems.put(Material.BLUE_CANDLE, 360.0);
        sellItems.put(Material.PURPLE_CANDLE, 370.0);
    }

    public static double getSellPrice(Material material) {
        return sellItems.getOrDefault(material, 0.0);
    }

    public static boolean isSellable(Material material) {
        return sellItems.containsKey(material);
    }

    public double sellItemWithMultiplier(Player player, Material material, int quantity) {
        if (!isSellable(material)) return 0;

        double multiplier = boosterManager.getBooster(player.getUniqueId(), BoosterManager.BoosterType.SELL);
        double price = getSellPrice(material) * quantity * multiplier;
        EconomyResponse r = Zana.getEconomy().depositPlayer(player, price);

        if (r.transactionSuccess()) {
            return price;
        } else {
            return 0;
        }
    }

    public double sellItemWithWandAndBooster(Player player, Material material, int quantity, double wandMultiplier) {
        if (!isSellable(material)) return 0;

        double boosterMultiplier = boosterManager.getBooster(player.getUniqueId(), BoosterManager.BoosterType.SELL);
        double price = getSellPrice(material) * quantity * boosterMultiplier * wandMultiplier;
        EconomyResponse r = Zana.getEconomy().depositPlayer(player, price);

        if (r.transactionSuccess()) {
            return price;
        } else {
            return 0;
        }
    }
}