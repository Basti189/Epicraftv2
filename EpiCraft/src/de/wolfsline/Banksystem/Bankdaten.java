package de.wolfsline.Banksystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.data.MySQL;

public class Bankdaten {

	private Epicraft plugin;
	
	public Bankdaten(Epicraft plugin){
		this.plugin = plugin;
		this.plugin = plugin;
		MySQL sql = this.plugin.getMySQL();
		sql.queryUpdate("CREATE TABLE IF NOT EXISTS Bank (id INT AUTO_INCREMENT PRIMARY KEY, UUID VARCHAR(36), itemTyp VARCHAR(25), data INT, amount INT)");
	}
	
	public boolean fillBankTable(int page, Inventory bankInventory, Player p){
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
			st = conn.prepareStatement("SELECT * FROM Bank WHERE UUID='" + p.getUniqueId() + "'");
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
	
	public void removeItemFromBankAndGiveItToUser(Player p, Inventory bankInventory, int posItem, int itemAmount){
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
	    	int newAmount = removeAmountFromItem(p, toPlayer.getType().toString(), toPlayer.getDurability(), 1);
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
	    		int newAmount = removeAmountFromItem(p, toPlayer.getType().toString(), toPlayer.getDurability(), freeSpace);
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
	    		int newAmount = removeAmountFromItem(p, toPlayer.getType().toString(), toPlayer.getDurability(), 64);
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
	
	public void removeItemFromUserAndGiveItToBank(Player p, int slot, int itemAmount){
		ItemStack fromPlayer = p.getInventory().getItem(slot);
		if(!canPutInBank(fromPlayer)){
			p.sendMessage(plugin.namespace + ChatColor.RED + "Dieses Item kann nicht eingezahlt werden!");
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
			
			addAmountToItem(p, fromPlayer.getType().toString(), fromPlayer.getDurability(), 1);
		}
		else if(itemAmount == 64){
			addAmountToItem(p, fromPlayer.getType().toString(), fromPlayer.getDurability(), amount);
			p.getInventory().clear(slot);
		}
		plugin.api.sendLog("[Epicraft - Bank] " + p.getName() + " zahlt " + String.valueOf(amount) + " " + fromPlayer.getType().toString() + " ein");
	}
	
	public void newEntry(Player p, String itemTyp, int data, int amount){
		MySQL sql = this.plugin.getMySQL();
		String update = "INSERT INTO Bank (UUID, itemTyp, data, amount) VALUES ('" + p.getUniqueId() + "', '" + itemTyp + "', '" + data + "', '" + amount + "')";
		plugin.api.sendLog("Neuer Eintrag: " + itemTyp);
		sql.queryUpdate(update);
	}
	
	public int removeAmountFromItem(Player p, String itemTyp, int data, int amount){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		plugin.api.sendLog("RemoveAmountFromItem: " + itemTyp);
		try {
			st = conn.prepareStatement("SELECT amount FROM Bank WHERE UUID='" + p.getUniqueId() + "' and itemTyp='" + itemTyp + "' and data='" + data + "'");
			rs = st.executeQuery();
			if(rs.next()){//item vorhanden
				int amountInDatabase = rs.getInt(1);
				sql.closeRessources(rs, st);
				amountInDatabase -= amount;
				if(amountInDatabase > 0){//Wenn items übrig bleiben
					String update = "UPDATE Bank SET amount='" + String.valueOf(amountInDatabase) + "' WHERE UUID='" + p.getUniqueId() + "' and itemTyp='" + itemTyp + "' and data='" + data + "'";
	                sql.queryUpdate(update);
	                return amountInDatabase;
				}
				else{//Wenn 0, dann lösche den eintrag
					String update = "DELETE FROM Bank WHERE UUID='" + p.getUniqueId() + "' and itemTyp='" + itemTyp + "' and data='" + data + "'";
	                sql.queryUpdate(update);
	                return amountInDatabase;
				}
			}
			else{
				sql.closeRessources(rs, st);
				return -1;
			}
		}
		catch(SQLException e){
			
		}
		finally{
			sql.closeRessources(rs, st);
		}
		return 0;
	}
	
	public boolean addAmountToItem(Player p, String itemTyp, int data, int amount){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		plugin.api.sendLog("AddAmountToItem: " + amount);
		try {
			st = conn.prepareStatement("SELECT amount FROM Bank WHERE UUID='" + p.getUniqueId() + "' and itemTyp='" + itemTyp + "' and data='" + data + "'");
			rs = st.executeQuery();
			if(rs.next()){//item vorhanden
				int amountInDatabase = rs.getInt(1);
				sql.closeRessources(rs, st);
				amountInDatabase += amount;
				String update = "UPDATE Bank SET amount='" + String.valueOf(amountInDatabase) + "' WHERE UUID='" + p.getUniqueId() + "' and itemTyp='" + itemTyp + "' and data='" + data + "'";
                sql.queryUpdate(update);
				return true;
			}
			else{
				sql.closeRessources(rs, st);
				newEntry(p, itemTyp, data, amount);
				return true;
			}
		}
		catch(SQLException e){
			
		}
		return false;
	}
	
	private boolean canPutInBank(ItemStack stack){
		return stack.getType().isBlock();
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
	
}
