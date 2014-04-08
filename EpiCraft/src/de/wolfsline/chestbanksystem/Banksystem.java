package de.wolfsline.chestbanksystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.data.MySQL;

public class Banksystem {
	
	private Epicraft plugin;
	public Banksystem(Epicraft plugin){
		this.plugin = plugin;
		MySQL sql = this.plugin.getMySQL();
		sql.queryUpdate("CREATE TABLE IF NOT EXISTS `ep-Bank` (id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(16), itemTyp VARCHAR(25), data INT, amount INT, enchantment VARCHAR(30))");
	}
	
	public void newEntry(Player p, String itemTyp, int data, int amount, String enchantment){
		MySQL sql = this.plugin.getMySQL();
		String update = "INSERT INTO `ep-Bank` (username, itemTyp, data, amount, enchantment) VALUES ('" + p.getName() + "', '" + itemTyp + "', '" + data + "', '" + amount + "', '" + enchantment + "')";
		plugin.api.sendLog("Neuer Eintrag: " + enchantment);
		sql.queryUpdate(update);
	}
	
	public int removeAmountFromItem(Player p, String itemTyp, int data, int amount, String enchantment){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		plugin.api.sendLog("RremoveAmountFromItem: " + enchantment);
		try {
			st = conn.prepareStatement("SELECT amount FROM `ep-Bank` WHERE username='" + p.getName() + "' and itemTyp='" + itemTyp + "' and data='" + data + "' and enchantment='" + enchantment + "'");
			rs = st.executeQuery();
			if(rs.next()){//item vorhanden
				int amountInDatabase = rs.getInt(1);
				sql.closeRessources(rs, st);
				amountInDatabase -= amount;
				if(amountInDatabase > 0){//Wenn items übrig bleiben
					String update = "UPDATE `ep-Bank` SET amount='" + String.valueOf(amountInDatabase) + "' WHERE username='" + p.getName() + "' and itemTyp='" + itemTyp + "' and data='" + data + "' and enchantment='" + enchantment + "'";
	                sql.queryUpdate(update);
	                return amountInDatabase;
				}
				else{//Wenn 0, dann lösche den eintrag
					String update = "DELETE FROM `ep-Bank` WHERE username='" + p.getName() + "' and itemTyp='" + itemTyp + "' and data='" + data + "' and enchantment='" + enchantment + "'";
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
	
	public boolean addAmountToItem(Player p, String itemTyp, int data, int amount, String enchantment){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		plugin.api.sendLog("AddAmountToItem: " + enchantment);
		try {
			st = conn.prepareStatement("SELECT amount FROM `ep-Bank` WHERE username='" + p.getName() + "' and itemTyp='" + itemTyp + "' and data='" + data + "' and enchantment='" + enchantment + "'");
			rs = st.executeQuery();
			if(rs.next()){//item vorhanden
				int amountInDatabase = rs.getInt(1);
				sql.closeRessources(rs, st);
				amountInDatabase += amount;
				String update = "UPDATE `ep-Bank` SET amount='" + String.valueOf(amountInDatabase) + "' WHERE username='" + p.getName() + "' and itemTyp='" + itemTyp + "' and data='" + data + "' and enchantment='" + enchantment + "'";
                sql.queryUpdate(update);
				return true;
			}
			else{
				sql.closeRessources(rs, st);
				newEntry(p, itemTyp, data, amount, enchantment);
				return true;
			}
		}
		catch(SQLException e){
			
		}
		return false;
	}
}
