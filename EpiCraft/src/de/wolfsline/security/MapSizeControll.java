package de.wolfsline.security;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import de.wolfsline.Epicraft.Epicraft;

public class MapSizeControll implements Listener{
	
	private Epicraft plugin;
	private double limit = 12000.0D;
	
	public MapSizeControll(Epicraft plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent event){
		Player p = event.getPlayer();
		Location to = event.getTo();
		if(to.getX() >= limit)
			event.setCancelled(true);
		else if(to.getX() <= -limit)
			event.setCancelled(true);
		if(to.getZ() >= 12000.0D)
			event.setCancelled(true);
		else if(to.getZ() <= -limit)
			event.setCancelled(true);
		if(event.isCancelled()){
			p.sendMessage(plugin.namespace + ChatColor.RED + "Mapgrenze erreicht!");
			plugin.api.sendLog("[Epicraft - Mapgrenze] " + p.getName() + " hat auf der Welt " + p.getLocation().getWorld().getName() + " die Mapgrenze erreicht");
		}
	}
}
