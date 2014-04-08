package de.wolfsline.unused;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnHideCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player))
			return false;
		if(!cs.isOp() || (!cs.hasPermission("AdminTools.hide"))){
			cs.sendMessage(ChatColor.RED + "[Hide] Du hast keine Rechte für diesen Befehl");
			return true;
		}
		Player[] onlinePlayerList = Bukkit.getOnlinePlayers();
		if(label.equalsIgnoreCase("hide")){
			for(Player p : onlinePlayerList){
				p.hidePlayer((Player) cs);
			}
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "dynmap hide " + cs.getName());
			cs.sendMessage(ChatColor.YELLOW + "[Hide] Du bist nun unsichtbar");
			return true;
		}
		else if(label.equalsIgnoreCase("unhide")){
			for(Player p : onlinePlayerList){
				p.showPlayer((Player) cs);
			}
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "dynmap show " + cs.getName());
			cs.sendMessage(ChatColor.YELLOW + "[Hide] Du bist nun sichtbar");
			return true;
		}
		return false;
	}

}
