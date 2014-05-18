package de.wolfsline.modify;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

public class Lava_Water_Controll implements Listener{

	@EventHandler
	public void onBucketEmptyEvent(PlayerBucketEmptyEvent event){
		Player p = event.getPlayer();
		if(p.hasPermission("epicraft.permission.stammspieler") ||
				p.hasPermission("epicraft.permission.guard") ||
				p.hasPermission("epicraft.permission.moderator") ||
				p.hasPermission("epicraft.permission.admin")){
			return;
		}
		else{
			event.setCancelled(true);
		}
	}
	
	/*@EventHandler
	public void onInteractEvent(PlayerInteractEvent event){
		Player p = event.getPlayer();
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
			ItemStack stack = p.getItemInHand();
			if(stack != null){
				if(stack.getType() == Material.LAVA ||
						stack.getType() == Material.LAVA_BUCKET || 
						stack.getType() == Material.WATER ||
						stack.getType() == Material.WATER_BUCKET){
					if(p.hasPermission("epicraft.permission.stammspieler") ||
							p.hasPermission("epicraft.permission.guard") ||
							p.hasPermission("epicraft.permission.moderator") ||
							p.hasPermission("epicraft.permission.admin")){
						return;
					}
					else{
						event.setCancelled(true);
					}
					
				}
			}
		}
	}
	
	@EventHandler
	public void onInteractPlayerEvent(PlayerInteractEntityEvent event){
		Player p = event.getPlayer();
		ItemStack stack = p.getItemInHand();
		if(stack != null){
			if(stack.getType() == Material.LAVA ||
					stack.getType() == Material.LAVA_BUCKET || 
					stack.getType() == Material.WATER ||
					stack.getType() == Material.WATER_BUCKET){
				if(p.hasPermission("epicraft.permission.stammspieler") ||
						p.hasPermission("epicraft.permission.guard") ||
						p.hasPermission("epicraft.permission.moderator") ||
						p.hasPermission("epicraft.permission.admin")){
					return;
				}
				else{
					event.setCancelled(true);
				}
			}
		}
	}*/
}
