package de.wolfsline.gs;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.wolfsline.Epicraft.Epicraft;

public class Grundstück implements CommandExecutor{
	
	private Epicraft plugin;
	
	public Grundstück(Epicraft plugin){
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(args.length == 0){
			
		}
		else if(args.length == 1){
			
		}
		else if(args.length == 2){
			
		}
		else if(args.length == 3){
			
		}
		else{
			p.sendMessage(plugin.namespace + ChatColor.RED + "/gs ");
			return true;
		}
		return false;
	}

}
