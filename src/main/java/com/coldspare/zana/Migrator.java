package com.coldspare.zana;

import com.coldspare.zana.gen.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Migrator {

    private final GeneratorFileStorage fileStorage;
    private final GeneratorDatabaseStorage databaseStorage;

    public Migrator(JavaPlugin plugin, Database database) {
        fileStorage = new GeneratorFileStorage(plugin);
        databaseStorage = new GeneratorDatabaseStorage(database);
    }

    public void migrate() {
        final int batchSize = 100;
        final long delay = 100;  // Delay in milliseconds

        // Migrate generators
        List<Generator> generators = fileStorage.loadGenerators();
        int totalGenerators = generators.size();
        int totalBatches = (int) Math.ceil((double) totalGenerators / batchSize);
        for (int i = 0; i < totalBatches; i++) {
            int start = i * batchSize;
            int end = Math.min(start + batchSize, totalGenerators);
            List<Generator> batch = generators.subList(start, end);
            databaseStorage.saveGenerators(batch);
            Bukkit.getLogger().info("Migrated batch " + (i + 1) + "/" + totalBatches + " generators.");
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Migrate generator slots
        Map<UUID, Integer> slots = fileStorage.loadGeneratorSlots();
        int totalSlots = slots.size();
        int batchNumber = 0;
        Map<UUID, Integer> batch = new HashMap<>();
        for (Map.Entry<UUID, Integer> entry : slots.entrySet()) {
            batch.put(entry.getKey(), entry.getValue());
            if (batch.size() == batchSize || ++batchNumber == totalSlots) {
                databaseStorage.saveGeneratorSlots(new HashMap<>(batch));
                batch.clear();
                Bukkit.getLogger().info("Migrated " + batchNumber + "/" + totalSlots + " generator slots.");
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}