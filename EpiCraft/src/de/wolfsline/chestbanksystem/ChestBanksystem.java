package de.wolfsline.chestbanksystem;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.data.MySQL;

public class ChestBanksystem implements CommandExecutor, Listener{

	private Epicraft plugin;
	private Economy econ;
	private double priceOUT = 10.0D, priceIN = 2.0D;
	private Banksystem system;
	private HashMap<String, Integer> map = new HashMap<String, Integer>();
	public ChestBanksystem(Epicraft plugin) {
		this.plugin = plugin;
		this.econ = plugin.economy;
		this.system = new Banksystem(plugin);
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler");
			return true;
		}
		Player p = (Player) cs;
		if(!p.hasPermission("epicraft.bank.use")){
			p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast keinen Zugriff auf diesen Befehl!");
			plugin.api.sendLog("[Epicraft - Bank] " + p.getName() + " versucht auf die Bank zuzugreifen");
			return true;
		}
		else if(p.getLocation().getWorld().getName().equalsIgnoreCase("games") || p.getLocation().getWorld().getName().equalsIgnoreCase("plots")){
			p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast in dieser Welt keinen Zugriff auf diesen Befehl!");
			plugin.api.sendLog("[Epicraft - Bank] " + p.getName() + " versucht von der Welt " + p.getLocation().getWorld().getName() + " auf die Bank zuzugreifen");
			return true;
		}
		if(args.length == 0){
			if(isPlayerInBank(p)){
				if(!econ.has(p.getName(), priceIN)){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast nicht genug Geld!");
					return true;
				}
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Zugriff auf dein Konto gewährt");
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Dies kostet dich " + String.valueOf((int) priceIN) + " Coins.");
				plugin.api.sendLog("[Epicraft - Bank] " + p.getName() + " ruft die Bank innerhalb der Bank auf");
				econ.withdrawPlayer(p.getName(), priceIN);
			}
			else{
				if(!econ.has(p.getName(), priceOUT)){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast nicht genug Geld!");
					return true;
				}
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Zugriff auf dein Konto gewährt");
				Bukkit.getServer().getLogger().info(p.getName() + " wurde Zugriff auf die Bank gewährt.");
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Dies kostet dich " + String.valueOf((int) priceOUT) + " Coins.");
				plugin.api.sendLog("[Epicraft - Bank] " + p.getName() + " ruft die Bank außerhalb der Bank auf");
				econ.withdrawPlayer(p.getName(), priceOUT);
			}
			this.openInv(p);
			return true;
		}
		return false;
	}
	
	@EventHandler
	public void onClickInventory(InventoryClickEvent e){
		Player p = (Player) e.getWhoClicked();
		if(!this.map.containsKey(p.getName()))
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
			        			fillBankTable(page, e.getInventory(), p);
			        		}
			        			
			        	}
			        	else if(e.getSlot() == 8){//next page 
			        		if(page != 20){
			        			e.getInventory().getItem(4).setAmount(++page);
			        			fillBankTable(page, e.getInventory(), p);
			        		}
			        			
			        	}
			        	else if(e.getSlot() == 4) // Aktualisieren
			        		fillBankTable(page, e.getInventory(), p);
			        	else if(e.getSlot() == 2){//Anzahl 1
			        		this.map.put(p.getName(), 1);
			        	}
			        	else if(e.getSlot() == 6){//Anzahl 64
			        		this.map.put(p.getName(), 64);
			        	}
					}
					else if(e.getInventory().getItem(e.getSlot()) != null){
						e.setCancelled(true);
						int amount = this.map.get(p.getName());
						removeItemFromBankAndGiveItToUser(p, e.getInventory(), e.getSlot(), amount);
					}
				}
			}
			else{
				if(e.getInventory().getTitle().equalsIgnoreCase(p.getName() + "'s Konto")){
					e.setCancelled(true);
					if(p.getInventory().getItem(e.getSlot()) != null){
						int amount = this.map.get(p.getName());
						removeItemFromUserAndGiveItToBank(p, e.getSlot(), amount);
					}
				}
			}
		}
		catch(IndexOutOfBoundsException e1){
			
		}
	}
	
	@EventHandler
	public void onOpenInventory(InventoryCloseEvent e){
		
	}
	
	@EventHandler
	public void onCloseInventory(InventoryCloseEvent e){
		Player p = (Player) e.getPlayer();
		if(this.map.containsKey(p.getName())){
			this.map.remove(p.getName());
			plugin.api.sendLog("[Epicraft - Bank] " + p.getName() + " hat die Bank verlassen");
		}
	}
	
	public void openInv(Player p){
		int lines = 6;
		Inventory inv = Bukkit.createInventory(null, lines*9, p.getName() + "'s Konto");
		inv.setMaxStackSize(100000000);
		ItemMeta meta = null;
		ItemStack back = new ItemStack(Material.WALL_SIGN, 1);//, (short) 11);
		meta = back.getItemMeta();
		meta.setDisplayName("Zurück");
		back.setItemMeta(meta);
    	ItemStack next = new ItemStack(Material.WALL_SIGN, 1);//, (short) 14);
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
    	this.map.put(p.getName(), 1);
    	fillBankTable(1, inv, p);
    	p.openInventory(inv);
	}
	
	public void closeInv(Player p){
		p.closeInventory();
	}
	
	private void removeItemFromBankAndGiveItToUser(Player p, Inventory bankInventory, int posItem, int itemAmount){
		int freeSpace = 0;
	    ItemStack[] invstack = p.getInventory().getContents();
	    for(ItemStack is : invstack){
	        if(is == null)
	            freeSpace += p.getInventory().getMaxStackSize();
	        else if(is.getType() == bankInventory.getItem(posItem).getType() && is.getDurability() == bankInventory.getItem(posItem).getDurability())
	            freeSpace += p.getInventory().getMaxStackSize() - is.getAmount();
	    }
	    if(freeSpace == 0){
	    	p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast keinen Platz in deinem Inventar!");
	    	return;
	    }
	    List<String> stackAmountList = bankInventory.getItem(posItem).getItemMeta().getLore();
	    String stackAmount = stackAmountList.get(0);
	    stackAmount = stackAmount.replaceAll(" ", "");
	    stackAmount = stackAmount.replaceAll("Anzahl", "");
	    stackAmount = stackAmount.replaceAll(":", "");
	    stackAmount = ChatColor.stripColor(stackAmount);
	    int amount = 0;
	    try{
	    	amount = Integer.valueOf(stackAmount);
	    }
	    catch(NumberFormatException nfe){
			return;
	    }
	    if(freeSpace == 0){
	    	p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast keinen Platz in deinem Inventar!");
	    	return;
	    }
	    ItemStack toPlayer = bankInventory.getItem(posItem);//.clone();//new ItemStack(bankInventory.getItem(posItem).getType(), 1, bankInventory.getItem(posItem).getDurability());
	    ItemMeta meta = toPlayer.getItemMeta();
	    meta.setLore(new ArrayList<String>());
	    toPlayer.setItemMeta(meta);
	    if(itemAmount == 1){
	    	int newAmount = system.removeAmountFromItem(p, toPlayer.getType().toString(), toPlayer.getDurability(), 1, getEnchantment(toPlayer));
	    	p.getInventory().addItem(toPlayer);
	    	plugin.api.sendLog("[Epicraft - Bank] " + p.getName() + " zahlt " + String.valueOf(toPlayer.getAmount()) + " " + toPlayer.getType().toString() + " aus");
	    	if(newAmount <= 0)
	    		bankInventory.clear(posItem);
	    	else
	    		bankInventory.setItem(posItem, modifyBlock(toPlayer.getType().toString(), toPlayer.getDurability(), newAmount));
	    	return;
	    }
	    else if(itemAmount == 64){
	    	if(freeSpace < 64){
	    		int newAmount = system.removeAmountFromItem(p, toPlayer.getType().toString(), toPlayer.getDurability(), freeSpace, getEnchantment(toPlayer));
	    		if(newAmount == -1){
	    			p.sendMessage(plugin.namespace + ChatColor.RED + "Bitte Bank aktualisieren!");
	    			return;
	    		}
		    	if(newAmount <= 0){
		    		toPlayer.setAmount(freeSpace + newAmount);
		    		bankInventory.clear(posItem);
		    	}	
		    	else{
		    		toPlayer.setAmount(freeSpace);
		    		bankInventory.setItem(posItem, modifyBlock(toPlayer.getType().toString(), toPlayer.getDurability(), newAmount));
		    	}
		    	p.getInventory().addItem(toPlayer);	
		    	plugin.api.sendLog("[Epicraft - Bank] " + p.getName() + " zahlt " + String.valueOf(toPlayer.getAmount()) + " " + toPlayer.getType().toString() + " aus");
	    	}
	    	else{
	    		int newAmount = system.removeAmountFromItem(p, toPlayer.getType().toString(), toPlayer.getDurability(), 64, getEnchantment(toPlayer));
	    		if(newAmount == -1){
	    			p.sendMessage(plugin.namespace + ChatColor.RED + "Bitte Bank aktualisieren!");
	    			return;
	    		}
		    	if(newAmount <= 0){
		    		toPlayer.setAmount(64 + newAmount);
		    		bankInventory.clear(posItem);
		    	}	
		    	else{
		    		toPlayer.setAmount(64);
		    		bankInventory.setItem(posItem, modifyBlock(toPlayer.getType().toString(), toPlayer.getDurability(), newAmount));
		    	}
				plugin.api.sendLog("[Epicraft - Bank] " + p.getName() + " zahlt " + String.valueOf(toPlayer.getAmount()) + " " + toPlayer.getType().toString() + " aus");
		    	p.getInventory().addItem(toPlayer);
	    	}
	    }
	}
	
	private void removeItemFromUserAndGiveItToBank(Player p, int slot, int itemAmount){
		ItemStack fromPlayer = p.getInventory().getItem(slot);
		Map<Enchantment, Integer> map = fromPlayer.getEnchantments();
		if(!map.isEmpty()){
			p.sendMessage(plugin.namespace + ChatColor.RED + "Items mit verzauberungen können nicht eingezahlt werden!");
			//p.sendMessage(plugin.namespace + ChatColor.RED + "Dieses Feature wird in kürze nachgereicht!");
			return;
		}
		if(fromPlayer.getType().equals(Material.ENCHANTED_BOOK)){
			p.sendMessage(plugin.namespace + ChatColor.RED + "Verzauberte Bücher können nicht eingezahlt werden!");
			return;
		}
		int amount = p.getInventory().getItem(slot).getAmount();
		if(itemAmount == 1){
			if(amount - 1 == 0){
				p.getInventory().clear(slot);
			}
			else{
				p.getInventory().getItem(slot).setAmount(amount - 1);
			}
			
			system.addAmountToItem(p, fromPlayer.getType().toString(), fromPlayer.getDurability(), 1, getEnchantment(fromPlayer));
		}
		else if(itemAmount == 64){
			system.addAmountToItem(p, fromPlayer.getType().toString(), fromPlayer.getDurability(), amount, getEnchantment(fromPlayer));
			p.getInventory().clear(slot);
		}
		plugin.api.sendLog("[Epicraft - Bank] " + p.getName() + " zahlt " + String.valueOf(amount) + " " + fromPlayer.getType().toString() + " ein");
		//p.sendMessage("Zahle " + amount + " " + fromPlayer.getType().toString() + " ein");
	}	

	private boolean fillBankTable(int page, Inventory bankInventory, Player p){
		//9 - 53
		//get data from sql
		for (int i = 9 ; i <= 53 ; i++)
			bankInventory.clear(i);
		int slot = 9;
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT * FROM `ep-Bank` WHERE username='" + p.getName() + "'");
			rs = st.executeQuery();
			for(int i = 0 ; i < 45*(page-1) ; i++){
				if(!rs.next()){
					sql.closeRessources(rs, st);
					return true;
				}
					
			}
			while(rs.next()){
				if(slot > 53){
					sql.closeRessources(rs, st);
					return true;
				}
				//ItemStack stack = getStackWithEntchantment(modifyBlock(rs.getString(3), rs.getInt(4), rs.getInt(5)), rs.getString(6));
				bankInventory.setItem(slot, modifyBlock(rs.getString(3), rs.getInt(4), rs.getInt(5)));
				//bankInventory.setItem(slot, stack);
				slot++;
			}
			sql.closeRessources(rs, st);
			return true;
		}
		catch(SQLException e){
			return false;
		}
	}
	
	private ItemStack modifyBlock(String m, int data, int amount){
		Material mat = Material.getMaterial(m);
		ItemStack stack = new ItemStack(mat, 1, (short) data);
		String anzahl = "Anzahl: " + String.valueOf(amount);
		ItemMeta meta = stack.getItemMeta();
		List<String> list = new ArrayList<String>();
		list.add(anzahl);
		meta.setLore(list);
		stack.setItemMeta(meta);
		list.clear();
		return stack;
		
	}
	
	private ItemStack getStackWithEntchantment(ItemStack stack, String enchantment){
		if(stack.getType() == Material.ENCHANTED_BOOK){
			String[] enchantmentAndLevel = enchantment.split(":");
			EnchantmentStorageMeta meta = (EnchantmentStorageMeta) stack.getItemMeta();
			for(String enchAndLvl : enchantmentAndLevel){
				String[] ench_lvl = enchAndLvl.split("_");
				if(ench_lvl.length == 2){
					Enchantment myEnchantment = new EnchantmentWrapper(Integer.valueOf(ench_lvl[0]));
					int lvl = Integer.valueOf(ench_lvl[1]);
					meta.addEnchant(myEnchantment, lvl, true);
				}
			}
			stack.setItemMeta(meta);
		}
		else{
			String[] enchantmentAndLevel = enchantment.split(":");
			ItemMeta meta = stack.getItemMeta();
			for(String enchAndLvl : enchantmentAndLevel){
				String[] ench_lvl = enchAndLvl.split("_");
				if(ench_lvl.length == 2){
					Enchantment myEnchantment = new EnchantmentWrapper(Integer.valueOf(ench_lvl[0]));
					int lvl = Integer.valueOf(ench_lvl[1]);
					meta.addEnchant(myEnchantment, lvl, true);
				}
			}
			stack.setItemMeta(meta);
		}
		return stack;
	}
	
	private String getEnchantment(ItemStack stack){
		return "";
		/*String enchantment = "";
		/*if(stack.getType() == Material.ENCHANTED_BOOK){
			EnchantmentStorageMeta meta = (EnchantmentStorageMeta) stack.getItemMeta();
			boolean isFirst = true;
			for(Entry<Enchantment, Integer> entry : meta.getStoredEnchants().entrySet()){
				String tmpEnchantment = "";
				if(!isFirst)
					tmpEnchantment = ":";
				isFirst = false;
				tmpEnchantment += String.valueOf(entry.getKey().getId());
				tmpEnchantment += "_" + String.valueOf(entry.getValue());
				enchantment += tmpEnchantment;
			}
		}
		else{
		List<String> sortStrings = new ArrayList<String>();
		for(Entry<Enchantment, Integer> entry : stack.getEnchantments().entrySet()){
			String tmpEnchantment = String.valueOf(entry.getKey().getId());
			tmpEnchantment += "_" + String.valueOf(entry.getValue());
			sortStrings.add(tmpEnchantment);
		}
		Collections.sort(sortStrings, String.CASE_INSENSITIVE_ORDER);
		enchantment = sortStrings.get(0);
		for(int i = 1 ; i < sortStrings.size() ; i++){
			enchantment += ":" + sortStrings.get(i);
		}
		//}
		return enchantment;*/
	}

	private boolean isPlayerInBank(Player p){
		if(!p.getLocation().getWorld().equals(Bukkit.getServer().getWorld("Survival")))
			return false;
		double x = p.getLocation().getX();
		double z = p.getLocation().getZ();
		double y = p.getLocation().getY();
		if((x <= -37 && x >= -43) && (z >= 255 && z <= 272) && (y >= 64 && y <= 66))
			return true;
		return false;
	}
	
	@EventHandler
	public void onQuitEvent(PlayerQuitEvent e){
		Player p = e.getPlayer();
		this.map.remove(p.getName());
	}
	
	
	@EventHandler
	public void OnPickUpItem(PlayerPickupItemEvent event){
		if(map.containsKey(event.getPlayer().getName()))
			event.setCancelled(true);
	}

	
}