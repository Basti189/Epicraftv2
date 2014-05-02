package de.wolfsline.Banksystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.wolfsline.Epicraft.Epicraft;

public class Bank implements CommandExecutor, Listener{

	private Epicraft plugin;
	private Bankdaten daten;
	
	private HashMap<UUID, Integer> map = new HashMap<UUID, Integer>();
	
	public Bank(Epicraft plugin){
		this.plugin = plugin;
		this.daten = new Bankdaten(plugin);
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) { 
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler");
			return true;
		}
		Player p = (Player) cs;
		if(!p.hasPermission("epicraft.bank")){
			p.sendMessage(plugin.error);
			plugin.api.sendLog("[Epicraft - Bank] " + p.getName() + " versucht auf die Bank zuzugreifen");
			return true;
		}
		if(!isPlayerInBank(p)){
			p.sendMessage(plugin.namespace + ChatColor.RED + "Du kannst von hier aus nicht auf die Bank zugreifen");
			return true;
		}
		if(args.length == 0){ //Wird normal vom Spieler aufgerufen
			openBankInv(p, null);
			return true;
		}
		else if(args.length == 1){//Auf die Bank eines anderen Spieler zugreife
			if(!p.hasPermission("epicraft.bank.team")){
				p.sendMessage(plugin.error);
				plugin.api.sendLog("[Epicraft - Bank] " + p.getName() + " versucht auf die Bank eines anderen Spielers zuzugreifen");
				return true;
			}
		}
		p.sendMessage(plugin.namespace + ChatColor.RED + "/bank");
		return true;
	}
	
	//-----------------------------------Eigene Methoden---------------------------------------//
	
	private void openBankInv(Player p, UUID uuid){
		if(uuid == null){
			int lines = 6;
			Inventory inv = Bukkit.createInventory(null, lines*9, p.getName() + "'s Konto");
			inv.setMaxStackSize(100000000);
			ItemMeta meta = null;
			ItemStack back = new ItemStack(Material.BEACON, 1);//, (short) 11);
			meta = back.getItemMeta();
			meta.setDisplayName("Zurück");
			back.setItemMeta(meta);
	    	ItemStack next = new ItemStack(Material.BEACON, 1);//, (short) 14);
	    	meta = next.getItemMeta();
			meta.setDisplayName("Weiter");
			next.setItemMeta(meta);
	    	ItemStack a1 = new ItemStack(Material.WOOL, 1);
	    	meta = a1.getItemMeta();
			meta.setDisplayName("Anzahl 1");
			a1.setItemMeta(meta);
	    	ItemStack a64 = new ItemStack(Material.WOOL, 64); 
	    	meta = a64.getItemMeta();
			meta.setDisplayName("Anzahl 64");
			a64.setItemMeta(meta);
			ItemStack page = new ItemStack(Material.PAPER, 1);
			meta = page.getItemMeta();
			meta.setDisplayName("Seite");
			List<String> tmp = new ArrayList<String>();
			tmp.add("Ansicht aktualisieren");
			meta.setLore(tmp);
			tmp.clear();
			page.setItemMeta(meta);
	    	inv.setItem(0, back);
	    	inv.setItem(8, next);
	    	inv.setItem(4, page);
	    	inv.setItem(2, a1);
	    	inv.setItem(6, a64);
	    	this.map.put(p.getUniqueId(), 1);
	    	daten.fillBankTable(1, inv, p);
	    	p.openInventory(inv);
		}
		else{
			
		}
	}
	
	private boolean isPlayerInBank(Player p){
		//return true; // <- Zum Test
		if(!p.getLocation().getWorld().equals(Bukkit.getServer().getWorld("Survival")))
			return false;
		 int x = p.getLocation().getBlockX();
		 int z = p.getLocation().getBlockZ();
		 int y = p.getLocation().getBlockY();
		if((x <= 442 && x >= 441) && (z >= -123 && z <= -105) && (y >= 68 && y <= 70))
			return true;
		return false;
	}
	
	//-----------------------------------Event Handler---------------------------------------//

	@EventHandler
	public void onClickInventory(InventoryClickEvent e){
		Player p = (Player) e.getWhoClicked();
		if(!this.map.containsKey(p.getUniqueId()))
			return;
		try{	
			if(e.getSlot() == e.getRawSlot()){
				if(e.getInventory().getTitle().equalsIgnoreCase(p.getName() + "'s Konto")){
					int page = e.getInventory().getItem(4).getAmount();
					if(e.getSlot() >= 0 && e.getSlot() <= 8){
						e.setCancelled(true);
						p.updateInventory();
						if(e.getSlot() == 0){ // page befor
			        		if(page != 1){
			        			e.getInventory().getItem(4).setAmount(--page);
			        			daten.fillBankTable(page, e.getInventory(), p);
			        		}
			        			
			        	}
			        	else if(e.getSlot() == 8){//next page 
			        		if(page != 20){
			        			e.getInventory().getItem(4).setAmount(++page);
			        			daten.fillBankTable(page, e.getInventory(), p);
			        		}
			        			
			        	}
			        	else if(e.getSlot() == 4) // Aktualisieren
			        		daten.fillBankTable(page, e.getInventory(), p);
			        	else if(e.getSlot() == 2){//Anzahl 1
			        		this.map.put(p.getUniqueId(), 1);
			        	}
			        	else if(e.getSlot() == 6){//Anzahl 64
			        		this.map.put(p.getUniqueId(), 64);
			        	}
					}
					else if(e.getInventory().getItem(e.getSlot()) != null){
						e.setCancelled(true);
						int amount = this.map.get(p.getUniqueId());
						daten.removeItemFromBankAndGiveItToUser(p, e.getInventory(), e.getSlot(), amount);
					}
				}
			}
			else{
				if(e.getInventory().getTitle().equalsIgnoreCase(p.getName() + "'s Konto")){
					e.setCancelled(true);
					if(p.getInventory().getItem(e.getSlot()) != null){
						int amount = this.map.get(p.getUniqueId());
						daten.removeItemFromUserAndGiveItToBank(p, e.getSlot(), amount);
					}
				}
			}
		}
		catch(IndexOutOfBoundsException e1){
			
		}
	}
	
	@EventHandler
	public void onQuitEvent(PlayerQuitEvent e){
		Player p = e.getPlayer();
		this.map.remove(p.getUniqueId());
	}
	
	
	@EventHandler
	public void OnPickUpItem(PlayerPickupItemEvent event){
		if(map.containsKey(event.getPlayer().getUniqueId()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onCloseInventory(InventoryCloseEvent e){
		Player p = (Player) e.getPlayer();
		if(this.map.containsKey(p.getUniqueId())){
			this.map.remove(p.getUniqueId());
			plugin.api.sendLog("[Epicraft - Bank] " + p.getName() + " hat die Bank verlassen");
		}
	}
	
}
