package de.wolfsline.security;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;

import de.wolfsline.Epicraft.Epicraft;

public class Region implements Listener{

	private Epicraft plugin;
	
	int warnung = 0;
	
	public Region(Epicraft plugin){
		this.plugin = plugin;
	}
	
	/*@EventHandler
	public void onFireDestroyBlock(BlockBurnEvent event){
		event.setCancelled(true);
	}*/
	
	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent event) {
		if(event.getCause() == IgniteCause.SPREAD || event.getCause() == IgniteCause.LIGHTNING){
			event.setCancelled(true);
		}
		else if(event.getCause() == IgniteCause.FLINT_AND_STEEL){
			Entity entity = event.getPlayer();
			if(entity instanceof Player){
				Player p = (Player) entity;
				if(!p.hasPermission("epicraft.flint")){
					event.setCancelled(true);
				}
			}
		}
    }
}
