package de.wolfsline.restriction;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.data.MySQL;

public class KickCommand implements CommandExecutor{

	private Epicraft plugin;
	public KickCommand(Epicraft plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		String reason = "";
		if(!cs.hasPermission("epicraft.ban")){
			cs.sendMessage(plugin.error);
			plugin.api.sendLog("[Epicraft - Kick] " + cs.getName() + " hat versucht auf den Befehl zuzugreifen");
			return true;
		}
		if(args.length > 1){
			for(int i = 1 ; i < args.length ; i++){
				reason += args[i] + " ";
			}
			Player targetPlayer = Bukkit.getPlayer(args[0]);
			if(targetPlayer != null){
				targetPlayer.kickPlayer(reason);
				plugin.api.sendLog("[Epicraft - Kick] " + cs.getName() + " hat den Spieler " + targetPlayer.getName() + " vom Server gekicked");
				plugin.api.sendLog("[Epicraft - Kick] Grund: " + reason);
				writeToDatabase(cs, targetPlayer, reason);
				return true;
			}
		}
		cs.sendMessage(ChatColor.RED + "/kick <Spieler> <Grund>");
		return true;
	}
	
	private void writeToDatabase(CommandSender cs, Player p, String reason){
		MySQL sql = this.plugin.getMySQL();
		String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
		String date = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
		String update = "INSERT INTO Verwarnung (Benutzername, Typ, Grund, Zeit, Datum, Team) VALUES ('" + p.getName() + "', 'kick', '" + reason + "', '" + time + "', '" + date + "', '" + cs.getName() + "')";
		sql.queryUpdate(update);
	}

}
