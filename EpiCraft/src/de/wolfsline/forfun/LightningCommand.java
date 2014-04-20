package de.wolfsline.forfun;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.wolfsline.Epicraft.Epicraft;

public class LightningCommand implements CommandExecutor{
	
	private Epicraft plugin;

	public LightningCommand(Epicraft plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!cs.hasPermission("epicraft.lightning")){
			cs.sendMessage(plugin.error);
			plugin.api.sendLog("[Epicraft - Blitz] " + cs.getName() + " wollte auf den Befehl zugreifen");
			return true;
		}
		if(args.length == 1){
			UUID targetUUID = plugin.uuid.getUUIDFromPlayer(args[0]);
			if(targetUUID == null){
				cs.sendMessage(plugin.uuid.ERROR);
				return true;
			}
			Player p = Bukkit.getPlayer(targetUUID);
			if(p == null){
				cs.sendMessage(ChatColor.RED + "Spieler nicht vorhanden");
				return true;
			}
			p.getLocation().getWorld().strikeLightning(p.getLocation());
			cs.sendMessage(plugin.namespace + ChatColor.WHITE + p.getName() + " wurde elektrisiert!");
			return true;
		}
		if(cs instanceof Player){
			Player sender = (Player) cs;
			sender.getLocation().getWorld().strikeLightning(sender.getTargetBlock(null, 200).getLocation());
		}
		return true;
	}

}
