package de.wolfsline.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
public class ArenaSignListener implements Listener{

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e){
		Player p = e.getPlayer();
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(e.getClickedBlock().getState() instanceof Sign){
				Sign s = (Sign) e.getClickedBlock().getState();
				String cmd = s.getLine(0);
				cmd = ChatColor.stripColor(cmd);
				if(cmd.equalsIgnoreCase("[MA]")){
					if(s.getLocation().getWorld() == Bukkit.getWorld("Lobby")){
						if(s.getLine(1).equalsIgnoreCase("Verlassen")){
							Bukkit.getServer().dispatchCommand(p, "ma leave");
							return;
						}
						String arena = s.getLine(1);
						Bukkit.getServer().dispatchCommand(p, "ma join " + arena);
					}
				}
				else if(cmd.equalsIgnoreCase("[DC]")){
					//Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "pex user " + p.getName() + " add " + "deathcube.use");
					p.teleport(Bukkit.getServer().getWorld("DeathCube").getSpawnLocation());
					p.chat("/dc tpi " + s.getLine(2));
					//Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "pex user " + p.getName() + " remove " + "deathcube.use");
				}
				else if(cmd.equalsIgnoreCase("[Spleef]")){
					
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerPreProcessCommandEvent(PlayerCommandPreprocessEvent event){
		Player p = event.getPlayer();
		//if(p.isOp())
		//	return;
		if(event.getMessage().contains("dc")){
			if(p.getLocation().getWorld().getName().equalsIgnoreCase("lobby") ||
			   p.getLocation().getWorld().getName().equalsIgnoreCase("deathcube")){
				
			}
			else{
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent event){
		Player p = event.getPlayer();
		if(p.getLocation().getWorld().getName().equalsIgnoreCase("lobby")){
			if(event.getFrom().getY() <= 30.0D){
				p.setFallDistance(0.0F);
				p.teleport(Bukkit.getServer().getWorld("Lobby").getSpawnLocation());
				p.setFallDistance(0.0F);
			}
		}
	}
}