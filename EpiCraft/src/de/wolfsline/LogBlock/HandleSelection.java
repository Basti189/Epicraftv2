package de.wolfsline.LogBlock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.data.MySQL;

public class HandleSelection {

	private Epicraft plugin;
	private LogBlock lb;
	
	public HandleSelection(Epicraft plugin, LogBlock lb){
		this.plugin = plugin;
		this.lb = lb;
	}
	
	public void RestoreSelectionFromPlayer(Player p, UUID targetUUID){
		
	}
	
	public void RestoreSelectionFromPlayerWithTimestamp(final Player p, final UUID targetUUID, final Date timestamp){
		final MySQL sql = this.plugin.getMySQL();
		final World world = p.getLocation().getWorld();
		new Thread(new Runnable() {
			@Override
			public void run() {
				int count = 0;
				long timeStart = System.currentTimeMillis();
				Connection conn = sql.getConnection();
				ResultSet rs = null;
				PreparedStatement st = null;
				try {
					p.sendMessage("Zeitstempel: " + getLongFromDate(timestamp));
					st = conn.prepareStatement("SELECT * FROM " + p.getLocation().getWorld().getName() + " WHERE UUID='" + targetUUID + "' AND Zeitstempel>='" + timestamp.getTime() + "'");
					rs = st.executeQuery();
					while(rs.next()){
						Material oldBlock = Material.getMaterial(rs.getString(3));
						Material newBlock = Material.getMaterial(rs.getString(4));
						int x = rs.getInt(6);
						int y = rs.getInt(7);
						int z = rs.getInt(8);
						if(oldBlock == newBlock)
							continue;
						if(lb.getWorldEditConnector().isCoorInsideSelection(p, x, y, z)){
							world.getBlockAt(rs.getInt(6), rs.getInt(7), rs.getInt(8)).setType(oldBlock);
							world.getBlockAt(rs.getInt(6), rs.getInt(7), rs.getInt(8)).setData(rs.getByte(5));
							count++;
						}
					}
					sql.closeRessources(rs, st);
				}
				catch(SQLException e){
					
				}
				long timeStop = System.currentTimeMillis();
				long timeDiff = (timeStop-timeStart)/1000;
				if(timeDiff == 0){
					p.sendMessage(plugin.namespace + ChatColor.GREEN + "Es wurden " + count + " Blöcke zurückgesetzt in einer Zeit von " + (timeStop-timeStart) + " Milisek.!");
				}
				else{
					p.sendMessage(plugin.namespace + ChatColor.GREEN + "Es wurden " + count + " Blöcke zurückgesetzt in einer Zeit von " + timeDiff + " Sek.!");
				}
				
			}
		}).start();
		
	}
	
	public void ShowChangesInSelection(Player p){
		
	}
	
	public void ShowChangesInSelectionFromPlayer(Player p, UUID targetUUID){
		
	}
	
	private int getLongFromDate(Date date){
		int n = (int) date.getTime() % 1000;
		return n<0 ? n+1000 : n;
	}
}
