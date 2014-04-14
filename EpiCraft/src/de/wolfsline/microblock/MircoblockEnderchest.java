package de.wolfsline.microblock;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import de.wolfsline.Epicraft.Epicraft;

public class MircoblockEnderchest implements Listener{
	
	private Epicraft plugin;

	public MircoblockEnderchest(Epicraft plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event){
		Player p = event.getPlayer();
		if(!(event.getClickedBlock() instanceof Block))
			return;
		if(event.getClickedBlock().getType() == Material.SKULL){
			Skull skull = (Skull) event.getClickedBlock().getState();
			String owner = skull.getOwner();
			if(owner == null)
				return;
			if(owner.equals("_Brennian")){
				p.openInventory(p.getEnderChest());
				event.setUseItemInHand(Result.DENY);
			}
		}
	}

}
