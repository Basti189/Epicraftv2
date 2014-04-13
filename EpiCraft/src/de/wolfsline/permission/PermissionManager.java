package de.wolfsline.permission;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.helpClasses.EpicraftPlayer;

public class PermissionManager implements CommandExecutor{
	
	private Epicraft plugin;
	private List<EpicraftPlayer> listEpicraftPlayer = new ArrayList<EpicraftPlayer>();

	public PermissionManager(Epicraft plugin) {
		this.plugin = plugin;
	}
	
	public void setPermissionToPlayer(Player p){
		EpicraftPlayer epicraftPlayer = getEpicraftPlayer(p.getName());
		if(epicraftPlayer == null)
			return;
		removePermissionFromPlayer(p);
		String permission = epicraftPlayer.permission;
		if(permission.equals("epicraft.permission.gast")){
			//Epicraft
			p.addAttachment(plugin, "epicraft.permission.gast", true);
			p.addAttachment(plugin, "epicraft.chat", true);
			p.addAttachment(plugin, "epicraft.chat.channel", true);;
			p.addAttachment(plugin, "epicraft.spawn", true);
			p.addAttachment(plugin, "epicraft.time", true);
			p.addAttachment(plugin, "epicraft.command.allow", true);
			p.addAttachment(plugin, "epicraft.auth", true);
			p.addAttachment(plugin, "epicraft.ticket", true);
		}
		else if(permission.equals("epicraft.permission.spieler")){
			//Epicraft
			p.addAttachment(plugin, "epicraft.permission.spieler", true);
			p.addAttachment(plugin, "epicraft.chat", true);
			p.addAttachment(plugin, "epicraft.chat.whisper", true);
			p.addAttachment(plugin, "epicraft.chat.channel", true);
			p.addAttachment(plugin, "epicraft.spawn", true);
			p.addAttachment(plugin, "epicraft.time", true);
			p.addAttachment(plugin, "epicraft.command.allow", true);
			p.addAttachment(plugin, "epicraft.auth", true);
			p.addAttachment(plugin, "epicraft.ticket", true);
			p.addAttachment(plugin, "epicraft.important", true);
			p.addAttachment(plugin, "epicraft.horse", true);
			p.addAttachment(plugin, "epicraft.blocksecure", true);
			p.addAttachment(plugin, "epicraft.pvp", true);
			p.addAttachment(plugin, "epicraft.gs", true);
			p.addAttachment(plugin, "epicraft.gs.sign", true);
			p.addAttachment(plugin, "epicraft.world.sign", true);
			p.addAttachment(plugin, "epicraft.home.one", true);
		}
		else if(permission.equals("epicraft.permission.stammi")){
			//Epicraft
			p.addAttachment(plugin, "epicraft.permission.stammi", true);
			p.addAttachment(plugin, "epicraft.chat", true);
			p.addAttachment(plugin, "epicraft.chat.whisper", true);
			p.addAttachment(plugin, "epicraft.chat.channel", true);
			p.addAttachment(plugin, "epicraft.chat.color", true);
			p.addAttachment(plugin, "epicraft.spawn", true);
			p.addAttachment(plugin, "epicraft.time", true);
			p.addAttachment(plugin, "epicraft.command.allow", true);
			p.addAttachment(plugin, "epicraft.auth", true);
			p.addAttachment(plugin, "epicraft.ticket", true);
			p.addAttachment(plugin, "epicraft.important", true);
			p.addAttachment(plugin, "epicraft.horse", true);
			p.addAttachment(plugin, "epicraft.blocksecure", true);
			p.addAttachment(plugin, "epicraft.pvp", true);
			p.addAttachment(plugin, "epicraft.sign.color", true);
			p.addAttachment(plugin, "epicraft.gs", true);
			p.addAttachment(plugin, "epicraft.gs.sign", true);
			p.addAttachment(plugin, "epicraft.world.sign", true);
			p.addAttachment(plugin, "epicraft.sign.color", true);
			p.addAttachment(plugin, "epicraft.egg.catch", true);
			p.addAttachment(plugin, "epicraft.home.one", true);
			p.addAttachment(plugin, "epicraft.home.multiple", true);
		}
		else if(permission.equals("epicraft.permission.guard")){
			//Epicraft
			p.addAttachment(plugin, "epicraft.permission.guard", true);
			p.addAttachment(plugin, "epicraft.chat", true);
			p.addAttachment(plugin, "epicraft.chat.whisper", true);
			p.addAttachment(plugin, "epicraft.chat.channel", true);
			p.addAttachment(plugin, "epicraft.chat.color", true);
			p.addAttachment(plugin, "epicraft.spawn", true);
			p.addAttachment(plugin, "epicraft.time", true);
			p.addAttachment(plugin, "epicraft.command.allow", true);
			p.addAttachment(plugin, "epicraft.auth", true);
			p.addAttachment(plugin, "epicraft.ticket", true);
			p.addAttachment(plugin, "epicraft.important", true);
			p.addAttachment(plugin, "epicraft.horse", true);
			p.addAttachment(plugin, "epicraft.blocksecure", true);
			p.addAttachment(plugin, "epicraft.sign.color", true);
			p.addAttachment(plugin, "epicraft.gs.sign", true);
			p.addAttachment(plugin, "epicraft.gs", true);
			p.addAttachment(plugin, "epicraft.world.sign", true);
			p.addAttachment(plugin, "epicraft.egg.catch", true);
			p.addAttachment(plugin, "epicraft.home.one", true);
			p.addAttachment(plugin, "epicraft.home.multiple", true);
			//Epicraft - Team
			p.addAttachment(plugin, "epicraft.fly", true);
			p.addAttachment(plugin, "epicraft.jail", true);
			p.addAttachment(plugin, "epicraft.teleport", true);
			p.addAttachment(plugin, "epicraft.hide", true);
			p.addAttachment(plugin, "epicraft.kick", true);
			p.addAttachment(plugin, "epicraft.warn", true);
			p.addAttachment(plugin, "epicraft.pvp", true);
			p.addAttachment(plugin, "epicraft.world.change", true);
		}
		else if(permission.equals("epicraft.permission.moderator")){
			//Epicraft
			p.addAttachment(plugin, "epicraft.permission.moderator", true);
			p.addAttachment(plugin, "epicraft.chat", true);
			p.addAttachment(plugin, "epicraft.chat.whisper", true);
			p.addAttachment(plugin, "epicraft.chat.channel", true);
			p.addAttachment(plugin, "epicraft.chat.color", true);
			p.addAttachment(plugin, "epicraft.spawn", true);
			p.addAttachment(plugin, "epicraft.time", true);
			p.addAttachment(plugin, "epicraft.command.allow", true);
			p.addAttachment(plugin, "epicraft.auth", true);
			p.addAttachment(plugin, "epicraft.ticket", true);
			p.addAttachment(plugin, "epicraft.important", true);
			p.addAttachment(plugin, "epicraft.horse", true);
			p.addAttachment(plugin, "epicraft.blocksecure", true);
			p.addAttachment(plugin, "epicraft.sign.color", true);
			p.addAttachment(plugin, "epicraft.world.sign", true);
			p.addAttachment(plugin, "epicraft.egg.catch", true);
			p.addAttachment(plugin, "epicraft.home.one", true);
			p.addAttachment(plugin, "epicraft.home.multiple", true);
			//Epicraft - Team
			p.addAttachment(plugin, "epicraft.chest", true);
			p.addAttachment(plugin, "epicraft.enderchest", true);
			p.addAttachment(plugin, "epicraft.fly", true);
			p.addAttachment(plugin, "epicraft.jail", true);
			p.addAttachment(plugin, "epicraft.restart", true);
			p.addAttachment(plugin, "epicraft.teleport", true);
			p.addAttachment(plugin, "epicraft.hide", true);
			p.addAttachment(plugin, "epicraft.whois", true);
			p.addAttachment(plugin, "epicraft.api", true);
			p.addAttachment(plugin, "epicraft.kick", true);
			p.addAttachment(plugin, "epicraft.warn", true);
			p.addAttachment(plugin, "epicraft.ban", true);
			p.addAttachment(plugin, "epicraft.pvp", true);
			p.addAttachment(plugin, "epicraft.world.change", true);
			p.addAttachment(plugin, "epicraft.world.change.other", true);
		}
		else if(permission.equals("epicraft.permission.admin")){
			//Epicraft
			p.addAttachment(plugin, "epicraft.permission.admin", true);
			p.addAttachment(plugin, "epicraft.chat", true);
			p.addAttachment(plugin, "epicraft.chat.whisper", true);
			p.addAttachment(plugin, "epicraft.chat.channel", true);
			p.addAttachment(plugin, "epicraft.chat.color", true);
			p.addAttachment(plugin, "epicraft.spawn", true);
			p.addAttachment(plugin, "epicraft.time", true);
			p.addAttachment(plugin, "epicraft.command.allow", true);
			p.addAttachment(plugin, "epicraft.auth", true);
			p.addAttachment(plugin, "epicraft.ticket", true);
			p.addAttachment(plugin, "epicraft.important", true);
			p.addAttachment(plugin, "epicraft.horse", true);
			p.addAttachment(plugin, "epicraft.blocksecure", true);
			p.addAttachment(plugin, "epicraft.sign.color", true);
			p.addAttachment(plugin, "epicraft.world.sign", true);
			p.addAttachment(plugin, "epicraft.egg.catch", true);
			p.addAttachment(plugin, "epicraft.home.one", true);
			p.addAttachment(plugin, "epicraft.home.multiple", true);
			//Epicraft - Team
			p.addAttachment(plugin, "epicraft.chest", true);
			p.addAttachment(plugin, "epicraft.enderchest", true);
			p.addAttachment(plugin, "epicraft.fly", true);
			p.addAttachment(plugin, "epicraft.jail", true);
			p.addAttachment(plugin, "epicraft.restart", true);
			p.addAttachment(plugin, "epicraft.teleport", true);
			p.addAttachment(plugin, "epicraft.hide", true);
			p.addAttachment(plugin, "epicraft.whois", true);
			p.addAttachment(plugin, "epicraft.api", true);
			p.addAttachment(plugin, "epicraft.kick", true);
			p.addAttachment(plugin, "epicraft.warn", true);
			p.addAttachment(plugin, "epicraft.ban", true);
			p.addAttachment(plugin, "epicraft.pvp", true);
			p.addAttachment(plugin, "epicraft.world.change", true);
			p.addAttachment(plugin, "epicraft.world.change.other", true);
			//WorldGuard
			p.addAttachment(plugin, "worldguard.*", true);
			//WorldEdit
			p.addAttachment(plugin, "worldedit.*", true);
		}
		else if(permission.equalsIgnoreCase("epicraft.permission.owner")){
			//Epicraft
			p.addAttachment(plugin, "epicraft.permission.admin", true);
			p.addAttachment(plugin, "epicraft.permission.owner", true);
			p.addAttachment(plugin, "epicraft.*", true);
			//WorldGuard
			p.addAttachment(plugin, "worldguard.*", true);
			//WorldEdit
			p.addAttachment(plugin, "worldedit.*", true);
		}
		String tmpgr = permission.subSequence((new String("epicraft.permission.").length()), permission.length()).toString();
		String gr = tmpgr.substring(0,1).toUpperCase() + tmpgr.substring(1, tmpgr.length());
		plugin.api.sendLog("[Epicraft - PermissionManager] " + p.getName() + " wurde der Gruppe " + gr + " zugeordnet");
		substringNameAndColor(p);
	}
	
	private void removePermissionFromPlayer(Player p){
		//Epicraft
		p.addAttachment(plugin, "epicraft.permission.gast", false);
		p.addAttachment(plugin, "epicraft.permission.spieler", false);
		p.addAttachment(plugin, "epicraft.permission.stammi", false);
		p.addAttachment(plugin, "epicraft.permission.guard", false);
		p.addAttachment(plugin, "epicraft.permission.moderator", false);
		p.addAttachment(plugin, "epicraft.permission.admin", false);
		p.addAttachment(plugin, "epicraft.permission.owner", false);
		p.addAttachment(plugin, "epicraft.*", false);
		//WorldGuard
		p.addAttachment(plugin, "worldguard.*", false);
		//WorldEdit
		p.addAttachment(plugin, "worldedit.*", false);
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs.hasPermission("epicraft.permission.admin") || cs.isOp())){
			cs.sendMessage(plugin.error);
			plugin.api.sendLog("[Epicraft - Permission] " + cs.getName() + " versucht auf den PermissionManager zuzugreifen!");
			return true;
		}
		if(args.length == 3){
			Player player = Bukkit.getServer().getPlayer(args[1]);
			if(player == null){
				cs.sendMessage(plugin.namespace + ChatColor.RED + "Spieler ist nicht online!");
				return true;
			}
			if(args[0].equals("set")){
				EpicraftPlayer epicraftPlayer = getEpicraftPlayer(player.getName());
				if(epicraftPlayer == null){
					cs.sendMessage(plugin.namespace + ChatColor.RED + "Spieler ist nicht online!");
					return true;
				}
				String group = args[2];
				if(group.equals("Gast")){
					epicraftPlayer.permission = "epicraft.permission.gast";
				}
				else if(group.equals("Spieler")){
					epicraftPlayer.permission = "epicraft.permission.spieler";
				}
				else if(group.equals("Stammi")){
					epicraftPlayer.permission = "epicraft.permission.stammi";
				}
				else if(group.equals("Guard")){
					epicraftPlayer.permission = "epicraft.permission.guard";
				}
				else if(group.equals("Moderator")){
					epicraftPlayer.permission = "epicraft.permission.moderator";
				}
				else if(group.equals("Admin")){
					epicraftPlayer.permission = "epicraft.permission.admin";
				}
				else if(group.equals("Owner")){
					epicraftPlayer.permission = "epicraft.permission.owner";
				}
				else {
					cs.sendMessage(plugin.namespace + ChatColor.RED + "Die Gruppe \"" + group + "\" gibt es nicht!");
					return true;
				}
				substringNameAndColor(player);
				epicraftPlayer.update();
				this.removePermissionFromPlayer(player);
				this.setPermissionToPlayer(player);
				for(Player tmpPlayer : Bukkit.getServer().getOnlinePlayers()){
					if(tmpPlayer.hasPermission("epicraft.permission.guard") || 
					   tmpPlayer.hasPermission("epicraft.permission.moderator") || 
					   tmpPlayer.hasPermission("epicraft.permission.admin") ||
					   tmpPlayer.hasPermission("epicraft.permission.owner")){
						tmpPlayer.sendMessage(plugin.namespace + ChatColor.WHITE + player.getName() + " ist nun " + group);
					}
				}
				plugin.api.sendLog("[Epicraft - Permission] " + player.getName() + " ist nun " + group);
				return true;
			}
			else if(args[0].equals("ap")){
				String permission = args[2];
				player.addAttachment(plugin, permission, true);
				cs.sendMessage(plugin.namespace + player.getName() + " wurde die Permission \"" + permission + "\" gegeben");
				plugin.api.sendLog("[Epicraft - Permission] " + player.getName() + " wurde die Permission \"" + permission + "\" gegeben");
				return true;
			}
			else if(args[0].equals("rp")){
				String permission = args[2];
				player.addAttachment(plugin, permission, false);
				cs.sendMessage(plugin.namespace + player.getName() + " wurde die Permission \"" + permission + "\" entfernt");
				plugin.api.sendLog("[Epicraft - Permission] " + player.getName() + " wurde die Permission \"" + permission + "\" entfernt");
				return true;
			}
			else{
				cs.sendMessage(plugin.namespace + ChatColor.RED + "Falscher Parameter: " + args[0] + "!");
				return true;
			}
			
		}
		else{
			cs.sendMessage(plugin.namespace + ChatColor.RED + "/permission set Spieler Gruppe");
			return true;
		}
	}
	
	public void substringNameAndColor(Player p){
		String name = p.getName();
		if(name.length() == 15){
			p.setPlayerListName(colorName(p) + name.substring(0, 13));
		}
		else if(name.length() == 16){
			p.setPlayerListName(colorName(p) + name.substring(0, 14));
		}
		else{
			p.setPlayerListName(colorName(p) + name);
		}
	}
	private ChatColor colorName(Player p){
		if(p.hasPermission("epicraft.permission.gast")){
			return ChatColor.AQUA;
		}
		else if(p.hasPermission("epicraft.permission.spieler")){
			return ChatColor.BLUE;
		}
		else if(p.hasPermission("epicraft.permission.spieler")){
			return ChatColor.DARK_BLUE;
		}
		else if(p.hasPermission("epicraft.permission.guard")){
			return ChatColor.DARK_PURPLE;
		}
		else if(p.hasPermission("epicraft.permission.moderator")){
			return ChatColor.DARK_GREEN;
		}
		else if(p.hasPermission("epicraft.permission.owner")){
			return ChatColor.DARK_GRAY;
		}
		else if(p.hasPermission("epicraft.permission.admin")){
			return ChatColor.DARK_RED;
		}
		else
			return ChatColor.GREEN;
	}
	
	//Rückgabe EpicraftPlayer -> Einstellungen des Spielers
	public EpicraftPlayer getEpicraftPlayer(String name){
		for(EpicraftPlayer tmpEpiPlayer : listEpicraftPlayer){
			if(tmpEpiPlayer.username.equals(name))
				return tmpEpiPlayer;
		}
		return null;
	}
	
	//EpicraftSpieler hinzufügen, wenn nicht vorhanden
	public void triggerEpicraftPlayerList(Player p, boolean isOnline){
		if(isOnline){ //Spieler ist online gekommen
			Connection conn = plugin.getMySQL().getConnection();
			ResultSet rs = null;
			PreparedStatement st = null;
			EpicraftPlayer epiPlayer = null;
			try{
				st = conn.prepareStatement("SELECT * FROM Einstellungen WHERE Name='" + p.getName() + "'");
				rs = st.executeQuery();
				rs.next();
				boolean eventMessages = rs.getBoolean(2);
				boolean chatMessages = rs.getBoolean(3);
				boolean chatTime = rs.getBoolean(4);
				boolean chatWorld = rs.getBoolean(5);
				boolean systemMessages = rs.getBoolean(6);
				boolean moneyForVote = rs.getBoolean(7);
				String permission = rs.getString(8);
				epiPlayer = new EpicraftPlayer(plugin, p.getName(), permission, eventMessages, chatMessages, systemMessages, chatTime, moneyForVote, chatWorld, false);
				plugin.getMySQL().closeRessources(rs, st);
			} catch (SQLException e){
				//e.printStackTrace();
				plugin.api.sendLog("[Epicraft - Login] Neuer Datenbankeintrag für " + p.getName());
			}
			if(epiPlayer == null)
				epiPlayer = new EpicraftPlayer(plugin, p.getName(), "epicraft.permission.gast", true, true, true, false, true, false, true);
			listEpicraftPlayer.add(epiPlayer);
			plugin.pManager.setPermissionToPlayer(p);
			plugin.pManager.substringNameAndColor(p);
			// *** TEST *** //
			if(p.hasPermission("epicraft.permission.gast"))
				Bukkit.getServer().broadcastMessage(plugin.namespace + ChatColor.WHITE + p.getName() + " ist Gast");
			if(p.hasPermission("epicraft.permission.spieler"))
				Bukkit.getServer().broadcastMessage(plugin.namespace + ChatColor.WHITE + p.getName() + " ist Spieler");
			if(p.hasPermission("epicraft.permission.guard"))
				Bukkit.getServer().broadcastMessage(plugin.namespace + ChatColor.WHITE + p.getName() + " ist Guard");
			if(p.hasPermission("epicraft.permission.moderator"))
				Bukkit.getServer().broadcastMessage(plugin.namespace + ChatColor.WHITE + p.getName() + " ist Moderator");
			if(p.hasPermission("epicraft.permission.admin"))
				Bukkit.getServer().broadcastMessage(plugin.namespace + ChatColor.WHITE + p.getName() + " ist Admin");
			// *** TEST *** //
		}
		else{
			//Player aus der Liste entfernen
			for(Iterator<EpicraftPlayer> it = listEpicraftPlayer.iterator() ; it.hasNext();){
				if(it.next().username.equals(p.getName())){
					it.remove();
				}
			}
		}
	}
	
	public EpicraftPlayer getEpicraftPlayerFromOfflinePlayer(String name){
		Connection conn = plugin.getMySQL().getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try{
			st = conn.prepareStatement("SELECT * FROM Einstellungen WHERE Name='" + name + "'");
			rs = st.executeQuery();
			if(rs.next()){
				plugin.getMySQL().closeRessources(rs, st);
				return null;
			}
			boolean eventMessages = rs.getBoolean(2);
			boolean chatMessages = rs.getBoolean(3);
			boolean chatTime = rs.getBoolean(4);
			boolean chatWorld = rs.getBoolean(5);
			boolean systemMessages = rs.getBoolean(6);
			boolean moneyForVote = rs.getBoolean(7);
			String permission = rs.getString(8);
			plugin.getMySQL().closeRessources(rs, st);
			return new EpicraftPlayer(plugin, name, permission, eventMessages, chatMessages, systemMessages, chatTime, moneyForVote, chatWorld, false);
			
		} catch (SQLException e){
			//e.printStackTrace();
		}
		return null;
	}
}
