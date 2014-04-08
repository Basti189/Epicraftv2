package de.wolfsline.modify;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.helpClasses.myPlayer;

public class ChatListener implements Listener{
	
	private Epicraft plugin;
	private String world = "";
	
	public ChatListener(Epicraft plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void OnChat(AsyncPlayerChatEvent e){
		if(e.isCancelled())
			return;
		Player p = e.getPlayer();
		for(myPlayer player : this.plugin.player){
			if(player.username.equalsIgnoreCase(p.getName())){
				if(!player.chatMessages){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Sie haben den Chat deaktiviert!");
					e.setCancelled(true);
					return;
				}
					
			}
		}
		world = p.getLocation().getWorld().getName().toString();
		world = getWorldNameChar(world);
		//String name = p.getDisplayName();
		String name = p.getName();
		int anzahlZeichen = 16 - name.length();
		if(anzahlZeichen < 4 ){
			name = name.substring(0, name.length() - (4 - anzahlZeichen));
			name = name + "..";
		}
		e.setCancelled(true);
		String prefix = "";//ChatColor.GOLD + "[" + ChatColor.GRAY + world + ChatColor.GOLD + "] ";
		String message = "";
		if(p.hasPermission("epicraft.chat.color")){
			message = ChatColor.translateAlternateColorCodes('$', e.getMessage().toString());
		}
		else{
			message = e.getMessage();
		}
		if(message.contains("I'm chatting on my iPhone using Minecraft Connect! Check it out, it's free :)")){
			p.kickPlayer("Minecraft Connect ist auf dem Server verboten!");
			e.setCancelled(true);
			return;
		}
		/*
		if(p.hasPermission("epicraft.chat.admin")){
			format = ChatColor.DARK_RED + "[Admin] " + name;
			p.setPlayerListName(ChatColor.DARK_RED + name);
			format += ChatColor.WHITE + ": " + e.getMessage();
			e.setFormat(format);
			return;
		}
		else if(p.hasPermission("epicraft.chat.moderator")){
			format = ChatColor.DARK_GREEN + "[Moderator] " + name;
			p.setPlayerListName(ChatColor.DARK_GREEN + name);
			format += ChatColor.WHITE + ": " + e.getMessage();
			e.setFormat(format);
			return;
		}
		else if(p.hasPermission("epicraft.chat.guard")){
			format = ChatColor.DARK_PURPLE + "[Guard] " + name;
			p.setPlayerListName(ChatColor.DARK_PURPLE + name);
			format += ChatColor.WHITE + ": " + e.getMessage();
			e.setFormat(format);
			return;

		}
		else if(p.hasPermission("epicraft.chat.stammi")){
			format = ChatColor.BLUE + "[Stammi] " + name;
			p.setPlayerListName(ChatColor.BLUE + name);
			format += ChatColor.WHITE + ": " + e.getMessage();
			e.setFormat(format);
			return;
		}
		else if(p.hasPermission("epicraft.chat.spieler")){
			format = ChatColor.DARK_BLUE + "[Spieler] " + name;
			p.setPlayerListName(ChatColor.DARK_BLUE + name);
			format += ChatColor.WHITE + ": " + e.getMessage();
			e.setFormat(format);
			return;
		}
		else if(p.hasPermission("epicraft.chat.gast")){
			format = ChatColor.AQUA + "[Gast] " + name;
			p.setPlayerListName(ChatColor.AQUA + name);
			format += ChatColor.WHITE + ": " + e.getMessage();
			e.setFormat(format);
			return;
		}
		else if(p.hasPermission("epicraft.chat.inhaftierter")){
			format = ChatColor.GREEN + "[Inhaftierter] " + name;
			p.setPlayerListName(ChatColor.GREEN + name);
			format += ChatColor.WHITE + ": " + e.getMessage();
			e.setFormat(format);
			return;
		}*/
		
		if(p.hasPermission("epicraft.chat.admin")){
			prefix += ChatColor.DARK_RED + p.getName();
			//p.setPlayerListName(ChatColor.DARK_RED + name);
			sendChat(prefix, message);
			return;
		}
		else if(p.hasPermission("epicraft.chat.moderator")){
			prefix += ChatColor.DARK_GREEN + p.getName();
			//p.setPlayerListName(ChatColor.DARK_GREEN + name);
			sendChat(prefix, message);
			return;
		}
		else if(p.hasPermission("epicraft.chat.guard")){
			prefix += ChatColor.DARK_PURPLE + p.getName();
			//p.setPlayerListName(ChatColor.DARK_PURPLE + name);
			sendChat(prefix, message);
			return;

		}
		else if(p.hasPermission("epicraft.chat.stammi")){
			prefix += ChatColor.BLUE + p.getName();
			//p.setPlayerListName(ChatColor.BLUE + name);
			sendChat(prefix, message);
			return;
		}
		else if(p.hasPermission("epicraft.chat.spieler")){
			prefix += ChatColor.DARK_BLUE + p.getName();
			//p.setPlayerListName(ChatColor.DARK_BLUE + p.getName());
			sendChat(prefix, message);
			return;
		}
		else if(p.hasPermission("epicraft.chat.gast")){
			prefix += ChatColor.AQUA + p.getName();
			//p.setPlayerListName(ChatColor.AQUA + name);
			sendChat(prefix, message);
			return;
		}
		else if(p.hasPermission("epicraft.chat.inhaftierter")){
			prefix += ChatColor.GREEN + p.getName();
			//p.setPlayerListName(ChatColor.GREEN + name);
			sendChat(prefix, message);
			return;
		}
	}
	
	private void sendChat(String prefix, String message){
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		Date date = new Date();
		String time = format.format(date);
		String timePrefix = ChatColor.GOLD + "[" + ChatColor.GRAY + time + ChatColor.GOLD + "]";
		String worldPrefix = ChatColor.GOLD + "[" + ChatColor.GRAY + world + ChatColor.GOLD + "]";
		for(Player p : Bukkit.getServer().getOnlinePlayers()){
			for(myPlayer player : this.plugin.player){
				if(player.username.equalsIgnoreCase(p.getName())){
					if(!player.chatMessages)
						break;
					String myPrefix = "";
					if(player.chatTime)
						myPrefix += timePrefix;
					if(player.chatWorld)
						myPrefix += worldPrefix;
					if(myPrefix.equalsIgnoreCase(""))
						p.sendMessage(prefix + ChatColor.WHITE + ": " + message);
					else
						p.sendMessage(myPrefix + " " + prefix + ChatColor.WHITE + ": " + message);
					break;
				}
			}
		}
	}
	
	private String getWorldNameChar(String worldname){
		if(worldname.equalsIgnoreCase("Survival_nether"))
			return "N";
		else if(worldname.equalsIgnoreCase("Survival_the_end"))
			return "E";
		return String.valueOf(worldname.charAt(0));
	}
}
