package de.wolfsline.sign;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import de.wolfsline.Epicraft.Epicraft;

public class Systemsign implements CommandExecutor, Listener {

	private final String WORLD = "Survival";
	
	private Epicraft plugin;
	
	private World world;
	
	
	public Systemsign(Epicraft plugin){
		this.plugin = plugin;
		world = Bukkit.getServer().getWorld(WORLD);
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, runnable, 1200L, 20L);
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command cms, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		
		return false;
	}
	
	Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			if(world == null){
				return;
			}
			
		}
	};
}
