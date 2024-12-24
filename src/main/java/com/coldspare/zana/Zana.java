package com.coldspare.zana;

import com.coldspare.zana.boost.*;
import com.coldspare.zana.gen.*;
import com.coldspare.zana.level.LevelCommands;
import com.coldspare.zana.level.Listeners;
import com.coldspare.zana.level.PlayerLevelExpansion;
import com.coldspare.zana.level.PlayerLevelManager;
import com.coldspare.zana.sell.*;
import com.coldspare.zana.tokens.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Zana extends JavaPlugin {
    private static Economy econ = null;
    private static Zana instance;
    private GeneratorManager generatorManager;
    private GeneratorSlotManager generatorSlotManager;
    private GeneratorDatabaseStorage generatorDatabaseStorage;
    private PlayerLevelManager playerLevelManager;
    private TokenManager tokenManager;
    private BoosterManager boosterManager;
    private SellManager sellManager;
    private GeneratorSlotsExpansion generatorSlotsExpansion;
    private PlayerLevelExpansion playerLevelExpansion;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        Database database = new Database("host", "dbname", "user", "password");
        try {
            database.connect();
        } catch (SQLException e) {
            e.printStackTrace();
            getLogger().severe("Failed to connect to the database.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        generatorDatabaseStorage = new GeneratorDatabaseStorage(database);
        boosterManager = new BoosterManager();
        generatorManager = new GeneratorManager(generatorDatabaseStorage);
        generatorSlotManager = new GeneratorSlotManager(database, generatorManager);
        generatorManager.setGeneratorSlotManager(generatorSlotManager);
        GeneratorCommand generatorCommand = new GeneratorCommand(generatorSlotManager, generatorManager);
        getCommand("generator").setExecutor(generatorCommand);
        getCommand("generator").setTabCompleter(generatorCommand);
        getServer().getPluginManager().registerEvents(new GeneratorListener(generatorManager), this);

        // Register the migrate command
        getCommand("migrate").setExecutor((sender, command, label, args) -> {
            if (!(sender instanceof ConsoleCommandSender)) {
                sender.sendMessage(ChatColor.RED + "This command can only be run from the console.");
                return true;
            }

            // Schedule the migration to run asynchronously
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                Migrator migrator = new Migrator(this, database);
                migrator.migrate();

                sender.sendMessage(ChatColor.GREEN + "Migration completed successfully!");
            });

            return true;
        });

        // Leveling
        playerLevelManager = new PlayerLevelManager(this, generatorSlotManager);
        playerLevelExpansion = new PlayerLevelExpansion(this, playerLevelManager);
        sellManager = new SellManager(playerLevelManager, boosterManager);
        if (!playerLevelExpansion.register()) {
            getLogger().warning("Could not register placeholder expansion");
        }

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            generatorManager.spawnItems();
            //getLogger().info("Attempting to spawn items");
        }, 300L, 300L);

        if (!setupEconomy() ) {
            getLogger().severe("Failed to setup Economy");
            return;
        }

        getLogger().info("Plugin enabled successfully");

        //Bukkit.getScheduler().runTaskAsynchronously(this, this::loadGenerators);

        // join/leave register
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(generatorManager, generatorSlotManager), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(generatorManager), this);

        // Starting backup tasks
        BackupService.startBackupTask(this, "generators.yml");
        BackupService.startBackupTask(this, "slots.yml");
        BackupService.startBackupTask(this, "playerdata.yml");
        BackupService.startBackupTask(this, "tokens.yml");

        // Register the PlaceholderAPI expansion
        generatorSlotsExpansion = new GeneratorSlotsExpansion(generatorManager, generatorSlotManager);
        generatorSlotsExpansion.register();


        // Event Listeners
        Listeners listeners = new Listeners(playerLevelManager);
        getServer().getPluginManager().registerEvents(listeners, this);

        // Selling
        getCommand("sell").setExecutor(new SellCommand());
        getCommand("sellwandgive").setExecutor(new SellWandGiveCommand());
        getServer().getPluginManager().registerEvents(new SellWandListener(), this);

        tokenManager = new TokenManager(this);
        TokenExpansion tokenExpansion = new TokenExpansion(this);
        if (!tokenExpansion.register()) {
            getLogger().warning("Could not register token placeholder expansion");
        }
        getCommand("addtokens").setExecutor(new AddTokensCommand(this));
        getCommand("addtokens").setTabCompleter(new AddTokensCommand(this));
        getCommand("removetokens").setExecutor(new RemoveTokensCommand(this));
        getCommand("removetokens").setTabCompleter(new RemoveTokensCommand(this));

        getCommand("leveltop").setExecutor(new LevelCommands(playerLevelManager));
        getCommand("resetlevels").setExecutor(new LevelCommands(playerLevelManager));
        getCommand("xpremove").setExecutor(new LevelCommands(playerLevelManager));
        getCommand("xpadd").setExecutor(new LevelCommands(playerLevelManager));
        getCommand("leveladd").setExecutor(new LevelCommands(playerLevelManager));
        getCommand("levelremove").setExecutor(new LevelCommands(playerLevelManager));
        getCommand("levelset").setExecutor(new LevelCommands(playerLevelManager));

        getCommand("boosteradd").setExecutor(new BoosterCommandExecutor(boosterManager));
        getCommand("boosterclear").setExecutor(new BoosterCommandExecutor(boosterManager));
        getCommand("boosteradd").setTabCompleter(new BoosterCommandExecutor(boosterManager));
        getCommand("boosterclear").setTabCompleter(new BoosterCommandExecutor(boosterManager));

        new BoosterPlaceholders(boosterManager).register();

        //save gen data every 5 min
        //this.getServer().getScheduler().runTaskTimerAsynchronously(this, generatorManager::saveAll, 6000L, 6000L);
    }

    @Override
    public void onDisable() {
        getLogger().info("Saving generators");
        generatorManager.shutdown();
        playerLevelManager.savePlayerData(false);
        tokenManager.saveTokens();
        playerLevelManager.stop();

        // Unregister the papi expansion
        if (generatorSlotsExpansion != null) {
            generatorSlotsExpansion.unregister();
        }

        getLogger().info("Plugin disabled successfully");
    }

    public synchronized void saveAllPlayerData() {
        for (UUID playerId : generatorManager.getPlayerGenerators().keySet()) {
            generatorManager.savePlayerData(playerId);
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static Zana getInstance() {
        return instance;
    }

    public TokenManager getTokenManager() {
        return tokenManager;
    }

    public BoosterManager getBoosterManager() {
        return boosterManager;
    }

    public SellManager getSellManager() {
        return sellManager;
    }
}
