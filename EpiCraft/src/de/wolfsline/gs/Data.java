package de.wolfsline.gs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.data.MySQL;

public class Data {	
	
	private Epicraft plugin;
	
	private final String WORLD = "world"; //Legt die Welt der Grundst�cke fest
	
	public Data(Epicraft plugin) {
		this.plugin = plugin;
		
		MySQL sql = this.plugin.getMySQL();
		sql.queryUpdate("CREATE TABLE IF NOT EXISTS Grundstuecke (Benutzername VARCHAR(16), Title VARCHAR(50), X INT, Y INT, Z INT, SIZE_X INT, SIZE_Y INT, Zeit VARCHAR(10), Datum VARCHAR(10))");
	}
	
	public boolean hasPlayerGSwithName(String name, String gsname){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT Title FROM Grundstuecke WHERE Benutzername='" + name + "'");
			rs = st.executeQuery();
			while(rs.next()){
				if(rs.getString(1).equalsIgnoreCase(gsname)){
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
	
	public boolean PlayerGSSizeOk(String name){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT * FROM Grundstuecke WHERE Benutzername='" + name + "'");
			rs = st.executeQuery();
			while(rs.next()){
				int gro�e_X = rs.getInt(6);
				int gro�e_Y = rs.getInt(7);
				if(gro�e_X < 50 || gro�e_Y < 50){
					sql.closeRessources(rs, st);
					return false;
				}
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			sql.closeRessources(rs, st);
		}
		return true;
	}
	
	public void newGS(int x, int y, int z, String gsname, String name, int groe�e_x, int groe�e_y) {
		MySQL sql = this.plugin.getMySQL();
		String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
		String date = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
		String update = "INSERT INTO Grundstuecke (Benutzername, Title, X, Y, Z, SIZE_X, SIZE_Y, Zeit, Datum) VALUES ('" + name + "', '" + gsname + "', '" + x + "', '" + y + "', '" + z + "', '" + groe�e_x + "', '" + groe�e_y + "', '" + time + "', '" + date + "')";
		sql.queryUpdate(update);
	}
	
	public void delGS(String name, String gsname)  {
		MySQL sql = this.plugin.getMySQL();
		String update = "DELETE FROM Grundstuecke WHERE Title='" + gsname + "' AND Benutzername='" + name + "'";
		sql.queryUpdate(update);
	}
	
	public int countGsFromPlayer(Player p) {
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		int count = 0;
		try {
			st = conn.prepareStatement("SELECT Benutzername FROM Grundstuecke");
			rs = st.executeQuery();
			while(rs.next()){
				if(rs.getString(1).equalsIgnoreCase(p.getName())){
					count++;
				}
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			sql.closeRessources(rs, st);
		}
		return count;
	}

	public boolean warpPlayerto(Player p, String name, String gsname){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT * FROM Grundstuecke WHERE Title='" + gsname + "' AND Benutzername='" + name + "'");
			rs = st.executeQuery();
			if(!rs.next()){
				sql.closeRessources(rs, st);
				return false;
			}
			int gsX = rs.getInt(3);
			int gsY = rs.getInt(4);
			int gsZ = rs.getInt(5);
			sql.closeRessources(rs, st);
			Location loc = new Location(Bukkit.getWorld(WORLD), gsX, gsY+25, gsZ);
			if(p.isInsideVehicle()){
				p.sendMessage(plugin.namespace + ChatColor.RED + "Bitte verlasse das Objekt zum Teleportieren!");
				return false;
			}
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
			st = conn.prepareStatement("SELECT Title FROM Grundstuecke WHERE Benutzername='" + name + "'");
			rs = st.executeQuery();
			while(rs.next()){
				String gsname = rs.getString(1);
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
			st = conn.prepareStatement("SELECT * FROM Grundstuecke WHERE Benutzername='" + name + "'");
			rs = st.executeQuery();
			if(p.getName().equalsIgnoreCase(name)){
				p.sendMessage(ChatColor.GOLD + "---------------[Dein(e) Grundst�ck(e)]---------------");
			}
			else{
				p.sendMessage(ChatColor.GOLD + "---------------[" + name + "'s Grundst�ck(e)]---------------");
			}
			while(rs.next()){
				String gsname = rs.getString(2);
				p.sendMessage(ChatColor.GOLD + "Name: " + gsname);
				p.sendMessage(ChatColor.GOLD + "Gr��e: " + String.valueOf(rs.getInt(6)) + "*" + String.valueOf(rs.getInt(7)));
				p.sendMessage(ChatColor.GOLD + "Erstellt am: " + String.valueOf(rs.getString(9)) + " um " + String.valueOf(rs.getString(8)) + " Uhr");
				p.sendMessage("");
			}
			if(p.getName().equalsIgnoreCase(name)){
				p.sendMessage(ChatColor.GOLD + "---------------[Dein(e) Grundst�ck(e)]---------------");
			}
			else{
				p.sendMessage(ChatColor.GOLD + "---------------[" + name + "'s Grundst�ck(e)]---------------");
			}
			return true;
			
		} 
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
