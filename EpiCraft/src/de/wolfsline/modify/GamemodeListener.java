package de.wolfsline.modify;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class GamemodeListener implements Listener{
	private Player p;
	private Player p2;
	
	@EventHandler
	public void onPlayerGamemodeChange(PlayerGameModeChangeEvent event){
		p = event.getPlayer();
		if(p.hasPermission("epicraft.fly.use"))
			return;
		new Thread(new Runnable() {
			@Override
			public void run() {
				Player myPlayer = p;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				myPlayer.setAllowFlight(false);
			}
		}).start();
	}
	
	@EventHandler
	public void onPlayerLoginEvent(PlayerLoginEvent event){
		p2 = event.getPlayer();
		new Thread(new Runnable() {
			@Override
			public void run() {
				Player myPlayer = p2;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				myPlayer.setAllowFlight(false);
			}
		}).start();
	}
	
	@EventHandler
	public void onPlayreInteractEvent(PlayerInteractEvent event){
		Player p = event.getPlayer();
		if(p.isOp())
			return;
		if(p.getItemInHand().getType() == Material.MONSTER_EGG)
			event.setCancelled(true);;
	}
}
