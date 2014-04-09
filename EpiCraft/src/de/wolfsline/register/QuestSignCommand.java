package de.wolfsline.register;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.wolfsline.Epicraft.Epicraft;

public class QuestSignCommand implements CommandExecutor{
	
	private QuestSignListener qsl;
	private Epicraft plugin;
	
	public QuestSignCommand(Epicraft plugin, QuestSignListener qsl) {
		this.plugin = plugin;
		this.qsl = qsl;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du musst ein Spieler sein");
			return true;
		}
		Player p = (Player) cs;
		if(!p.isOp()){
			p.sendMessage(plugin.error);
			plugin.api.sendLog("[Epicraft - Fragebogen] " + p.getName() + " wollte auf den Befehl zugreifen!");
			return true;
		}
		if(args.length == 2){
			if(args[0].equalsIgnoreCase("pos")){
				if(args[1].equalsIgnoreCase("start")){
					p.sendMessage(plugin.namespace + "Startpunkt für Fragebogen gesetzt");
					this.qsl.setStartLocation(p);
					return true;
				}
				else if(args[1].equalsIgnoreCase("raum")){
					p.sendMessage(plugin.namespace + "Raum für Fragebogen gesetzt");
					this.qsl.setRaumLocation(p);
					return true;
				}
			}
		}
		return false;
	}
}
