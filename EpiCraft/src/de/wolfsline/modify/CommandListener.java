package de.wolfsline.modify;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import de.wolfsline.Epicraft.Epicraft;

public class CommandListener implements Listener{
	
	private Epicraft plugin;
	
	public CommandListener(Epicraft plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void PlayerCommandListener(PlayerCommandPreprocessEvent e){
		e.setMessage(ChatColor.stripColor(e.getMessage()));
		if((!e.getPlayer().hasPermission("epicraft.command.allow"))){
			e.setCancelled(true);
			e.getPlayer().sendMessage(plugin.namespace + ChatColor.RED + "Du hast keinen Zugriff auf diesen Befehl!");
		}	
	}
}
