package de.wolfsline.security;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import de.wolfsline.Epicraft.Epicraft;

public class CreatureSpawnListener implements Listener{
	
	private Epicraft plugin;
	
	private final String WORLD = "world";
	
	public CreatureSpawnListener(Epicraft plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onCreatureSpawnListener(CreatureSpawnEvent event){
		if(event.getEntityType() == EntityType.BAT)//Fledermäuse
			if(event.getLocation().getWorld().getName().toString().equalsIgnoreCase(WORLD))
				event.setCancelled(true);
	}
}
