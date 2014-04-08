package de.wolfsline.security;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class CreatureSpawnListener implements Listener{
	
	@EventHandler
	public void onCreatureSpawnListener(CreatureSpawnEvent event){
		if(event.getEntityType() == EntityType.BAT)
			if(event.getLocation().getWorld().getName().toString().equalsIgnoreCase("survival"))
				event.setCancelled(true);
	}
}
