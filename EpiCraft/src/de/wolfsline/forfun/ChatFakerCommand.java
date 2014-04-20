package de.wolfsline.forfun;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.wolfsline.Epicraft.Epicraft;

public class ChatFakerCommand implements CommandExecutor{

	private Epicraft plugin;
	public ChatFakerCommand(Epicraft plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(!p.hasPermission("epicraft.chat.fake"))
			return true;
		if(args.length >= 2){
			UUID targetUUID = plugin.uuid.getUUIDFromPlayer(args[0]);
			if(targetUUID == null){
				p.sendMessage(plugin.uuid.ERROR);
				return true;
			}
			Player pu = Bukkit.getPlayer(targetUUID);
			if(pu == null){
				p.sendMessage(plugin.namespace + ChatColor.RED + "Spieler ist nicht online!");
			}
			if(pu.isOp()){
				p.sendMessage(plugin.namespace + ChatColor.RED + "Diesen Befehl kannst du bei diesem Spieler nicht ausführen!");
				return true;
			}
			String nachricht = "";
			for(int i = 1 ; i < args.length ; i++)
				nachricht += args[i] + " ";
			pu.chat(nachricht);
			return true;
		}
		return false;
	}

}
