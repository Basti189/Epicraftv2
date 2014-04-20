package de.wolfsline.administration;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.wolfsline.Epicraft.Epicraft;

public class TeleportCommand implements CommandExecutor{

	private HashMap<UUID, Location> map = new HashMap<UUID, Location>();
	private Epicraft plugin;

	public TeleportCommand(Epicraft plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(!p.hasPermission("epicraft.teleport")){
			p.sendMessage(plugin.error);
			plugin.api.sendLog("[Epicraft - Teleport] " + p.getName() + " hat versucht auf den Teleport-Befehl zuzugreifen");
			return true;
		}
		if(label.equalsIgnoreCase("tp")){
			if(args.length == 1){
				if(args[0].equalsIgnoreCase("back")){
					if(!map.containsKey(p.getUniqueId())){
						p.sendMessage(plugin.namespace + ChatColor.RED + "Keine gespeicherte Location gefunden!");
						return true;
					}
					p.teleport(map.get(p.getUniqueId()));
					map.remove(p.getUniqueId());
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Du wurdest zurück teleportiert");
					return true;
				}
				UUID destinationUUID = plugin.uuid.getUUIDFromPlayer(args[0]);
				if(destinationUUID == null){
					p.sendMessage(plugin.uuid.ERROR);
					return true;
				}
				Player destinationPlayer = Bukkit.getServer().getPlayer(destinationUUID);
				if(destinationPlayer == null){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Der Spieler ist nicht online!");
					return true;
				}
				map.put(p.getUniqueId(), p.getLocation());
				p.setAllowFlight(true);
				p.teleport(destinationPlayer.getLocation());
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Du wurdest zu " + destinationPlayer.getName() + " teleportiert.");
				plugin.api.sendLog("[Epicraft - Teleport] " + p.getName() + " hat sich zu " + destinationPlayer.getName() + " teleportert");
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Flymode wurde aktiviert.");
				return true;
			}
			else if(args.length == 2){
				UUID getUUID = plugin.uuid.getUUIDFromPlayer(args[0]);
				UUID toUUID = plugin.uuid.getUUIDFromPlayer(args[1]);
				if(getUUID == null || toUUID == null){
					p.sendMessage(plugin.uuid.ERROR);
					return true;
				}
				Player getPlayer = Bukkit.getServer().getPlayer(getUUID);
				Player toPlayer = Bukkit.getServer().getPlayer(toUUID);
				
				if(getPlayer == null || toPlayer == null){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Der Spieler ist nicht online!");
					return true;
				}
				getPlayer.teleport(toPlayer.getLocation());
				p.sendMessage(plugin.namespace + ChatColor.WHITE + getPlayer.getName() + " wurde zu " + toPlayer.getName() + " teleportiert.");
				plugin.api.sendLog("[Epicraft - Teleport] " + getPlayer.getName() + " wurde zu " + toPlayer.getName() + " teleportiert.");
				return true;
			}
			else if(args.length == 3){
				try{
					float x = Float.valueOf(args[0]);
					float y = Float.valueOf(args[1]);
					float z = Float.valueOf(args[2]);
					Location myLocation = new Location(p.getWorld(), x, y, z);
					map.put(p.getUniqueId(), p.getLocation());
					p.teleport(myLocation);
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Du wurdest teleportiert.");
					plugin.api.sendLog("[Epicraft - Teleport] " + p.getName() + " wurde zu X: " + String.valueOf(x) + " Y:" + String.valueOf(y) + " Z:" + String.valueOf(z) + " teleportiert.");
				}
				catch(NumberFormatException nfe){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Bitte gebe gültige Zahlen an!");
				}
				return true;
			}	
		}
		return true;
	}
	
}
