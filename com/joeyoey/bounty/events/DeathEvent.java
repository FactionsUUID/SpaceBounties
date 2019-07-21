package com.joeyoey.bounty.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import com.joeyoey.bounty.Bounty;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.EconomyResponse;

public class DeathEvent implements Listener {
    private Bounty plugin;
    public DeathEvent(Bounty instance) {
        plugin = instance;
    }
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player killer = e.getEntity().getKiller();
        Player dead = e.getEntity();

        if (plugin.getPlayerBounty().containsKey(dead.getUniqueId())) {
            double moneyToGive = plugin.getPlayerBounty().get(dead.getUniqueId());
            plugin.getPlayerBounty().put(dead.getUniqueId(), 0.0);
            plugin.getTopPlayers().put(dead.getUniqueId(), 0.0);
            EconomyResponse er = plugin.getEconomy().depositPlayer(killer, moneyToGive);
            if (er.transactionSuccess()) {
                killer.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.bounty-aquired").replaceAll("%dead%", dead.getName()).replaceAll("%amount%", moneyToGive + "")));
                dead.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.lost-bounty")));
            }
        }

    }

}
