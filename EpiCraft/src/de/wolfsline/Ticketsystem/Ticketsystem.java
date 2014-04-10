package de.wolfsline.Ticketsystem;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.wolfsline.Epicraft.Epicraft;

public class Ticketsystem implements CommandExecutor{
	
	private Epicraft plugin;
	private Ticketsystem_Daten data;
	
	public Ticketsystem(Epicraft plugin){
		this.plugin = plugin;
		this.data = new Ticketsystem_Daten(plugin);
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(!p.hasPermission("epicraft.ticket")){
			p.sendMessage(plugin.error);
			plugin.api.sendLog("[Epicraft - Ticketsystem] " + p.getName() + " hat versucht auf das Ticketsystem zuzugreifen!");
			return true;
		}
		if(args.length == 0){
			return false;
		}
		else if(args.length >= 1){
			String ticket = "";
			for(String tmp : args){
				ticket += tmp + " ";
			}
			data.createTicket(p, ticket);
			p.sendMessage(plugin.namespace + ChatColor.WHITE + "Ticket wurde erstellt");
			return true;
		}
		else {
			p.sendMessage(plugin.namespace + ChatColor.RED + "Bitte gib ein Grund für das Ticket an!");
			return true;
		}
	}

}
