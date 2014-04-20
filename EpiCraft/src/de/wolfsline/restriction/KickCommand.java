package de.wolfsline.restriction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

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
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler");
			return true;
		}
		Player p = (Player) cs;
		String reason = "";
		if(!p.hasPermission("epicraft.kick")){
			p.sendMessage(plugin.error);
			plugin.api.sendLog("[Epicraft - Kick] " + p.getName() + " hat versucht auf den Befehl zuzugreifen");
			return true;
		}
		if(args.length > 1){
			for(int i = 1 ; i < args.length ; i++){
				reason += args[i] + " ";
			}
			UUID targetUUID = plugin.uuid.getUUIDFromPlayer(args[0]);
			if(targetUUID == null){
				p.sendMessage(plugin.uuid.ERROR);
				return true;
			}
			Player targetPlayer = Bukkit.getPlayer(targetUUID);
			if(targetPlayer != null){
				targetPlayer.kickPlayer(reason);
				plugin.api.sendLog("[Epicraft - Kick] " + p.getName() + " hat den Spieler " + targetPlayer.getName() + " vom Server gekicked");
				plugin.api.sendLog("[Epicraft - Kick] Grund: " + reason);
				writeToDatabase(p, targetPlayer, reason);
				return true;
			}
		}
		cs.sendMessage(ChatColor.RED + "/kick <Spieler> <Grund>");
		return true;
	}
	
	private void writeToDatabase(Player p, Player kickPlayer, String reason){
		MySQL sql = this.plugin.getMySQL();
		String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
		String date = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
		String update = "INSERT INTO Verwarnung (UUID, Typ, Grund, Zeit, Datum, Team) VALUES ('" + kickPlayer.getUniqueId() + "', 'kick', '" + reason + "', '" + time + "', '" + date + "', '" + p.getUniqueId() + "')";
		sql.queryUpdate(update);
	}

}
