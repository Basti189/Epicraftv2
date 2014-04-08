package de.wolfsline.unused;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.wolfsline.Epicraft.Epicraft;

public class ReloadCommand implements CommandExecutor{

	private Epicraft plugin;
	public ReloadCommand(Epicraft epiCraft) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!cs.isOp()){
			return true;
		}
		this.plugin.reloadConfig();
		return false;
	}

}
