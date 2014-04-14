package de.wolfsline.microblock;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import de.wolfsline.Epicraft.Epicraft;

public class Microblock implements CommandExecutor,Listener {
	
	private Epicraft plugin;
	private MicroblockType mbType = new MicroblockType();
	
	private HashMap<UUID, String> map = new HashMap<>();
	
	public Microblock(Epicraft plugin){
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(!(p.hasPermission("epicraft.micro.block") && p.hasPermission("epicraft.micro.skull"))){
			p.sendMessage(plugin.error);
			plugin.api.sendLog("[Epicraft - Microblock] " + p.getName() + " hat versucht auf den Befehl zuzugreifen");
			return true;
		}
		if(args.length == 1){
			if(label.equalsIgnoreCase("mb") || label.equalsIgnoreCase("microblock")){
				map.put(p.getUniqueId(), mbType.getBlockName(args[0]));
			}
			else if(label.equalsIgnoreCase("skull")){
				map.put(p.getUniqueId(), args[0]);
			}
			p.sendMessage(plugin.namespace + ChatColor.GOLD + "Warte auf Skull!");
			return true;
		}
		return false;
	}
	
	//---------------------------------------------------------------------------------------------------------------------//
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event){
		Player p = event.getPlayer();
		if(!(p.hasPermission("epicraft.micro.block") && p.hasPermission("epicraft.micro.skull"))){
			return;
		}
		if(!(event.getClickedBlock() instanceof Block))
			return;
		if(event.getClickedBlock().getType() == Material.SKULL){
			Skull skull = (Skull) event.getClickedBlock().getState();
			skull.setSkullType(SkullType.PLAYER);
			skull.setOwner(map.get(p.getUniqueId()));
			skull.update();
			p.sendMessage(plugin.namespace + ChatColor.GOLD + "Skull wurde gesetzt!");
			map.remove(p.getUniqueId());
		}
	}
}
