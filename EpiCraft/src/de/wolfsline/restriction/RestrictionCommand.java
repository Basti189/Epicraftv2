package de.wolfsline.restriction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
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
import org.bukkit.Material;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.data.MySQL;
import de.wolfsline.helpClasses.myLocation;

public class RestrictionCommand implements CommandExecutor, Listener{
    
    private List<String> inInventory = new ArrayList<String>();
	private Epicraft plugin;
	
	public RestrictionCommand(Epicraft plugin) {
		this.plugin = plugin;
		MySQL sql = this.plugin.getMySQL();
		sql.queryUpdate("CREATE TABLE IF NOT EXISTS warning (id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(16), typ VARCHAR(5) , reason VARCHAR(70), time VARCHAR(10), date VARCHAR(10), teamuser VARCHAR(16))");
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label,String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(args.length == 0){
			openInv(p);
			return true;
		}
		if(!(p.hasPermission("epicraft.restriction.use") || p.isOp())){
			p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast keinen Zugriff auf diesen Befehl!");
			plugin.api.sendLog("[Epicraft - Verwarnung] " + p.getName() + " hat versucht den Verwarn-Befehl zu benutzen");
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
			}
			else{
				p.sendMessage(plugin.namespace + ChatColor.RED + "Unbekannter Befehl");
			}
		}
		return false;
	}
	
	public boolean isPlayerinDatabase(String name){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT username FROM warning WHERE username='" + name + "'");
			rs = st.executeQuery();
			while(rs.next()){
				if(rs.getString(1).equalsIgnoreCase(name)){
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

	
	public boolean newWarning(String name, String reason, Player p){
		MySQL sql = this.plugin.getMySQL();
		String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
		String date = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
		String update = "INSERT INTO warning (username, typ, reason, time, date, teamuser) VALUES ('" + name + "', 'warn', '" + reason + "', '" + time + "', '" + date + "', '" + p.getName() + "')";
		sql.queryUpdate(update);
		return true;
	}


	public void showInfo(String name, Player p){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT * FROM warning WHERE username='" + name + "'");
			rs = st.executeQuery();
			p.sendMessage(ChatColor.GOLD + "---------------[Benutzerverwaltung]---------------");
			p.sendMessage(ChatColor.GOLD + "Benutzer: " + name);
			int kick = 0;
			int ban = 0;
			int warn = 0;
			while(rs.next()){
				String typ = rs.getString(3);
				if(typ.equalsIgnoreCase("warn"))
					warn++;
				else if(typ.equalsIgnoreCase("kick"))
					kick++;
				else if(typ.equalsIgnoreCase("ban"))
					ban++;
			}
			p.sendMessage(ChatColor.GOLD + "Anzahl Verwarnungen: " + String.valueOf(warn));
			p.sendMessage(ChatColor.GOLD + "Anzahl Kicks: " + String.valueOf(kick));
			p.sendMessage(ChatColor.GOLD + "Anzahl Bans: " + String.valueOf(ban));
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
			st = conn.prepareStatement("SELECT * FROM warning WHERE username='" + name + "'");
			rs = st.executeQuery();
			p.sendMessage(ChatColor.GOLD + "---------------[Benutzerverwaltung]---------------");
			p.sendMessage(ChatColor.GOLD + "Benutzer: " + name);
			int i = 0;
			while(rs.next()){
				String typ = rs.getString(3);
				if(typ.equalsIgnoreCase("warn")){
					p.sendMessage(ChatColor.GOLD + "Verwarnung: ");
					p.sendMessage(ChatColor.GOLD + "- Grund: " + rs.getString(4));
					p.sendMessage(ChatColor.GOLD + "- Datum: " + rs.getString(5) + " Uhr am " + rs.getString(6) + " von " + rs.getString(7));
				}
				else if(typ.equalsIgnoreCase("kick")){
					p.sendMessage(ChatColor.GOLD + "Gekickt: ");
					p.sendMessage(ChatColor.GOLD + "- Grund: " + rs.getString(4));
					p.sendMessage(ChatColor.GOLD + "- Datum: " + rs.getString(5) + " Uhr am " + rs.getString(6) + " von " + rs.getString(7));
				}
				else if(typ.equalsIgnoreCase("ban")){
					p.sendMessage(ChatColor.GOLD + "Gebannt: ");
					p.sendMessage(ChatColor.GOLD + "- Grund: " + rs.getString(4));
					p.sendMessage(ChatColor.GOLD + "- Datum: " + rs.getString(5) + " Uhr am " + rs.getString(6) + " von " + rs.getString(7));
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
	
	public void openInv(Player p){
		List<String> myWarn = getWarn(p);
		Iterator<String> it = myWarn.iterator();
		int lines = (myWarn.size()/3);
		if(lines == 0){
			p.sendMessage(plugin.namespace + ChatColor.WHITE + "Du hast keine Einträge!");
			return;
		}
		Inventory inv = Bukkit.createInventory(null, lines*9, "Deine Einträge");
		if(inv == null){
			p.sendMessage(plugin.namespace + ChatColor.RED + "Es ist ein interner Fehler aufgetreten!");
			return;
		}
		while(it.hasNext()){
			String what = it.next();
			String reason = it.next();
			String timestamp = it.next();
			ItemStack stack = null;
			ItemMeta meta = null;
			if(what.equals("gekicked"))
				stack = new ItemStack(Material.WOOL, 1, (short) 4);
			else if(what.equalsIgnoreCase("verwarnung"))
				stack = new ItemStack(Material.WOOL, 1, (short) 1);
			else if(what.equalsIgnoreCase("gebannt"))
				stack = new ItemStack(Material.WOOL, 1, (short) 14);
			meta = stack.getItemMeta();
			
			meta.setDisplayName(what);
			List<String> tmp = new ArrayList<String>();
			tmp.add(reason);
			tmp.add(timestamp);
			meta.setLore(tmp);
			stack.setItemMeta(meta);
			inv.addItem(stack);
		}
		p.sendMessage(plugin.namespace + ChatColor.WHITE + "Benutzerverwaltung wird geöffnet");
		inInventory.add(p.getName());
		p.openInventory(inv);
	}
	
	private List<String> getWarn(Player p){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		List<String> myWarn = new ArrayList<String>();
		try {
			st = conn.prepareStatement("SELECT * FROM warning WHERE username='" + p.getName() + "'");
			rs = st.executeQuery();
			while(rs.next()){
				String typ = rs.getString(3);
				if(typ.equalsIgnoreCase("warn"))
					myWarn.add("Verwarnung");
				
				else if(typ.equalsIgnoreCase("kick"))
					myWarn.add("Gekicked");
				
				else if(typ.equalsIgnoreCase("ban"))
					myWarn.add("Gebannt");
				myWarn.add("Grund: " + rs.getString(4));
				myWarn.add("Datum: " + rs.getString(5) + " Uhr am " + rs.getString(6));
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			sql.closeRessources(rs, st);
		}
		return myWarn;
	}
	
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
