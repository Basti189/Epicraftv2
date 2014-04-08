package de.wolfsline.forfun;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class VelocityShooter implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(!p.isOp())
			return true;
		Vector dir = p.getLocation().getDirection();
		Vector vec = new Vector(dir.getX() * 25.8D, 10.0D, dir.getZ() * 1.2D);
        p.setVelocity(vec);
        //p.setFallDistance(-100.0F);
		return true;
	}

}
