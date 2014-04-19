package de.wolfsline.administration;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.wolfsline.Epicraft.Epicraft;

public class FlyCommand implements CommandExecutor{

	private Epicraft plugin;
	public FlyCommand(Epicraft plugin) {
		this.plugin = plugin;
	}
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(!p.hasPermission("epicraft.fly")){
			p.sendMessage(plugin.error);
			plugin.api.sendLog("[Epicraft - Flugmodus] " + p.getName() + " versuchte den Flugmodus zu benutzen");
			return true;
		}
		if(args.length == 0){
			if(p.getAllowFlight()){
				p.setAllowFlight(false);
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Flymodus wurde deaktiviert");
				plugin.api.sendLog("[Epicraft - Flugmodus] " + p.getName() + " deaktiviert Flugmodus");
				return true;
			}
			else{
				p.setAllowFlight(true);
				p.setFlying(true);
				p.setFlySpeed(0.1F);
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Flymodus wurde aktiviert");
				plugin.api.sendLog("[Epicraft - Flugmodus] " + p.getName() + " aktiviert Flugmodus");
				return true;
			}
		}
		else if(args.length == 1){
			float speed = 0.1f;
			try{
				speed = Float.valueOf(args[0]);
				if(speed < 0.0f && speed > 1.0f){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Zahl muss zwischen 0 und 1 seind!");
					return true;
				}
			}
			catch(NumberFormatException nfe){
				p.sendMessage(plugin.namespace + ChatColor.RED + args[1] + " ist keine Zahl!");
				return true;
			}
			if(!p.getAllowFlight()){
				p.setAllowFlight(true);
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Flymodus wurde aktiviert");
			}
			p.setFlySpeed(speed);
			plugin.api.sendLog("[Epicraft - Flugmodus] " + p.getName() + " aktiviert Flugmodus mit der Geschwindigkeit: " + String.valueOf(speed));
			return true;
		}
		return false;
	}

}
