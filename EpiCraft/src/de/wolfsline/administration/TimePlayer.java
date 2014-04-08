package de.wolfsline.administration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.data.MySQL;

public class TimePlayer implements CommandExecutor, Listener{

	private Epicraft plugin;
	private HashMap<String, Long> map = new HashMap<String, Long>();
	public TimePlayer(Epicraft plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(!p.hasPermission("epicraft.time.look")){
			p.sendMessage(plugin.error);
			plugin.api.sendLog("[Epicraft - Onlinezeit] " + p.getName() + " hat versucht seine Onlinezeit anzeigen zu lassen");
			return true;
		}
		int seconds = getOnlineTime(p);
		if(seconds == -1){
			p.sendMessage(plugin.namespace + ChatColor.RED + "Zeit konnte nicht ermittelt werden!");
			plugin.api.sendLog("[Epicraft - Onlinezeit] " + p.getName() + " Onlinzeit konnte nicht abgerufen werden");
			return true;
		}
		long onlineTime = System.currentTimeMillis();
		if(map.containsKey(p.getName()))
			onlineTime -= map.get(p.getName());
		 int day = (int)TimeUnit.SECONDS.toDays(seconds);        
		 long hours = TimeUnit.SECONDS.toHours(seconds) - (day *24);
		 long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
		 long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);
		 int day1 = (int)TimeUnit.MILLISECONDS.toDays(onlineTime); 
		 long hours1 = TimeUnit.MILLISECONDS.toHours(onlineTime) - (day1 *24);
		 long minute1 = TimeUnit.MILLISECONDS.toMinutes(onlineTime) - (TimeUnit.MILLISECONDS.toHours(onlineTime)* 60);
		 long second1 = TimeUnit.MILLISECONDS.toSeconds(onlineTime) - (TimeUnit.MILLISECONDS.toMinutes(onlineTime) *60);
		 p.sendMessage(plugin.namespace + ChatColor.WHITE + "Insgesamte Zeit auf unserem Server:");
		 p.sendMessage(plugin.namespace + ChatColor.WHITE + String.valueOf(day) + " Tag(e) " + String.valueOf(hours) + " Stunde(n) " + String.valueOf(minute) + " Minute(n) " + String.valueOf(second) + " Sekunde(n)");
		 p.sendMessage(plugin.namespace + ChatColor.WHITE + "Insgesamte Zeit seit dem letzten Login:");
		 p.sendMessage(plugin.namespace + ChatColor.WHITE + String.valueOf(day1) + " Tag(e) " + String.valueOf(hours1) + " Stunde(n) " + String.valueOf(minute1) + " Minute(n) " + String.valueOf(second1) + " Sekunde(n)");
		 plugin.api.sendLog("[Epicraft - Onlinezeit] " + p.getName() + " hat seine Onlinezeit abgerufen");
		 return true; // minute ist bei -240 ?! 
	}
	
	private int getOnlineTime(Player p){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		int gsX = 0;
		int gsY = 0;
		try {
			st = conn.prepareStatement("SELECT onlinetime FROM `lb-players` WHERE playername='" + p.getName() + "'");
			rs = st.executeQuery();
			rs.next();
			int onlineTime = rs.getInt(1);
			sql.closeRessources(rs, st);
			return onlineTime;
		} 
		catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	
	@EventHandler
	public void onLogin(PlayerJoinEvent e){
		map.put(e.getPlayer().getName(), System.currentTimeMillis());
	}
}
