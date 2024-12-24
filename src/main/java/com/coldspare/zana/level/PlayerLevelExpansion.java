package com.coldspare.zana.level;

import com.coldspare.zana.Zana;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class PlayerLevelExpansion extends PlaceholderExpansion {

    private JavaPlugin plugin;
    private PlayerLevelManager playerLevelManager;

    public PlayerLevelExpansion(JavaPlugin plugin, PlayerLevelManager playerLevelManager) {
        this.plugin = plugin;
        this.playerLevelManager = playerLevelManager;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "zana";
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        if (player == null) {
            return "";
        }

        if (identifier.equals("level")) {
            return String.valueOf(playerLevelManager.getPlayerLevel(player.getUniqueId()));
        } else if (identifier.equals("xp")) {
            return String.valueOf(playerLevelManager.getPlayerXP(player.getUniqueId()));
        } else if (identifier.equals("xp_to_level")) {
            int playerLevel = playerLevelManager.getPlayerLevel(player.getUniqueId());
            return String.valueOf(PlayerLevelManager.getBaseXP() + (playerLevel * PlayerLevelManager.getIncrementXP()));
        }

        return null;
    }
}