package de.wolfsline.Ticketsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.data.MySQL;

public class Ticketsystem_Daten {

	private Epicraft plugin;
	
	private HashMap<UUID, Location> map = new HashMap<UUID, Location>();
	
	public Ticketsystem_Daten(Epicraft plugin){
		this.plugin = plugin;
		MySQL sql = this.plugin.getMySQL();
		sql.queryUpdate("CREATE TABLE IF NOT EXISTS Tickets (ID INT AUTO_INCREMENT PRIMARY KEY, UUID VARCHAR(36), Nachricht VARCHAR(100), X INT, Y INT, Z INT, Welt VARCHAR(50), Status VARCHAR(50), Team VARCHAR(36), Zeit VARCHAR(10), Datum VARCHAR(10))");
	}
	
	public boolean createTicket(Player p, String ticket){
		Location loc = p.getLocation();
		String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
		String date = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
		String query = "INSERT INTO Tickets (UUID, Nachricht, X, Y, Z, Welt, Status, Team, Zeit, Datum) VALUES";
		query += "('" + p.getUniqueId() + "', '" + ticket + "', '" + loc.getBlockX() + "', '" + loc.getBlockY() + "', '" + loc.getBlockZ() + "', '" + loc.getWorld().getName() + "', '" + "offen" + "', '" + "" + "', '" + time + "', '" + date + "')";
		plugin.getMySQL().queryUpdate(query);
		triggerTeamForTicket();
		plugin.api.sendLog("[Epicraft - Ticketsystem] " + p.getName() + " hat ein neues Ticket erstellt");
		return true;
	}
	
	private void triggerTeamForTicket(){
		int count = countTicketsWithState("offen");
		String msg = "";
		if(count == 1)
			msg = plugin.namespace + ChatColor.WHITE + "Es ist 1 Ticket offen!";
		else
			msg = plugin.namespace + ChatColor.WHITE + "Es sind " + String.valueOf(count) + " Tickets offen!";
		for(Player p : Bukkit.getServer().getOnlinePlayers()){
			if(p.hasPermission("epicraft.permission.guard") || p.hasPermission("epicraft.permission.moderator") || p.hasPermission("epicraft.permission.admin")){
				p.sendMessage(msg);
			}
		}
	}
	
	public void TicketForTeam(Player p){
		if(countTicketsWithState("offen") == 0){
			p.sendMessage(plugin.namespace + ChatColor.RED + "Es gibt keine Tickets zu bearbeiten");
			return;
		}
		
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT * FROM Tickets WHERE Status='offen'");
			rs = st.executeQuery();
			int ID = 0;
			if(rs.next()){
				World world = Bukkit.getServer().getWorld(rs.getString(7));
				if(world == null){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Die Welt\"" + rs.getString(7) + "\" wurde nicht geladen!");
					p.sendMessage(plugin.namespace + ChatColor.RED + "Vorgang wurde abgebrochen. Bitte Administrator kontaktieren!");
					return;
				}
				ID = rs.getInt(1);
				p.sendMessage(ChatColor.GOLD + "---------------[Dein Ticket]---------------");
				p.sendMessage("ID: " + ID);
				UUID uuid = UUID.fromString(rs.getString(2));
				p.sendMessage("Von: " + plugin.uuid.getNameFromUUID(uuid));
				p.sendMessage("Ticket: " + rs.getString(3));
				p.sendMessage("Erstellt: am " + rs.getString(11) + " um " + rs.getString(10));
				p.sendMessage(ChatColor.GOLD + "---------------[Dein Ticket]---------------");
				map.put(p.getUniqueId(), p.getLocation());
				Location loc = new Location(Bukkit.getServer().getWorld(rs.getString(7)), rs.getDouble(4), rs.getDouble(5), rs.getDouble(6));
				p.setAllowFlight(true);
				p.setFlying(true);
				p.setFlySpeed(0.1F);
				p.teleport(loc);
				Player player = Bukkit.getServer().getPlayer(UUID.fromString(rs.getString(2)));
				if(player != null)
					player.sendMessage(plugin.namespace + ChatColor.WHITE + p.getName() + " bearbeitet dein Ticket[" + ID + "] gerade.");
			}
			sql.closeRessources(rs, st);
			String query = "UPDATE Tickets SET Team='" + p.getUniqueId() + "', Status='wird bearbeitet' WHERE ID='" + ID + "'";
			plugin.getMySQL().queryUpdate(query);
			plugin.api.sendLog("[Epicraft - Ticketsystem] " + p.getName() + " bearbeitet nun Ticket[" + ID + "]");
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int isTeamOnTicket(Player p){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT ID FROM Tickets WHERE Status='wird bearbeitet' AND Team='" + p.getUniqueId() + "'");
			rs = st.executeQuery();
			int ID = 0;
			while(rs.next()){
				ID = rs.getInt(1);
				sql.closeRessources(rs, st);
				return ID;
			}
			sql.closeRessources(rs, st);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public boolean setTicketState(Player p, String state){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT * FROM Tickets WHERE Status='wird bearbeitet' AND Team='" + p.getUniqueId() + "'");
			rs = st.executeQuery();
			int ID = 0;
			while(rs.next()){
				ID = rs.getInt(1);
				UUID uuid = UUID.fromString(rs.getString(2));
				sql.closeRessources(rs, st);
				if(state.equalsIgnoreCase("release")){
					String query = "UPDATE Tickets SET Team='" + "" + "', Status='offen' WHERE ID='" + ID + "'";
					plugin.getMySQL().queryUpdate(query);
					plugin.api.sendLog("[Epicraft - Ticketsystem] " + p.getName() + " hat Ticket[" + ID + "] als offen markiert");
					return true;
				}
				else if(state.equalsIgnoreCase("finish")){
					String query = "UPDATE Tickets SET Team='" + p.getUniqueId() + "', Status='bearbeitet' WHERE ID='" + ID + "'";
					plugin.getMySQL().queryUpdate(query);
					plugin.api.sendLog("[Epicraft - Ticketsystem] " + p.getName() + " hat Ticket[" + ID + "] fertig bearbeitet");
					Player player = Bukkit.getServer().getPlayer(uuid);
					if(player != null){
						player.sendMessage(plugin.namespace + ChatColor.WHITE + "Dein Ticket[" + ID + "] wurde bearbeitet");
					}
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Du hast das Ticket[" + ID + "] fertig bearbeitet");
					return true;
				}
			}
			sql.closeRessources(rs, st);
			p.sendMessage(plugin.namespace + ChatColor.RED + "Du arbeitest zur Zeit an keinem Ticket!");
			return false;
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public int countTicketsWithState(String state){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		int count = 0;
		try {
			st = conn.prepareStatement("SELECT Status FROM Tickets");
			rs = st.executeQuery();
			while(rs.next()){
				if(rs.getString(1).equals(state)){
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
	
	public void showTicketsWithStateOpen(Player p){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT * FROM Tickets WHERE UUID='" + p.getUniqueId() + "'");
			rs = st.executeQuery();
			p.sendMessage(ChatColor.GOLD + "---------------[Deine Tickets]---------------");
			while(rs.next()){
				if(rs.getString(8).equals("bearbeitet"))
					continue;
				p.sendMessage(ChatColor.GOLD + "[" + rs.getInt(1) + "]" + ChatColor.WHITE + " - " + rs.getString(8) + " -> " + rs.getString(3));
				if(rs.getString(8).equalsIgnoreCase("wird bearbeitet")){
					p.sendMessage(ChatColor.WHITE + "Wird von " + plugin.uuid.getNameFromUUID(UUID.fromString(rs.getString(9))) + " bearbeitet");
				}
			}
			p.sendMessage(ChatColor.GOLD + "---------------[Deine Tickets]---------------");
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			sql.closeRessources(rs, st);
		}
	}
	
	public void showTicketWithNumber(Player p, int ID, boolean withTeleport){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT * FROM Tickets WHERE ID='" + ID + "'");
			rs = st.executeQuery();
			
			if(rs.next()){
				p.sendMessage(ChatColor.GOLD + "---------------[Ticket " + ID + "]---------------");
				p.sendMessage("ID: " + ID);
				p.sendMessage("Von: " + plugin.uuid.getNameFromUUID(UUID.fromString(rs.getString(2))));
				p.sendMessage("Ticket: " + rs.getString(3));
				p.sendMessage("Erstellt: am " + rs.getString(11) + " um " + rs.getString(10));
				p.sendMessage("Status: " + rs.getString(8));
				p.sendMessage("Team: " + plugin.uuid.getNameFromUUID(UUID.fromString(rs.getString(9))));
				p.sendMessage(ChatColor.GOLD + "---------------[Ticket " + ID + "]---------------");
				if(withTeleport){
					map.put(p.getUniqueId(), p.getLocation());
					Location loc = new Location(Bukkit.getServer().getWorld(rs.getString(7)), rs.getDouble(4), rs.getDouble(5), rs.getDouble(6));
					p.setAllowFlight(true);
					p.setFlying(true);
					p.setFlySpeed(0.1F);
					p.teleport(loc);
				}
			}
			else{
				p.sendMessage(plugin.namespace + ChatColor.RED + "Das Ticket[" + ID + "] konnte nicht aufgerufen werden!");
			}
			
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			sql.closeRessources(rs, st);
		}
	}
	
	public Location getLastLocationFromPlayer(UUID uuid){
		if(map.containsKey(uuid)){
			Location loc = map.get(uuid);
			map.remove(uuid);
			return loc;
		}
		return null;
	}
}
