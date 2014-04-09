package de.wolfsline.forfun;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import de.wolfsline.Epicraft.Epicraft;

public class TntCommand implements CommandExecutor{

	private Epicraft plugin;
	public TntCommand(Epicraft plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(!(p.hasPermission("epicraft.grenade.use") || p.isOp())){
			p.sendMessage(plugin.error);
			return true;
		}
		if(args.length == 0){
			Location loc = p.getTargetBlock(null, 200).getLocation();
			loc.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT);
			return true;
		}
		return false;
	}

}
