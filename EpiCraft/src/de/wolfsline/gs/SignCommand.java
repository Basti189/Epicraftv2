package de.wolfsline.gs;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.wolfsline.Epicraft.Epicraft;

public class SignCommand implements CommandExecutor{
private Epicraft plugin;
	public SignCommand(Epicraft plugin) {
		this.plugin = plugin;
		this.plugin.signmap = new HashMap<String, Boolean>();
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player))
			return true;
		Player p = (Player) cs;
		if(!(p.hasPermission("epicraft.gs.sign") || p.isOp())){
			p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast keinen Zugriff auf diesen Befehl!");
			plugin.api.sendLog("[Epicraft - Grundstücksschilder] " + p.getName() + " hat versucht auf den Grundstücksschild-Befehl zuzugreifen");
			return true;
		}
		boolean tmp = false;
		if(plugin.signmap.containsKey(p.getName())){
			tmp = plugin.signmap.get(p.getName());
		}
		if(tmp){
			p.sendMessage(plugin.namespace + ChatColor.WHITE + "Schilder werden nun nicht mehr beschriftet");
			plugin.api.sendLog("[Epicraft - Grundstücksschilder] " + p.getName() + " beschriftet nun keine Schilder mehr automatisch");
			tmp = false;
		}
		else{
			p.sendMessage(plugin.namespace + ChatColor.WHITE + "Schilder werden nun beschriftet");
			plugin.api.sendLog("[Epicraft - Grundstücksschilder] " + p.getName() + " beschriftet nun Schilder automatisch");
			tmp = true;
		}
		plugin.signmap.put(p.getName(), tmp);
		return true;
	}
}
