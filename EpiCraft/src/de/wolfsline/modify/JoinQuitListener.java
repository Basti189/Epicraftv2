package de.wolfsline.modify;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.helpClasses.EpicraftPlayer;

public class JoinQuitListener implements Listener {
	
	File file = new File("plugins/Epicraft/players.yml");
	FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
	List<String> list = cfg.getStringList("Spieler");
	private Epicraft plugin;
	
	private boolean isKickEvent = false;
	
	public JoinQuitListener(Epicraft plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerLoginEvent(PlayerLoginEvent event){
		Player p = event.getPlayer();
		if(p.isBanned()){
			String banReason = "$4Du wurdest gebannt!\n\n$fInformationen findest du bei uns im Forum unter\nhttp://forum.epicraft.de";
			event.setResult(Result.KICK_BANNED);
			event.setKickMessage(ChatColor.translateAlternateColorCodes('$', banReason));
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		Player p = e.getPlayer();
		if(list == null)
			list = new ArrayList<String>();
		if(!list.contains(p.getUniqueId().toString())){
			e.setJoinMessage(ChatColor.GOLD + "***  " + p.getName() + " hat den Server betreten  ***\nDies ist sein erster Besuch auf Epicraft");
			e.getPlayer().sendMessage(plugin.namespace + ChatColor.WHITE + "Herzlich Willkommen auf Epicraft " + e.getPlayer().getName() + "!");
			plugin.api.sendLog("[Epicraft - Login] " + p.getName() + " hat sich eingeloggt");
			plugin.api.sendLog("[Epicraft - Login] Dies ist sein erster Besuch");
			list.add(p.getUniqueId().toString());
			this.cfg.set("Spieler", list);
			try {
				this.cfg.save(file);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		else{
			e.setJoinMessage("");
			for(Player player : Bukkit.getServer().getOnlinePlayers()){
				EpicraftPlayer playerSettings = plugin.pManager.getEpicraftPlayer(player.getUniqueId());
				if(playerSettings == null)
					continue;
				if(playerSettings.uuid.equals(player.getUniqueId())){
					if(playerSettings.eventMessages)
						player.sendMessage(ChatColor.GOLD + "***  " + p.getName() + " hat den Server betreten  ***");
					continue;
				}
			}
			plugin.api.sendLog("[Epicraft - Login] " + p.getName() + " hat sich eingeloggt");
		}
		plugin.pManager.triggerEpicraftPlayerList(p, true);
		//setDisplayName(p);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		e.setQuitMessage("");
		plugin.pManager.triggerEpicraftPlayerList(e.getPlayer(), false);
		if(isKickEvent){
			isKickEvent = false;
			return;
		}
			
		Player p = e.getPlayer();
		for(Player player : Bukkit.getServer().getOnlinePlayers()){
			EpicraftPlayer playerSettings = plugin.pManager.getEpicraftPlayer(player.getUniqueId());
			if(playerSettings == null)
				continue;
			if(playerSettings.uuid.equals(player.getUniqueId())){
				if(playerSettings.eventMessages)
					player.sendMessage(ChatColor.GOLD + "***  " + p.getName() + " hat den Server verlassen  ***");
				continue;
			}
		}
		plugin.api.sendLog("[Epicraft - Logout] " + p.getName() + " hat sich ausgeloggt");
	}
	
	@EventHandler
	public void onKick(PlayerKickEvent e){
		isKickEvent = true;
		Player p = e.getPlayer();
		e.setLeaveMessage("");
		for(Player player : Bukkit.getServer().getOnlinePlayers()){
			EpicraftPlayer playerSettings = plugin.pManager.getEpicraftPlayer(player.getUniqueId());
			if(playerSettings == null)
				continue;
			if(playerSettings.uuid.equals(player.getUniqueId())){
				if(playerSettings.eventMessages)
					player.sendMessage(ChatColor.GOLD + "***  " + p.getName() + " hat den Server unfreiwillig verlassen  ***");
				continue;
			}
		}
	}
}
