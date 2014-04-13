package de.wolfsline.afk;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.helpClasses.EpicraftPlayer;

public class AFK implements CommandExecutor{
	
	private Epicraft plugin;
	
	public AFK(Epicraft plugin){
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(!p.hasPermission("epicraft.afk")){
			p.sendMessage(plugin.error);
			plugin.api.sendLog("[Epicraft - AFK] " + p.getName() + " hat versucht auf den Befehl zuzugreifen");
			return true;
		}
		EpicraftPlayer epiPlayer = plugin.pManager.getEpicraftPlayer(p.getName());
		if(epiPlayer == null){
			p.sendMessage(plugin.namespace + ChatColor.RED + "Es ist ein Fehler aufgetreten!");
			return true;
		}
		if(epiPlayer.isAFK){//Wenn er AFK ist
			p.setDisplayName(p.getDisplayName().replace("*", ""));
			p.sendMessage(plugin.namespace + ChatColor.WHITE + "Du bist nun nicht mehr AFK!");
			plugin.api.sendLog("[Epicraft - AFK] " + p.getName() + " ist nun wieder da");
			epiPlayer.isAFK = false;
			return true;
		}
		else{ //Wenn er nicht AFK ist
			String name = p.getDisplayName();
			if(name.length() == 16)
				name = name.substring(0, 14);
			name += "*";
			p.setDisplayName(name);
			p.sendMessage(plugin.namespace + ChatColor.WHITE + "Du bist nun AFK");
			plugin.api.sendLog("[Epicraft - AFK] " + p.getName() + " ist nun AFK");
			epiPlayer.isAFK = true;
			return true;
		}
	}

}
