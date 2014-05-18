package de.wolfsline.teleport;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class EnderPearlTeleport implements Listener{

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event)
	{
		Player p = event.getPlayer();
		if(event.getCause() == TeleportCause.ENDER_PEARL){
			if(!p.hasPermission("epicraft.teleport.enderpearl")){
				event.setCancelled(true);
			}
		}
	}
}
