package com.coldspare.zana.level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FileStorage {
    private final File playerFile;
    private final FileConfiguration configuration;

    public FileStorage(JavaPlugin plugin, String fileName) {
        playerFile = new File(plugin.getDataFolder(), fileName);
        if (!playerFile.exists()) {
            try {
                playerFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        configuration = YamlConfiguration.loadConfiguration(playerFile);
    }

    public Map<UUID, Double> loadPlayerXP() {
        Map<UUID, Double> playerXP = new ConcurrentHashMap<>();
        for (String key : configuration.getKeys(false)) {
            playerXP.put(UUID.fromString(key), configuration.getDouble(key + ".xp"));
        }
        return playerXP;
    }

    public Map<UUID, Integer> loadPlayerLevels() {
        Map<UUID, Integer> playerLevels = new ConcurrentHashMap<>();
        for (String key : configuration.getKeys(false)) {
            playerLevels.put(UUID.fromString(key), configuration.getInt(key + ".level"));
        }
        return playerLevels;
    }

    public void savePlayerXP(Map<UUID, Double> playerXP) {
        for (Map.Entry<UUID, Double> entry : playerXP.entrySet()) {
            configuration.set(entry.getKey().toString() + ".xp", entry.getValue());
        }
        saveFile();
    }

    public void savePlayerLevels(Map<UUID, Integer> playerLevels) {
        for (Map.Entry<UUID, Integer> entry : playerLevels.entrySet()) {
            configuration.set(entry.getKey().toString() + ".level", entry.getValue());
        }
        saveFile();
    }

    public Map<UUID, String> loadPlayerNames() {
        Map<UUID, String> playerNames = new ConcurrentHashMap<>();
        for (String key : configuration.getKeys(false)) {
            String playerName = configuration.getString(key + ".name");
            if(playerName != null) {
                playerNames.put(UUID.fromString(key), playerName);
            }
        }
        return playerNames;
    }

    public void savePlayerNames(Map<UUID, String> playerNames) {
        for (Map.Entry<UUID, String> entry : playerNames.entrySet()) {
            configuration.set(entry.getKey().toString() + ".name", entry.getValue());
        }
        saveFile();
    }

    private void saveFile() {
        try {
            configuration.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}