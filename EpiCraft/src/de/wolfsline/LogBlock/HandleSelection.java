package de.wolfsline.LogBlock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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
	
	public void RestoreSelectionFromPlayer(final Player p, final UUID targetUUID){
		if(!lb.getWorldEditConnector().hasPlayerSelected(p)){
			p.sendMessage(plugin.namespace + ChatColor.RED + "Es wurde kein Bereich markiert!");
			return;
		}
		final MySQL sql = this.plugin.getMySQL();
		final World world = p.getLocation().getWorld();
		new Thread(new Runnable() {
			@Override
			public void run() {
				int count = 0;
				long timeStart = System.currentTimeMillis();
				Connection conn = sql.getConnection();
				ResultSet rs = null;
				PreparedStatement st = null;;
				try {
					st = conn.prepareStatement("SELECT * FROM " + p.getLocation().getWorld().getName() + " WHERE UUID='" + targetUUID + "' ORDER BY Zeitstempel DESC");
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
	
	public void RestoreSelectionFromPlayerWithTimestamp(final Player p, final UUID targetUUID, final Date timestamp){
		if(!lb.getWorldEditConnector().hasPlayerSelected(p)){
			p.sendMessage(plugin.namespace + ChatColor.RED + "Es wurde kein Bereich markiert!");
			return;
		}
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
					st = conn.prepareStatement("SELECT * FROM " + p.getLocation().getWorld().getName() + " WHERE UUID='" + targetUUID + "' AND Zeitstempel>='" + timestamp.getTime() + "' ORDER BY Zeitstempel DESC");
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
	
	public void ShowChangesInSelection(final Player p){
		if(!lb.getWorldEditConnector().hasPlayerSelected(p)){
			p.sendMessage(plugin.namespace + ChatColor.RED + "Es wurde kein Bereich markiert!");
			return;
		}
		final MySQL sql = this.plugin.getMySQL();
		p.sendMessage(plugin.namespace + ChatColor.GOLD + "---------------[LogBlock]---------------");
		new Thread(new Runnable() {
			@Override
			public void run() {
				int count = 0;
				Connection conn = sql.getConnection();
				ResultSet rs = null;
				PreparedStatement st = null;
				SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.GERMANY);
				try {
					st = conn.prepareStatement("SELECT * FROM " + p.getLocation().getWorld().getName() + " ORDER BY Zeitstempel DESC");
					rs = st.executeQuery();
					while(rs.next()){
						Material oldBlock = Material.getMaterial(rs.getString(3));
						Material newBlock = Material.getMaterial(rs.getString(4));
						int x = rs.getInt(6);
						int y = rs.getInt(7);
						int z = rs.getInt(8);
						if(oldBlock == newBlock){
							continue;
						}
						if(lb.getWorldEditConnector().isCoorInsideSelection(p, x, y, z)){
							UUID targetUUID = UUID.fromString(rs.getString(1));
							if(targetUUID == null){
								continue;
							}
							p.sendMessage(ChatColor.WHITE + 
								sdf.format(new Date(rs.getLong(2))) + 
								" " + 
								plugin.uuid.getNameFromUUID(targetUUID) + 
								ChatColor.GOLD + " - " + ChatColor.WHITE +  
								oldBlock + 
								ChatColor.GOLD + " -> " + 
								ChatColor.WHITE + newBlock + ":" + rs.getByte(5));
							count++;
						}
						if(count == 20){
							break;
						}
					}
					sql.closeRessources(rs, st);
					p.sendMessage(plugin.namespace + ChatColor.GOLD + "---------------[LogBlock]---------------");
				}
				catch(SQLException e){
					
				}
			}
		}).start();
	}
	
	public void ShowChangesInSelectionFromPlayer(final Player p, final UUID targetUUID){
		if(!lb.getWorldEditConnector().hasPlayerSelected(p)){
			p.sendMessage(plugin.namespace + ChatColor.RED + "Es wurde kein Bereich markiert!");
			return;
		}
		final MySQL sql = this.plugin.getMySQL();
		p.sendMessage(plugin.namespace + ChatColor.GOLD + "---------------[LogBlock]---------------");
		new Thread(new Runnable() {
			@Override
			public void run() {
				int count = 0;
				Connection conn = sql.getConnection();
				ResultSet rs = null;
				PreparedStatement st = null;
				SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.GERMANY);
				try {
					st = conn.prepareStatement("SELECT * FROM " + p.getLocation().getWorld().getName() + " WHERE UUID='" + targetUUID + "' ORDER BY Zeitstempel DESC");
					rs = st.executeQuery();
					while(rs.next()){
						Material oldBlock = Material.getMaterial(rs.getString(3));
						Material newBlock = Material.getMaterial(rs.getString(4));
						int x = rs.getInt(6);
						int y = rs.getInt(7);
						int z = rs.getInt(8);
						if(oldBlock == newBlock){
							continue;
						}
						if(lb.getWorldEditConnector().isCoorInsideSelection(p, x, y, z)){
							UUID targetUUID = UUID.fromString(rs.getString(1));
							if(targetUUID == null){
								continue;
							}
							p.sendMessage(ChatColor.WHITE + 
								sdf.format(new Date(rs.getLong(2))) + 
								" " + 
								plugin.uuid.getNameFromUUID(targetUUID) + 
								ChatColor.GOLD + " - " + ChatColor.WHITE +  
								oldBlock + 
								ChatColor.GOLD + " -> " + 
								ChatColor.WHITE + " " + newBlock + ":" + rs.getByte(5));
							count++;
						}
						if(count == 20){
							break;
						}
					}
					sql.closeRessources(rs, st);
					p.sendMessage(plugin.namespace + ChatColor.GOLD + "---------------[LogBlock]---------------");
				}
				catch(SQLException e){
					
				}
			}
		}).start();
	}
}
