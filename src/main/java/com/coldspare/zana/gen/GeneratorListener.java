package com.coldspare.zana.gen;

import com.coldspare.zana.Zana;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;

public class GeneratorListener implements Listener {

    private final GeneratorManager generatorManager;

    public GeneratorListener(GeneratorManager generatorManager) {
        this.generatorManager = generatorManager;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        //Bukkit.getLogger().info("onBlockPlace called, player: " + player.getName() + " block: " + block.getType() + " at location: " + block.getLocation());

        ItemStack itemInHand = event.getItemInHand();
        // Check if the item is a generator
        GeneratorType type = null;
        if (itemInHand != null) {
            type = getGeneratorTypeFromItem(itemInHand);
        }

        Location location = block.getLocation();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(location));

        if (!set.testState(WorldGuardPlugin.inst().wrapPlayer(player), Flags.BUILD)) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "You cannot place this here!"));
            event.setCancelled(true);
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }

        if(type != null) {
            // Check for cooldown
            long lastPlacementTime = generatorManager.getLastPlacementTime(player.getUniqueId());
            long currentTime = System.currentTimeMillis();

            if (currentTime - lastPlacementTime < GeneratorManager.PLACE_PICKUP_COOLDOWN) {
                //player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "You are placing generators too fast!"));
                event.setCancelled(true);
                //player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return;
            }

            // Check if player has enough generator slots
            if (!generatorManager.hasGeneratorSlot(player)) {
                event.setCancelled(true);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "You do not have enough generator slots!"));
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return;
            }


            // Check if generator already exists at location
            if(generatorManager.getGeneratorByLocation(location) != null) {
                event.setCancelled(true);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "A generator already exists at this location!"));
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return;
            }

            // Add the generator to the manager only if all the conditions are met and block placement is successful
            int slot = generatorManager.getNextAvailableSlot(player);
            Generator generator = new Generator(player.getUniqueId(), type, block.getLocation(), slot);

            // Scheduling a check if the block was placed successfully
            Bukkit.getScheduler().runTaskLater(Zana.getInstance(), () -> {
                // Confirm that the block was placed and the event was not cancelled
                if (!event.isCancelled() && block.getWorld().getBlockAt(block.getLocation()).getType() == block.getType()) {
                    generatorManager.addGenerator(generator.getOwnerId(), generator);
                    // Update placement time
                    generatorManager.setLastPlacementTime(player.getUniqueId(), currentTime);
                    //Bukkit.getLogger().info("Block was placed successfully. Adding generator.");
                } else {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "You cannot place this here!"));
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    //Bukkit.getLogger().info("Block was not placed. Event was cancelled or block type was changed.");
                }
            }, 1L);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        //Zana.getInstance().getLogger().info("PlayerInteractEvent triggered");
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        Action action = event.getAction();

        if (block == null) {
            return;
        }

        // Check if the player interacted with a generator block
        Generator generator = generatorManager.getGeneratorByLocation(block.getLocation());

        if(generator != null) {
            long lastUpgradeTime = generatorManager.getLastUpgradeTime(player.getUniqueId());
            long currentTime = System.currentTimeMillis();

            // Cooldown check for upgrade
            if (currentTime - lastUpgradeTime < GeneratorManager.UPGRADE_COOLDOWN) {
                //player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "You are upgrading too fast!"));
                event.setCancelled(true);
                //player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return;
            }

            // Check if the player is the owner of the generator
            if (!generator.getOwnerId().equals(player.getUniqueId())) {
                event.setCancelled(true);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "This generator does not belong to you!"));
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return;
            }

            if(action == Action.LEFT_CLICK_BLOCK) {
                long lastPickupTime = generatorManager.getLastPickupTime(player.getUniqueId());
                long pickupTime = System.currentTimeMillis();

                if (pickupTime - lastPickupTime < GeneratorManager.PLACE_PICKUP_COOLDOWN) {
                    //player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "You are picking up generators too fast!"));
                    event.setCancelled(true);
                    //player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    return;
                }

                // Play sound effect for picking up a generator
                player.playSound(block.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);

                // Remove the generator and give it to the player
                generatorManager.removeGenerator(player.getUniqueId(), generator);

                // Break the generator block
                block.setType(Material.AIR);
                block.breakNaturally();

                // Add the generator item to the player's inventory
                player.getInventory().addItem(generator.getType().toItemStack());

                // Update placement time
                generatorManager.setLastPickupTime(player.getUniqueId(), pickupTime);
            }
            else if(action == Action.RIGHT_CLICK_BLOCK && player.isSneaking()) {

                if (!generator.getOwnerId().equals(player.getUniqueId())) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "This generator does not belong to you!"));
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    return;
                }
                GeneratorType nextType = generator.getType().getNextType();
                if (nextType == null) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED +  "Your generator is already at the highest level!"));
                    return;
                }

                // Check if the player has enough money to upgrade the generator
                if (!hasEnoughMoney(player, nextType.getPrice())) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "You do not have enough money to upgrade this generator!"));
                    return;
                }

                // Deduct the money from the player
                EconomyResponse r = Zana.getEconomy().withdrawPlayer(player, generator.getType().getNextType().getPrice());
                if (!r.transactionSuccess()) {
                    player.sendMessage(ChatColor.RED + "An error occurred while upgrading your generator!");
                    return;
                }

                // Upgrade the generator
                if (!generatorManager.upgradeGenerator(player, generator)) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Your generator is already at the highest level!"));
                }
                else {
                    // Replace the block with the upgraded one
                    block.setType(nextType.getBlockMaterial());
                    //Zana.getInstance().getLogger().info("Generator upgraded by " + player.getName());

                    // After upgrading successfully, update the last upgrade time.
                    generatorManager.setLastUpgradeTime(player.getUniqueId(), currentTime);
                }
            }
        }
    }

    private GeneratorType getGeneratorTypeFromItem(ItemStack item) {
        for (GeneratorType type : GeneratorType.values()) {
            if (itemHasTag(item, type.getTag())) {
                return type;
            }
        }
        return null;
    }

    private boolean itemHasTag(ItemStack item, String tag) {
        if (item.hasItemMeta()) {
            PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(Zana.getInstance(), tag);
            if (container.has(key, PersistentDataType.STRING)) {
                return container.get(key, PersistentDataType.STRING).equals(tag);
            }
        }
        return false;
    }

    private boolean hasEnoughMoney(Player player, double amount) {
        return Zana.getEconomy().has(player, amount);
    }
}