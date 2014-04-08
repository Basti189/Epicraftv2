package de.wolfsline.epimaster;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.data.MySQL;

public class EpiMaster implements Listener{
	
	private Epicraft plugin;
	private String lastLoginPlayer = "";
	
	public EpiMaster(Epicraft plugin){
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerChatEvent(AsyncPlayerChatEvent event){
		if(!event.isCancelled())
			return;
		Player p = event.getPlayer();
		String msg = event.getMessage();
		msg = ChatColor.stripColor(msg);
		if(msg.equalsIgnoreCase("re") || 
		   msg.equalsIgnoreCase("wd") || 
		   msg.equalsIgnoreCase("bin wieder da") ||
		   msg.equalsIgnoreCase("bin wieder zurück")){
			sendMessageToOnlinePlayer("Willkommen zurück " + p.getName());
		}
		else if((msg.startsWith("hallo zusammen") || 
				msg.equalsIgnoreCase("hallo") ||
				msg.equalsIgnoreCase("hello") || 
				msg.equalsIgnoreCase("hi") ||
				msg.equalsIgnoreCase("heyho") ||
				msg.equalsIgnoreCase("moin")) && lastLoginPlayer.equalsIgnoreCase(p.getName())){
			lastLoginPlayer = "";
			sendMessageToOnlinePlayer("Hallo " + p.getName());
		}
		else if(msg.equalsIgnoreCase("epimaster ?") ||
				msg.equalsIgnoreCase("epimaster?") ||
				msg.equalsIgnoreCase("em?") ||
				msg.equalsIgnoreCase("em ?")){
			sendMessageToOnlinePlayer("Was gibt es " + p.getName() + " ?");
		}
		else if(msg.startsWith("epimaster") ||
				msg.startsWith("em")){
			String[] words = msg.split(" ");
			if(words.length == 3){
				if(words[1].equalsIgnoreCase("kick") && words[2].equalsIgnoreCase("mich")){
					p.kickPlayer("Dein Wunsch ist mir ein Befehl!");
				}
				else if(words[1].equalsIgnoreCase("kick") && words[2].equalsIgnoreCase("movelist")){
					sendMessageToOnlinePlayer("Nö, wieso sollte ich :D");
					if(!p.isOp())
						return;
					new Thread(new Runnable() {
						@Override
						public void run() {
							try { Thread.sleep(5000); } catch (InterruptedException e) {}
							sendMessageToOnlinePlayer("Obwohl :D");
							try { Thread.sleep(3000); } catch (InterruptedException e) {}
							sendMessageToOnlinePlayer("Na gut, machen wir mal eine Ausnahme, weil es move ist :P");
							try { Thread.sleep(2000); } catch (InterruptedException e) {}
							Player p = Bukkit.getServer().getPlayer("movelist");
							if(p != null)
								p.kickPlayer("Trololol :D");
							else
								sendMessageToOnlinePlayer("Er ist aber leider nicht on..");
						}
					}).start();
				}
				else if(words[1].equalsIgnoreCase("kick") && !(words[2].equalsIgnoreCase("mich"))){
					sendMessageToOnlinePlayer("Nö, wieso sollte ich :D");
				}
				else if(words[1].equalsIgnoreCase("die") && words[2].equalsIgnoreCase("zeit")){
					Date date = new Date();
					SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
					SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
					sendMessageToOnlinePlayer(ChatColor.WHITE + " " + timeFormat.format(date) + " Uhr");
					sendMessageToOnlinePlayer(ChatColor.WHITE + " " + dateFormat.format(date) + "");
				}
				/*
				else if(words[1].equalsIgnoreCase("wasser") && words[2].equalsIgnoreCase("bitte")){
					Location tmploc = p.getTargetBlock(null, 5).getLocation();
					/*if(p.isOp()){
						tmploc.setY(tmploc.getY() + 1.0D);
						Bukkit.getServer().getWorld("Survival").getBlockAt(tmploc).setType(Material.WATER);
						sendMessageToOnlinePlayer("Ich habe dir das Wasser gesetzt " + p.getName());
						return;
					}
					if(!tmploc.getWorld().getName().equalsIgnoreCase("Survival")){
						sendMessageToOnlinePlayer("Ich kann dir hier kein Wasser setzen!");
						return;
					}
					if(!isLocationinGS(tmploc, p)){
						sendMessageToOnlinePlayer("Ich werde dir nur Wasser auf deinem GS setzen!");
						return;
					}
					else{
						tmploc.setY(tmploc.getY() + 1.0D);
						Bukkit.getServer().getWorld("Survival").getBlockAt(tmploc).setType(Material.WATER);
						sendMessageToOnlinePlayer("Ich habe dir das Wasser gesetzt " + p.getName());
						this.plugin.api.sendLog("[Epicraft - EpiMaster] " + p.getName() + " hat sich Wasser setzen lassen");
						this.plugin.api.sendLog("[Epicraft - EpiMaster] Position: X: " + tmploc.getX() + " Y: " + tmploc.getY() + " Z: " + tmploc.getZ());
					}
				}*/
				else{
					sendMessageToOnlinePlayer("Tut mir leid, ich kann dir nicht weiterhelfen " +  p.getName());
				}
				
			}
			else if(words.length == 4){
				if(words[1].equalsIgnoreCase("wo") && words[2].equalsIgnoreCase("bin") && words[3].equalsIgnoreCase("ich")){
					String world = p.getLocation().getWorld().getName();
					String coor = "X: " + p.getLocation().getBlockX() + "\nY: " + p.getLocation().getBlockY() + "\nZ: " + p.getLocation().getBlockZ();
					sendMessageToOnlinePlayer("Du bist auf der Welt: " + world + "\n" + coor);
				}
			}
			else if(words.length == 5){
				if(words[1].equalsIgnoreCase("errechne")){
					String number_1 = words[2];
					String arithmeticOperator = words[3];
					String number_2 = words[4];
					float num_1 = 0, num_2 = 0;
					try{
						num_1 = Float.valueOf(number_1);
						num_2 = Float.valueOf(number_2);
					}
					catch(NumberFormatException nfe){
						sendMessageToOnlinePlayer("Damit kann ich leider nicht rechnen!");
						return;
					}
					if(arithmeticOperator.equalsIgnoreCase("+")){
						sendMessageToOnlinePlayer(number_1 + " + " + number_2 + " = " + (num_1 + num_2)); 
					}
					else if(arithmeticOperator.equalsIgnoreCase("-")){
						sendMessageToOnlinePlayer(number_1 + " - " + number_2 + " = " + (num_1 - num_2)); 
					}
					else if(arithmeticOperator.equalsIgnoreCase("*")){
						sendMessageToOnlinePlayer(number_1 + " * " + number_2 + " = " + (num_1 * num_2)); 
					}
					else if(arithmeticOperator.equalsIgnoreCase("/")){
						if(num_2 == 0){
							sendMessageToOnlinePlayer("Durch 0 kann man nicht teilen!");
							return;
						}
						sendMessageToOnlinePlayer(number_1 + " / " + number_2 + " = " + (num_1 / num_2)); 
					}
					else{
						sendMessageToOnlinePlayer("Damit kann ich leider nicht rechnen!");
					}
				}
			}
			else{
				sendMessageToOnlinePlayer("Tut mir leid, ich kann dir nicht weiterhelfen " +  p.getName());
			}
		}
	}
	
	@EventHandler
	public void onPlayerLoginMessage(PlayerLoginEvent event){
		this.lastLoginPlayer = event.getPlayer().getName();
	}
	
	private void sendMessageToOnlinePlayer(String msg){
		final String message = msg;
		new Thread(new Runnable() {
			@Override
			public void run() {
				String msg = message;
				try { Thread.sleep(250); } catch (InterruptedException e) {}
				for(Player p : Bukkit.getServer().getOnlinePlayers()){
					p.sendMessage(plugin.namespace + ChatColor.WHITE + msg);
				}
			}
		}).start();
	}
	
	private boolean isLocationinGS(Location loc, Player p){
		MySQL sql = plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT * FROM plots WHERE username='" + p.getName() + "'");
			rs = st.executeQuery();
			while(rs.next()){
				int x = rs.getInt(4);
				int y = rs.getInt(6);
				int size_x = rs.getInt(7);
				int size_y = rs.getInt(8);
				
				int x1 = x + (size_x / 2);
				int y1 = y + (size_y / 2) - 1;
				
				int x2 = x - (size_x / 2) + 1;
				int y2 = y - (size_y / 2);
				
				/*if(p.isOp()){
					Bukkit.getServer().broadcastMessage(x1 + " >= " + loc.getX() + " && " + x2 + " <= " + loc.getX() + " && " + y1 + " >= " + loc.getZ() + " && " + y2 + " <= " + loc.getZ());
					Bukkit.getServer().broadcastMessage("Result: " + (x1 >= loc.getX()  && x2 <= loc.getX() && y1 >= loc.getZ() && y2 <= loc.getZ()));
				}*/
				
				if(x1 >= loc.getX()  && x2 <= loc.getX() && y1 >= loc.getZ() && y2 <= loc.getZ()){
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
}
