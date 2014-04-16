package de.wolfsline.UUID;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.data.MySQL;

public class MyUUID implements CommandExecutor, Listener{
	
	private Epicraft plugin;
	
	public MyUUID(Epicraft plugin){
		this.plugin = plugin;
		plugin.getMySQL().queryUpdate("CREATE TABLE IF NOT EXISTS UUID (IID INT AUTO_INCREMENT PRIMARY KEY, UUID VARCHAR(36), Benutzername VARCHAR(16))");
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		p.sendMessage(plugin.namespace + ChatColor.WHITE + "Deine UUID: " + p.getUniqueId());
		return true;
	}
	
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event){
		Player p = event.getPlayer();
		UUID uuid = p.getUniqueId();
		boolean hasPlayerEntry = hasPlayerEntry(uuid);
		if(!hasPlayerEntry){//Eitnrag nicht vorhanden
			String query = "INSERT INTO UUID (UUID, Benutzername) VALUES ('" + uuid.toString() + "', '" + p.getName() + "')";
			plugin.getMySQL().queryUpdate(query);
		}
		else { //Eintrag vorhanden
			String query = "UPDATE UUID SET Benutzername='" + p.getName() + "' WHERE UUID='" + uuid.toString() + "'";
			plugin.getMySQL().queryUpdate(query);
		}
	}
	
	public boolean hasPlayerEntry(UUID uuid){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT * FROM UUID WHERE UUID='" + uuid.toString() + "'");
			rs = st.executeQuery();
			if(rs.next()){
				sql.closeRessources(rs, st);
				return true;
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
	
	public int getIIDFromUUID(UUID uuid){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT IID FROM UUID WHERE UUID='" + uuid + "'");
			rs = st.executeQuery();
			if(rs.next()){
				int iid = rs.getInt(1);
				sql.closeRessources(rs, st);
				return iid;
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			sql.closeRessources(rs, st);
		}
		return -1;
	}
	
	public UUID getUUIDFromPlayer(String name){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT UUID FROM UUID WHERE Benutzername='" + name + "'");
			rs = st.executeQuery();
			if(rs.next()){
				UUID uuid = UUID.fromString(rs.getString(1));
				sql.closeRessources(rs, st);
				return uuid;
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			sql.closeRessources(rs, st);
		}
		return null;
	}
	
	public String getNameFromUUID(UUID uuid){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT Benutzername FROM UUID WHERE UUID='" + uuid + "'");
			rs = st.executeQuery();
			if(rs.next()){
				String result = rs.getString(1);
				sql.closeRessources(rs, st);
				return result;
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			sql.closeRessources(rs, st);
		}
		return "";
	}
}
