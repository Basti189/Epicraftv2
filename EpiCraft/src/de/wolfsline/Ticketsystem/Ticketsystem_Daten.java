package de.wolfsline.Ticketsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.data.MySQL;

public class Ticketsystem_Daten {

	private Epicraft plugin;
	
	public Ticketsystem_Daten(Epicraft plugin){
		this.plugin = plugin;
		MySQL sql = this.plugin.getMySQL();
		sql.queryUpdate("CREATE TABLE IF NOT EXISTS Tickets (Benutzername VARCHAR(16), Nachricht VARCHAR(200), X INT, Y INT, Z INT, Welt VARCHAR(50), Status VARCHAR(50), Team VARCHR(16) Zeit VARCHAR(10), Datum VARCHAR(10))");
	}
	
	public boolean createTicket(Player p, String ticket){
		Location loc = p.getLocation();
		String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
		String date = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
		String query = "INSERT INTO Tickets (Benutzername, Nachricht, X, Y, Z, Welt, Status, Team, Zeit, Datum) VALUES";
		query += "('" + p.getName() + "', '" + ticket + "', '" + loc.getBlockX() + "', '" + loc.getBlockY() + "', '" + loc.getBlockZ() + "', '" + loc.getWorld().getName() + "', '" + "" + "', '" + "" + "', '" + time + "', '" + date + "')";
		plugin.getMySQL().queryUpdate(query);
		triggerTeamForTicket();
		return false;
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
	
	private int countTicketsWithState(String state){
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
}
