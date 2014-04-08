package de.wolfsline.restriction;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.wolfsline.Epicraft.Epicraft;

public class AutoKickListener implements Listener{

	HashMap<String, Long> map = new HashMap<String, Long>();
	private Epicraft plugin;
	
	public AutoKickListener(Epicraft plugin_1){
		this.plugin = plugin_1;
		Thread thread = new Thread(new Runnable() {
			private long waitBeforKickTime = 1000*60*7;
			@Override
			public void run() {
				while(true){
					try { Thread.sleep(1000*10); } catch (InterruptedException e) {}
					HashMap<String, Long> myMap = new HashMap<String, Long>();
					myMap = map;
					for(Entry<String, Long> entry : myMap.entrySet()){
						long waitTime = System.currentTimeMillis() - entry.getValue();
						if(waitTime >= waitBeforKickTime){
							Player p = Bukkit.getServer().getPlayer(entry.getKey());
							if(p != null){
								p.kickPlayer("Entschuldigung\n\nZu lange abwesend!");
								plugin.api.sendLog("[Epicraft - AutoKick] " + p.getName() + " war zu lange abwesend und wurde gekicked");
								map.remove(p.getName());
							}	
						}
					}
				}
			}
		});
		thread.start();
	}

	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent event){
		if(event.getPlayer().isOp())
			return;
		map.put(event.getPlayer().getName(), System.currentTimeMillis());
	}
	
	@EventHandler
	public void onPlayerChatEvent(AsyncPlayerChatEvent event){
		if(event.getPlayer().isOp())
			return;
		map.put(event.getPlayer().getName(), System.currentTimeMillis());
	}
	
	@EventHandler
	public void onPlayerLoginEvent(PlayerJoinEvent event){
		if(event.getPlayer().isOp())
			return;
		map.put(event.getPlayer().getName(), System.currentTimeMillis());
	}
	
	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event){
		if(event.getPlayer().isOp())
			return;
		map.remove(event.getPlayer().getName());
	}
	
	@EventHandler
	public void onPlayerKickEvent(PlayerKickEvent event){
		if(event.getPlayer().isOp())
			return;
		map.remove(event.getPlayer().getName());
	}
}
