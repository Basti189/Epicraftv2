package de.wolfsline.LogBlock;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import de.wolfsline.Epicraft.Epicraft;

public class BlockPlaceListener implements Listener{

	private Epicraft plugin;
	private LogBlock lb;
	
	private final Material toolBLock = Material.BEDROCK;
	
	public BlockPlaceListener(Epicraft plugin, LogBlock lb){
		this.plugin = plugin;
		this.lb = lb;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockPlaceEvent(BlockPlaceEvent event){
		Player p = event.getPlayer();
		Block block = event.getBlock();
		if(block.getType() == toolBLock){
			if(p.hasPermission("epicraft.logblock.view")){
				event.setCancelled(true);
				lb.showLocForTool(p, block.getLocation());
				return;
			}
		}
		Location loc = block.getLocation();
		
		Material material = block.getType();
		byte data = block.getData();
		
		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();
		
		String query = "INSERT INTO " + loc.getWorld().getName() + " (" +
				"UUID," +
				" Zeitstempel," +
				" `alter Block`," +
				" `neuer Block`," +
				" Datenwert," +
				" X," +
				" Y," +
				" Z)" +
				" VALUES (" +
				"'" + p.getUniqueId() + "'," +
				"'" + System.currentTimeMillis() + "'," +
				"'" + "AIR" + "'," +
				"'" + material.toString() + "'," +
				"'" + String.valueOf(data) + "'," +
				"'" + x + "'," +
				"'" + y + "'," +
				"'" + z + "')";
		
		this.plugin.getMySQL().queryUpdate(query);
	}
}
