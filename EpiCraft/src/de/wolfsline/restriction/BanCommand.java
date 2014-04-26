package de.wolfsline.restriction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.wolfsline.Epicraft.Epicraft;

public class BanCommand implements CommandExecutor{
	
	private Epicraft plugin;
	private BanList banList = Bukkit.getServer().getBanList(BanList.Type.NAME);
	
	public BanCommand(Epicraft plugin) {
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
		if(!p.hasPermission("epicraft.ban")){
			p.sendMessage(plugin.error);
			plugin.api.sendLog("[Epicraft - Ban] " + p.getName() + " hat versucht auf den Befehl zuzugreifen");
			return true;
		}
		if(args.length > 1){
			for(int i = 1 ; i < args.length ; i++){
				reason += args[i] + " ";
			}
			p.sendMessage(plugin.namespace + ChatColor.WHITE + "Suche nach Spieler: " + args[0]);
			UUID targetUUID = plugin.uuid.getUUIDFromPlayer(args[0]);
			if(targetUUID == null){
				p.sendMessage(plugin.uuid.ERROR);
				return true;
			}
			Player targetPlayer = Bukkit.getPlayer(targetUUID);
			if(targetPlayer != null){
				cs.sendMessage(plugin.namespace + ChatColor.WHITE + args[0] + " ist online");
				targetPlayer.kickPlayer(reason);
				banList.addBan(targetPlayer.getName(), reason, null, null);
				writeToDatabase(p, targetUUID, reason);
				plugin.api.sendLog("[Epicraft - Ban] " + p.getName() + " hat den Spieler(online)" + targetPlayer.getName() + " vom Server gebannt");
				plugin.api.sendLog("[Epicraft - Ban] Grund: " + reason);
				
				return true;
			}
			else{
				UUID offlineUUID = plugin.uuid.getUUIDFromPlayer(args[0]);
				if(offlineUUID == null){
					cs.sendMessage(plugin.uuid.ERROR);
					return true;
				}
				OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(offlineUUID);
				if(offlinePlayer != null){
					cs.sendMessage(plugin.namespace + ChatColor.WHITE + "Spieler ist nicht online");
					cs.sendMessage(plugin.namespace + ChatColor.WHITE + "Banne offline Spieler");
					if(offlinePlayer.isBanned()){
						cs.sendMessage(plugin.namespace + ChatColor.WHITE + offlinePlayer.getName() + " ist bereits gebannt!");
						return true;
					}
					banList.addBan(offlinePlayer.getName(), reason, null, null); //<-- FIX
					writeToDatabase(p, offlineUUID, reason);
					plugin.api.sendLog("[Epicraft - Ban] " + p.getName() + " hat den Spieler(Offline)" + offlinePlayer.getName() + " vom Server gebannt");
					return true;
				}
				else{
					cs.sendMessage(plugin.namespace + ChatColor.RED + "Kann Spieler nicht bannen!");
					return true;
				}
			}
		}
		cs.sendMessage(plugin.namespace + ChatColor.RED + "/ban <Spieler> <Grund>");
		return true;
	}
	
	private void writeToDatabase(Player p, UUID uuid, String reason){
		String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
		String date = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
		String update = "INSERT INTO Verwarnung (UUID, Typ, Grund, Zeit, Datum, Team) VALUES ('" + uuid + "', 'ban', '" + reason + "', '" + time + "', '" + date + "', '" + p.getUniqueId() + "')";
		plugin.getMySQL().queryUpdate(update);
	}
}
