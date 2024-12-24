package com.coldspare.zana.gen;

import com.coldspare.zana.Zana;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.UUID;

public class Generator {
    private final UUID ownerId;
    private final Location location;
    private GeneratorType type;
    private int slot;

    public Generator(UUID ownerId, GeneratorType type, Location location, int slot) {
        this.ownerId = ownerId;
        this.type = type;
        this.location = location;
        this.slot = slot;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public Location getLocation() {
        return location;
    }

    public GeneratorType getType() {
        return type;
    }

    public void setType(GeneratorType type) {
        this.type = type;
    }

    public int getSlot() {
        return slot;
    }

    public boolean isChunkLoaded() {
        World world = location.getWorld();
        if (world != null) {
            int chunkX = location.getBlockX() >> 4;
            int chunkZ = location.getBlockZ() >> 4;
            return world.isChunkLoaded(chunkX, chunkZ);
        }
        return false;
    }

    public void spawnItem() {
        if (location.getBlock().getType() != type.getBlockMaterial()) {
            return;
        }

        // Don't spawn items if chunk is not loaded
        if (!isChunkLoaded()) {
            return;
        }

        Location dropLocation = location.clone().add(0.5, 1, 0.5);
        World world = location.getWorld();
        if (world == null) {
            return;
        }

        // Run on main server thread
        Bukkit.getScheduler().runTask(Zana.getInstance(), () -> {
            Item droppedItem = world.dropItem(dropLocation, new ItemStack(type.getItemType()));
            // Set item velocity to zero
            droppedItem.setVelocity(new Vector(0, 0, 0));
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Generator generator = (Generator) o;
        return Objects.equals(ownerId, generator.ownerId) &&
                Objects.equals(type, generator.type) &&
                Objects.equals(location, generator.location) &&
                slot == generator.slot;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ownerId, type, location, slot);
    }
}
