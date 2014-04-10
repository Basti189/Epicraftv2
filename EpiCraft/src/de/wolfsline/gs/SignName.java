package de.wolfsline.gs;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import de.wolfsline.Epicraft.Epicraft;

public class SignName implements CommandExecutor, Listener{

	private Epicraft plugin;
	
	private HashMap<String, Boolean> signmap = new HashMap<String, Boolean>();
	
	public SignName(Epicraft plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player))
			return true;
		Player p = (Player) cs;
		if(!(p.hasPermission("epicraft.gs.sign") || p.isOp())){
			p.sendMessage(plugin.error);
			plugin.api.sendLog("[Epicraft - Grundstücksschilder] " + p.getName() + " hat versucht auf den Grundstücksschild-Befehl zuzugreifen");
			return true;
		}
		boolean tmp = false;
		if(signmap.containsKey(p.getName())){
			tmp = signmap.get(p.getName());
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
		signmap.put(p.getName(), tmp);
		return true;
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent event){
		Sign sign = (Sign) event.getBlock().getState();
		Player p = event.getPlayer();
		if(signmap.containsKey(p.getName())){
			if(signmap.get(p.getName())){
				event.setLine(0, "---------------");
				event.setLine(1, "Grundstück von");
				event.setLine(2, p.getName());
				event.setLine(3, "---------------");
				sign.update();
			}
		}
	}
	
}
