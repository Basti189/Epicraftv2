package de.wolfsline.administration;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.wolfsline.Epicraft.Epicraft;

public class SpawnCommand implements CommandExecutor{
	private Epicraft plugin;
	
	public SpawnCommand(Epicraft plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			return true;
		}
		Player p = (Player) cs;
		if(!p.hasPermission("epicraft.spawn")){
			p.sendMessage(plugin.error);
			plugin.api.sendLog("[Epicraft - Spawn] " + p.getName() + " hat versucht sich zum Spawn zu teleportieren");
			return true;
		}
		if(args.length == 0){
			if(p.isInsideVehicle()){
				p.sendMessage(plugin.namespace + ChatColor.RED + "Du kannst zurzeit nicht teleportiert werden!");
				plugin.api.sendLog("[Epicraft - Spawn] " + p.getName() + " sitzt in einem " + p.getVehicle().getType().toString() + " und kann nicht teleportiert werden");
				return true;
			}
			p.teleport(Bukkit.getServer().getWorld("world").getSpawnLocation());
			p.sendMessage(plugin.namespace + ChatColor.WHITE + "Willkommen am Spawn!");
			plugin.api.sendLog("[Epicraft - Spawn] " + p.getName() + " hat sich zum Spawn teleportiert");
			return true;
		}
		if(args.length == 1){
			if(!p.hasPermission("epicraft.spawn.set")){
				return false;
			}
			p.getWorld().setSpawnLocation((int)p.getLocation().getX(), (int)p.getLocation().getY(),(int)p.getLocation().getZ());
			p.sendMessage(plugin.namespace + ChatColor.WHITE + "Spawn gesetzt!");
			return true;
		}
		return false;
	}
}
