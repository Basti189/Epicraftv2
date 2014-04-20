package de.wolfsline.administration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import de.wolfsline.Epicraft.Epicraft;

public class UnHideCommand implements CommandExecutor, Listener{

    private Epicraft plugin;
    
    private List<UUID> list = new ArrayList<UUID>();
    
    public UnHideCommand(Epicraft plugin) {
        this.plugin = plugin;
    }

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(!p.hasPermission("epicraft.hide")){
			p.sendMessage(plugin.error);
			plugin.api.sendLog("[Epicraft - Unsichtbar] " + cs.getName() + " hat versucht auf den Hide-Befehl zuzugreifen");
			return true;
		}
		Player[] onlinePlayerList = Bukkit.getOnlinePlayers();
		if(label.equalsIgnoreCase("hide")){
			for(Player player : onlinePlayerList){
				if(!player.hasPermission("epicraft.hide.use"))
					player.hidePlayer(p);
			}
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "dynmap hide " + p.getName());
			p.sendMessage(plugin.namespace + ChatColor.WHITE + "Du bist nun unsichtbar");
			plugin.api.sendLog("[Epicraft - Unsichtbar] " + p.getName() + " ist nun Unsichtbar");
			if(!list.contains(p.getUniqueId())){
				list.add(p.getUniqueId());
			}
			return true;
		}
		else if(label.equalsIgnoreCase("unhide")){
			for(Player player : onlinePlayerList){
				player.showPlayer(p);
			}
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "dynmap show " + p.getName());
			cs.sendMessage(plugin.namespace + ChatColor.WHITE + "Du bist nun sichtbar");
			plugin.api.sendLog("[Epicraft - Unsichtbar] " + p.getName() + " ist nun Sichtbar");
			list.remove(p.getUniqueId());
			return true;
		}
		p.sendMessage(plugin.namespace + ChatColor.RED + "/hide, unhide");
		return true;
	}
	
	@EventHandler
	public void onCollectItem(PlayerPickupItemEvent e){
		Player p = e.getPlayer();
		if(list.contains(p.getUniqueId()))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityTargetEvent(EntityTargetEvent event){
		if(event.getTarget() instanceof Player){
			Player p = (Player) event.getTarget();
			if(list.contains(p.getUniqueId())){
				EntityType type = event.getEntity().getType();
				if (type == EntityType.ENDERMAN || type == EntityType.WOLF || type == EntityType.PIG_ZOMBIE || type == EntityType.BLAZE || type == EntityType.CAVE_SPIDER || type == EntityType.CREEPER || type == EntityType.GHAST || type == EntityType.MAGMA_CUBE || type == EntityType.SILVERFISH || type == EntityType.SKELETON || type == EntityType.SLIME || type == EntityType.SPIDER || type == EntityType.WITCH || type == EntityType.WITHER_SKULL || type == EntityType.ZOMBIE || type == EntityType.IRON_GOLEM || type == EntityType.ENDER_DRAGON || type == EntityType.WITHER){
					event.setCancelled(true);
				}
			}
		}
	}

}
