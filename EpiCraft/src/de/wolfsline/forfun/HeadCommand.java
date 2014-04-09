package de.wolfsline.forfun;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Skull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import de.wolfsline.Epicraft.Epicraft;

public class HeadCommand implements CommandExecutor{

	private Epicraft plugin;
	
	public HeadCommand(Epicraft plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args){
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(!p.isOp()){
			p.sendMessage(plugin.error);
			return true;
		}
		
		if(args.length == 0){
			p.getInventory().setHelmet(p.getItemInHand());
			p.sendMessage(plugin.namespace + "Du hast nun einen neuen Kopfschmuck");
			return true;
		}
		else if(args.length == 1){
			String name = args[0];
			ItemStack stackSkull = new ItemStack(Material.SKULL_ITEM, 1,(byte) 3);
            SkullMeta metaSkull = (SkullMeta) stackSkull.getItemMeta();
            metaSkull.setOwner(name);
            stackSkull.setItemMeta(metaSkull);
            p.getInventory().setHelmet(stackSkull);
			p.sendMessage(plugin.namespace + "Du hast nun " + name + "'s Kopf");
			return true;
		}
		return true;
	}

}
