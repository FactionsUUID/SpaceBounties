package com.joeyoey.bounty.commands;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.joeyoey.bounty.Bounty;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.EconomyResponse;

public class Commands implements CommandExecutor, TabCompleter {

    private Bounty plugin;

    public Commands(Bounty instance) {
        plugin = instance;
    }

    @Override
    public List < String > onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (cmd.getName().equalsIgnoreCase("bounty")) {

                if (args.length > 0) {

                    if (args[0].equalsIgnoreCase("view")) {

                        if (args.length >= 1) {

                            if (args.length == 2) {
                                String name = args[1];
                                try {
                                    OfflinePlayer offP = Bukkit.getOfflinePlayer(name);
                                    buildInvViewOne(offP, p);
                                } catch (NullPointerException e) {
                                    p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                        plugin.getConfig().getString("messages.player-doesnt-exist")));
                                }
                            } else if (args.length == 1) {
                                buildInvGUITop(p);
                            }
                        } else {
                            plugin.getConfig().getStringList("messages.view-help").forEach(s -> {
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
                            });
                        }
                    } else if (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("add")) {
                        if (args.length > 1) {
                            try {
                                OfflinePlayer off = Bukkit.getOfflinePlayer(args[1]);
                                Double bounty = Double.parseDouble(args[2]);
                                EconomyResponse er = plugin.getEconomy().withdrawPlayer(p, bounty);
                                if (er.transactionSuccess()) {
                                    if (bounty > plugin.getConfig().getInt("settings.min-amount") &&
                                        bounty < plugin.getConfig().getInt("settings.max-amount")) {
                                        if (plugin.getPlayerBounty().containsKey(off.getUniqueId())) {
                                            plugin.getPlayerBounty().put(off.getUniqueId(),
                                                plugin.getPlayerBounty().get(off.getUniqueId()) + bounty);
                                            p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                plugin.getConfig().getString("messages.bounty-success")
                                                .replaceAll("%player%", off.getName())
                                                .replaceAll("%amount%", numberDecFormatter(bounty))
                                                .replaceAll("%setter%", sender.getName())));
                                            if (plugin.getConfig().getBoolean("settings.broadcast-bounty")) {
                                                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                                                    plugin.getConfig().getString("messages.bounty-broadcast")
                                                    .replaceAll("%setter%", sender.getName())
                                                    .replaceAll("%player%", off.getName())
                                                    .replaceAll("%amount%", bounty + "")
                                                    .replaceAll("%total%", numberDecFormatter(plugin
                                                        .getPlayerBounty().get(off.getUniqueId())))));
                                            }
                                        } else {
                                            plugin.getPlayerBounty().put(off.getUniqueId(), bounty);
                                            p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                plugin.getConfig().getString("messages.bounty-success")
                                                .replaceAll("%player%", off.getName())
                                                .replaceAll("%amount%", numberDecFormatter(bounty))));
                                            if (plugin.getConfig().getBoolean("settings.broadcast-bounty")) {
                                                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                                                    plugin.getConfig().getString("messages.bounty-new-broadcast")
                                                    .replaceAll("%player%", off.getName())
                                                    .replaceAll("%amount%", bounty + "")
                                                    .replaceAll("%total%", "")
                                                    .replaceAll("%setter%", sender.getName())));
                                            }
                                        }
                                    } else {
                                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig()
                                            .getString("messages.bounty-fail")
                                            .replaceAll("%min%", numberDecFormatter(
                                                (double) plugin.getConfig().getInt("settings.min-amount")))
                                            .replaceAll("%max%", numberDecFormatter(
                                                (double) plugin.getConfig().getInt("settings.max-amount")))));
                                    }
                                } else {
                                    p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                        plugin.getConfig().getString("messages.no-money")));
                                }
                            } catch (Exception e) {
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    plugin.getConfig().getString("messages.error")));
                            }
                        } else {
                            plugin.getConfig().getStringList("messages.set-help").forEach(s -> {
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
                            });
                        }
                    } else if (args[0].equalsIgnoreCase("help")) {
                        plugin.getConfig().getStringList("messages.help").forEach(s -> {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
                        });
                    } else {
                        plugin.getConfig().getStringList("messages.help").forEach(s -> {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
                        });
                    }
                } else {
                    plugin.getConfig().getStringList("messages.help").forEach(s -> {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
                    });
                }
            }
        } else {
            return false;
        }
        return false;
    }

    public void buildInvGUITop(Player viewer) {
        Inventory inv = Bukkit.createInventory(null, plugin.getConfig().getInt("settings.inv-size"),
            ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("settings.inv-name")));
        viewer.sendMessage(
            ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.loading")));

        ItemStack fill = new ItemStack(
            Material.getMaterial(plugin.getConfig().getString("settings.filler-material").toUpperCase()), 1,
            (short) plugin.getConfig().getInt("settings.filler-data"));
        ItemMeta fillM = fill.getItemMeta();
        fillM.setDisplayName(" ");
        fill.setItemMeta(fillM);

        for (int i = 0; i < plugin.getConfig().getInt("settings.inv-size"); i++) {
            inv.setItem(i, fill);
        }

        HashMap < UUID, Double > sorted = plugin.getTopPlayers();

        plugin.getConfig().getStringList("settings.slots");

        int[] rank = {
            1
        };
        sorted.forEach((k, v) -> {
            OfflinePlayer p = Bukkit.getOfflinePlayer(k);
            int slot = Integer.parseInt(plugin.getConfig().getStringList("settings.slots").get(rank[0] - 1));
            ItemStack ownerSkull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            SkullMeta meta = (SkullMeta) ownerSkull.getItemMeta();

            if (p.isOnline()) {

            }

            try {
                if (p.isOnline()) {
                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                        plugin.getConfig().getString("BountyDisplayItem.Name").replaceAll("%name%", p.getName())));
                } else {
                    String name = ChatColor.translateAlternateColorCodes('&',
                        plugin.getConfig().getString("BountyDisplayItem.Name").replaceAll("%name%", p.getName()));
                    String newName = ChatColor.stripColor(name);
                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c&l" + newName));
                }
                meta.setOwner(p.getName());
                List < String > lore = new ArrayList < String > ();

                String[] status = {
                    ChatColor.translateAlternateColorCodes('&',
                        plugin.getConfig().getString("BountyDisplayItem.Status.Offline"))
                };
                if (p.isOnline()) {
                    status[0] = ChatColor.translateAlternateColorCodes('&',
                        plugin.getConfig().getString("BountyDisplayItem.Status.Online"));
                }
                plugin.getConfig().getStringList("BountyDisplayItem.Lore").forEach(s -> {
                    lore.add(ChatColor.translateAlternateColorCodes('&', s.replaceAll("%amount%", numberDecFormatter(v))
                        .replaceAll("%rank%", rank[0] + "").replaceAll("%status%", status[0])));
                });
                meta.setLore(lore);
                ownerSkull.setItemMeta(meta);
                inv.setItem(slot, ownerSkull);
            } catch (NullPointerException e) {
                meta.setDisplayName("???");
                ownerSkull.setItemMeta(meta);
                inv.setItem(slot, ownerSkull);

            }
            rank[0]++;
        });
        viewer.openInventory(inv);
        plugin.getInvs().put(viewer.getUniqueId(), inv);
    }

    public void buildInvViewOne(OfflinePlayer player, Player viewer) {

        if (plugin.getTopPlayers().containsKey(player.getUniqueId())) {
            viewer.sendMessage(
                ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.loading")));
            Inventory inv = Bukkit.createInventory(null, InventoryType.DISPENSER,
                ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("settings.single-inv-name")
                    .replaceAll("%name%", player.getName())));

            ItemStack fill = new ItemStack(
                Material.getMaterial(plugin.getConfig().getString("settings.filler-material").toUpperCase()), 1,
                (short) plugin.getConfig().getInt("settings.filler-data"));
            ItemMeta fillM = fill.getItemMeta();
            fillM.setDisplayName(" ");
            fill.setItemMeta(fillM);

            for (int i = 0; i < 9; i++) {
                inv.setItem(i, fill);
            }

            ItemStack ownerSkull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            SkullMeta meta = (SkullMeta) ownerSkull.getItemMeta();
            if (player.isOnline()) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("BountyDisplayItem.Name").replaceAll("%name%", player.getName())));
            } else {
                String name = ChatColor.RED + ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("BountyDisplayItem.Name").replaceAll("%name%", player.getName()));
                String newName = ChatColor.stripColor(name);
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c&l" + newName));
            }
            meta.setOwner(player.getName());
            List < String > lore = new ArrayList < String > ();
            String[] status = {
                ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("BountyDisplayItem.Status.Offline"))
            };
            if (player.isOnline()) {
                status[0] = ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("BountyDisplayItem.Status.Online"));
            }
            plugin.getConfig().getStringList("BountyDisplayItem.Lore").forEach(s -> {
                lore.add(
                    ChatColor
                    .translateAlternateColorCodes(
                        '&', s
                        .replaceAll("%amount%",
                            numberDecFormatter(
                                plugin.getPlayerBounty().get(player.getUniqueId())))
                        .replaceAll("%status%", status[0])
                        .replaceAll("%rank%", fetchRank(player.getUniqueId()) + "")));
            });
            meta.setLore(lore);
            ownerSkull.setItemMeta(meta);
            inv.setItem(4, ownerSkull);
            viewer.openInventory(inv);
            plugin.getInvs().put(viewer.getUniqueId(), inv);
        } else {
            viewer.sendMessage(ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("messages.player-no-bounty")));
        }
    }

    public int fetchRank(UUID id) {
        int i = 1;
        for (UUID value: plugin.getTopPlayers().keySet()) {
            if (value.equals(id)) {
                return i;
            }
            i++;
        }
        return -999;
    }

    public String numberDecFormatter(Double v) {
        DecimalFormat mFormatter = new DecimalFormat("###,###,###,###.##");
        String output = mFormatter.format(v);
        return output;
    }
}
