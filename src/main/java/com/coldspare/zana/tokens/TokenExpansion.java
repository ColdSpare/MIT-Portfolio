package com.coldspare.zana.tokens;

import com.coldspare.zana.Zana;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

// %zana_tokens%
public class TokenExpansion extends PlaceholderExpansion {
    private Zana plugin;

    public TokenExpansion(Zana plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "zana2";
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().get(0);
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

        if ("tokens".equals(identifier)) {
            if (player.isOnline()) {
                return String.valueOf(plugin.getTokenManager().getTokens((Player) player));
            } else {
                return "0";
            }
        }

        return null;
    }
}