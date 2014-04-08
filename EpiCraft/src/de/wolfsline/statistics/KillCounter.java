package de.wolfsline.statistics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.data.MySQL;

public class KillCounter implements Listener{
	
	private Epicraft plugin;
	
	public KillCounter(Epicraft plugin){
		this.plugin = plugin;
		MySQL sql = this.plugin.getMySQL();
		sql.queryUpdate("CREATE TABLE IF NOT EXISTS statistics (id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(16), mob VARCHAR(50), count INT)");
	}
	
	@EventHandler
	public void onKill(EntityDeathEvent e){
		try{
			Player killer = e.getEntity().getKiller();
			if(killer == null)
				return;
			if(killer.getWorld().getName().equalsIgnoreCase("Games"))
				return;
			String mob = e.getEntityType().name();
			updateDatabase(killer, mob);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private boolean updateDatabase(Player p, String mob){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT * FROM statistics WHERE username='" + p.getName() + "'");
			rs = st.executeQuery();
			while(rs.next()){
				if(rs.getString(3).equalsIgnoreCase(mob)){
					int count = rs.getInt(4);
					sql.closeRessources(rs, st);
					String update = "UPDATE statistics SET count='" + String.valueOf(count+1) + "' WHERE username='" + p.getName() + "' and mob='" + mob + "'";
					sql.queryUpdate(update);
					return true;
				}
			}
			sql.closeRessources(rs, st);
			newEntry(p, mob);
			return true;
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private void newEntry(Player p, String mob){
		MySQL sql = this.plugin.getMySQL();
		String update = "INSERT INTO statistics (username, mob, count) VALUES ('" + p.getName() + "', '" + mob + "', '1' )";
		sql.queryUpdate(update);
	}

}
