package de.wolfsline.modify;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
import de.wolfsline.helpClasses.myPlayer;

public class JoinQuitListener implements Listener{
	File file = new File("plugins/EpiCraft/players.yml");
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
		if(!list.contains(p.getName())){
			e.setJoinMessage(ChatColor.GOLD + "***  " + p.getName() + " hat den Server betreten  ***\nDies ist sein erster Besuch auf EpiCraft");
			e.getPlayer().sendMessage(plugin.namespace + ChatColor.WHITE + "Herzlich Willkommen auf EpiCraft " + e.getPlayer().getName() + "!");
			plugin.api.sendLog("[Epicraft - Login] " + p.getName() + " hat sich eingeloggt");
			plugin.api.sendLog("[Epicraft - Login] Dies ist sein erster Besuch");
			list.add(p.getName());
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
				for(myPlayer playerSettings : this.plugin.player){
					if(playerSettings.username.equalsIgnoreCase(player.getName())){
						if(playerSettings.eventMessages)
							player.sendMessage(ChatColor.GOLD + "***  " + p.getName() + " hat den Server betreten  ***");
						break;
					}
				}
			}
			plugin.api.sendLog("[Epicraft - Login] " + p.getName() + " hat sich eingeloggt");
		}
		setDisplayName(p);
	}
	
	private void setDisplayName(Player p){
		String name = p.getDisplayName();
		int anzahlZeichen = 16 - name.length();
		if(anzahlZeichen < 4 ){
			name = name.substring(0, name.length() - (4 - anzahlZeichen));
			name = name + "..";
		}
		p.setDisplayName(name);
		p.setPlayerListName(name);
		if(p.hasPermission("epicraft.chat.admin")){
			p.setPlayerListName(ChatColor.DARK_RED + name);
			return;
		}
		else if(p.hasPermission("epicraft.chat.moderator")){
			p.setPlayerListName(ChatColor.DARK_GREEN + name);
			return;
		}
		else if(p.hasPermission("epicraft.chat.guard")){
			p.setPlayerListName(ChatColor.DARK_PURPLE + name);
			return;

		}
		else if(p.hasPermission("epicraft.chat.stammi")){
			p.setPlayerListName(ChatColor.BLUE + name);
			return;
		}
		else if(p.hasPermission("epicraft.chat.spieler")){
			p.setPlayerListName(ChatColor.DARK_BLUE + name);
			return;
		}
		else if(p.hasPermission("epicraft.chat.gast")){
			p.setPlayerListName(ChatColor.AQUA + name);
			return;
		}
		else if(p.hasPermission("epicraft.chat.inhaftierter")){
			p.setPlayerListName(ChatColor.GREEN + name);
			return;
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		e.setQuitMessage("");
		if(isKickEvent){
			isKickEvent = false;
			return;
		}
			
		Player p = e.getPlayer();
		for(Player player : Bukkit.getServer().getOnlinePlayers()){
			for(myPlayer playerSettings : this.plugin.player){
				if(playerSettings.username.equalsIgnoreCase(player.getName())){
					if(playerSettings.eventMessages)
						player.sendMessage(ChatColor.GOLD + "***  " + p.getName() + " hat den Server verlassen  ***");
					break;
				}
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
			for(myPlayer playerSettings : this.plugin.player){
				if(playerSettings.username.equalsIgnoreCase(player.getName())){
					if(playerSettings.eventMessages)
						player.sendMessage(ChatColor.GOLD + "***  " + p.getName() + " hat den Server unfreiwillig verlassen  ***");
					break;
				}
			}
			
		}
	}
}
