package de.wolfsline.API;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class EventAPI extends Handler implements Listener, CommandExecutor{
	
	private List<String> list = new ArrayList<String>();
	private List<apiClientHandler> clients = new ArrayList<apiClientHandler>();
	private String message = "";
	private List<String> cmds = new ArrayList<String>(Arrays.asList("spawn", "gs", "restart", "sign", "ep", "warn", "kick", "ban", "home", "bank", "hide", "unhide", "invsee", "lightning", "gun", "gernade", "ensee", "jail", "chat", "head", "login", "support", "l", "changepw", "register", "email", "onlinetime", "ot", "pvp", "fly", "walk", "ts", "settings", "me", "tp", "horse", "app", "secure", "api"));

	/* ---------------- LogHandler ------------------- */
	@Override
	public void close() throws SecurityException {}

	@Override
	public void flush() {}

	@Override
	public void publish(LogRecord record) {
		String msg = record.getMessage();
		String pluginName = record.getLevel().getLocalizedName();
		//sendLog(pluginName + " - " + msg);
	}
	
	
	/* ---------------- Listener ------------------- */
	
	@EventHandler
	public void onPlayerCommandListener(PlayerCommandPreprocessEvent event){
		String who = event.getPlayer().getName();
		String command = event.getMessage();
		for(String tmpcmd : cmds){
			if(command.startsWith("/" + tmpcmd))
				return;
		}
		String result = "[Epicraft - Befehl] " + who + " führt " + command + " aus";
		sendLog(result);
	}
	
	@EventHandler
	public void onPlayerChatEvent(AsyncPlayerChatEvent event){
		String who = event.getPlayer().getName();
		String message = event.getMessage();
		String result = "[Epicraft - Chat] " + who + " schreibt " + message;
		sendLog(result);
	}
	
	@EventHandler
	public void onPlayerDeathEvent(PlayerDeathEvent event){
		String killer = "";
		try{
			killer = event.getEntity().getKiller().getName();
		}
		catch(NullPointerException ex){
		}
		String result = "[Epicraft - Tod] ";
		if(killer == "")
			result += event.getEntity().getName() + " ist gestorben";
		
		else if(killer.equalsIgnoreCase(event.getEntity().getName()))
			result += event.getEntity().getName() + " hat sich selber getötet";
		else
			result += event.getEntity().getName() + " wurde von " + killer + " getötet";
		sendLog(result);
	}
	
	@EventHandler
	public void onPlayerGameModeChangeEvent(PlayerGameModeChangeEvent event){
		String who = event.getPlayer().getName();
		GameMode gm = event.getNewGameMode();
		String result = "[Epicraft - Spielmodus] " + who + " spielt jetzt im ";
		if(gm == GameMode.SURVIVAL)
			result += "Überlebensmodus";
		else if(gm == GameMode.CREATIVE)
			result += "Kreativmodus";
		sendLog(result);
	}
	
	@EventHandler
	public void onPlayerWorldChangeEvent(PlayerChangedWorldEvent event){
		String who = event.getPlayer().getName();
		String result = "[Epicraft - Welt] " + who + " wechselt von der Welt " + event.getFrom().getName().toString() + " zur Welt " + event.getPlayer().getLocation().getWorld().getName();
		sendLog(result);
	}
	
	@EventHandler
	public void onWeatherChangeEvent(WeatherChangeEvent event){
		String where = event.getWorld().getName();
		String result = "[Epicraft - Wetter] Es ";
		if(event.toWeatherState())
			result += "regnet nun in der Welt ";
		else
			result += "scheint nun die Sonne in der Welt ";
		result += where;
		sendLog(result);
		
	}
	
	@EventHandler
	public void onPlayerDropItemEvent(PlayerDropItemEvent event){
		String who = event.getPlayer().getName();
		String what = event.getItemDrop().getItemStack().getType().toString();
		if(what.equalsIgnoreCase("diamond") || what.equalsIgnoreCase("skull_item")){
			String result = "[Epicraft - Drop] " + who + " hat " + event.getItemDrop().getItemStack().getAmount() + " " + what + " in der Welt " + event.getPlayer().getLocation().getWorld().getName().toString() + " gedroppt";
			sendLog(result);
		}
	}
	
	@EventHandler
	public void onPlayerPickUpItemEvent(PlayerPickupItemEvent event){
		String who = event.getPlayer().getName();
		String what = event.getItem().getItemStack().getType().toString();
		String amount = String.valueOf(event.getItem().getItemStack().getAmount());
		if(what.equalsIgnoreCase("diamond") || what.equalsIgnoreCase("skull_item")){
			String result = "[Epicraft - PickUp] " + who + " hat " + amount + " " + what + " in der Welt " + event.getPlayer().getLocation().getWorld().getName().toString() + " aufgehoben";
			sendLog(result);
		}
	}
	
	/* ---------------- HandleMessages ------------------- */
	
	public void sendLog(String result){
		Bukkit.getServer().getLogger().info(result);
		this.message = result;
		for(Player p : Bukkit.getServer().getOnlinePlayers()){
			if(list.contains(p.getName())){
				p.sendMessage(result);
			}
		}
		for(apiClientHandler client : clients){
			client.addToList(result);
		}
	}
	
	public void addClientToList(apiClientHandler client){
		clients.add(client);
	}
	
	public void removeClientFromList(apiClientHandler client){
		clients.remove(client);
	}
	
	public String getLog(){
		return this.message;
	}
	
	public boolean isPlayerVerifyed(String username){
		return this.list.contains(username);
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!cs.hasPermission("epicraft.api")){
			return true;
		}
		if(list.contains(cs.getName())){
			list.remove(cs.getName());
			cs.sendMessage(ChatColor.GREEN + "Du erhälst nun keine Nachrichten mehr von der Epicraft-EventAPI!");
		}
			
		else{
			list.add(cs.getName());
			cs.sendMessage(ChatColor.GREEN + "Du hast zugriff auf die EpiCraft-EventAPI erhalten!");
		}
		return true;
	}

}
