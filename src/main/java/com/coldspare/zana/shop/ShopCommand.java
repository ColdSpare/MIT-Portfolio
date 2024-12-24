/*package com.coldspare.zana;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }
        Player player = (Player) sender;

        MainMenu mainMenu = new MainMenu();
        Bukkit.getPluginManager().registerEvents(mainMenu, Zana.getInstance()); // register the MainMenu instance as a listener
        mainMenu.openInventory(player);

        Zana.getInstance().openShop(player, mainMenu);

        return true;
    }
}*/