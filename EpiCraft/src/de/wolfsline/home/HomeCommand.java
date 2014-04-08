package de.wolfsline.home;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.data.MySQL;

public class HomeCommand implements CommandExecutor{
	
	private Epicraft plugin;
	public HomeCommand(Epicraft plugin) {
		this.plugin = plugin;
		
		MySQL sql = this.plugin.getMySQL();
		sql.queryUpdate("CREATE TABLE IF NOT EXISTS home (id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(16), title VARCHAR(50), x INT, y INT, z INT)");
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler");
			return true;
		}
		Player p = (Player) cs;
		if(!p.hasPermission("epicraft.home.one")){
			p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast keinen Zugriff auf diesen Befehl!");
			plugin.api.sendLog("[Epicraft - Home] " + p.getName() + " versucht auf den Home-Befehl zuzugreifen");
			return true;
		}
		if(label.equalsIgnoreCase("sethome")){
			if(!p.getLocation().getWorld().equals(Bukkit.getServer().getWorld("Survival"))){
				p.sendMessage(plugin.namespace + ChatColor.RED + "Du kannst in dieser Welt keine Homepunkte setzen");
				plugin.api.sendLog("[Epicraft - Home] " + p.getName() + " versucht auf der Welt " + p.getLocation().getWorld().getName() + " einen Homepunkt zu setzen");
				return true;
			}
			if(args.length == 1){
				String title = args[0];
				if(title.length() > 16){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Der Name " + title + " ist zu lang!");
					return true;
				}
				if(hasPlayerHomeWithName(p.getName(), title)){
					updateHome(p, title);
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Homepunkt " + title + " wurde überschrieben");
					plugin.api.sendLog("[Epicraft - Home] " + p.getName() + " hat den Homepunkt mit dem Namen " + title + " neu gesetzt");
					return true;
				}
				else{
					if(countHome(p) < countHomeLimit(p)){
						newHome(p, title);
						p.sendMessage(plugin.namespace + ChatColor.WHITE + "Neuer Homepunkt " + title + " gesetzt");
						plugin.api.sendLog("[Epicraft - Home] " + p.getName() + " hat einen neuen Homepunkt mit dem Namen " + title + " erstellt");
						return true;
					}
					else{
						p.sendMessage(plugin.namespace + ChatColor.RED + "Die maximale Anzahl an Homepunkten wurde erreicht!");
						return true;
					}
				}
			}
			p.sendMessage(plugin.namespace + ChatColor.RED + "/sethome <name>");
			return true;
		}
		else if(label.equalsIgnoreCase("home")){
			if(args.length == 1){
				if(p.isInsideVehicle()){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Du kannst zurzeit nicht teleportiert werden!");
					plugin.api.sendLog("[Epicraft - Home] " + p.getName() + " sitzt in einem " + p.getVehicle().getType().toString() + " und kann nicht teleportiert werden");
					return true;
				}
				String title = args[0];
				portPlayerToHome(p, title);
				return true;
			}
			p.sendMessage(plugin.namespace + ChatColor.RED + "/home <name>");
			return true;
		}
		else if(label.equalsIgnoreCase("listhome")){
			showHomePoints(p, p.getName());
			return true;
		}
		else if(label.equalsIgnoreCase("rehome")){
			if(args.length == 2){
				if(args[1].length() > 16){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Der Name " + args[1] + " ist zu lang!");
					return true;
				}
				if(renameHomePoint(p.getName(), args[0], args[1])){
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Homepunkt " + args[0] + " wurde umbenannt zu " + args[1]);
					return true;
				}
				p.sendMessage(plugin.namespace + ChatColor.RED + "/rehome <name> <neuerName>");
				return true;
			}
			else{
				p.sendMessage(plugin.namespace + ChatColor.RED + "/rehome <name> <neuerName>");
				return true;
			}
		}
		return false;
	}
	
	private int countHomeLimit(Player p){
		if(!p.hasPermission("epicraft.home.multiple")){
			return 1;
		}
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		int i = 0;
		try {
			st = conn.prepareStatement("SELECT username FROM plots WHERE username='" + p.getName() + "'");
			rs = st.executeQuery();
			while(rs.next()){
				if(rs.getString(1).equalsIgnoreCase(p.getName())){
					i++;
				}
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			sql.closeRessources(rs, st);
		}
		return i;
	}

	private int countHome(Player p){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		int i = 0;
		try {
			st = conn.prepareStatement("SELECT username FROM home WHERE username='" + p.getName() + "'");
			rs = st.executeQuery();
			while(rs.next()){
				if(rs.getString(1).equalsIgnoreCase(p.getName())){
					i++;
				}
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			sql.closeRessources(rs, st);
		}
		return i;
	}	
	
	private void newHome(Player p, String title){
		MySQL sql = this.plugin.getMySQL();
		String update = "INSERT INTO home (username, title, x, y, z) VALUES ('" + p.getName() + "', '" + p.getName() + "_" + title + "', '" + p.getLocation().getX() + "', '" + p.getLocation().getY() + "', '" + p.getLocation().getZ() + "')";
		sql.queryUpdate(update);
	}

	private boolean hasPlayerHomeWithName(String name, String title){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT title FROM home WHERE username='" + name + "'");
			rs = st.executeQuery();
			while(rs.next()){
				if(rs.getString(1).equalsIgnoreCase(name + "_" + title)){
					sql.closeRessources(rs, st);
					return true;
				}
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			sql.closeRessources(rs, st);
		}
		return false;
	}
	
	private boolean renameHomePoint(String name, String title, String newTitle){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT title FROM home WHERE username='" + name + "'");
			rs = st.executeQuery();
			while(rs.next()){
				if(rs.getString(1).equalsIgnoreCase(name + "_" + title)){
					sql.closeRessources(rs, st);
					sql.queryUpdate("UPDATE home SET title='" + name + "_" + newTitle + "' WHERE title='" + name + "_" + title + "'");
					return true;
				}
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			sql.closeRessources(rs, st);
		}
		return false;
	}
	
	private void updateHome(Player p, String title){
		MySQL sql = this.plugin.getMySQL();
		String update = "UPDATE home SET x='" + p.getLocation().getX() + "', y='" + p.getLocation().getY() + "', z='" + p.getLocation().getZ() + "' WHERE title='" + p.getName() + "_" + title + "'";
		sql.queryUpdate(update);
	}

	private void portPlayerToHome(Player p, String title){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT * FROM home WHERE title='" + p.getName() + "_" + title + "'");
			rs = st.executeQuery();
			while(rs.next()){
				if(rs.getString(3).equalsIgnoreCase(p.getName() + "_" + title)){
					Location loc = new Location(Bukkit.getServer().getWorld("Survival"), rs.getInt(4), rs.getInt(5), rs.getInt(6));
					p.teleport(loc);
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Willkommen zu Hause");
					plugin.api.sendLog("[Epicraft - Home] " + p.getName() + " hat sich zum Homepunkt " + title + " teleportiert");
					sql.closeRessources(rs, st);
					return;
				}
			}
			p.sendMessage(plugin.namespace + ChatColor.RED + "Kein Homepunkt names " + title + " gefunden!");
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			sql.closeRessources(rs, st);
		}
	}

	private void showHomePoints(Player p, String name){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		String home = plugin.namespace + ChatColor.WHITE + " ";
		try {
			st = conn.prepareStatement("SELECT title FROM home WHERE username='" + name + "'");
			rs = st.executeQuery();
			while(rs.next()){
				String tmp = rs.getString(1);
				tmp = tmp.replaceAll(p.getName(), "");
				tmp = tmp.replaceFirst("_", "");
				home += tmp + ", ";
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			sql.closeRessources(rs, st);
			p.sendMessage(home);
		}
	}
}