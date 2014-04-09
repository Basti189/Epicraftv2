package de.wolfsline.modify;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.wolfsline.Epicraft.Epicraft;

public class MECommand implements CommandExecutor{
	
	private Epicraft plugin;
	
	public MECommand(Epicraft plugin){
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler");
			return true;
		}
		Player p = (Player) cs;
		if(!p.hasPermission("epicraft.important")){
			p.sendMessage(plugin.error);
			return true;
		}
		if(args.length >= 1){
			String msg = "";
			for(String tmp : args){
				msg += tmp + " ";
			}
			for(Player player : Bukkit.getServer().getOnlinePlayers()){
				player.sendMessage(ChatColor.DARK_AQUA + "[WICHTIG] " + p.getName() + ChatColor.WHITE + ": " + msg);
			}
			return true;
		}
		p.sendMessage(plugin.namespace + ChatColor.RED + "/me <Nachricht>");
		return true;
	}

}
