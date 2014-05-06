package de.wolfsline.register;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.helpClasses.EpicraftPlayer;

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
		if(!(p.hasPermission("epicraft.fragebogen.team"))){
			p.sendMessage(plugin.error);
			plugin.api.sendLog("[Epicraft - Fragebogen] " + p.getName() + " wollte auf den Befehl zugreifen!");
			return true;
		}
		if(args.length == 1){
			String targetName = args[0];
			UUID targetUUID = plugin.uuid.getUUIDFromPlayer(targetName);
			if(targetUUID == null){
				p.sendMessage(plugin.uuid.ERROR);
				return true;
			}
			Connection conn = plugin.getMySQL().getConnection();
			ResultSet rs = null;
			PreparedStatement st = null;
			try{
				st = conn.prepareStatement("SELECT * FROM Fragebogen WHERE UUID='" + targetUUID + "'");
				rs = st.executeQuery();
				if(rs.next()){
					for(int i = 0 ; i < 10 ; i++){
						p.sendMessage(ChatColor.GOLD + "Frage: " + ChatColor.WHITE + this.qsl.Questions[i]);
						p.sendMessage(ChatColor.GOLD + "Spieler: " + getAnswerFromInt(rs.getInt(i+2)));
					}
					plugin.getMySQL().closeRessources(rs, st);
					return true;
				}
				else{
					p.sendMessage(plugin.namespace + ChatColor.RED + "Keinen Eintrag gefunden!");
					return true;
				}
				
			} catch(SQLException e){
				
			}
			
		}
		else if(args.length == 2){
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
	
	private String getAnswerFromInt(int a){
		if(a == 0){
			return ChatColor.DARK_RED + "Falsch";
		}
		else if(a == 1){
			return ChatColor.DARK_GREEN + "Richtig";
		}
		else{
			return String.valueOf(a);
		}
	}
}
