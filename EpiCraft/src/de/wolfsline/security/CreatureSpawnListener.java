package de.wolfsline.security;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class CreatureSpawnListener implements Listener{
	
	private final String WORLD = "world";
	
	@EventHandler
	public void onCreatureSpawnListener(CreatureSpawnEvent event){
		if(event.getEntityType() == EntityType.BAT)//Fledermäuse
			if(event.getLocation().getWorld().getName().toString().equalsIgnoreCase(WORLD))
				event.setCancelled(true);
	}
}
