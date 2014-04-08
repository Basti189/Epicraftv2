package de.wolfsline.teleport;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Directional;

import de.wolfsline.Epicraft.Epicraft;

public class SignLift implements Listener{
	
	private Epicraft plugin;
	
	public SignLift(Epicraft plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onSignChangeEvent(SignChangeEvent event){
		if(event.getLine(0).equalsIgnoreCase("[Lift]")){
			if(!event.getPlayer().isOp()){
				if(event.getPlayer().getLocation().getWorld().getName().equalsIgnoreCase("plots")){
					return;
				}
				event.setCancelled(true);
				event.getBlock().breakNaturally();
				event.getPlayer().sendMessage(plugin.namespace + ChatColor.RED + "Du hast keinen Zugriff auf den Lift!");
			}	
		}
	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event){
		Player p = event.getPlayer();
		if(!(event.getClickedBlock() instanceof Block))
			return;
		if (event.getClickedBlock().getType() == Material.WALL_SIGN && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Sign sign = (Sign) event.getClickedBlock().getState();
			String line1 = sign.getLine(0);
			if(line1.equalsIgnoreCase("[Lift]")){
				String line3 = sign.getLine(2);
				if(line3.equalsIgnoreCase("hoch")){
					Location loc = event.getClickedBlock().getLocation();
					int x = loc.getBlockX();
					int y = loc.getBlockY();
					int z = loc.getBlockZ();
					for(int i = y + 1 ; i < 250 ; i++){
						Block block = new Location(loc.getWorld(), x, i, z).getBlock();
						if(block.getType() == Material.WALL_SIGN){
							Sign mySign = (Sign) block.getState();
							if(mySign.getLine(0).equalsIgnoreCase("[Lift]")){
								p.teleport(getLocationInFrontSign(block));
								String floor = mySign.getLine(1);
								if(!floor.equalsIgnoreCase(""))
									p.sendMessage(plugin.namespace + ChatColor.WHITE + "Willkommen im " + floor);
								return;
							}
						}
					}
					p.sendMessage(plugin.namespace + ChatColor.RED + "Sie sind bereits in der obersten Etage");
				}
				else if(line3.equalsIgnoreCase("runter")){
					Location loc = event.getClickedBlock().getLocation();
					int x = loc.getBlockX();
					int y = loc.getBlockY();
					int z = loc.getBlockZ();
					for(int i = y - 1 ; i > 10 ; i--){
						Block block = new Location(loc.getWorld(), x, i, z).getBlock();
						if(block.getType() == Material.WALL_SIGN){
							Sign mySign = (Sign) block.getState();
							if(mySign.getLine(0).equalsIgnoreCase("[Lift]")){
								p.teleport(getLocationInFrontSign(block));
								String floor = mySign.getLine(1);
								if(!floor.equalsIgnoreCase(""))
									p.sendMessage(plugin.namespace + ChatColor.WHITE + "Willkommen im " + floor);
								return;
							}
						}
					}
					p.sendMessage(plugin.namespace + ChatColor.RED + "Sie sind bereits in der untersten Etage");
				}
			}
		}
	}
	
	private Location getLocationInFrontSign(Block block){
		String direction = ((Directional)block.getType().getNewData(block.getData())).getFacing().toString();
		Location loc = new Location(block.getWorld(), block.getX(), block.getY() - 1, block.getZ());
		if(direction.equalsIgnoreCase("north")){
			loc.setZ(loc.getZ() - 1.5);
			loc.setYaw(0);
		}
		else if(direction.equalsIgnoreCase("east")){
			loc.setX(loc.getX() + 1.5);
			loc.setYaw(90);
		}
		else if(direction.equalsIgnoreCase("south")){
			loc.setZ(loc.getZ() + 1.5);
			loc.setYaw(180);
		}
		else if(direction.equalsIgnoreCase("west")){
			loc.setX(loc.getX() - 1.5);
			loc.setYaw(-90);
		}
		return loc;
	}
}
