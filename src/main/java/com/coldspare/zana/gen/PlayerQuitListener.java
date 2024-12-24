package com.coldspare.zana.gen;

import com.coldspare.zana.Zana;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerQuitListener implements Listener {
    private final GeneratorManager generatorManager;

    public PlayerQuitListener(GeneratorManager generatorManager) {
        this.generatorManager = generatorManager;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(Zana.getInstance(), () -> {

            UUID playerId = event.getPlayer().getUniqueId();
            //generatorManager.handlePlayerLogout(playerId);
        });
    }
}

