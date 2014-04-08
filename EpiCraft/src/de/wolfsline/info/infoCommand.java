package de.wolfsline.info;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.wolfsline.Epicraft.Epicraft;

public class infoCommand implements CommandExecutor{
	
	private Epicraft plugin;
	public infoCommand(Epicraft plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		cs.sendMessage(plugin.namespace + ChatColor.WHITE + "ts3.epicraft.de");
		return true;
	}

}
