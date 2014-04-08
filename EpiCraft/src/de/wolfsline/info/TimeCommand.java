package de.wolfsline.info;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.wolfsline.Epicraft.Epicraft;

public class TimeCommand implements CommandExecutor {

	
	private Epicraft plugin;
	
	public TimeCommand(Epicraft plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		Date date = new Date();
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		cs.sendMessage(plugin.namespace + ChatColor.WHITE + " " + timeFormat.format(date) + " Uhr");
		cs.sendMessage(plugin.namespace + ChatColor.WHITE + " " + dateFormat.format(date) + "");
		return true;
	}

}
