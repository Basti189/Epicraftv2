package de.wolfsline.security;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardRegionMoveEvent;

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
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
		Entity damager = event.getDamager();
		Entity victim = event.getEntity();
		if(damager instanceof Player){
			Player p = (Player) damager;
			Location loc = victim.getLocation();
			WorldGuardPlugin wgPlugin = plugin.getWorldGuard();
			if(wgPlugin == null){
				return;
			}
			boolean canBuild = wgPlugin.canBuild(p, loc.getBlock());
			if(!canBuild){
				event.setCancelled(true);
			}
		}
	}
	
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
	
	@EventHandler
	public void soilChangePlayer(PlayerInteractEvent event) {
		if ((event.getAction() == Action.PHYSICAL) && (event.getClickedBlock().getType() == Material.SOIL)){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void soilChangeEntity(EntityInteractEvent event)	{
		if ((event.getEntityType() != EntityType.PLAYER) && (event.getBlock().getType() == Material.SOIL)){
			event.setCancelled(true);
		}
	}
	
	/*@EventHandler
	public void onTrampleEvent(PlayerInteractEvent event){
		if (event.isCancelled()){
			return;
		}
		if (event.getAction() == Action.PHYSICAL){
			Block block = event.getClickedBlock();
			if(block == null)
        		return;
			int blockType = block.getTypeId();
			if(blockType == Material.getMaterial(59).getId()){
				event.setUseInteractedBlock(Result.DENY);
            	event.setCancelled(true);
            	
        		block.setTypeId(blockType);
        		block.setData(block.getData());
			}
		}
		if(event.getAction() == Action.PHYSICAL)
        {
        	Block block = event.getClickedBlock();
        	if(block == null)
        		return;

        	int blockType = block.getTypeId();
        	if(blockType == Material.getMaterial(60).getId())
        	{
            	event.setUseInteractedBlock(Result.DENY);
            	event.setCancelled(true);
            	
        		block.setType(Material.getMaterial(60));
        		block.setData(block.getData());
        	}
		}
	}*/
}
