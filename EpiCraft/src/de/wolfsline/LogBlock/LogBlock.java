package de.wolfsline.LogBlock;

import org.bukkit.Bukkit;
import org.bukkit.World;

import de.wolfsline.Epicraft.Epicraft;

public class LogBlock {

	private Epicraft plugin;
	
	public LogBlock(Epicraft plugin){
		this.plugin = plugin;
		for(World w : Bukkit.getServer().getWorlds()){
			plugin.getMySQL().queryUpdate("CREATE IF NOT EXISTS " + w.getName() + "(" +
					"UUID VARCHAR(36)," +
					" Datum VARCHAR(10)," +
					" Uhrzeit VARCHAR(8)," +
					" `alter Block` VARCHAR(20)," +
					" `neuer Block` VARCHAR(20)," +
					" Datenwert TINYINT," +
					"X MEDIUMINT," +
					"Y MEDIUMINT," +
					"Z MEDIUMINT)");
		}
	}
	
	
}
