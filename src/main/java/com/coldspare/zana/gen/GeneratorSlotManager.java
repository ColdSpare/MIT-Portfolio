package com.coldspare.zana.gen;

import com.coldspare.zana.Database;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class GeneratorSlotManager {

    private final Map<UUID, Integer> generatorSlots = new HashMap<>();
    private final GeneratorDatabaseStorage generatorDatabaseStorage;
    private final GeneratorManager generatorManager;

    public GeneratorSlotManager(Database database, GeneratorManager generatorManager) {
        this.generatorManager = generatorManager;
        this.generatorDatabaseStorage = new GeneratorDatabaseStorage(database);
        generatorDatabaseStorage.loadGeneratorSlots().thenAcceptAsync(generatorSlots::putAll);
    }

    public void saveSlots() {
        generatorDatabaseStorage.saveGeneratorSlots(generatorSlots);
    }

    public boolean hasGeneratorSlot(Player player) {
        UUID playerId = player.getUniqueId();
        int totalSlots = generatorSlots.getOrDefault(playerId, 0);
        int usedSlots = generatorManager.getGeneratorCount(playerId);
        return totalSlots > usedSlots;
    }

    public int getNextAvailableSlot(Player player) {
        UUID playerId = player.getUniqueId();
        int totalSlots = generatorSlots.getOrDefault(playerId, 0);
        int usedSlots = generatorManager.getGeneratorCount(playerId);
        if (totalSlots > usedSlots) {
            return usedSlots + 1;
        }
        return -1; // No available slots
    }

    public void addSlots(UUID ownerId, int count) {
        int oldCount = generatorSlots.getOrDefault(ownerId, 0);
        generatorSlots.put(ownerId, oldCount + count);
        saveSlots();
    }

    public void removeSlots(Player player, int count) {
        UUID playerId = player.getUniqueId();
        int oldCount = generatorSlots.getOrDefault(playerId, 0);
        generatorSlots.put(playerId, Math.max(oldCount - count, 0));
        saveSlots();
    }

    public int getUsedSlots(UUID playerId) {
        return generatorManager.getGeneratorCount(playerId);
    }

    public int getTotalSlots(Player player) {
        UUID playerId = player.getUniqueId();
        int totalSlots = generatorSlots.getOrDefault(playerId, 0);
        return totalSlots;
    }
    public boolean containsSlot(UUID playerId) {
        return generatorSlots.containsKey(playerId);
    }
}