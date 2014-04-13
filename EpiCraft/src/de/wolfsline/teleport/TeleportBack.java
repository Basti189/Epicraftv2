package de.wolfsline.teleport;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import de.wolfsline.Epicraft.Epicraft;

public class TeleportBack implements CommandExecutor, Listener{
	
	private HashMap<String, Location> deathLocation = new HashMap<String, Location>();
	
	private Epicraft plugin;
	
	public TeleportBack(Epicraft plugin){
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(!p.hasPermission("epicraft.tp.back")){
			p.sendMessage(plugin.error);
			plugin.api.sendLog("[Epicraft - Back] " + p.getName() + " hat versucht auf den Befehl zuzugreifen");
			return true;
		}
		if(p.isInsideVehicle()){
			p.sendMessage(plugin.namespace + ChatColor.RED + "Du kannst nicht teleportiert werden");
			return true;
		}
		if(deathLocation.containsKey(p.getName())){
			p.teleport(deathLocation.get(p.getName()));
			p.sendMessage(plugin.namespace + ChatColor.WHITE + "Du wurdest an deinen letzen Todespunkt teleportiert");
			plugin.api.sendLog("[Epicraft - Back] " + p.getName() + " wurde an den letzten Todespunkt teleportiert");
			deathLocation.remove(p.getName());
			return true;
		}
		else{
			p.sendMessage(plugin.namespace + ChatColor.RED + "Kein gespeicherten Todespunkt gefunden!");
			return true;
		}
	}
	
	@EventHandler
	public void onDeathEvent(PlayerDeathEvent event){
		if(event.getEntity() instanceof Player){
			Player p = event.getEntity();
			Location loc = new Location(p.getLocation().getWorld(), p.getLocation().getX(), (p.getLocation().getY() + 2.0D), p.getLocation().getZ());
			deathLocation.put(p.getName(), loc);
		}
	}

}
