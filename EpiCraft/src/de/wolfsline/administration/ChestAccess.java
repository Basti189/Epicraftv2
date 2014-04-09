package de.wolfsline.administration;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import de.wolfsline.Epicraft.Epicraft;

public class ChestAccess implements CommandExecutor{
	
	Epicraft plugin;
	
	public ChestAccess(Epicraft plugin){
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(!(p.hasPermission("epicraft.chest.access") || p.isOp())){
			p.sendMessage(plugin.error);
			this.plugin.api.sendLog("[Epicraft - Kistenzugiff] " + p.getName() + " versuchte den Befehl zu benutzen");
			return true;
		}
		Block block = p.getTargetBlock(null, 200).getLocation().getBlock();
		if(block.getState() instanceof Chest){
			Chest chest = ((Chest) block.getState());
			p.openInventory(chest.getInventory());
			this.plugin.api.sendLog("[Epicraft - Kistenzugiff] " + p.getName() + " greift auf eine Kiste zu");
			return true;
		}
		else{
			p.sendMessage(plugin.namespace + ChatColor.RED + "Dies ist keine Kiste!");
			return true;
		}
	}

}
