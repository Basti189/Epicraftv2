package de.wolfsline.administration;

import java.util.ArrayList;

import de.wolfsline.Epicraft.Epicraft;

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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class UnHideCommand implements CommandExecutor, Listener{

    private Epicraft plugin;
    private ArrayList<String> list;
    public UnHideCommand(Epicraft plugin) {
        this.plugin = plugin;
        this.list = new ArrayList<>();
    }

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player))
			return false;
		if(!cs.hasPermission("epicraft.hide")){
			cs.sendMessage(plugin.error);
			plugin.api.sendLog("[Epicraft - Unsichtbar] " + cs.getName() + " hat versucht auf den Hide-Befehl zuzugreifen");
			return true;
		}
		Player[] onlinePlayerList = Bukkit.getOnlinePlayers();
		if(label.equalsIgnoreCase("hide")){
			if(!list.contains(cs.getName()))
				list.add(cs.getName());
			else{
				cs.sendMessage(plugin.namespace + ChatColor.RED + "Du bist bereits Unsichtbar!");
				return true;
			}
			for(Player p : onlinePlayerList){
				if(!p.hasPermission("epicraft.hide.use"))
					p.hidePlayer((Player) cs);
			}
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "dynmap hide " + cs.getName());
			cs.sendMessage(plugin.namespace + ChatColor.WHITE + "Du bist nun unsichtbar");
			plugin.api.sendLog("[Epicraft - Unsichtbar] " + cs.getName() + " ist nun Unsichtbar");
			if(args.length == 1){
				if(args[0].equalsIgnoreCase("leave")){
					plugin.api.sendLog("[Epicraft - Unsichtbar] " + cs.getName() + " simuliert Ausloggen");
					for(Player player : Bukkit.getServer().getOnlinePlayers()){
						player.sendMessage(ChatColor.GOLD + "***  " + cs.getName() + " hat den Server verlassen  ***");
					}
				}
				
			}
			return true;
		}
		else if(label.equalsIgnoreCase("unhide")){
			for(Player p : onlinePlayerList){
				p.showPlayer((Player) cs);
			}
			list.remove(cs.getName());
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "dynmap show " + cs.getName());
			cs.sendMessage(plugin.namespace + ChatColor.WHITE + "Du bist nun sichtbar");
			plugin.api.sendLog("[Epicraft - Unsichtbar] " + cs.getName() + " ist nun Sichtbar");
			if(args.length == 1){
				if(args[0].equalsIgnoreCase("join")){
					plugin.api.sendLog("[Epicraft - Unsichtbar] " + cs.getName() + " simuliert Login");
					for(Player player : Bukkit.getServer().getOnlinePlayers()){
						player.sendMessage(ChatColor.GOLD + "***  " + cs.getName() + " hat den Server betreten  ***");
					}
				}
				
			}
			return true;
		}
		return false;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		Player p = e.getPlayer();
		if(p.hasPermission("epicraft.hide.use"))
			return;
		for(String team : list){
			Player bukkit = Bukkit.getServer().getPlayer(team);
			if(bukkit == null){
				list.remove(team);
				continue;
			}
			else if(bukkit.getName().equalsIgnoreCase(p.getName())){
				p.sendMessage(plugin.namespace + ChatColor.RED + "Hinweis! Du befindest dich noch im Hide!");
				continue;
			}
			else{
				p.hidePlayer(bukkit);
			}
		}
	}
	
	@EventHandler
	public void onCollectItem(PlayerPickupItemEvent e){
		Player p = e.getPlayer();
		if(list.contains(p.getName()))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityTargetEvent(EntityTargetEvent event){
		if(event.getTarget() instanceof Player){
			Player p = (Player) event.getTarget();
			if(list.contains(p.getName())){
				EntityType type = event.getEntity().getType();
				if (type == EntityType.ENDERMAN || type == EntityType.WOLF || type == EntityType.PIG_ZOMBIE || type == EntityType.BLAZE || type == EntityType.CAVE_SPIDER || type == EntityType.CREEPER || type == EntityType.GHAST || type == EntityType.MAGMA_CUBE || type == EntityType.SILVERFISH || type == EntityType.SKELETON || type == EntityType.SLIME || type == EntityType.SPIDER || type == EntityType.WITCH || type == EntityType.WITHER_SKULL || type == EntityType.ZOMBIE || type == EntityType.IRON_GOLEM || type == EntityType.ENDER_DRAGON || type == EntityType.WITHER){
					event.setCancelled(true);
				}
			}
		}
	}

}
