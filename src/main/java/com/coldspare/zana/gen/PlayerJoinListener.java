package com.coldspare.zana.gen;

import com.coldspare.zana.Zana;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerJoinListener implements Listener {
    private final GeneratorManager generatorManager;
    private final GeneratorSlotManager generatorSlotManager;

    public PlayerJoinListener(GeneratorManager generatorManager, GeneratorSlotManager generatorSlotManager) {
        this.generatorManager = generatorManager;
        this.generatorSlotManager = generatorSlotManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(Zana.getInstance(), () -> {
            UUID playerId = event.getPlayer().getUniqueId();
            if (!generatorSlotManager.containsSlot(playerId)) {
                generatorSlotManager.addSlots(playerId, 25); // Assign 25 slots when player first join
            }
            generatorManager.loadAndAssignGenerators(playerId);
        });
    }
}