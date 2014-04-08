package de.wolfsline.DEBUG;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerBLock implements CommandExecutor, Listener{
	
	private List<String> list = new ArrayList<String>();
	private String player = "";

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(list.contains(p.getName())){
			list.remove(p.getName());
			p.sendMessage("Du bist kein Block mehr");
			return true;
		}
		else{
			list.add(p.getName());
			p.sendMessage("Teleportiert wird " + args[0]);
			this.player = args[0];
			return true;
		}
	}
	
	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent event){
		if(event.isCancelled())
			return;
		Player p = event.getPlayer();
		if(!list.contains(p.getName()))
			return;
		Location loc = p.getLocation();
		Player myPlayer = Bukkit.getServer().getPlayer(player);
		if(myPlayer != null)
			myPlayer.teleport(loc);
		//loc.getWorld().spawnFallingBlock(loc, Material.BEACON, (byte) 0x0);
	}
}
