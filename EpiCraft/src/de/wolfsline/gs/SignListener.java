package de.wolfsline.gs;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

import de.wolfsline.Epicraft.Epicraft;

public class SignListener implements Listener{
private Epicraft plugin;

	public SignListener(Epicraft plugin) {
		this.plugin = plugin;
	}

	/*
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e){
		Player p = e.getPlayer();
		if(!p.getWorld().equals(Bukkit.getWorld("Survival")))
			return;
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(e.getClickedBlock().getState() instanceof Sign){
				if(plugin.signmap.containsKey(p.getName())){
					if(plugin.signmap.get(p.getName())){
						Sign sign = (Sign) e.getClickedBlock().getState();
						if(plugin.getWorldGuard().canBuild(p, e.getClickedBlock().getLocation()) == false){
							p.sendMessage(plugin.namespace + ChatColor.RED + "Du kannst dieses Schild nicht beschriften!");
							return;
						}
						e.setCancelled(true);
						sign.setLine(0, "---------------");
						sign.setLine(1, "Grundstück von");
						sign.setLine(2, p.getName());
						sign.setLine(3, "---------------");
						sign.update();
					}
				}
			}
		}
	}*/
	
	@EventHandler
	public void onSignChange(SignChangeEvent event){
		Sign sign = (Sign) event.getBlock().getState();
		Player p = event.getPlayer();
		if(plugin.signmap.containsKey(p.getName())){
			if(plugin.signmap.get(p.getName())){
				event.setLine(0, "---------------");
				event.setLine(1, "Grundstück von");
				event.setLine(2, p.getName());
				event.setLine(3, "---------------");
				sign.update();
			}
		}
	}
	/*
	@EventHandler
	public void onSignCreate(BlockPlaceEvent event){
		if(!(event.getBlock().getType().equals(Material.WALL_SIGN) || event.getBlock().getType().equals(Material.SIGN_POST))){
			return;
		}
		Player p = event.getPlayer();
		Sign sign = (Sign) event.getBlock().getState();
		if(plugin.signmap.containsKey(p.getName())){
			if(plugin.signmap.get(p.getName())){
				sign.setLine(0, "---------------");
				sign.setLine(1, "Grundstück von");
				sign.setLine(2, p.getName());
				sign.setLine(3, "---------------");
				sign.update();
				event.setCancelled(true);
			}
		}
	}*/
}