package de.wolfsline.modify;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener{

	@EventHandler
	public void onDeathEvent(PlayerDeathEvent event){
		String killer = "";
		try{
			killer = event.getEntity().getKiller().getName();
		}
		catch(NullPointerException ex){
			
		}
		
		if(killer == ""){
			event.setDeathMessage(ChatColor.GOLD + event.getEntity().getName() + " ist gestorben");
		}
		else if(killer.equalsIgnoreCase(event.getEntity().getName()))
			event.setDeathMessage(ChatColor.GOLD + event.getEntity().getName() + " hat sich selber getötet");
		else
			event.setDeathMessage(ChatColor.GOLD + event.getEntity().getName() + " wurde quallvoll von " + event.getEntity().getKiller().getName() + " getötet");
	}
}
