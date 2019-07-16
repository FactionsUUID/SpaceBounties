package com.joeyoey.bounty.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import com.joeyoey.bounty.Bounty;

public class InventoryClick implements Listener {

	private Bounty plugin;
	
	public InventoryClick(Bounty instance) {
		plugin = instance;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (plugin.getInvs().containsKey(e.getWhoClicked().getUniqueId())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if (plugin.getInvs().containsKey(e.getPlayer().getUniqueId())) {
			plugin.getInvs().remove(e.getPlayer().getUniqueId());
		}
	}
	
	
}
