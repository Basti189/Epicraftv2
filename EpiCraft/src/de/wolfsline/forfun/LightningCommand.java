package de.wolfsline.forfun;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LightningCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!cs.hasPermission("epicraft.lightning"))
			return true;
		if(args.length == 1){
			Player p = Bukkit.getPlayer(args[0]);
			if(p == null){
				cs.sendMessage(ChatColor.RED + "Spieler nicht vorhanden");
				return true;
			}
			p.getLocation().getWorld().strikeLightning(p.getLocation());
			cs.sendMessage(p.getName() + ChatColor.WHITE + " hat noch " + p.getHealth() + "Leben!");
			return true;
		}
		if(cs instanceof Player){
			Player sender = (Player) cs;
			sender.getLocation().getWorld().strikeLightning(sender.getTargetBlock(null, 200).getLocation());
		}
		return true;
	}

}
