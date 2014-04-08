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
		if(!(cs.hasPermission("epicraft.restriction.team") || cs.isOp())){
			cs.sendMessage(plugin.namespace + ChatColor.RED + "Du hast keinen Zugriff auf diesen Befehl");
			plugin.api.sendLog("[Epicraft - Kick] " + cs.getName() + " hat versucht auf den Kick-Befehl zuzugreifen");
			return true;
		}
		if(args.length > 1){
			for(int i = 1 ; i < args.length ; i++){
				reason += args[i] + " ";
			}
			Player p = Bukkit.getPlayer(args[0]);
			if(p != null){
				if(cs instanceof Player){
					Player team = (Player) cs;
					if(!team.isOp()){
						if(team.hasPermission("epicraft.restriction.guard") && (p.hasPermission("epicraft.restriction.mod") || p.hasPermission("epicraft.guard"))){
							team.kickPlayer(ChatColor.RED + "Hör auf dich selbst zu kicken ;)");
							return true;
						}
						else if(p.hasPermission("epicraft.restriction.mod") && team.hasPermission("epicraft.restriction.mod")){
							team.kickPlayer(ChatColor.RED + "Hör auf dich selbst zu kicken ;)");
							return true;
						}
					}
				}
				p.kickPlayer(reason);
				plugin.api.sendLog("[Epicraft - Kick] " + cs.getName() + " hat den Spieler " + p.getName() + " vom Server gegicked");
				plugin.api.sendLog("[Epicraft - Kick] Grund: " + reason);
				writeToDatabase(cs, p, reason);
				return true;
			}
		}
		else
			cs.sendMessage(ChatColor.RED + "Zu wenig Argumente!");
		return false;
	}
	
	private void writeToDatabase(CommandSender cs, Player p, String reason){
		MySQL sql = this.plugin.getMySQL();
		String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
		String date = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
		String update = "INSERT INTO warning (username, typ, reason, time, date, teamuser) VALUES ('" + p.getName() + "', 'kick', '" + reason + "', '" + time + "', '" + date + "', '" + cs.getName() + "')";
		sql.queryUpdate(update);
	}

}
