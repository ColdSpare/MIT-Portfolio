package com.coldspare.zana.boost;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BoosterPlaceholders extends PlaceholderExpansion {

    private BoosterManager boosterManager;

    public BoosterPlaceholders(BoosterManager boosterManager){
        this.boosterManager = boosterManager;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "booster";
    }

    @Override
    public @NotNull String getAuthor() {
        return "ColdSpare";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if(player == null){
            return "";
        }

        // if the identifier is booster_active, return whether booster is active
        if(identifier.equals("active")){
            return boosterManager.isBoosterActive(player.getUniqueId()) ? "YES" : "NO";
        }

        // if the identifier is booster_sell, return the sell booster
        if(identifier.equals("sell")){
            return boosterManager.getSellBooster(player.getUniqueId());
        }

        // if the identifier is booster_xp, return the xp booster
        if(identifier.equals("xp")){
            return boosterManager.getXpBooster(player.getUniqueId());
        }

        return null;
    }
}