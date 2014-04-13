package de.wolfsline.administration;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import de.wolfsline.Epicraft.Epicraft;

public class DoorAccess implements Listener{

	private Epicraft plugin;
	
	public DoorAccess(Epicraft plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event){
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK){
			return;
		}
		if(!event.getPlayer().hasPermission("epicraft.door.access")){
			return;
		}
		Block block = event.getClickedBlock();
		if (block.getType() == Material.IRON_DOOR_BLOCK) {
			if (block.getData() >= 8) {
				block = block.getRelative(BlockFace.DOWN);
			}
			if (block.getType() == Material.IRON_DOOR_BLOCK) {
				if (block.getData() < 4) {
					block.setData((byte) (block.getData() + 4));
					block.getWorld().playEffect(block.getLocation(), Effect.DOOR_TOGGLE, 0);
				} else {
					block.setData((byte) (block.getData() - 4));
					block.getWorld().playEffect(block.getLocation(), Effect.DOOR_TOGGLE, 0);
				}
				event.setUseItemInHand(Result.DENY);
			}
		}
	}
}
