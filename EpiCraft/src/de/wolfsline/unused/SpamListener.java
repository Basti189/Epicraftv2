package de.wolfsline.unused;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;


public class SpamListener implements Listener{
	private HashMap<String, Spieler> map = new HashMap<String, Spieler>();
	private String beleidigung[] = {"Assi", "Arschloch", "Hurensohn", "Hure", "Pisser", "Fischkopf"};
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e){
		if(e.getPlayer().isOp()){
			return;
		}
		Long time = System.currentTimeMillis();
		if(map.containsKey(e.getPlayer().getName())){
			Spieler tmp = this.map.get(e.getPlayer().getName());
			Long lastUsage = tmp.getTimestamp();
			if((!tmp.isSameMessage(e.getMessage())) || lastUsage + 120*1000 < time){
				map.remove(e.getPlayer().getName());
				map.put(e.getPlayer().getName(), new Spieler(time, e.getMessage(), time));
				return;
			}
			if(lastUsage + 120*1000 > time && tmp.getAnzahl() >= 5){
				if(tmp.getAnzahlKick() < 3)
				{
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "kick " + e.getPlayer().getName() + " Spam!");
					tmp.setAnzahlKick(tmp.getAnzahlKick()+1);
					tmp.setTimestamp(time);
					tmp.setAnzahl(0);
					map.put(e.getPlayer().getName(), tmp);
				}
				else
				{
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "ban " + e.getPlayer().getName() + " Spam!");
					map.remove(e.getPlayer().getName());
					Bukkit.getConsoleSender().sendMessage(e.getPlayer().getName() + " wurde automatisch gebannt");
				}
			}
		}
		else{
			map.put(e.getPlayer().getName(), new Spieler(time, e.getMessage(), time));
		}
		
	}

}
