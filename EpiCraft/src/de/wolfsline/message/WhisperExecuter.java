package de.wolfsline.message;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.wolfsline.Epicraft.Epicraft;

public class WhisperExecuter implements CommandExecutor {

	private Epicraft plugin;
	
	private HashMap<String,String> answer = new HashMap<String,String>();
	private HashMap<String,String> repeat = new HashMap<String,String>();
	
	public WhisperExecuter(Epicraft plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(p.hasPermission("epicraft.chat.whisper")){
			p.sendMessage(plugin.error);
			plugin.api.sendLog("[Epicraft - Flüstern] " + p.getName() + " versucht auf den Befehl zuzugreifen!");
			return true;
		}
		if(label.equals("r")){
			if(args.length == 0){
				p.sendMessage(plugin.namespace + ChatColor.RED + "Nachricht und fehlt!");
				return true;
			}
			if(repeat.containsKey(p.getName())){
				String name = repeat.get(p.getName());
				Player recv = Bukkit.getServer().getPlayer(name);
				if(recv == null){
					p.sendMessage(plugin.namespace + ChatColor.RED + name + " ist nicht online!");
					return true;
				}
				String msg = "";
				for(int i = 0 ; i < args.length ; i++){
					msg += args[i] + " ";
				}
				recv.sendMessage(ChatColor.GOLD + "Nachricht von " + p.getName() + ": " + ChatColor.WHITE + msg);
				p.sendMessage(ChatColor.GOLD + "Nachricht an " + recv.getName() + ": " + ChatColor.WHITE + msg);
				plugin.api.sendLog("[Epicraft - Flüstern] " + p.getName() + " flüstert " + recv.getName() + " eine Nachricht");
				return true;
			}
			else{
				p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast derzeit keinen Chatpartner!");
				return true;
			}
		}
		else if(label.equals("a")){
			if(args.length == 0){
				p.sendMessage(plugin.namespace + ChatColor.RED + "Nachricht und fehlt!");
				return true;
			}
			if(answer.containsKey(p.getName())){
				String name = answer.get(p.getName());
				Player recv = Bukkit.getServer().getPlayer(name);
				if(recv == null){
					p.sendMessage(plugin.namespace + ChatColor.RED + name + " ist nicht online!");
					return true;
				}
				String msg = "";
				for(int i = 0 ; i < args.length ; i++){
					msg += args[i] + " ";
				}
				recv.sendMessage(ChatColor.GOLD + "Nachricht von " + p.getName() + ": " + ChatColor.WHITE + msg);
				p.sendMessage(ChatColor.GOLD + "Nachricht an " + recv.getName() + ": " + ChatColor.WHITE + msg);
				plugin.api.sendLog("[Epicraft - Flüstern] " + p.getName() + " flüstert " + recv.getName() + " eine Nachricht");
				return true;
			}
			else{
				p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast derzeit keinen Chatpartner!");
				return true;
			}
		}
		if(args.length >= 2){ // w
			Player recv = Bukkit.getPlayer(args[0]);
			if(recv == null){
				p.sendMessage(plugin.namespace + ChatColor.RED + "Spieler ist nicht online!");
				return true;
			}
			String msg = "";
			for(int i = 1 ; i < args.length ; i++){
				msg += args[i] + " ";
			}
			recv.sendMessage(ChatColor.GOLD + "Nachricht von " + p.getName() + ": " + ChatColor.WHITE + msg);
			p.sendMessage(ChatColor.GOLD + "Nachricht an " + recv.getName() + ": " + ChatColor.WHITE + msg);
			plugin.api.sendLog("[Epicraft - Flüstern] " + p.getName() + " flüstert " + recv.getName() + " eine Nachricht");
			answer.put(recv.getName(), p.getName());
			repeat.put(p.getName(), recv.getName());
			return true;
		}
		else{
			p.sendMessage(plugin.namespace + ChatColor.RED + "/w <Spieler> <Nachricht>!");
			return true;
		}
	}

}
