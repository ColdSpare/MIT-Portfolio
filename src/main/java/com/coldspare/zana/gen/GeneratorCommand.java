package com.coldspare.zana.gen;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GeneratorCommand implements CommandExecutor, TabCompleter {
    private final GeneratorSlotManager generatorSlotManager;
    private final GeneratorManager generatorManager;

    public GeneratorCommand(GeneratorSlotManager generatorSlotManager, GeneratorManager generatorManager) {
        this.generatorSlotManager = generatorSlotManager;
        this.generatorManager = generatorManager;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("addslots");
            completions.add("removeslots");
            completions.add("getslots");
            completions.add("give");
            completions.add("reset");
        } else if (args.length == 2) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("give")) {
                for (GeneratorType type : GeneratorType.values()) {
                    completions.add(type.name().toLowerCase());
                }
            }
        }
        return completions;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("zana.generator")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }
        if (args.length < 1) {
            return false; // usage msg
        }

        Player target;

        switch (args[0].toLowerCase()) {
            case "addslots":
            case "removeslots":
            case "getslots":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /generator " + args[0] + " <player>");
                    return true;
                }
                target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found.");
                    return true;
                }
                if ("addslots".equals(args[0].toLowerCase())) {
                    if (args.length < 3) {
                        sender.sendMessage(ChatColor.RED + "Usage: /generator addslots <player> <count>");
                        return true;
                    }
                    int count = Integer.parseInt(args[2]);
                    generatorSlotManager.addSlots(target.getUniqueId(), count);
                    sender.sendMessage(ChatColor.GREEN + "Added " + count + " generator slots to " + target.getName());
                } else if ("removeslots".equals(args[0].toLowerCase())) {
                    if (args.length < 3) {
                        sender.sendMessage(ChatColor.RED + "Usage: /generator removeslots <player> <count>");
                        return true;
                    }
                    int count = Integer.parseInt(args[2]);
                    generatorSlotManager.removeSlots(target, count);
                    sender.sendMessage(ChatColor.GREEN + "Removed " + count + " generator slots from " + target.getName());
                } else if ("getslots".equals(args[0].toLowerCase())) {
                    UUID targetUUID = target.getUniqueId();
                    int usedSlots = generatorSlotManager.getUsedSlots(targetUUID);
                    int totalSlots = generatorSlotManager.getTotalSlots(target);
                    sender.sendMessage(ChatColor.GREEN + target.getName() + " is using " + usedSlots + "/" + totalSlots + " generator slots.");
                }
                break;
            case "give":
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + "Usage: /generator give <player> <type> [amount]");
                    return true;
                }
                target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found.");
                    return true;
                }
                try {
                    GeneratorType type = GeneratorType.valueOf(args[2].toUpperCase());
                    int amount = args.length > 3 ? Integer.parseInt(args[3]) : 1; // Default to 1 if no amount is specified
                    ItemStack generator = type.toItemStack();
                    generator.setAmount(amount);
                    target.getInventory().addItem(generator);
                    sender.sendMessage(ChatColor.GREEN + "Gave " + target.getName() + " " + amount + " " + type.name() + " generator(s).");
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid generator type or amount.");
                }
                break;
            case "reset":
                if (!sender.hasPermission("zana.generator.reset")) {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /generator reset <player>");
                    return true;
                }
                target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found.");
                    return true;
                }
                if (args.length == 2) {
                    sender.sendMessage(ChatColor.YELLOW + "Are you sure you want to reset " + target.getName() + "'s generators? This action cannot be undone. Type /generator reset " + target.getName() + " confirm to confirm.");
                    return true;
                }
                if (args.length == 3 && args[2].equalsIgnoreCase("confirm")) {
                    generatorManager.resetPlayerGenerators(target.getUniqueId());
                    sender.sendMessage(ChatColor.GREEN + "Successfully reset " + target.getName() + "'s generators.");
                    return true;
                }
                break;
            default: // usage message
        }
        return true;
    }
}