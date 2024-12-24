package com.coldspare.zana.level;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Listeners implements Listener {
    private final PlayerLevelManager playerLevelManager;

    public Listeners(PlayerLevelManager playerLevelManager) {
        this.playerLevelManager = playerLevelManager;
    }

    private static final Map<Material, Double> MATERIAL_XP = new HashMap<Material, Double>() {{
        put(Material.SPRUCE_LOG, 1.1);
        put(Material.BIRCH_LOG, 1.5);
        put(Material.JUNGLE_LOG, 1.8);
        put(Material.WHEAT, 0.1);
        put(Material.CARROTS, 0.25);
        put(Material.POTATOES, 0.5);
        put(Material.BEETROOTS, 0.8);
        put(Material.COAL_ORE, 0.1);
        put(Material.IRON_ORE, 0.3);
        put(Material.GOLD_ORE, 0.5);
        put(Material.DIAMOND_ORE, 0.8);
        put(Material.EMERALD_ORE, 1.1);
        put(Material.ANCIENT_DEBRIS, 1.5);
    }};

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Material material = event.getBlock().getType();

        // If the block doesn't give XP, skip checking the player's level.
        if (!MATERIAL_XP.containsKey(material)) {
            return;
        }

        Player player = event.getPlayer();
        int playerLevel = playerLevelManager.getPlayerLevel(player);

        // If player's level is lower than required for mining the crops, cancel the event and send them an action bar message
        if ((material == Material.CARROTS && playerLevel < 15) || (material == Material.POTATOES && playerLevel < 50) || (material == Material.BEETROOTS && playerLevel < 100)) {
            event.setCancelled(true);
            player.sendActionBar(ChatColor.RED + "You need to be level " +
                    (material == Material.CARROTS ? 15 : material == Material.POTATOES ? 50 : 100) +
                    " to mine " + material.name().toLowerCase().replace('_', ' '));
            return;
        }

        double xp = MATERIAL_XP.get(material);
        if (xp > 0) {
            playerLevelManager.giveXP(player, xp);
        }
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            Player player = event.getPlayer();
            Random rand = new Random();
            int xp = rand.nextInt(21) + 5; // Random number between 5 and 25
            playerLevelManager.giveXP(player, xp);
        }
    }
}