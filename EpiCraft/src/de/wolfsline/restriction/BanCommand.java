package de.wolfsline.restriction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

/*
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
*/
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criterias;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.data.MySQL;

public class BanCommand implements CommandExecutor{
	
	private Epicraft plugin;
	public BanCommand(Epicraft plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		String reason = "";
		if(!(cs.hasPermission("epicraft.restriction.team.ban") || cs.isOp())){
			cs.sendMessage(ChatColor.RED + "Du hast keinen Zugriff auf diesen Befehl");
			plugin.api.sendLog("[Epicraft - Ban] " + cs.getName() + " hat versucht auf den Ban-Befehl zuzugreifen");
			return true;
		}
		if(args.length > 1){
			for(int i = 1 ; i < args.length ; i++){
				reason += args[i] + " ";
			}
			Player p = Bukkit.getPlayer(args[0]);
			cs.sendMessage(plugin.namespace + ChatColor.WHITE + "Suche nach Spieler: " + args[0]);
			if(p != null){
				if(cs instanceof Player){
					Player team = (Player) cs;
					if(!team.isOp()){
						if(p.hasPermission("epicraft.restriction.team") || p.hasPermission("epicraft.restriction.team.ban") ){
							team.kickPlayer(plugin.namespace + ChatColor.RED + "Hör auf dich selbst zu kicken ;)");
							return true;
						}
					}
				}
				cs.sendMessage(plugin.namespace + ChatColor.WHITE + args[0] + " ist online");
				p.kickPlayer(reason);
				p.setBanned(true);
				writeToDatabase(cs, p.getName(), reason);
				plugin.api.sendLog("[Epicraft - Ban] " + cs.getName() + " hat den Spieler(online)" + p.getName() + " vom Server gebannt");
				plugin.api.sendLog("[Epicraft - Ban] Grund: " + reason);
				return true;
			}
			else{
				OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
				if(offlinePlayer != null){
					cs.sendMessage(plugin.namespace + ChatColor.WHITE + "Spieler ist nicht online");
					cs.sendMessage(plugin.namespace + ChatColor.WHITE + "Banne offline Spieler");
					if(offlinePlayer.isBanned()){
						cs.sendMessage(plugin.namespace + ChatColor.WHITE + offlinePlayer.getName() + " ist bereits gebannt!");
						return true;
					}
					offlinePlayer.setBanned(true);
					writeToDatabase(cs, offlinePlayer.getName(), reason);
					plugin.api.sendLog("[Epicraft - Ban] " + cs.getName() + " hat den Spieler(Offline)" + offlinePlayer.getName() + " vom Server gebannt");
					return true;
				}
				else{
					cs.sendMessage(plugin.namespace + ChatColor.RED + "Kann Spieler nicht bannen!");
					return true;
				}
			}
		}
		else
			cs.sendMessage(plugin.namespace + ChatColor.RED + "Zu wenig Argumente!");
		return false;
	}
	
	private void writeToDatabase(CommandSender cs, String name, String reason){
		MySQL sql = this.plugin.getMySQL();
		String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
		String date = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
		String update = "INSERT INTO warning (username, typ, reason, time, date, teamuser) VALUES ('" + name + "', 'ban', '" + reason + "', '" + time + "', '" + date + "', '" + cs.getName() + "')";
		sql.queryUpdate(update);
	}
	
	private String showDetails(String name){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		String text = "";
		try {
			st = conn.prepareStatement("SELECT * FROM warning WHERE username='" + name + "'");
			rs = st.executeQuery();
			while(rs.next()){
				String typ = rs.getString(3);
				if(typ.equalsIgnoreCase("warn")){
					text += "Verwarnung:\n";
				}
				else if(typ.equalsIgnoreCase("kick")){
					text += "Kick:\n";
				}
				else if(typ.equalsIgnoreCase("ban")){
					text += "Ban:\n";
				}
				text += "- Grund: " + rs.getString(4) + "\n";
				text += "- Datum: " + rs.getString(5) + " Uhr am " + rs.getString(6) + "\n\n";
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			sql.closeRessources(rs, st);
		}
		return text;
	}
}
