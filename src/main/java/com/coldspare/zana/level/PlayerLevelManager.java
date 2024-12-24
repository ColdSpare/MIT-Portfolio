package com.coldspare.zana.level;

import com.coldspare.zana.Zana;
import com.coldspare.zana.boost.BoosterManager;
import com.coldspare.zana.gen.GeneratorSlotManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.bukkit.Sound;
import net.md_5.bungee.api.ChatColor;

public class PlayerLevelManager {
    private final Map<UUID, Double> playerXP = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> playerLevels = new ConcurrentHashMap<>();
    private static final int BASE_XP = 100;
    private static final int INCREMENT_XP = 50;
    private final GeneratorSlotManager generatorSlotManager;
    private final FileStorage playerFileStorage;
    private final Map<UUID, String> playerNames = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final JavaPlugin plugin;

    public PlayerLevelManager(JavaPlugin plugin, GeneratorSlotManager generatorSlotManager) {
        this.plugin = plugin;
        this.generatorSlotManager = generatorSlotManager;
        playerFileStorage = new FileStorage(plugin, "playerdata.yml");
        loadPlayerData();
        startPeriodicSave();
    }

    private void startPeriodicSave() {
        executorService.scheduleAtFixedRate(() -> this.savePlayerData(true), 1, 1, TimeUnit.MINUTES);
    }

    public void loadPlayerData() {
        playerXP.putAll(playerFileStorage.loadPlayerXP());
        playerLevels.putAll(playerFileStorage.loadPlayerLevels());
        playerNames.putAll(playerFileStorage.loadPlayerNames());
    }

    public void savePlayerData(boolean async) {
        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                playerFileStorage.savePlayerXP(playerXP);
                playerFileStorage.savePlayerLevels(playerLevels);
                playerFileStorage.savePlayerNames(playerNames);
            });
        } else {
            playerFileStorage.savePlayerXP(playerXP);
            playerFileStorage.savePlayerLevels(playerLevels);
            playerFileStorage.savePlayerNames(playerNames);
        }
    }

    public int getPlayerLevel(Player player) {
        return playerLevels.getOrDefault(player.getUniqueId(), 0);
    }

    public void giveXP(Player player, double xp) {
        DecimalFormat df = new DecimalFormat("#.##"); // round decimal x.x

        UUID playerId = player.getUniqueId();
        playerNames.put(playerId, player.getName());
        double currentXP = playerXP.getOrDefault(playerId, 0.0);
        xp *= Zana.getInstance().getBoosterManager().getBooster(player.getUniqueId(), BoosterManager.BoosterType.XP);
        int currentLevel = playerLevels.getOrDefault(playerId, 0);
        int requiredXP = BASE_XP + (currentLevel * INCREMENT_XP);
        double newXP = currentXP + xp;


        if (newXP >= requiredXP) {
            newXP -= requiredXP;
            currentLevel++;
            playerLevels.put(playerId, currentLevel);

            // Give extra generator slot for every 1 level
            if (currentLevel % 1 == 0) {
                // Reference to GeneratorSlotManager
                generatorSlotManager.addSlots(playerId, 1);
            }
            // Play a sound and show a Title when leveling up
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            player.sendTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "LEVEL-UP",
                    ChatColor.DARK_AQUA + "☯" + (currentLevel-1) + ChatColor.GRAY + " - " + ChatColor.DARK_AQUA + "☯" + currentLevel,
                    10, 70, 20);
        }

        // Calculate XP required for the next level
        int nextLevelXP = BASE_XP + (currentLevel * INCREMENT_XP);

        // Format newXP and nextLevelXP to have two decimal places when displayed
        String formattedNewXP = df.format(newXP);
        String formattedNextLevelXP = df.format(nextLevelXP);

        // Show the current and required XP in the Action Bar
        player.sendActionBar(ChatColor.AQUA + "XP: " + formattedNewXP + "/" + formattedNextLevelXP);

        // Play a sound when gaining XP
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.0f);

        playerXP.put(playerId, newXP);
    }

    public static int getBaseXP() {
        return BASE_XP;
    }

    public static int getIncrementXP() {
        return INCREMENT_XP;
    }

    public double getPlayerXP(UUID playerId) {
        return playerXP.getOrDefault(playerId, 0.0);
    }

    public void setPlayerXP(UUID playerId, double xp) {
        playerXP.put(playerId, xp);
    }

    public int getPlayerLevel(UUID playerId) {
        return playerLevels.getOrDefault(playerId, 0);
    }

    public void setPlayerLevel(UUID playerId, int level) {
        playerLevels.put(playerId, level);
    }

    public Map<UUID, Integer> getAllPlayerLevels() {
        return new HashMap<>(playerLevels);
    }

    public void clearAllPlayerLevels() {
        playerLevels.clear();
    }

    public void clearAllPlayerXP() {
        playerXP.clear();
    }

    public String getPlayerName(UUID playerId) {
        return playerNames.get(playerId);
    }

    public Map<UUID, String> getAllPlayerNames() {
        return new HashMap<>(playerNames);
    }

    public void stop() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException ex) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}