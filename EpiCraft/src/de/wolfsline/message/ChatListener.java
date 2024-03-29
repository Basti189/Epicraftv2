package de.wolfsline.message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.healthbar.Utils;
import de.wolfsline.helpClasses.EpicraftPlayer;

public class ChatListener implements Listener, CommandExecutor{

	private Epicraft plugin;
	
	//0 = �ffentlicher Channel //1 = �ffentlicher Channel //2 = Supportchannel1 //3 = Supportchannel2
	public HashMap<UUID, Integer> mapChannel = new HashMap<UUID, Integer>();
	
	public ChatListener(Epicraft plugin) {
		this.plugin = plugin;
	}
	
	// **** Channel **** //
	
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(!(p.hasPermission("epicraft.chat.channel") || p.hasPermission("epicraft.permission.admin") || p.isOp())){
			p.sendMessage(plugin.error);
			plugin.api.sendLog("[Epicraft - Chat] " + p.getName() + " hat versucht den Channel zu wechseln");
			return true;
		}
		if(args.length == 0){//Zeige Channel
			p.sendMessage(plugin.namespace + ChatColor.GOLD + "Folgende Channel stehen zur Auswahl:");
			p.sendMessage(ChatColor.WHITE + "- 1\n- 2\n- Support1\n- Support2");
			return true;
		}
		else if(args.length == 1){
			if(args[0].equalsIgnoreCase("1")){
				mapChannel.put(p.getUniqueId(), 0);
				p.sendMessage(plugin.namespace + "Du bist nun im Channel \"1\"");
				return true;
			}
			else if(args[0].equalsIgnoreCase("2")){
				mapChannel.put(p.getUniqueId(), 1);
				p.sendMessage(plugin.namespace + "Du bist nun im Channel \"2\"");
				return true;
			}
			else if(args[0].equalsIgnoreCase("support1")){
				mapChannel.put(p.getUniqueId(), 2);
				p.sendMessage(plugin.namespace + "Du bist nun im Channel \"Support1\"");
				return true;
			}
			else if(args[0].equalsIgnoreCase("support2")){
				mapChannel.put(p.getUniqueId(), 3);
				p.sendMessage(plugin.namespace + "Du bist nun im Channel \"Support2\"");
				return true;
			}
			else {
				//Annehmen, das ein Spieler gesucht wird
				UUID targetUUID = plugin.uuid.getUUIDFromPlayer(args[0]);
				if(targetUUID == null){
					p.sendMessage(plugin.uuid.ERROR);
					return true;
				}
				if(!mapChannel.containsKey(targetUUID)){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Der Spieler " + args[0] + " konnte in keinem Channel gefunden werden");
					return true;
				}
				int channel = mapChannel.get(targetUUID);
				switch(channel){
				case 0:
					p.sendMessage(plugin.namespace + args[0] + " ist im Channel \"1\"");
					break;
				case 1:
					p.sendMessage(plugin.namespace + args[0] + " ist im Channel \"2\"");
					break;
				case 2:
					p.sendMessage(plugin.namespace + args[0] + " ist im Channel \"Support1\"");
					break;
				case 3:
					p.sendMessage(plugin.namespace + args[0] + " ist im Channel \"Support2\"");
				}
				return true;
			}
		}
		else {
			p.sendMessage(plugin.namespace + ChatColor.RED + "/channel <channelname oder spielername>");
			return true;
		}
	}
	
	@EventHandler
	public void onJoinEvent(PlayerJoinEvent event){
		Player p = event.getPlayer();
		mapChannel.put(p.getUniqueId(), 0); //Standartm��ig in den �ffentlichen Channel 0
	}
	
	@EventHandler
	public void OnQuitEvent(PlayerQuitEvent event){
		Player p = event.getPlayer();
		mapChannel.remove(p.getUniqueId());
	}
	
	// **** Chat **** //

	@EventHandler
	public void onChatEvent(AsyncPlayerChatEvent e){
		Player p = e.getPlayer();
		String msg = e.getMessage();

		//Event abbrechen
		e.setCancelled(true);
		
		if(p.hasPermission("epicraft.chat.color")){
			msg = ChatColor.translateAlternateColorCodes('$', msg);
			msg = msg.replace("<3", "\u2764"); //Herzen
		}
		
		EpicraftPlayer epiSender = plugin.pManager.getEpicraftPlayer(p.getUniqueId());
		if(epiSender != null){
			if(epiSender.chatMessages == false){
				p.sendMessage(plugin.namespace + ChatColor.RED + "Du kannst zur Zeit keine Nachrichten senden!");
				return;
			}
		}
		if(!p.hasPermission("epicraft.chat")){
			p.sendMessage(plugin.error);
			plugin.api.sendLog("[Epicraft - Chat] " + p.getName() + " versucht auf den Chat zuzugreifen");
			return;
		}
		
		//Zeit formatieren und bereitstellen
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.GERMAN);
		String timeStamp = sdf.format(new Date());
		
		//Rufe Spieler auf dem Server ab
		if(!mapChannel.containsKey(p.getUniqueId()))
			mapChannel.put(p.getUniqueId(), 0);
		int channel = mapChannel.get(p.getUniqueId());
		for(Player player : Bukkit.getServer().getOnlinePlayers()){
			//Einstellung des Spielers aufrufen
			if(channel != mapChannel.get(player.getUniqueId()))
				continue;
			EpicraftPlayer epiPlayer = plugin.pManager.getEpicraftPlayer(player.getUniqueId());
			
			if(epiPlayer == null){ // TRUE = Annehmen, das alle Einstellungen auf TRUE stehen
				player.sendMessage(ChatColor.GOLD + "[" + ChatColor.GRAY + BukkitWorld2Name(p.getWorld()) + ChatColor.GOLD + "]" 
						+ "[" + ChatColor.GRAY + timeStamp + ChatColor.GOLD + "] "
						+ getRang(p) + ChatColor.GRAY + ": " + msg);
			}
			else{
				//Nachricht vorbereiten
				String resultMessage = "";
				
				if(!epiPlayer.chatMessages){// M�chte der Spieler Chatnachrichten bekommen
					continue;
				}
				if(epiPlayer.chatWorld){//M�chte der Spieler die Welt des Senders angezeigt bekommen
					resultMessage = ChatColor.GOLD + "[" + ChatColor.GRAY + BukkitWorld2Name(p.getWorld()) + ChatColor.GOLD + "]";
				}
				if(epiPlayer.chatTime){//M�chte der Spieler die Zeit angezeigt bekommen
					resultMessage += ChatColor.GOLD + "[" + ChatColor.GRAY + timeStamp + ChatColor.GOLD + "]";
				}
				resultMessage += getRang(p) + ChatColor.GRAY + ": " + msg;
				player.sendMessage(resultMessage);
			}
		}
	}
	
	private String BukkitWorld2Name(World w){
		String name = w.getName();
		if(name.equalsIgnoreCase("world"))
			return "S";
		return "?";
	}
	
	private String getRang(Player p){
		//Rang herausfinden
		if(p.hasPermission("epicraft.permission.gast")){
			return ChatColor.AQUA + p.getName();
		}
		else if(p.hasPermission("epicraft.permission.spieler")){
			return ChatColor.BLUE + p.getName();
		}
		else if(p.hasPermission("epicraft.permission.stammi")){
			return ChatColor.DARK_BLUE + p.getName();
		}
		else if(p.hasPermission("epicraft.permission.guard")){
			return ChatColor.DARK_PURPLE + p.getName();
		}
		else if(p.hasPermission("epicraft.permission.moderator")){
			return ChatColor.DARK_GREEN + p.getName();
		}
		else if(p.hasPermission("epicraft.permission.admin")){
			return ChatColor.DARK_RED + p.getName();
		}
		else
			return ChatColor.GREEN + p.getName();
		
	}
}
