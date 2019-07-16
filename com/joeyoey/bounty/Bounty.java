package com.joeyoey.bounty;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.joeyoey.bounty.commands.Commands;
import com.joeyoey.bounty.events.DeathEvent;
import com.joeyoey.bounty.events.InventoryClick;
import com.joeyoey.bounty.serialutil.SerialUtil;

import net.milkbowl.vault.economy.Economy;

public class Bounty extends JavaPlugin {

	private HashMap<UUID, Double> playerBounty = new HashMap<UUID, Double>();
	private File file;
	private Economy economy = null;
    private HashMap<UUID, Double> topPlayers = new LinkedHashMap<UUID, Double>();
    private HashMap<UUID, Inventory> invs = new HashMap<UUID, Inventory>();

	
	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager()
				.getRegistration(Economy.class);
		if (economyProvider != null) {
			economy = (Economy) economyProvider.getProvider();
		}
		return economy != null;
	}
	
	
	
	public void onEnable() {
		saveDefaultConfig();
		setupEconomy();
		try {
			createOutBin();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			playerBounty = SerialUtil.readFromFile(file);
		} catch (ClassNotFoundException | IOException e) {
			getLogger().log(Level.INFO, "Thanks for using my plugin, leave a rating if you like it.");
		}
		
		getCommand("bounty").setExecutor(new Commands(this));
		
		
		new BukkitRunnable() {

			@Override
			public void run() {
				topBounties();
			}
			
		}.runTaskTimer(this, 0, 20);
		
		getServer().getPluginManager().registerEvents(new DeathEvent(this), this);
		getServer().getPluginManager().registerEvents(new InventoryClick(this), this);

	}
	
	
	
	public void onDisable() {
		try {
			SerialUtil.writeToFile(playerBounty, file);
		} catch (IOException e) {
			getLogger().log(Level.SEVERE, "There was an error while saving.. PM Joeyoey on spigot!");
		}
	}
	
	
	
	public HashMap<UUID, Double> getPlayerBounty() {
		return playerBounty;
	}



	private void createOutBin() throws IOException {
		file = new File(getDataFolder(), "bounties.bin");
		file.createNewFile();
	}



	public Economy getEconomy() {
		return economy;
	}
	
	public void topBounties() {
		List<Entry<UUID, Double>> list = new LinkedList<HashMap.Entry<UUID, Double>>(getPlayerBounty().entrySet());
		Collections.sort(list, new Comparator<Entry<UUID, Double>>() {

			@Override
			public int compare(Entry<UUID, Double> o1, Entry<UUID, Double> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
			
		});
	
		Collections.reverse(list);
		
		HashMap<UUID, Double> sortedMap = new LinkedHashMap<UUID, Double>();
		for (Entry<UUID, Double> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		setTopPlayers(sortedMap);
	}



	public HashMap<UUID, Double> getTopPlayers() {
		return topPlayers;
	}
	
	

	public HashMap<UUID, Inventory> getInvs() {
		return invs;
	}



	public void setTopPlayers(HashMap<UUID, Double> topPlayers) {
		this.topPlayers = topPlayers;
	}
	
}
