package de.wolfsline.gs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.data.MySQL;

public class Data {	
	
	private Epicraft plugin;
	
	private final String WORLD = "world"; //Legt die Welt der Grundstücke fest
	
	public Data(Epicraft plugin) {
		this.plugin = plugin;
		
		MySQL sql = this.plugin.getMySQL();
		sql.queryUpdate("CREATE TABLE IF NOT EXISTS plots (id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(16), title VARCHAR(50), x INT, y INT, z INT, size_x INT, size_y INT, time VARCHAR(10), date VARCHAR(10))");
	}
	
	public boolean hasplayerGSwithName(String name, String gsname){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT title FROM plots WHERE username='" + name + "'");
			rs = st.executeQuery();
			while(rs.next()){
				if(rs.getString(1).equalsIgnoreCase(name + "_" + gsname)){
					sql.closeRessources(rs, st);
					return true;
				}
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			sql.closeRessources(rs, st);
		}
		return false;
	}
	
	public void newGS(int x, int y, int z, String gsname, String name, int groeße_x, int groeße_y) {
		MySQL sql = this.plugin.getMySQL();
		String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
		String date = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
		String update = "INSERT INTO plots (username, title, x, y, z, size_x, size_y, time, date) VALUES ('" + name + "', '" + name + "_" + gsname + "', '" + x + "', '" + y + "', '" + z + "', '" + groeße_x + "', '" + groeße_y + "', '" + time + "', '" + date + "')";
		sql.queryUpdate(update);
	}
	
	public boolean updateGS(Player p, String gsname, int x, int y, Economy econ, boolean add){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		int gsX = 0;
		int gsY = 0;
		try {
			st = conn.prepareStatement("SELECT * FROM plots WHERE title='" + p.getName() + "_" + gsname + "'");
			rs = st.executeQuery();
			rs.next();
			gsX = rs.getInt(7);
			gsY = rs.getInt(8);
			sql.closeRessources(rs, st);
			int betrag =  x*gsY*10 + y*(gsX+x)*10;
			if(econ.has(p.getName(), betrag) && add){
				String update = "UPDATE plots SET size_X='" + String.valueOf(x+gsX) + "', size_y='" + String.valueOf(y+gsY) + "' WHERE title='" + p.getName() + "_" + gsname + "'";
				sql.queryUpdate(update);
				econ.withdrawPlayer(p.getName(), betrag);
				return true;
			}
			if(add){
				p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast nicht genug Geld");
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Du hast nur " + econ.getBalance(p.getName()) + " Coins");
			}
			p.sendMessage(plugin.namespace + ChatColor.WHITE + "Erweiterung kostet: " + String.valueOf(betrag) + " Coins");
			return false;
			
		} 
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void delGS(String name, String gsname)  {
		MySQL sql = this.plugin.getMySQL();
		String update = "DELETE FROM plots WHERE title='" + name + "_" + gsname + "'";
		sql.queryUpdate(update);
	}
	
	public int countGsFromPlayer(Player p) {
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		int i = 0;
		try {
			st = conn.prepareStatement("SELECT username FROM plots WHERE username='" + p.getName() + "'");
			rs = st.executeQuery();
			while(rs.next()){
				if(rs.getString(1).equalsIgnoreCase(p.getName())){
					i++;
				}
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			sql.closeRessources(rs, st);
		}
		return i;
	}

	public boolean warpPlayerto(Player p, String name, String gsname){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT * FROM plots WHERE title='" + name + "_" + gsname + "'");
			rs = st.executeQuery();
			if(!rs.next()){
				sql.closeRessources(rs, st);
				return false;
			}
			int gsX = rs.getInt(4);
			int gsY = rs.getInt(5);
			int gsZ = rs.getInt(6);
			sql.closeRessources(rs, st);
			Location loc = new Location(Bukkit.getWorld(WORLD), gsX, gsY+25, gsZ);
			p.teleport(loc);
			p.setAllowFlight(true);
			p.setFlying(true);
			return true;
		} 
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public List<String> getGSFromPlayer(String name){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		List<String> gs = new ArrayList<String>();
		try {
			st = conn.prepareStatement("SELECT title FROM plots WHERE username='" + name + "'");
			rs = st.executeQuery();
			while(rs.next()){
				String gsname = rs.getString(1);
				gsname = gsname.replaceFirst(name, "");
				gsname = gsname.replaceFirst("_", "");
				gs.add(gsname);	
			}
			sql.closeRessources(rs, st);
			return gs;
			
		} 
		catch (SQLException e) {
			e.printStackTrace();
			return gs;
		}
	}

	public boolean showGSfromPlayer(Player p, String name){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT * FROM plots WHERE username='" + name + "'");
			rs = st.executeQuery();
			if(p.getName().equalsIgnoreCase(name)){
				p.sendMessage(ChatColor.GOLD + "---------------[Dein(e) Grundstück(e)]---------------");
			}
			else{
				p.sendMessage(ChatColor.GOLD + "---------------[" + name + "'s Grundstück(e)]---------------");
			}
			while(rs.next()){
				String gsname = rs.getString(3);
				gsname = gsname.replaceFirst(name, "");
				gsname = gsname.replaceFirst("_", "");
				p.sendMessage(ChatColor.GOLD + "Name: " + gsname);
				p.sendMessage(ChatColor.GOLD + "Größe: " + String.valueOf(rs.getInt(7)) + "*" + String.valueOf(rs.getInt(8)));
				p.sendMessage(ChatColor.GOLD + "Erstellt am: " + String.valueOf(rs.getString(10)) + " um " + String.valueOf(rs.getString(9)) + " Uhr");
				p.sendMessage("");
			}
			if(p.getName().equalsIgnoreCase(name)){
				p.sendMessage(ChatColor.GOLD + "---------------[Dein(e) Grundstück(e)]---------------");
			}
			else{
				p.sendMessage(ChatColor.GOLD + "---------------[" + name + "'s Grundstück(e)]---------------");
			}
			return true;
			
		} 
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
