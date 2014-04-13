package de.wolfsline.administration;

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

public class DebugCommand implements CommandExecutor, Listener{

	private boolean isDebugService = false;
	private Epicraft plugin;
	
	public DebugCommand(Epicraft plugin){
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs.isOp() || cs.hasPermission("epicraft.wartung")))
			return true;
		if(args.length == 1){
			String state = args[0].toLowerCase();
			if(state.equals("false")){
				isDebugService = false;
				cs.sendMessage(ChatColor.WHITE + "DebugService ist deaktiviert");
				plugin.api.sendLog("[Epicraft - Wartung] Server hat den Wartungsmodus beendet");
			}
			else if(state.equals("true")){
				isDebugService = true;
				cs.sendMessage(ChatColor.WHITE + "DebugService ist aktiviert");
				plugin.api.sendLog("[Epicraft - Wartung] Server ist im Wartungsmodus");
				
			}
			else{
				cs.sendMessage(ChatColor.RED + "Zu wenig Argumente");
			}
			return true;
		}
		cs.sendMessage("/debug <true>, <false>");
		return true;
	}
	
	@EventHandler
	public void onPlayerLoginEvent(PlayerLoginEvent event){
		if(!isDebugService)
			return;
		Player p = event.getPlayer();
		if(!p.isOp()){
			String Reason = "$4Wartungsarbeiten!\n\n$fWir bitten um Entschuldigung!\nWeitere Informationen unter\nhttp://forum.epicraft.de";
			event.setResult(Result.KICK_OTHER);
			event.setKickMessage(ChatColor.translateAlternateColorCodes('$', Reason));
		}
	}

}
