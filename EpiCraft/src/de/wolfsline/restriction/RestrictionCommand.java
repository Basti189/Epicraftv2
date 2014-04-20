package de.wolfsline.restriction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.data.MySQL;

public class RestrictionCommand implements CommandExecutor, Listener{
    
    private List<String> inInventory = new ArrayList<String>();
	private Epicraft plugin;
	
	public RestrictionCommand(Epicraft plugin) {
		this.plugin = plugin;
		MySQL sql = this.plugin.getMySQL();
		sql.queryUpdate("CREATE TABLE IF NOT EXISTS Verwarnung (Benutzername VARCHAR(16), Typ VARCHAR(5) , Grund VARCHAR(70), Zeit VARCHAR(10), Datum VARCHAR(10), Team VARCHAR(16))");
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label,String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(args.length == 0){//Zeige dem Spieler seine Verwarnungen
			showInfo(p.getName(), p);
			return true;
		}
		if(!p.hasPermission("epicraft.warn")){
			p.sendMessage(plugin.error);
			plugin.api.sendLog("[Epicraft - Verwarnung] " + p.getName() + " hat versucht den Befehl zu benutzen!");
			return true;
		}
		if(args.length >= 3){
			if(args[0].equalsIgnoreCase("neu") || args[0].equalsIgnoreCase("new")){//warn neu spieler reason
				String name = args[1];
				String reason = "";
				for(int i = 2 ; i < args.length ; i++){
					reason += args[i] + " ";
				}
				if(reason.length() > 65){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Der Grund ist zu lang! Bitte kürzen");
					return true;
				}
				newWarning(name, reason, p);
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Neuer Eintrag: " + name + " " + reason + ".");
				plugin.api.sendLog("[Epicraft - Verwarnung] " + p.getName() + " hat " + name + " eine Verwarnung geschrieben");
				plugin.api.sendLog("[Epicraft - Verwarnung] Grund: " + reason);
				return true;
			}
			else if(args[0].equalsIgnoreCase("show") || args[0].equalsIgnoreCase("zeige")){//warn show spieler info, detail
				String name = args[1];
				if(!isPlayerinDatabase(name)){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Dieser Spieler hat noch keine Verwarnungen erhalten");
					return true;
				}
				else if(args[2].equalsIgnoreCase("details")){
					showDetails(name, p);
					return true;
				}
				else if(args[2].equalsIgnoreCase("info")){
					showInfo(name, p);
					return true;
				}
				else{
					p.sendMessage(plugin.namespace + ChatColor.RED + "/warn zeige <Spieler> <details> oder <info>");
				}
			}
			else{
				p.sendMessage(plugin.namespace + ChatColor.RED + "Unbekannter Befehl");
			}
		}
		p.sendMessage(plugin.namespace + ChatColor.RED + "/warn <zeige> oder <neu>");
		return true;
	}
	
	public boolean isPlayerinDatabase(String name){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT Benutzername FROM Verwarnung WHERE Benutzername='" + name + "'");
			rs = st.executeQuery();
			while(rs.next()){
				sql.closeRessources(rs, st);
				return true;
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

	
	public boolean newWarning(String name, String reason, Player p){
		MySQL sql = this.plugin.getMySQL();
		String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
		String date = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
		String update = "INSERT INTO Verwarnung (Benutzername, Typ, Grund, Zeit, Datum, Team) VALUES ('" + name + "', 'warn', '" + reason + "', '" + time + "', '" + date + "', '" + p.getName() + "')";
		sql.queryUpdate(update);
		return true;
	}


	public void showInfo(String name, Player p){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT * FROM Verwarnung WHERE Benutzername='" + name + "'");
			rs = st.executeQuery();
			int kick = 0;
			int ban = 0;
			int warn = 0;
			while(rs.next()){
				String typ = rs.getString(2);
				if(typ.equalsIgnoreCase("warn"))
					warn++;
				else if(typ.equalsIgnoreCase("kick"))
					kick++;
				else if(typ.equalsIgnoreCase("ban"))
					ban++;
			}
			p.sendMessage(ChatColor.GOLD + "---------------[Benutzerverwaltung]---------------");
			if(!p.getName().equals(name))
				p.sendMessage(ChatColor.GOLD + "Benutzer: " + ChatColor.WHITE + name);
			p.sendMessage(ChatColor.GOLD + "Verwarnungen: " + ChatColor.WHITE +  String.valueOf(warn));
			p.sendMessage(ChatColor.GOLD + "Kicks: " + ChatColor.WHITE +  String.valueOf(kick));
			p.sendMessage(ChatColor.GOLD + "Bans: " + ChatColor.WHITE +  String.valueOf(ban));
			p.sendMessage(ChatColor.GOLD + "---------------[Benutzerverwaltung]---------------");
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			sql.closeRessources(rs, st);
		}
	}
	
	public void showDetails(String name, Player p){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT * FROM Verwarnung WHERE Benutzername='" + name + "'");
			rs = st.executeQuery();
			p.sendMessage(ChatColor.GOLD + "---------------[Benutzerverwaltung]---------------");
			p.sendMessage(ChatColor.GOLD + "Spieler: " +ChatColor.WHITE + name);
			while(rs.next()){
				String typ = rs.getString(2);
				if(typ.equalsIgnoreCase("warn")){
					p.sendMessage(ChatColor.GOLD + "Verwarnung: ");
					p.sendMessage(ChatColor.GOLD + "- Grund: " + ChatColor.WHITE + rs.getString(3));
					p.sendMessage(ChatColor.GOLD + "- Erstellt: am " + ChatColor.WHITE + rs.getString(5) + ChatColor.GOLD + " um " + ChatColor.WHITE + rs.getString(4) + ChatColor.GOLD + " von " + ChatColor.WHITE + rs.getString(6));
				}
				else if(typ.equalsIgnoreCase("kick")){
					p.sendMessage(ChatColor.GOLD + "Gekickt: ");
					p.sendMessage(ChatColor.GOLD + "- Grund: " + ChatColor.WHITE + rs.getString(3));
					p.sendMessage(ChatColor.GOLD + "- Erstellt: am " + ChatColor.WHITE + rs.getString(5) + ChatColor.GOLD + " um " + ChatColor.WHITE + rs.getString(4) + ChatColor.GOLD + " von " + ChatColor.WHITE + rs.getString(6));
				}
				else if(typ.equalsIgnoreCase("ban")){
					p.sendMessage(ChatColor.GOLD + "Gebannt: ");
					p.sendMessage(ChatColor.GOLD + "- Grund: " + ChatColor.WHITE + rs.getString(3));
					p.sendMessage(ChatColor.GOLD + "- Erstellt: am " + ChatColor.WHITE + rs.getString(5) + ChatColor.GOLD + " um " + ChatColor.WHITE + rs.getString(4) + ChatColor.GOLD + " von " + ChatColor.WHITE + rs.getString(6));
				}
				p.sendMessage("");
			};
			p.sendMessage(ChatColor.GOLD + "---------------[Benutzerverwaltung]---------------");
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			sql.closeRessources(rs, st);
		}
	}
	
	
	//----------------------------------------------------------------------------------------------------------//
	
	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent event){
		Player p = (Player) event.getWhoClicked();
		if(inInventory.contains(p.getName()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onQuitEvent(PlayerQuitEvent e){
		Player p = e.getPlayer();
		this.inInventory.remove(p.getName());
	}
	
	
	@EventHandler
	public void OnPickUpItem(PlayerPickupItemEvent event){
		if(inInventory.contains(event.getPlayer().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onInventoryCloseEvent(InventoryCloseEvent event){
		inInventory.remove(event.getPlayer().getName());
	}
}
