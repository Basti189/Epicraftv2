package de.wolfsline.message;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.wolfsline.Epicraft.Epicraft;

public class WhisperExecuter implements CommandExecutor {

	private Epicraft plugin;
	
	private HashMap<UUID,UUID> answer = new HashMap<UUID,UUID>();
	private HashMap<UUID,UUID> repeat = new HashMap<UUID,UUID>();
	
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
			if(repeat.containsKey(p.getUniqueId())){
				UUID name = repeat.get(p.getUniqueId());
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
			if(answer.containsKey(p.getUniqueId())){
				UUID name = answer.get(p.getUniqueId());
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
			UUID targetUUID = plugin.uuid.getUUIDFromPlayer(args[0]);
			if(targetUUID == null){
				p.sendMessage(plugin.uuid.ERROR);
				return true;
			}
			Player recv = Bukkit.getPlayer(targetUUID);
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
			answer.put(recv.getUniqueId(), p.getUniqueId());
			repeat.put(p.getUniqueId(), recv.getUniqueId());
			return true;
		}
		else{
			p.sendMessage(plugin.namespace + ChatColor.RED + "/w <Spieler> <Nachricht>!");
			return true;
		}
	}

}
