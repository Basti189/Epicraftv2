package de.wolfsline.administration;

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

public class SkullPlayer implements CommandExecutor, Listener{
	
	private Epicraft plugin;
	
	private boolean isWaiting = false;
	private String name = "";
	
	public SkullPlayer(Epicraft plugin){
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(!p.hasPermission("epicraft.skull"))
			return true;
		if(args.length == 1){
			this.name = args[0];
			this.isWaiting = true;
			p.sendMessage(plugin.namespace + ChatColor.GOLD + "Warte auf Skull!");
			return true;
		}
		return false;
	}
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event){
		Player p = event.getPlayer();
		if(!(p.isOp() || p.hasPermission("epicraft.permission.admin")))
			return;
		if(!(event.getClickedBlock() instanceof Block))
			return;
		if(event.getClickedBlock().getType() == Material.SKULL && isWaiting){
			Skull skull = (Skull) event.getClickedBlock().getState();
			skull.setSkullType(SkullType.PLAYER);
			skull.setOwner(name);
			skull.update();
			p.sendMessage(plugin.namespace + ChatColor.GOLD + "Skull wurde gesetzt!");
			this.isWaiting = false;
		}
	}

}
