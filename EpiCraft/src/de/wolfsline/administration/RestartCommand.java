package de.wolfsline.administration;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import de.wolfsline.Epicraft.Epicraft;

public class RestartCommand implements CommandExecutor, Listener{
	private Epicraft plugin;
	private boolean restart = false;
	private Thread thread = null;
	private int time = 0;

	public RestartCommand(Epicraft plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs.hasPermission("epicraft.restart") || cs.isOp())){
			cs.sendMessage(plugin.error);
			plugin.api.sendLog("[Epicraft - Neustart] " + cs.getName() + " versuchte den Server neuzustarten");
			return true;
		}
		if(!restart){
			plugin.api.sendLog("[Epicraft - Neustart] " + cs.getName() + " hat einen Serverneustart veranlasst");
			Bukkit.getServer().broadcastMessage(plugin.namespace + ChatColor.DARK_PURPLE + "Achtung! Server neustart steht bevor!");
			Bukkit.getServer().broadcastMessage(plugin.namespace + ChatColor.DARK_PURPLE + "Achtung! Bitte Items einsammeln!");
			Bukkit.getServer().broadcastMessage(plugin.namespace + ChatColor.DARK_PURPLE + "Achtung! Neustart in 60 Sekunden!");
			if(args.length == 1){
				Bukkit.getServer().broadcastMessage(plugin.namespace + ChatColor.DARK_PURPLE + "Grund: " + args[0]);
			}
			plugin.api.sendLog("[Epicraft - Neustart] Server startet in 60 Sekunden neu");
			time = 60;
			thread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(1000*30);
					} catch (InterruptedException e) {
					}
					Bukkit.getServer().broadcastMessage(plugin.namespace + ChatColor.DARK_PURPLE + "Neustart in 30 Sekunden!");
					plugin.api.sendLog("[Epicraft - Neustart] Server startet in 30 Sekunden neu");
					time = 30;
					try {
						Thread.sleep(1000*10);
					} catch (InterruptedException e) {
					}
					Bukkit.getServer().broadcastMessage(plugin.namespace + ChatColor.DARK_PURPLE + "Neustart in 20 Sekunden!");
					plugin.api.sendLog("[Epicraft - Neustart] Server startet in 20 Sekunden neu");
					time = 20;
					try {
						Thread.sleep(1000*10);
					} catch (InterruptedException e) {
					}
					Bukkit.getServer().broadcastMessage(plugin.namespace + ChatColor.DARK_PURPLE + "Neustart in 10 Sekunden!");
					plugin.api.sendLog("[Epicraft - Neustart] Server startet in 10 Sekunden neu");
					time = 10;
					try {
						Thread.sleep(1000*5);
					} catch (InterruptedException e) {
					}
					Bukkit.getServer().broadcastMessage(plugin.namespace + ChatColor.DARK_PURPLE + "Neustart in 5 Sekunden!");
					plugin.api.sendLog("[Epicraft - Neustart] Server startet in 5 Sekunden neu");
					time = 5;
					try {
						Thread.sleep(1000*1);
					} catch (InterruptedException e) {
					}
					Bukkit.getServer().broadcastMessage(plugin.namespace + ChatColor.DARK_PURPLE + "Neustart in 4 Sekunden!");
					plugin.api.sendLog("[Epicraft - Neustart] Server startet in 4 Sekunden neu");
					time = 4;
					try {
						Thread.sleep(1000*1);
					} catch (InterruptedException e) {
					}
					Bukkit.getServer().broadcastMessage(plugin.namespace + ChatColor.DARK_PURPLE + "Neustart in 3 Sekunden!");
					plugin.api.sendLog("[Epicraft - Neustart] Server startet in 3 Sekunden neu");
					time = 3;
					try {
						Thread.sleep(1000*1);
					} catch (InterruptedException e) {
					}
					Bukkit.getServer().broadcastMessage(plugin.namespace + ChatColor.DARK_PURPLE + "Neustart in 2 Sekunden!");
					plugin.api.sendLog("[Epicraft - Neustart] Server startet in 2 Sekunden neu");
					time = 2;
					try {
						Thread.sleep(1000*1);
					} catch (InterruptedException e) {
					}
					Bukkit.getServer().broadcastMessage(plugin.namespace + ChatColor.DARK_PURPLE + "Neustart in 1 Sekunden!");
					plugin.api.sendLog("[Epicraft - Neustart] Server startet in 1 Sekunden neu");
					time = 1;
					try {
						Thread.sleep(1000*1);
					} catch (InterruptedException e) {
					}
					Bukkit.getServer().broadcastMessage(plugin.namespace + ChatColor.DARK_PURPLE + "  --> Neustart <--");
					plugin.api.sendLog("[Epicraft - Neustart] Server startet neu");
					time = 0;
					try {
						Thread.sleep(1000*1);
					} catch (InterruptedException e) {
					}
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop Server startet neu!\n\nBis gleich!");
				}
			});
			restart = true;
			thread.start();
			return true;
		}
		else{
			if(thread != null)
				thread.stop();
			Bukkit.getServer().broadcastMessage(plugin.namespace + ChatColor.DARK_PURPLE + "Neustart wurde abgebrochen!");
			plugin.api.sendLog("[Epicraft - Neustart] " + cs.getName() + " hat den Serverneustart abgebrochen");
			restart = false;
			return true;
		}
	}
	
	@EventHandler
	public void onPlayerLoginEvent(PlayerLoginEvent event){
		if(!restart)
			return;
		Player p = event.getPlayer();
		if(!p.isOp()){
			if(time == 60){
				return;
			}
			else if(time <= 20){
				String Reason = "$4Serverneustart!\n\n$fDer Server startet in wenigen Sekunden neu!";
				event.setResult(Result.KICK_OTHER);
				event.setKickMessage(ChatColor.translateAlternateColorCodes('$', Reason));
			}
			else if (time == 0){
				String Reason = "$4Serverneustart!\n\n$fDer Server startet jetzt neu";
				event.setResult(Result.KICK_OTHER);
				event.setKickMessage(ChatColor.translateAlternateColorCodes('$', Reason));
			}
			else{
				String Reason = "$4Serverneustart!\n\n$fDer Server startet in " + time + " Sekunden neu";
				event.setResult(Result.KICK_OTHER);
				event.setKickMessage(ChatColor.translateAlternateColorCodes('$', Reason));
			}
			
		}
	}
}
