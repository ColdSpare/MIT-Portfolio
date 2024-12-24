package com.coldspare.zana.boost;

import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BoosterManager {
    public enum BoosterType {
        XP, SELL
    }

    private final Map<UUID, Map<BoosterType, Double>> playerBoosters = new ConcurrentHashMap<>();

    public void addBooster(UUID playerId, BoosterType type, double multiplier) {
        playerBoosters.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>()).put(type, multiplier);
    }

    public void clearBoosters(UUID playerId) {
        playerBoosters.remove(playerId);
    }

    public void clearAllBoosters() {
        playerBoosters.clear();
    }

    public double getBooster(UUID playerId, BoosterType type) {
        return playerBoosters.getOrDefault(playerId, Collections.emptyMap()).getOrDefault(type, 1.0);
    }

    public boolean isBoosterActive(UUID playerId) {
        return playerBoosters.containsKey(playerId);
    }

    public String getSellBooster(UUID playerId) {
        if (!isBoosterActive(playerId)) return "1.00";

        Map<BoosterType, Double> boosters = playerBoosters.get(playerId);
        double sellMultiplier = boosters.getOrDefault(BoosterType.SELL, 1.0);

        return String.format("%.2f", sellMultiplier);
    }

    public String getXpBooster(UUID playerId) {
        if (!isBoosterActive(playerId)) return "1.00";

        Map<BoosterType, Double> boosters = playerBoosters.get(playerId);
        double xpMultiplier = boosters.getOrDefault(BoosterType.XP, 1.0);

        return String.format("%.2f", xpMultiplier);
    }
}