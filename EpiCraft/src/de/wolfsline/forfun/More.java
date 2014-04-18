package de.wolfsline.forfun;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.wolfsline.Epicraft.Epicraft;

public class More implements CommandExecutor{

	private Epicraft plugin;
	
	public More(Epicraft plugin){
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(!p.hasPermission("epicraft.more")){
			p.sendMessage(plugin.error);
			return true;
		}
		p.getInventory().getItemInHand().setAmount(64);
		p.sendMessage(plugin.namespace + ChatColor.WHITE + "Items wurden auf 64 aufgestockt");
		return true;
	}

}
