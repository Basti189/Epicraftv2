package de.wolfsline.tcp;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.wolfsline.Epicraft.Epicraft;

public class appUserCommand implements CommandExecutor {
	
	private Epicraft plugin;
	private Server serv;

	public appUserCommand(Epicraft plugin, Server serv) {
		this.plugin = plugin;
		this.serv = serv;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(args.length == 0){
			if(!cs.hasPermission("epicraft.app.show")){
				cs.sendMessage(plugin.namespace + ChatColor.RED + "Du hast keinen Zugriff auf diesen Befehl!");
				return true;
			}
			String User = "";
			for(ClientHandler handler : serv.getClients()){
				User += handler.getUsername() + ", ";
			}
			cs.sendMessage(plugin.namespace + ChatColor.WHITE + "Appuser:");
			cs.sendMessage(User);
			return true;
		}
		else if(args.length == 2){
			if(!(cs.isOp() || cs.hasPermission("epicraft.app.team"))){
				cs.sendMessage(plugin.namespace + ChatColor.RED + "Du hast keinen Zugriff auf diesen Befehl!");
				return true;
			}
			String command = args[0];
			String who = args[1];
			if(command.equals("kick")){
				for(ClientHandler handler : serv.getClients()){
					if(handler.getUsername().equalsIgnoreCase(who)){
						handler.kickUser();
						return true;
					}
					cs.sendMessage(plugin.namespace + ChatColor.RED + "Unbekannter Spieler: " + who + "!");
					return true;
				}
			}
			else{
				cs.sendMessage(plugin.namespace + ChatColor.RED + "Unbekannter Befehl: " + command + "!");
				return true;
			}
		}
			
		
		return false;
	}

}
