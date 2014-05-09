package de.wolfsline.ProtocolLib.BlockChanger;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.helpClasses.changeID;

public class BlockCommand implements CommandExecutor{
	
	private Epicraft plugin;
	
	private Calculations calc;
	
	public BlockCommand(Epicraft plugin, Calculations calc){
		this.plugin = plugin;
		this.calc = calc;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(!p.hasPermission("epicraft.protocol.blockchange")){
			p.sendMessage(plugin.error);
			return true;
		}
		if(args.length == 3){
			String playername = args[0];
			String srcID = args[1];
			String destID = args[2];
			UUID targetUUID = plugin.uuid.getUUIDFromPlayer(playername);
			if(targetUUID == null){
				p.sendMessage(plugin.uuid.ERROR);
				return true;
			}
			try{
				int srcID_INT = Integer.valueOf(srcID);
				int destID_INT = Integer.valueOf(destID);
				if(calc.map.containsKey(targetUUID)){
					calc.map.get(targetUUID).add(srcID_INT, destID_INT);
				}
				else{
					calc.map.put(targetUUID, new changeID(srcID_INT, destID_INT));
				}
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Dem Spieler wird nun die BlockID " + srcID + " durch " + destID + " ersetzt!");
				updateChunk(targetUUID);
				
				return true;
			}
			catch(NumberFormatException nfe){
				p.sendMessage(plugin.namespace + ChatColor.RED + "Bitte gib gültige Zahlen ein!");
				return true;
			}
		}
		if(args.length == 2){
			String playername = args[0];
			String srcID = args[1];
			UUID targetUUID = plugin.uuid.getUUIDFromPlayer(playername);
			if(targetUUID == null){
				p.sendMessage(plugin.uuid.ERROR);
				return true;
			}
			if(srcID.equalsIgnoreCase("list")){
				if(calc.map.containsKey(targetUUID)){
					changeID cID = calc.map.get(targetUUID);
					HashMap<Integer, Integer> map = cID.getList();
					p.sendMessage(ChatColor.GOLD + "Spieler: " + ChatColor.WHITE + playername);
					p.sendMessage(ChatColor.GOLD + "UUID: " + ChatColor.WHITE + targetUUID);
					for(Entry<Integer, Integer> entry : cID.getList().entrySet()){
						p.sendMessage(ChatColor.GOLD + "BlockID: " + ChatColor.WHITE + entry.getKey() + ChatColor.GOLD + " -> " + ChatColor.WHITE + entry.getValue());
					}
					return true;
				}
				p.sendMessage(plugin.namespace + ChatColor.RED + "Dem Spieler werden keine BlockID's verändert");
				updateChunk(targetUUID);
				return true;
				
			}
			else if(srcID.equalsIgnoreCase("clear")){
				if(calc.map.containsKey(targetUUID)){
					calc.map.get(targetUUID).clear();
					calc.map.remove(targetUUID);
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Alle Änderungen gelöscht");
					updateChunk(targetUUID);
					return true;
				}
				p.sendMessage(plugin.namespace + ChatColor.RED + playername + " werden keine BlockID's verändert");
				return true;
			}
			try{
				int srcID_INT = Integer.valueOf(srcID);
				if(calc.map.containsKey(targetUUID)){
					calc.map.get(targetUUID).remove(srcID_INT);
					if(calc.map.get(targetUUID).count() == 0){
						calc.map.get(targetUUID).clear();
						calc.map.remove(targetUUID);
						p.sendMessage(plugin.namespace + ChatColor.WHITE + playername + " werden keine BlockID's mehr geändert");
						updateChunk(targetUUID);
						return true;
					}
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Die BlockID " + srcID + " wird nun nicht mehr verändert!");
					updateChunk(targetUUID);
					return true;
				}
				p.sendMessage(plugin.namespace + ChatColor.RED + playername + " werden keine BlockID's verändert");
				return true;
			}
			catch(NumberFormatException nfe){
				p.sendMessage(plugin.namespace + ChatColor.RED + "Bitte gib gültige Zahlen ein!");
				return true;
			}
		}
		p.sendMessage(plugin.namespace + ChatColor.RED + "/block <Spieler> <srcID> [destID]");
		return true;
	}

	private void updateChunk(UUID targetUUID){ //Funktioniert nicht
		Player targetPlayer = Bukkit.getServer().getPlayer(targetUUID);
		Chunk chunk = targetPlayer.getLocation().getChunk();
		targetPlayer.getWorld().refreshChunk(chunk.getX(), chunk.getZ());
	}
	
}
