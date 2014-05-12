package de.wolfsline.LogBlock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.data.MySQL;

public class LogBlock {

	private Epicraft plugin;
	
	private WorldEditConnector wec;
	private HandleSelection hSel;
	
	public LogBlock(Epicraft plugin){
		this.plugin = plugin;
		wec = new WorldEditConnector(plugin);
		hSel = new HandleSelection(plugin, this);
		for(World w : Bukkit.getServer().getWorlds()){
			plugin.api.sendLog("[Epicraft - LogBlock] Datenbank für " + w.getName() + " wird initalisiert");
			plugin.getMySQL().queryUpdate("CREATE TABLE IF NOT EXISTS " + w.getName() + " (" +
					"UUID VARCHAR(36)," +
					" Zeitstempel VARCHAR(20)," +
					" `alter Block` VARCHAR(20)," +
					" `neuer Block` VARCHAR(20)," +
					" Datenwert TINYINT," +
					" X MEDIUMINT," +
					" Y MEDIUMINT," +
					" Z MEDIUMINT)");
		}
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 0);
		cal.add(Calendar.HOUR, 0);
		cal.add(Calendar.DAY_OF_MONTH, +1);
		cal.add(Calendar.SECOND, 0);
		long diffTime = cal.getTimeInMillis() - System.currentTimeMillis();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				clearOldEntrys();
			}
		}, diffTime, 86400000L);
	}
	
	public void showLocForTool(Player p, Location loc){
		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.GERMANY);
		
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT * FROM " + loc.getWorld().getName() + " WHERE X='" + x + "' AND Y ='" + y + "' AND Z='" + z + "' ORDER BY Zeitstempel DESC LIMIT 20");
			rs = st.executeQuery();
			boolean hasFound = false;
			p.sendMessage(plugin.namespace + ChatColor.GOLD + "Blockänderungen bei " + ChatColor.WHITE + x + " " + y + " " + z + ChatColor.GOLD + " Welt: " + ChatColor.WHITE + loc.getWorld().getName());
			while(rs.next()){
				hasFound = true;
				UUID targetUUID = UUID.fromString(rs.getString(1));
				String name = plugin.uuid.getNameFromUUID(targetUUID);
				Material oldBlock = Material.getMaterial(rs.getString(3));
				Material newBlock = Material.getMaterial(rs.getString(4));
				String timeStamp = sdf.format(new Date(rs.getLong(2)));
				String action = ChatColor.WHITE + timeStamp + ChatColor.GOLD + " -> " + ChatColor.WHITE + name + ChatColor.GOLD;
				if(oldBlock == newBlock){
					switch (oldBlock) {
					case LEVER:
					case WOOD_BUTTON:
					case STONE_BUTTON:
					case FENCE_GATE:
					case WOODEN_DOOR:
					case TRAP_DOOR:
					case NOTE_BLOCK:
					case DIODE_BLOCK_OFF:
					case DIODE_BLOCK_ON:
					case REDSTONE_COMPARATOR_OFF:
					case REDSTONE_COMPARATOR_ON:
						action += " hat " + ChatColor.WHITE + oldBlock.toString() + ChatColor.GOLD + " benutzt";
						break;
					case CHEST:
					case TRAPPED_CHEST:
						action += " hat die " + ChatColor.WHITE + oldBlock.toString() + ChatColor.GOLD + " geöffnet";
						break;
					case CAKE_BLOCK:
						action += " hat vom " + ChatColor.WHITE + oldBlock.toString() + ChatColor.GOLD + " gegessen";
						break;
					case WOOD_PLATE:
					case STONE_PLATE:
					case IRON_PLATE:
					case GOLD_PLATE:
					case TRIPWIRE:
						action += " hat auf die " + ChatColor.WHITE + oldBlock.toString() + ChatColor.GOLD + " getreten";
					break;
					default:
						break;
					}
					
				}
				else{
					if(oldBlock == Material.AIR){ //Block gesetzt
						action += ChatColor.GOLD + " setzte " + ChatColor.WHITE + newBlock.toString();
					}
					else{ //Block gesetzt
						action += ChatColor.GOLD + " zerstörte " + ChatColor.WHITE + oldBlock.toString();
					}
				}
				p.sendMessage(action);
				
			}
			sql.closeRessources(rs, st);
			if(!hasFound){
				p.sendMessage(ChatColor.RED + "Keine Einträge gefunden");
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void clearOldEntrys(){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -2);
		Date date = cal.getTime();
		long dayBefor = date.getTime();
		for(World w : Bukkit.getServer().getWorlds()){
			plugin.api.sendLog("[Epicraft - LogBlock] Lösche alte Einträge der Welt " + w.getName());
			plugin.getMySQL().queryUpdate("DELETE FROM " + w.getName() + " WHERE Zeitstempel<='" + dayBefor + "'");
		}
	}
	
	public WorldEditConnector getWorldEditConnector(){
		return this.wec;
	}
	
	public HandleSelection getSelectionHandler(){
		return this.hSel;
	}
	
}
