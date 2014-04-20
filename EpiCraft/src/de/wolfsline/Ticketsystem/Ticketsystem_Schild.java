package de.wolfsline.Ticketsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.data.MySQL;

public class Ticketsystem_Schild implements CommandExecutor, Listener{
	
	private Epicraft plugin;
	
	private HashMap<UUID, String> map = new HashMap<UUID, String>();
	
	public Ticketsystem_Schild(Epicraft plugin){
		this.plugin = plugin;
		plugin.getMySQL().queryUpdate("CREATE TABLE IF NOT EXISTS `Ticket Nachrichten` (ID INT AUTO_INCREMENT PRIMARY KEY, UUID VARCHAR(36), Nachricht VARCHAR(200), Team VARCHAR(36), Zeit VARCHAR(10), Datum VARCHAR(10))");
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(!p.hasPermission("epicraft.ticket.team")){
			p.sendMessage(plugin.error);
			return true;
		}
		if(args.length == 1){
			
		}
		if(args.length > 1){
			String msg = "";
			if(args[0].equalsIgnoreCase("neu")){
				String targetPlayer = args[1];
				for(int i = 2 ; i < args.length ; i++){
					msg += args[i] + " ";
				}
				if(msg.length() > 200){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Deine Nachricht darf nicht länger als 200 Zeichen sein!");
					return true;
				}
				int ticketID = createNewSignTicket(p, msg, targetPlayer);
				if(ticketID > 0){
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Nachricht wurde ergoflreich erstellt");
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Zugewiesene ID: " + ticketID);
					map.put(p.getUniqueId(), String.valueOf(ticketID));
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Bitte auf das Schild klicken");
					return true;
				}
				else{
					p.sendMessage(plugin.namespace + ChatColor.RED + "Es ist ein Fehler aufgetreten!");
					return true;
				}
			}
			else if(args[0].equalsIgnoreCase("erstellen")){
				String ticketID = args[1];
				map.put(p.getUniqueId(), ticketID);
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Bitte auf das Schild klicken");
				return true;
			}
		}
		p.sendMessage(plugin.namespace + ChatColor.RED + "/ticketschild <neu>, <erstellen>");
		return true;
	}
	
	private int createNewSignTicket(Player p, String msg, String targetPlayer){
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.GERMANY);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
		String time = timeFormat.format(new Date());
		String date = dateFormat.format(new Date());
		UUID targetUUID = plugin.uuid.getUUIDFromPlayer(targetPlayer);
		if(targetUUID == null){
			p.sendMessage(plugin.namespace + ChatColor.RED + "UUID des Spielers " + targetPlayer + " konnte nicht gefunden werden!");
			return 0;
		}
		plugin.getMySQL().queryUpdate("INSERT INTO `Ticket Nachrichten` (UUID, Nachricht, Team, Zeit, Datum) VALUES ('" + targetUUID +"', '" + msg + "', '" + p.getUniqueId() + "', '" + time + "', '" + date + "')");
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT ID FROM `Ticket Nachrichten` WHERE Team='" + p.getUniqueId() + "' AND Zeit='" + time + "' AND Datum='" + date + "'");
			rs = st.executeQuery();
			int ID = 0;
			if(rs.next()){
				ID = rs.getInt(1);
				sql.closeRessources(rs, st);
			}
			return ID;
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		return 0;
	}
	
	private void showPlayerSignMessage(Player p, String ticketID){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT * FROM `Ticket Nachrichten` WHERE ID='" + ticketID + "'");
			rs = st.executeQuery();
			if(rs.next()){
				String targetPlayer = plugin.uuid.getNameFromUUID(UUID.fromString(rs.getString(2)));
				p.sendMessage(ChatColor.GOLD + "---------------[Nachricht " + ticketID + "]---------------");
				p.sendMessage(ChatColor.GOLD + "Nachricht: " + ChatColor.WHITE + rs.getString(3).replace("%p", ChatColor.GRAY + targetPlayer + ChatColor.WHITE));
				if(p.hasPermission("epicraft.ticket.team")){
					String team = plugin.uuid.getNameFromUUID(UUID.fromString(rs.getString(4)));
					p.sendMessage(ChatColor.GOLD + "Team: " + ChatColor.WHITE + team);
				}
				p.sendMessage(ChatColor.GOLD + "Erstellt: am " + ChatColor.WHITE + rs.getString(6) + ChatColor.GOLD + " um " + ChatColor.WHITE + rs.getString(5));
				p.sendMessage(ChatColor.GOLD + "---------------[Nachricht " + ticketID + "]---------------");
				sql.closeRessources(rs, st);
				return;
			}
			p.sendMessage(plugin.namespace + ChatColor.RED + "Die Nachricht[" + ticketID +"] konnte nicht aufgerufen werden!");
		}
		catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void onInteractEvent(PlayerInteractEvent event){
		Player p = event.getPlayer();
		if(!(event.getClickedBlock() instanceof Block))
			return;
		if ((event.getClickedBlock().getType() == Material.WALL_SIGN || event.getClickedBlock().getType() == Material.SIGN_POST) && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Sign sign = (Sign) event.getClickedBlock().getState();
			if(map.containsKey(p.getUniqueId())){
				String ticketID = map.get(p.getUniqueId());
				sign.setLine(0, ChatColor.RED + "[Ticket]");
				sign.setLine(1, ChatColor.BLUE + ticketID);
				sign.setLine(2, ChatColor.WHITE + "Rechtsklick");
				sign.setLine(3, "-----------------");
				sign.update();
				map.remove(p.getUniqueId());
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Schild wurde gesetzt");				
			}
			else{
				String line0 = ChatColor.stripColor(sign.getLine(0));
				if(line0.equalsIgnoreCase("[Ticket]")){
					String ticketID = ChatColor.stripColor(sign.getLine(1));
					showPlayerSignMessage(p, ticketID);
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent event){
		Player p = event.getPlayer();
		if (event.getBlock().getType() == Material.WALL_SIGN || event.getBlock().getType() == Material.SIGN_POST) {
			Sign sign = (Sign) event.getBlock().getState();
			String line0 = ChatColor.stripColor(sign.getLine(0));
			if(line0.equalsIgnoreCase("[Ticket]")){
				if(!p.hasPermission("epicraft.ticket.team")){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Du kannst das Schild nicht entfernen!\n" +
							"Bitte wende dich an einen Teamler");
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent event){
		Player p = event.getPlayer();
		String line0 = ChatColor.stripColor(event.getLine(0));
		if(line0.equalsIgnoreCase("[Ticket]")){
			p.kickPlayer(ChatColor.RED + "Nein!");
			event.getBlock().breakNaturally();
		}
	}
}
