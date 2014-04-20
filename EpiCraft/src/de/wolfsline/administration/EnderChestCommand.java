package de.wolfsline.administration;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.wolfsline.Epicraft.Epicraft;


public class EnderChestCommand implements CommandExecutor {

    private Epicraft plugin;
    public EnderChestCommand(Epicraft plugin) {
        this.plugin = plugin;   
    }
    
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if(!(cs instanceof Player)){
            cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
            return true;
        }
        Player p = (Player) cs;
        if(!p.hasPermission("epicraft.enderchest")){
            p.sendMessage(plugin.error);
            plugin.api.sendLog("[Epicraft - Enderchest] " + p.getName() + " versuchte auf die Enderchest von " + args[0] + " zuzugreifen");
            return true;
        }
        if(args.length < 1){
        	p.openInventory(p.getEnderChest());
            return true;
        }
        UUID targetUUID = plugin.uuid.getUUIDFromPlayer(args[0]);
        if(targetUUID == null){
        	p.sendMessage(plugin.uuid.ERROR);
        	return true;
        }
        Player pu = Bukkit.getPlayer(targetUUID);
        if(pu == null){
        	p.sendMessage(plugin.namespace + ChatColor.RED + "Spieler ist nicht online!");
        	return true;
        }
        if(pu.isOp() && !p.isOp()){
        	p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast keinen Zugriff auf diese EnderChest!");
	        return true;
        }
        if(p.getLocation().getWorld().getName().equals(pu.getLocation().getWorld().getName()) || p.isOp()){
        	p.sendMessage(plugin.namespace + "EnderChest von " + args[0] + " wird geöffnet");
        	plugin.api.sendLog("[Epicraft - Enderchest] " + p.getName() + " öffnet Enderchest von " + args[0]);
        	p.openInventory(pu.getEnderChest());
        }
        else{
        	p.sendMessage(plugin.namespace + ChatColor.RED + "Die Zielenderchest befindet sich auf einer anderen Welt!");
        	p.sendMessage(plugin.namespace + ChatColor.WHITE + pu.getName() + " befindet sich auf der Welt: " + ChatColor.GOLD + pu.getLocation().getWorld().getName());
        }
      
        return true;
    }
}
