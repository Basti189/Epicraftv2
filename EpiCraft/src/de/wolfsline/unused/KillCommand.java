package de.wolfsline.unused;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KillCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		Player p = null;
		if(args.length == 1){
			if(cs.isOp()){
				p = Bukkit.getPlayer(args[0]);
				if(p == null){
					cs.sendMessage(ChatColor.RED + args[0] + " wurde nicht gefunden");
					return true;
				}
				else{
					p.setHealth(0);
					cs.sendMessage(ChatColor.GOLD + p.getName() + " wurde getötet");
					p.sendMessage(ChatColor.GOLD + cs.getName() + " hat dich getötet");
					return true;
				}
			}
			else{
				cs.sendMessage(ChatColor.RED + "Du hast keine Rechte für diesen Befehl");
				return true;
			}
		}
		else{
			p = (Player) cs;
			p.setHealth(0);
			return true;
		}
	}
}
