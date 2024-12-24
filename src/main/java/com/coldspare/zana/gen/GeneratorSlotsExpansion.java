package com.coldspare.zana.gen;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GeneratorSlotsExpansion extends PlaceholderExpansion {
    private final GeneratorManager generatorManager;
    private final GeneratorSlotManager slotManager;

    public GeneratorSlotsExpansion(GeneratorManager generatorManager, GeneratorSlotManager slotManager) {
        this.generatorManager = generatorManager;
        this.slotManager = slotManager;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "generatorslots";
    }

    @Override
    public @NotNull String getAuthor() {
        return "ColdSpare";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return "";
        }

        // %generator_total%
        if ("total".equals(identifier)) {
            return String.valueOf(slotManager.getTotalSlots(player));
        }

        // %generator_used%
        if ("used".equals(identifier)) {
            return String.valueOf(slotManager.getUsedSlots(player.getUniqueId()));
        }

        return null;
    }
}

