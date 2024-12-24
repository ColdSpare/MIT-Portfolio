/*package com.coldspare.zana.farming;

import com.coldspare.zana.Zana;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class HoeEventHandler implements Listener {
    private final Zana plugin;

    public HoeEventHandler(Zana plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType().toString().contains("ANVIL")) {
            if (player.getInventory().getItemInMainHand().getType().toString().contains("HOE")) {
                ItemStack hoeItem = player.getInventory().getItemInMainHand();
                Hoe hoe = Hoe.fromItemStack(hoeItem);

                // If the item does not have Hoe metadata, create a new Hoe and attach it to the item
                if (hoe == null) {
                    hoe = new Hoe();
                    hoeItem.getItemMeta().setMetadata(Hoe.METADATA_KEY, new FixedMetadataValue(plugin, hoe));
                }

                hoe.setItemMeta(hoeItem);
                HoeUpgradeGUI gui = new HoeUpgradeGUI(hoe);
                gui.openInventory(player);
            }
        }
    }
}
*/