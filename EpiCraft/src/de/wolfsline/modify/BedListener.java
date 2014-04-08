package de.wolfsline.modify;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import de.wolfsline.Epicraft.Epicraft;

public class BedListener implements Listener{
	
	private Epicraft plugin;
	public BedListener(Epicraft plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e)
	{
		Player p = e.getPlayer();
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(e.getClickedBlock().getType().equals(Material.BED_BLOCK) || e.getClickedBlock().getType().equals(Material.BED)){{
					p.sendMessage(plugin.namespace + ChatColor.RED + "Betten können derzeit nicht genutzt werden!");
					e.setCancelled(true);
				}
			}
		}
	}
}