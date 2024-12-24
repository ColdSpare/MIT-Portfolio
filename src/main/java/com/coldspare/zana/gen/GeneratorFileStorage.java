package com.coldspare.zana.gen;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

// THIS CLASS IS ONLY USED FOR MIGRATION
// YAML --> DATABASE

public class GeneratorFileStorage {

    private final Object saveLock = new Object();

    private final File generatorFile;
    private final FileConfiguration generatorConfig;

    private final File slotFile;
    private final FileConfiguration slotConfig;

    public GeneratorFileStorage(JavaPlugin plugin) {
        generatorFile = new File(plugin.getDataFolder(), "generators.yml");
        slotFile = new File(plugin.getDataFolder(), "slots.yml");
        if (!generatorFile.exists()) {
            try {
                generatorFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!slotFile.exists()) {
            try {
                slotFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        generatorConfig = YamlConfiguration.loadConfiguration(generatorFile);
        slotConfig = YamlConfiguration.loadConfiguration(slotFile);
    }

    public void saveGenerators(List<Generator> generators) {
        synchronized (saveLock) {
            for (Generator generator : generators) {
                if (generator == null || generator.getType() == null || generator.getLocation() == null || generator.getLocation().getWorld() == null) {
                    continue; // skip this iteration if generator or its properties are null
                }

                String path = "generators." + generator.getOwnerId() + "." + generator.getSlot();
                generatorConfig.set(path + ".type", generator.getType().name());
                generatorConfig.set(path + ".location", generator.getLocation().getWorld().getName() + "," + generator.getLocation().getBlockX() + "," + generator.getLocation().getBlockY() + "," + generator.getLocation().getBlockZ());
            }
            try {
                generatorConfig.save(generatorFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Generator> loadGenerators() {
        List<Generator> generators = new ArrayList<>();
        if (generatorConfig.contains("generators")) {
            for (String uuid : generatorConfig.getConfigurationSection("generators").getKeys(false)) {
                for (String slot : generatorConfig.getConfigurationSection("generators." + uuid).getKeys(false)) {
                    String path = "generators." + uuid + "." + slot;
                    String materialName = generatorConfig.getString(path + ".type");
                    Material material = Material.valueOf(materialName);
                    GeneratorType type = GeneratorType.fromMaterial(material);
                    String[] locParts = generatorConfig.getString(path + ".location").split(",");

                    // Ensure that the world is loaded before using it
                    World world = Bukkit.getWorld(locParts[0]);
                    if (world == null) {
                        Bukkit.createWorld(new WorldCreator(locParts[0]));
                        world = Bukkit.getWorld(locParts[0]);
                    }

                    Location location = new Location(world, Integer.parseInt(locParts[1]), Integer.parseInt(locParts[2]), Integer.parseInt(locParts[3]));
                    generators.add(new Generator(UUID.fromString(uuid), type, location, Integer.parseInt(slot)));
                }
            }
        }
        return generators;
    }

    public void saveGeneratorSlots(Map<UUID, Integer> slots) {
        for (Map.Entry<UUID, Integer> entry : slots.entrySet()) {
            slotConfig.set("slots." + entry.getKey(), entry.getValue());
        }
        try {
            slotConfig.save(slotFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<UUID, Integer> loadGeneratorSlots() {
        Map<UUID, Integer> slots = new HashMap<>();
        if (slotConfig.contains("slots")) {
            for (String uuid : slotConfig.getConfigurationSection("slots").getKeys(false)) {
                slots.put(UUID.fromString(uuid), slotConfig.getInt("slots." + uuid));
            }
        }
        return slots;
    }

    public List<Generator> loadGeneratorsForOwner(UUID ownerId) {
        List<Generator> allGenerators = loadGenerators();
        return allGenerators.stream()
                .filter(generator -> generator.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
    }

    public void removeGeneratorsForOwner(UUID ownerId) {
        synchronized (saveLock) {
            // 1. Load all generators from file
            List<Generator> allGenerators = loadGenerators();
            // 2. Remove generators owned by the specific player
            List<Generator> remainingGenerators = allGenerators.stream()
                    .filter(generator -> generator != null && !generator.getOwnerId().equals(ownerId))
                    .collect(Collectors.toList());
            // 3. Clear the generators from the config
            if(generatorConfig.getConfigurationSection("generators") != null) {
                generatorConfig.getConfigurationSection("generators").getKeys(false).forEach(key -> {
                    generatorConfig.set("generators." + key, null);
                });
            }
            // 4. Save the remaining generators
            saveGenerators(remainingGenerators);
        }
    }
}