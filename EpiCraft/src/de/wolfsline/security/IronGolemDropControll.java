package de.wolfsline.security;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.IronGolem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class IronGolemDropControll implements Listener{
	
	@EventHandler
	public void onIronGolemDeathEvent(EntityDeathEvent event){
		if(event.getEntity() instanceof IronGolem){
			/*IronGolem golem = (IronGolem) event.getEntity();
			if(golem.isPlayerCreated())
				return;*/
			List<ItemStack> drop = event.getDrops();
			Iterator<ItemStack> it = drop.iterator();
			while(it.hasNext()){
				ItemStack stack = it.next();
				if(stack.getType().equals(Material.IRON_INGOT))
					it.remove();
			}
		}
	}
}
