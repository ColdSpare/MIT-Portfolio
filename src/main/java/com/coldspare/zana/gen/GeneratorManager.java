package com.coldspare.zana.gen;

import com.coldspare.zana.Zana;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.bukkit.World;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import me.clip.placeholderapi.PlaceholderAPI;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class GeneratorManager {
    private final ConcurrentHashMap<UUID, List<Generator>> playerGenerators = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Location, Generator> locationToGenerator = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, List<Generator>> queuedGenerators = new ConcurrentHashMap<>();

    // db operations
    private ExecutorService dbExecutorService = Executors.newCachedThreadPool();

    private ExecutorService taskExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final GeneratorDatabaseStorage generatorDatabaseStorage;
    private final ConcurrentHashMap<UUID, Long> lastUpgradeTimes = new ConcurrentHashMap<>();
    public static final long UPGRADE_COOLDOWN = 100;
    private GeneratorSlotManager generatorSlotManager;
    private ConcurrentHashMap<UUID, Long> lastPlacementTimes = new ConcurrentHashMap<>();
    private ConcurrentHashMap<UUID, Long> lastPickupTimes = new ConcurrentHashMap<>();
    public static final long PLACE_PICKUP_COOLDOWN = 100;
    private final ScheduledExecutorService scheduledExecutorService;

    public GeneratorManager(GeneratorDatabaseStorage generatorDatabaseStorage) {
        this.generatorDatabaseStorage = generatorDatabaseStorage;
        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
        // saving gens every 5 minutes
        this.scheduledExecutorService.scheduleAtFixedRate(this::saveAllGenerators, 5, 5, TimeUnit.MINUTES);
    }

    public long getLastUpgradeTime(UUID playerId) {
        return lastUpgradeTimes.getOrDefault(playerId, 0L);
    }
    public void setLastUpgradeTime(UUID playerId, long time) {
        lastUpgradeTimes.put(playerId, time);
    }

    private void saveAllGenerators() {
        playerGenerators.values().forEach(generatorDatabaseStorage::saveGenerators);
        Bukkit.getLogger().info("All generators saved successfully!");
    }

    public void shutdown() {
        synchronized(playerGenerators) {
            synchronized(locationToGenerator) {
                // Fetch all the generators for each player from the database first
                ConcurrentHashMap<UUID, List<Generator>> dbGenerators = new ConcurrentHashMap<>();
                playerGenerators.keySet().forEach(playerId -> {
                    CompletableFuture<List<Generator>> generators = generatorDatabaseStorage.loadGeneratorsForOwner1(playerId);
                    dbGenerators.put(playerId, generators.join());
                });

                // Compare the generators in memory with the ones in the database and collect the missing ones
                List<Generator> missingGenerators = new ArrayList<>();
                playerGenerators.forEach((playerId, generators) -> {
                    List<Generator> generatorsInDB = dbGenerators.getOrDefault(playerId, Collections.emptyList());
                    generators.stream().filter(generator -> !generatorsInDB.contains(generator)).forEach(missingGenerators::add);
                });

                // Save the missing generators synchronously
                if (!missingGenerators.isEmpty()) {
                    generatorDatabaseStorage.saveGenerators1(missingGenerators);
                }
            }
        }

        // Shut down the executor service
        taskExecutorService.shutdown();
        try {
            taskExecutorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        // save the generators to database
        List<Generator> allGenerators = new ArrayList<>();
        for (List<Generator> generators : playerGenerators.values()) {
            allGenerators.addAll(generators);
        }
        generatorDatabaseStorage.saveGenerators(allGenerators);
    }

    public void setGeneratorSlotManager(GeneratorSlotManager generatorSlotManager) {
        this.generatorSlotManager = generatorSlotManager;
    }

    public Generator getGeneratorByLocation(Location location) {
        return locationToGenerator.get(location);
    }

    public void addGenerator(UUID ownerId, Generator generator) {
        taskExecutorService.submit(() -> {
            if (locationToGenerator.putIfAbsent(generator.getLocation(), generator) != null) {
                Bukkit.getLogger().info("Generator at location " + generator.getLocation().toString() + " already exists.");
                return;
            }

            playerGenerators.compute(ownerId, (key, val) -> {
                List<Generator> newList = val == null ? new ArrayList<>() : new ArrayList<>(val);
                newList.add(generator);
                return newList;
            });

            Bukkit.getScheduler().runTask(Zana.getInstance(), () -> {
                generatorSlotManager.saveSlots();
                updatePlayerSlotsUsedMessage(ownerId);
            });
        });
    }

    public void removeGenerator(UUID playerId, Generator generator) {
        dbExecutorService.submit(() -> {
            if (locationToGenerator.remove(generator.getLocation(), generator)) {
                playerGenerators.computeIfPresent(playerId, (key, list) -> {
                    List<Generator> newList = new ArrayList<>(list);
                    newList.remove(generator);
                    return newList.isEmpty() ? null : newList;
                });

                generatorSlotManager.saveSlots();
                updatePlayerSlotsUsedMessage(playerId);
            } else {
                Bukkit.getLogger().info("Generator at location " + generator.getLocation().toString() + " does not exist.");
            }
        });
    }

    private void updatePlayerSlotsUsedMessage(UUID playerId) {
        Player player = Bukkit.getPlayer(playerId);
        if (player != null && player.isOnline()) {
            int usedSlots = getGeneratorCount(playerId);
            int totalSlots = generatorSlotManager.getTotalSlots(player);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "" + ChatColor.BOLD + "GENS " + ChatColor.GRAY + "» " + ChatColor.WHITE + "Slots used: " + usedSlots + "/" + totalSlots));
        }
    }

    public boolean upgradeGenerator(Player player, Generator generator) {
        GeneratorType nextType = generator.getType().getNextType();
        if (nextType != null) {
            generator.setType(nextType);

            // Save the upgraded generator immediately
            generatorDatabaseStorage.saveGenerators(Arrays.asList(generator));
            return true;
        }
        return false;
    }

    public void spawnItems() {
        taskExecutorService.submit(() -> {
            Map<UUID, List<Generator>> clonedMap = new HashMap<>(playerGenerators);
            for (UUID playerId : clonedMap.keySet()) {
                Player player = Bukkit.getPlayer(playerId);
                if (player != null && player.isOnline() && player.getWorld().getName().equals("plots")) {
                    List<Generator> generators = clonedMap.get(playerId);
                    Bukkit.getScheduler().runTask(Zana.getInstance(), () -> {
                        for (Generator generator : generators) {
                            if (generator.isChunkLoaded()) {
                                generator.spawnItem();
                            }
                        }
                    });
                }
            }
        });
    }

    public boolean hasGeneratorSlot(Player player) {
        return generatorSlotManager.hasGeneratorSlot(player);
    }

    public int getNextAvailableSlot(Player player) {
        return generatorSlotManager.getNextAvailableSlot(player);
    }

    public int getGeneratorCount(UUID playerId) {
        return playerGenerators.getOrDefault(playerId, Collections.emptyList()).size();
    }

    public void loadAndAssignGenerators(UUID ownerId) {
        if (playerGenerators.containsKey(ownerId)) {
            return;
        }
        List<Generator> generators = generatorDatabaseStorage.loadGeneratorsForOwner(ownerId).join();
        if(generators != null) {
            for(Generator generator : generators) {
                addGenerators(ownerId, generator);
                //Bukkit.getServer().getLogger().info("Loaded generator for player " + ownerId + " at location " + generator.getLocation());
            }
        }
    }

    public void addGenerators(UUID ownerId, Generator generator) {
        taskExecutorService.submit(() -> {
            if (locationToGenerator.containsKey(generator.getLocation())) {
                Bukkit.getLogger().info("Generator at location " + generator.getLocation().toString() + " already exists.");
                return;
            }

            if(locationToGenerator.putIfAbsent(generator.getLocation(), generator) != null) {
                return;
            }

            playerGenerators.compute(ownerId, (key, val) -> {
                if (val == null) {
                    val = new ArrayList<>();
                }
                val.add(generator);
                return val;
            });

            generatorSlotManager.saveSlots();
            updatePlayerSlotsUsedMessage(ownerId);
        });
    }


    public long getLastPlacementTime(UUID playerId) {
        return lastPlacementTimes.getOrDefault(playerId, 0L);
    }

    public void setLastPlacementTime(UUID playerId, long time) {
        lastPlacementTimes.put(playerId, time);
    }

    public long getLastPickupTime(UUID playerId) {
        return lastPickupTimes.getOrDefault(playerId, 0L);
    }

    public void setLastPickupTime(UUID playerId, long time) {
        lastPickupTimes.put(playerId, time);
    }

    public synchronized void savePlayerData(UUID playerId) {
        // Fetch the generators for the player from the database first
        CompletableFuture<List<Generator>> dbGeneratorsFuture = generatorDatabaseStorage.loadGeneratorsForOwner(playerId);
        List<Generator> dbGenerators = dbGeneratorsFuture.join();

        // Get the generators in memory for the player
        List<Generator> playerGenerators = this.playerGenerators.getOrDefault(playerId, Collections.emptyList());

        // Compare the generators in memory with the ones in the database and collect the missing ones
        List<Generator> missingGenerators = playerGenerators.stream()
                .filter(generator -> !dbGenerators.contains(generator))
                .collect(Collectors.toList());

        // Save the missing generators asynchronously
        if (!missingGenerators.isEmpty()) {
            System.out.println("Resaving " + missingGenerators.size() + " generators that were missing in the database.");
            generatorDatabaseStorage.saveGenerators(missingGenerators);
        }
    }

    public void resetPlayerGenerators(UUID playerId) {
        // Remove from memory
        List<Generator> generators = playerGenerators.remove(playerId);
        if (generators != null) {
            for (Generator generator : generators) {
                locationToGenerator.remove(generator.getLocation());
            }
        }

        // Remove generators in file
        generatorDatabaseStorage.removeGeneratorsForOwner(playerId);
    }

    public Map<UUID, List<Generator>> getPlayerGenerators() {
        return playerGenerators;
    }
}