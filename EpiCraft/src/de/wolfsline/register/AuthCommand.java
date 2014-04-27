package de.wolfsline.register;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.data.MySQL;

public class AuthCommand implements CommandExecutor, Listener{

	private Epicraft plugin;
	public HashMap<UUID, Boolean> map;
	
	public AuthCommand(Epicraft plugin) {
		this.plugin = plugin;
		map = new HashMap<UUID, Boolean>();
		MySQL sql = this.plugin.getMySQL();
		sql.queryUpdate("CREATE TABLE IF NOT EXISTS Auth (UUID VARCHAR(36), Passwort VARCHAR(255), IP4 VARCHAR(40), `letzter Login` VARCHAR(15), `E-Mail` VARCHAR(255) )");
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(!p.hasPermission("epicraft.auth")){
			p.sendMessage(plugin.error);
			plugin.api.sendLog("[Epicraft - Passwort] " + p.getName() + " versucht auf den Befehl zuzugreifen!");
			return true;
		}
		if((label.equalsIgnoreCase("l") || label.equalsIgnoreCase("login"))){
			if(args.length == 1){
				String password = args[0];
				String convertedPassword = StringToSHA256(password);
				if(password.equals(convertedPassword)){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Es ist ein Fehler aufgetreten!");
					p.sendMessage(plugin.namespace + ChatColor.RED + "Bitte versuche es erneut!");
					plugin.api.sendLog("[Epicraft - Passwort] " + p.getName() + " hat einen Fehler verursacht");
					return true;
				}
				if(loginPlayer(p, convertedPassword)){
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Erfolgreich eingeloggt!");
					plugin.api.sendLog("[Epicraft - Passwort] " + p.getName() + " hat sich erfolgreich angemeldet");
					map.remove(p.getUniqueId());
					/*for(PotionEffect effect : p.getActivePotionEffects()){
						if(effect.getType() == PotionEffectType.BLINDNESS){
							p.removePotionEffect(effect.getType());
							break;
						}
					}*/
					return true;
				}
				else{
					p.sendMessage(plugin.namespace + ChatColor.RED + "Einloggen nicht erfolgreich!");
					plugin.api.sendLog("[Epicraft - Passwort] " + p.getName() + " hat sich nicht erfolgreich angemeldet");
					return true;
				}
			}
			else{
				p.sendMessage(plugin.namespace + ChatColor.RED + "/login <Passwort>");
				return true;
			}
		}
		else if(label.equalsIgnoreCase("register")){
			if(args.length == 2){
				String password = args[0];
				String password2 = args[1];
				if(!password.equals(password2)){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Passwörter sind nicht identisch!");
					return true;
				}
				String convertedPassword = StringToSHA256(password);
				if(registerPlayer(p, convertedPassword)){
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Erfolgreich registriert!");
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Bitte füge noch deine E-Mail Adresse hinzu");
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "/email add <email> <email>");
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Du weißt nicht wozu? Frag ein Teammitglied");
					plugin.api.sendLog("[Epicraft - Passwort] " + p.getName() + " hat sich erfolgreich registriert");
					map.remove(p.getUniqueId());
					return true;
				}
				return true;
			}
			else{
				p.sendMessage(plugin.namespace + ChatColor.RED + "/register <passwort> <passwort>");
				return true;
			}
		}
		else if(label.equalsIgnoreCase("email")){
			if(args.length == 3){
				String email = args[1];
				String email2 = args[2];
				if(!email.equals(email2)){
					p.sendMessage(plugin.namespace + ChatColor.RED + "E-Mail Adressen sind nicht identisch!");
					return true;
				}
				if(addEmail(p, email))
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "E-Mail Adresse wurde erfolgreich eingetragen!");
				return true;
			}
			else{
				p.sendMessage(plugin.namespace + ChatColor.RED + "/email add <e-mail> <e-mail>");
				return true;
			}
		}
		else if(label.equalsIgnoreCase("cp") || label.equalsIgnoreCase("changepw")){
			if(args.length == 3){
				String oldpw = args[0];
				String newpw = args[1];
				String newpw2 = args[2];
				if(newpw.equals(newpw2)){
					String convertedPassword = StringToSHA256(oldpw);
					if(loginPlayer(p, convertedPassword)){
						MySQL sql = this.plugin.getMySQL();
						String update = "UPDATE auth SET password='" + StringToSHA256(newpw) + "' WHERE UUID='" + p.getUniqueId() + "'";
						sql.queryUpdate(update);
						p.sendMessage(plugin.namespace + ChatColor.WHITE + "Passwort wurde erfolgreich geändert.");
						plugin.api.sendLog("[Epicraft - Passwort] " + p.getName() + " hat sein Passwort erfolgreich geändert");
						return true;
					}
					else{
						p.sendMessage(plugin.namespace + ChatColor.RED + "Altes Passwort nicht korrekt!");
						return true;
					}
				}
				else{
					p.sendMessage(plugin.namespace + ChatColor.RED + "Neue Passwörter sind nicht identisch!");
					return true;
				}
			}
			else{
				p.sendMessage(plugin.namespace + ChatColor.RED + "/changepw altesPW neuesPW neuesPW!");
				return true;
			}
		}
		return false;
	}
	
	private String StringToSHA256(String password){
		try{
	        MessageDigest digest = MessageDigest.getInstance("SHA-256");
	        byte[] hash = digest.digest(password.getBytes("UTF-8"));
	        StringBuffer hexString = new StringBuffer();
	        for (int i = 0; i < hash.length; i++) {
	            String hex = Integer.toHexString(0xff & hash[i]);
	            if(hex.length() == 1) hexString.append('0');
	            hexString.append(hex);
	        }
	        return hexString.toString();
	    } 
		catch(Exception ex){
	    }
		return password;
	}
	
	private boolean registerPlayer(Player p, String convertedPassword){
		String date = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
		String Adress = p.getAddress().toString();
		Adress = Adress.substring(1, Adress.indexOf(':'));
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT * FROM Auth WHERE UUID='" + p.getUniqueId() + "'");
			rs = st.executeQuery();
			while(rs.next()){
				sql.closeRessources(rs, st);
				p.sendMessage(plugin.namespace + ChatColor.RED + "Du bist schon registriert!");
				return false;
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		String update = "INSERT INTO Auth (UUID, Passwort, IP4, `letzter Login`, `E-Mail`) VALUES ('" + p.getUniqueId() + "', '" + convertedPassword + "', '" + Adress + "', '" + date + "', 'your@email.com')";
		sql.queryUpdate(update);
		return true;
	}
	
	private boolean loginPlayer(Player p, String convertedPassword){
		String date = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
		String Adress = p.getAddress().toString();
		Adress = Adress.substring(1, Adress.indexOf(':'));
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		boolean login = false;
		try {
			st = conn.prepareStatement("SELECT Passwort FROM Auth WHERE UUID='" + p.getUniqueId() + "'");
			rs = st.executeQuery();
			if(rs.next()){
				if(rs.getString(1).equals(convertedPassword))
					login = true;
				sql.closeRessources(rs, st);
				if(login){
					String update = "UPDATE Auth SET `letzter Login`='" + date + "', IP4='" + Adress + "' WHERE UUID='" + p.getUniqueId() + "'";
					sql.queryUpdate(update);
				}
			}
			return login;
		} 
		catch (SQLException e) {
			//e.printStackTrace();
			sql.closeRessources(rs, st);
			return false;
		}
	}
	
	private boolean internetAdress(Player p, String Adress){
		Adress = Adress.substring(1, Adress.indexOf(':'));
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT IP4 FROM Auth WHERE UUID='" + p.getUniqueId() + "'");
			rs = st.executeQuery();
			while(rs.next()){
				if(rs.getString(1).equalsIgnoreCase(Adress)){
					sql.closeRessources(rs, st);
					return true;
				}
			}
			sql.closeRessources(rs, st);
			return false;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean addEmail(Player p, String email){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT `E-Mail` FROM Auth WHERE UUID='" + p.getUniqueId() + "'");
			rs = st.executeQuery();
			while(rs.next()){
				if(!rs.getString(1).equalsIgnoreCase("your@email.com")){
					p.sendMessage(plugin.namespace + ChatColor.RED + "E-Mail adresse wurde schon gesetzt!");
					sql.closeRessources(rs, st);
					return false;
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		sql = this.plugin.getMySQL();
		String update = "UPDATE Auth SET `E-Mail`='" + email + "' WHERE UUID='" + p.getUniqueId() + "'";
		sql.queryUpdate(update);
		return true;
	}
	
	@EventHandler
	public void PlayerMoveEvent(PlayerMoveEvent event){
		if(map.containsKey(event.getPlayer().getUniqueId())){
			event.setTo(event.getFrom());
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		Player p = e.getPlayer();
		if(internetAdress(p, p.getAddress().toString())){
			p.sendMessage(plugin.namespace + ChatColor.WHITE + "Erfolgreich eingeloggt!");
			return;
		}
		//p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 9999999, 2));
		p.sendMessage(plugin.namespace + ChatColor.RED + "Bitte einloggen oder registrieren!");
		map.put(p.getUniqueId(), false);
	}
	
	@EventHandler
	public void PlayerCommandListener(PlayerCommandPreprocessEvent e){
		if(map.containsKey(e.getPlayer().getUniqueId())){
			if(e.getMessage().startsWith("/login") || e.getMessage().startsWith("/register")){
				return;
			}
			else{
				e.setCancelled(true);
				e.getPlayer().sendMessage(plugin.namespace + ChatColor.RED + "Du hast keinen Zugriff auf diesen Befehl!");
			}
			
		}	
	}
	
	@EventHandler
	public void PlayerBreakBlockEvent(BlockBreakEvent event){
		if(map.containsKey(event.getPlayer().getUniqueId())){
			event.setCancelled(true);
		}
	}
}
