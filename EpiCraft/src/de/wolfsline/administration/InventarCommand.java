package de.wolfsline.administration;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.wolfsline.Epicraft.Epicraft;


public class InventarCommand implements CommandExecutor {

    private Epicraft plugin;
    public InventarCommand(Epicraft plugin) {
        this.plugin = plugin;   
    }
    
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if(!(cs instanceof Player)){
            cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
            return true;
        }
        Player p = (Player) cs;
        if(!p.hasPermission("epicraft.inv")){
            p.sendMessage(plugin.error);
            plugin.api.sendLog("[Epicraft - Inventar] " + p.getName() + " versucht auf den Befehl zuzugreifen");
            return true;
        }
        if(args.length < 1){
            p.sendMessage(plugin.namespace + ChatColor.RED + "Zu wenig Argumente!");
            return true;
        }
        UUID targetUUID = plugin.uuid.getUUIDFromPlayer(args[0]);
        if(targetUUID == null){
        	p.sendMessage(plugin.uuid.ERROR);
        	return true;
        }
        Player pu = Bukkit.getPlayer(targetUUID);
        if(pu == null){
            p.sendMessage(plugin.namespace + ChatColor.RED + "Spieler existiert nicht, oder ist nicht online!");
            return true;
        }
        if(pu.isOp() && !p.isOp()){
            p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast keinen Zugriff auf das Inventar!");
            return true;
        }
        if(p.getLocation().getWorld().getName().equals(pu.getLocation().getWorld().getName()) || p.isOp()){
        	p.sendMessage(plugin.namespace + "Inventar von " + args[0] + " wird ge�ffnet");
        	plugin.api.sendLog("[Epicraft - Inventar] " + p.getName() + " �ffnet Inventar von " + args[0]);
        	p.openInventory(pu.getInventory());
        	return true;
        }
        else{
        	p.sendMessage(plugin.namespace + ChatColor.RED + "Das Zielinventar befindet sich auf einer anderen Welt!");
        	p.sendMessage(plugin.namespace + ChatColor.WHITE + pu.getName() + " befindet sich auf der Welt: " + ChatColor.GOLD + pu.getLocation().getWorld().getName());
        }
      
        return true;
    }
    
}
