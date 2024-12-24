package com.coldspare.zana.tokens;

import com.coldspare.zana.Zana;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TokenManager {
    private static Map<UUID, Integer> playerTokens = new ConcurrentHashMap<>();
    private final Zana plugin;

    public TokenManager(Zana plugin) {
        this.plugin = plugin;
        loadTokens();
        startSavingTask();
    }

    public void addTokens(Player player, int amount) {
        UUID playerUUID = player.getUniqueId();
        int currentTokens = playerTokens.getOrDefault(playerUUID, 0);
        playerTokens.put(playerUUID, currentTokens + amount);
        saveTokens();
    }

    public int getTokens(Player player) {
        return playerTokens.getOrDefault(player.getUniqueId(), 0);
    }

    public void loadTokens() {
        File dataFile = new File(plugin.getDataFolder(), "tokens.yml");
        if (!dataFile.exists()) {
            return;
        }

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(dataFile);
        for (String key : yaml.getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            int tokens = yaml.getInt(key);
            playerTokens.put(uuid, tokens);
        }
    }

    public void saveTokens() {
        File dataFile = new File(plugin.getDataFolder(), "tokens.yml");
        YamlConfiguration yaml = new YamlConfiguration();
        for (Map.Entry<UUID, Integer> entry : playerTokens.entrySet()) {
            yaml.set(entry.getKey().toString(), entry.getValue());
        }

        try {
            yaml.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save tokens");
            e.printStackTrace();
        }
    }

    public void startSavingTask() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::saveTokens, 1200L, 1200L);
    }

    public void removeTokens(Player player, int amount) {
        UUID playerUUID = player.getUniqueId();
        int currentTokens = playerTokens.getOrDefault(playerUUID, 0);
        if (currentTokens < amount) {
            return; // or throw an error
        }
        playerTokens.put(playerUUID, currentTokens - amount);
        saveTokens();
    }
}
