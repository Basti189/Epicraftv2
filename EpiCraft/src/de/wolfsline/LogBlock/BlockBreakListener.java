package de.wolfsline.LogBlock;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import de.wolfsline.Epicraft.Epicraft;

public class BlockBreakListener implements Listener{

	private Epicraft plugin;
	
	public BlockBreakListener(Epicraft plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerBreakBlock(BlockBreakEvent event){
		Player p = event.getPlayer();
		Block block = event.getBlock();
		Location loc = block.getLocation();
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.GERMANY);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
		Date date = new Date();
		
		Material material = block.getType();
		byte data = block.getData();
		
		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();
		
		String query = "INSERT INTO " + loc.getWorld().getName() + " (" +
				"UUID," +
				" Datum," +
				" Uhrzeit," +
				" `alter Block`," +
				" `neuer Block`," +
				" Datenwert," +
				" X," +
				" Y," +
				" Z)" +
				" VALUES (" +
				"'" + p.getUniqueId() + "'," +
				"'" + dateFormat.format(date) + "'," +
				"'" + timeFormat.format(date) + "'," +
				"'" + material.toString() + "'," +
				"'" + 0 + "'," +
				"'" + String.valueOf(data) + "'," +
				"'" + x + "'," +
				"'" + y + "'," +
				"'" + z + "',)";
		
		this.plugin.getMySQL().queryUpdate(query);
		
	}
}
