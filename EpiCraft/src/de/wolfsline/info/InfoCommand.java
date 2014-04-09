package de.wolfsline.info;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.wolfsline.Epicraft.Epicraft;

public class InfoCommand implements CommandExecutor{
	
	private Epicraft plugin;
	public InfoCommand(Epicraft plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(label.equalsIgnoreCase("info")){
			cs.sendMessage(plugin.namespace + ChatColor.WHITE + "---- [INFO] ----\n" +
					"/ts -> Zeigt dir unsere TeamSpeak3 Adresse\n" +
					"---- [INFO] ----");
			return true;
		}
		else if(label.equalsIgnoreCase("ts") || label.equalsIgnoreCase("ts3") || label.equalsIgnoreCase("teamspeak")){
			cs.sendMessage(plugin.namespace + ChatColor.WHITE + "Unsere TeamSpeak3 Adresse:");
			cs.sendMessage(plugin.namespace + ChatColor.WHITE + "ts3.epicraft.de");
			return true;
		}
		return true;
	}

}
