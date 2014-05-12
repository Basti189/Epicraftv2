package de.wolfsline.LogBlock;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.wolfsline.Epicraft.Epicraft;

public class LogBlockCommand implements CommandExecutor{

	private Epicraft plugin;
	private LogBlock lb;
	
	public LogBlockCommand(Epicraft plugin, LogBlock lb){
		this.plugin = plugin;
		this.lb = lb;
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(args.length == 0){
			Location[] points = lb.getWorldEditConnector().getSelection(p);
			points[0].getBlock().setType(Material.OBSIDIAN);
			points[1].getBlock().setType(Material.OBSIDIAN);
		}
		else if(args.length == 1){
			if(args[0].equalsIgnoreCase("show")){
				lb.getSelectionHandler().ShowChangesInSelection(p);
				return true;
			}
			else if(args[0].equalsIgnoreCase("clear")){
				lb.clearOldEntrys();
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Einträge, älter als 2 Tage, wurden gelöscht");
				return true;
			}
		}
		else if(args.length == 2){
			if(args[0].equalsIgnoreCase("rollback")){
				String playerName = args[1];
				UUID targetUUID = plugin.uuid.getUUIDFromPlayer(playerName);
				if(targetUUID == null){
					p.sendMessage(plugin.uuid.ERROR);
					return true;
				}
				lb.getSelectionHandler().RestoreSelectionFromPlayer(p, targetUUID);
				return true;
			}
			else if(args[0].equalsIgnoreCase("show")){
				String playerName = args[1];
				UUID targetUUID = plugin.uuid.getUUIDFromPlayer(playerName);
				if(targetUUID == null){
					p.sendMessage(plugin.uuid.ERROR);
					return true;
				}
				lb.getSelectionHandler().ShowChangesInSelectionFromPlayer(p, targetUUID);
				return true;
			}
				
		}
		else if(args.length == 4){
			if(args[0].equalsIgnoreCase("rollback")){
				String playerName = args[1];
				String datum = args[2];
				String zeit = args[3];
				SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyy HH:mm:ss");
				Date targetDate = null;
				
				UUID targetUUID = plugin.uuid.getUUIDFromPlayer(playerName);
				if(targetUUID == null){
					p.sendMessage(plugin.uuid.ERROR);
					return true;
				}
				
				try{
					targetDate = sdf.parse(datum + " " + zeit); 
				}
				catch(ParseException e){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Zeitformat falsch! -> tt.MM.jjjj HH:mm:ss");
					return true;
				}
				lb.getSelectionHandler().RestoreSelectionFromPlayerWithTimestamp(p, targetUUID, targetDate);
				return true;
			}
		}
		p.sendMessage(plugin.namespace + ChatColor.RED + "/lb [option] <parameter>");
		return true;
	}
}
