package de.wolfsline.modify;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import de.wolfsline.Epicraft.Epicraft;

public class CommandListener implements Listener{
	
	private Epicraft plugin;
	
	private List<String> commandsSpieler = new ArrayList<String>();
	
	public CommandListener(Epicraft plugin) {
		this.plugin = plugin;
		commandsSpieler.add("spawn");
		commandsSpieler.add("gs");
		commandsSpieler.add("sign");
		commandsSpieler.add("warn");
		commandsSpieler.add("home");
		commandsSpieler.add("sethome");
		commandsSpieler.add("listhome");
		commandsSpieler.add("rehome");
		commandsSpieler.add("delhome");
		commandsSpieler.add("login");
		commandsSpieler.add("email");
		commandsSpieler.add("register");
		commandsSpieler.add("cp");
		commandsSpieler.add("changepw");
		commandsSpieler.add("w");
		commandsSpieler.add("r");
		commandsSpieler.add("a");
		commandsSpieler.add("pvp");
		commandsSpieler.add("me");
		commandsSpieler.add("secure");
		commandsSpieler.add("horse");
		commandsSpieler.add("channel");
		commandsSpieler.add("ticket");
		commandsSpieler.add("afk");
		commandsSpieler.add("secure");
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void PlayerCommandListener(PlayerCommandPreprocessEvent event){
		event.setMessage(ChatColor.stripColor(event.getMessage()));
		Player p = event.getPlayer();
		if(!p.hasPermission("epicraft.command.allow")){
			event.setCancelled(true);
			event.getPlayer().sendMessage(plugin.namespace + ChatColor.RED + "Du hast keinen Zugriff auf diesen Befehl!");
		}
		else if((p.hasPermission("epicraft.permission.spieler") || p.hasPermission("epicraft.permission.stammi")) && !p.isOp()){
			String preCmd = event.getMessage().replaceFirst("/", "");
			for(String cmd : commandsSpieler){
				if(preCmd.startsWith(cmd)){
					return;
				}
			}
			event.setCancelled(true);
			p.sendMessage(plugin.namespace + ChatColor.RED + "Unbekannter Befehl!");
		}
	}
}
