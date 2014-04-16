package de.wolfsline.gs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

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
		sql.queryUpdate("CREATE TABLE IF NOT EXISTS Grundstuecke (UUID VARCHAR(36), Title VARCHAR(50), X INT, Y INT, Z INT, SIZE_X INT, SIZE_Y INT, Zeit VARCHAR(10), Datum VARCHAR(10))");
	}
	
	public boolean hasPlayerGSwithName(UUID uuid, String gsname){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT Title FROM Grundstuecke WHERE UUID='" + uuid.toString() + "'");
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
	
	public boolean PlayerGSSizeOk(UUID uuid){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT * FROM Grundstuecke WHERE UUID='" + uuid.toString() + "'");
			rs = st.executeQuery();
			while(rs.next()){
				int große_X = rs.getInt(6);
				int große_Y = rs.getInt(7);
				if(große_X < 50 || große_Y < 50){
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
	
	public void newGS(int x, int y, int z, String gsname, Player p, int groeße_x, int groeße_y) {
		MySQL sql = this.plugin.getMySQL();
		String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
		String date = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
		String update = "INSERT INTO Grundstuecke (UUID, Title, X, Y, Z, SIZE_X, SIZE_Y, Zeit, Datum) VALUES ('" + p.getUniqueId() + "', '" + gsname + "', '" + x + "', '" + y + "', '" + z + "', '" + groeße_x + "', '" + groeße_y + "', '" + time + "', '" + date + "')";
		sql.queryUpdate(update);
	}
	
	public void delGS(UUID uuid, String gsname)  {
		MySQL sql = this.plugin.getMySQL();
		String update = "DELETE FROM Grundstuecke WHERE Title='" + gsname + "' AND UUID='" + uuid.toString() + "'";
		sql.queryUpdate(update);
	}
	
	public int countGsFromPlayer(Player p) {
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		int count = 0;
		try {
			st = conn.prepareStatement("SELECT UUID FROM Grundstuecke");
			rs = st.executeQuery();
			while(rs.next()){
				if(rs.getString(1).equals(p.getUniqueId().toString())){
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

	public boolean warpPlayerto(Player p, UUID targetUUID, String gsname){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT * FROM Grundstuecke WHERE Title='" + gsname + "' AND UUID='" + targetUUID.toString() + "'");
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
	
	public List<String> getGSFromPlayer(UUID uuid){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		List<String> gs = new ArrayList<String>();
		try {
			st = conn.prepareStatement("SELECT Title FROM Grundstuecke WHERE UUID='" + uuid.toString() + "'");
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

	public boolean showGSfromPlayer(Player p, UUID targetUUID){
		String name = plugin.uuid.getNameFromUUID(targetUUID);
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT * FROM Grundstuecke WHERE UUID='" + targetUUID.toString() + "'");
			rs = st.executeQuery();
			if(p.getUniqueId().equals(targetUUID)){
				p.sendMessage(ChatColor.GOLD + "---------------[Dein(e) Grundstück(e)]---------------");
			}
			else{
				p.sendMessage(ChatColor.GOLD + "---------------[" + name + "'s Grundstück(e)]---------------");
			}
			while(rs.next()){
				String gsname = rs.getString(2);
				p.sendMessage(ChatColor.GOLD + "Name: " + gsname);
				p.sendMessage(ChatColor.GOLD + "Größe: " + String.valueOf(rs.getInt(6)) + "*" + String.valueOf(rs.getInt(7)));
				p.sendMessage(ChatColor.GOLD + "Erstellt am: " + String.valueOf(rs.getString(9)) + " um " + String.valueOf(rs.getString(8)) + " Uhr");
				p.sendMessage("");
			}
			if(p.getUniqueId().equals(targetUUID)){
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
