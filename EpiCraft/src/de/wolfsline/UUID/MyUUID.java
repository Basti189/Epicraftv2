package de.wolfsline.UUID;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.data.MySQL;

public class MyUUID implements Listener{
	
	private Epicraft plugin;
	
	public MyUUID(Epicraft plugin){
		this.plugin = plugin;
		plugin.getMySQL().queryUpdate("CREATE TABLE IF NOT EXISTS UUID (IID INT AUTO_INCREMENT PRIMARY KEY, UUID VARCHAR(36), Benutzername VARCHAR(16))");
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
			st = conn.prepareStatement("SELECT IID FROM UUID WHERE UUID='" + uuid.toString() + "'");
			rs = st.executeQuery();
			if(rs.next()){
				sql.closeRessources(rs, st);
				return rs.getInt(1);
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

}
