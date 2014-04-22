package de.wolfsline.teleport;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
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
			if(!(event.getPlayer().isOp() || event.getPlayer().hasPermission("epicraft.sign.lift.create"))){
				event.setCancelled(true);
				event.getBlock().breakNaturally();
				event.getPlayer().sendMessage(plugin.namespace + ChatColor.RED + "Du hast keinen Zugriff auf den Lift!");
			}
			String direction = ((Directional)event.getBlock().getType().getNewData(event.getBlock().getData())).getFacing().toString();
			if(!(direction.equalsIgnoreCase("north") ||
					direction.equalsIgnoreCase("east") || 
					direction.equalsIgnoreCase("south") || 
					direction.equalsIgnoreCase("west") || 
					direction.equalsIgnoreCase("north_west") || 
					direction.equalsIgnoreCase("south_west") || 
					direction.equalsIgnoreCase("south_east") || 
					direction.equalsIgnoreCase("north_east"))){
				event.setLine(0, ChatColor.RED + "Richtung:");
				event.setLine(1, ChatColor.WHITE + direction);
				event.setLine(2, ChatColor.RED + "nicht");
				event.setLine(3, ChatColor.RED + "unterstützt!");
			}
		}
	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event){
		Player p = event.getPlayer();
		if(!p.hasPermission("epicraft.sign.lift"))
			return;
		if(!(event.getClickedBlock() instanceof Block))
			return;
		if ((event.getClickedBlock().getType() == Material.WALL_SIGN || event.getClickedBlock().getType() == Material.SIGN_POST) && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Sign sign = (Sign) event.getClickedBlock().getState();
			String line1 = ChatColor.stripColor(sign.getLine(0));
			if(line1.equalsIgnoreCase("[Lift]")){
				event.setUseItemInHand(Result.DENY);
				String line3 = ChatColor.stripColor(sign.getLine(2));
				if(line3.equalsIgnoreCase("hoch")){
					Location loc = event.getClickedBlock().getLocation();
					int x = loc.getBlockX();
					int y = loc.getBlockY();
					int z = loc.getBlockZ();
					for(int i = y + 1 ; i < 250 ; i++){
						Block block = new Location(loc.getWorld(), x, i, z).getBlock();
						if(block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST){
							Sign mySign = (Sign) block.getState();
							if(ChatColor.stripColor(mySign.getLine(0)).equalsIgnoreCase("[Lift]")){
								p.teleport(getLocationInFrontSign(block));
								String floor = ChatColor.stripColor(mySign.getLine(1));
								if(!floor.equalsIgnoreCase(""))
									p.sendMessage(plugin.namespace + ChatColor.WHITE + "Willkommen im " + floor);
								return;
							}
						}
					}
					p.sendMessage(plugin.namespace + ChatColor.RED + "Du bist bereits in der obersten Etage");
				}
				else if(line3.equalsIgnoreCase("runter")){
					Location loc = event.getClickedBlock().getLocation();
					int x = loc.getBlockX();
					int y = loc.getBlockY();
					int z = loc.getBlockZ();
					for(int i = y - 1 ; i > 10 ; i--){
						Block block = new Location(loc.getWorld(), x, i, z).getBlock();
						if(block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST){
							Sign mySign = (Sign) block.getState();
							if(ChatColor.stripColor(mySign.getLine(0)).equalsIgnoreCase("[Lift]")){
								p.teleport(getLocationInFrontSign(block));
								String floor = ChatColor.stripColor(mySign.getLine(1));
								if(!floor.equalsIgnoreCase(""))
									p.sendMessage(plugin.namespace + ChatColor.WHITE + "Willkommen im " + floor);
								return;
							}
						}
					}
					p.sendMessage(plugin.namespace + ChatColor.RED + "Du bist bereits in der untersten Etage");
				}
			}
		}
		if ((event.getClickedBlock().getType() == Material.WALL_SIGN || event.getClickedBlock().getType() == Material.SIGN_POST) && event.getAction() == Action.LEFT_CLICK_BLOCK) {
			Sign sign = (Sign) event.getClickedBlock().getState();
			String lineLift = ChatColor.stripColor(sign.getLine(0));
			if(lineLift.equalsIgnoreCase("[Lift]")){
				String lineDir = ChatColor.stripColor(sign.getLine(2));
				String lineDirWithColor = sign.getLine(2);
				if(lineDir.equalsIgnoreCase("hoch")){
					Location loc = event.getClickedBlock().getLocation();
					int x = loc.getBlockX();
					int y = loc.getBlockY();
					int z = loc.getBlockZ();
					for(int i = y - 1 ; i > 10 ; i--){
						Block block = new Location(loc.getWorld(), x, i, z).getBlock();
						if(block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST){
							Sign mySign = (Sign) block.getState();
							if(ChatColor.stripColor(mySign.getLine(0)).equalsIgnoreCase("[Lift]")){//Lift runter gefunden
								sign.setLine(2, lineDirWithColor.replace("hoch", "runter"));
								sign.update();
								return;
							}
						}
					}
					p.sendMessage(plugin.namespace + ChatColor.RED + "Dies ist die unterste Etage!");
				}
				else if(lineDir.equalsIgnoreCase("runter")){
					Location loc = event.getClickedBlock().getLocation();
					int x = loc.getBlockX();
					int y = loc.getBlockY();
					int z = loc.getBlockZ();
					for(int i = y + 1 ; i < 250 ; i++){
						Block block = new Location(loc.getWorld(), x, i, z).getBlock();
						if(block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST){
							Sign mySign = (Sign) block.getState();
							if(ChatColor.stripColor(mySign.getLine(0)).equalsIgnoreCase("[Lift]")){
								sign.setLine(2, lineDirWithColor.replace("runter", "hoch"));
								sign.update();
								return;
							}
						}
					}
					p.sendMessage(plugin.namespace + ChatColor.RED + "Dies ist die oberste Etage!");
				}
			}
		}
	}
	
	private Location getLocationInFrontSign(Block block){
		String direction = ((Directional)block.getType().getNewData(block.getData())).getFacing().toString();
		Location loc = new Location(block.getWorld(), block.getX(), block.getY() - 1, block.getZ());
		//Bukkit.getServer().broadcastMessage("Direction: " + direction);
		if(direction.equalsIgnoreCase("north")){
			loc.setZ(loc.getZ() - 1.0); //-1.5
			loc.setX(loc.getX() + 0.5);
			loc.setYaw(0.0F);
		}
		else if(direction.equalsIgnoreCase("east")){
			loc.setX(loc.getX() + 2.0); //+.1.5
			loc.setZ(loc.getZ() + 0.5);
			loc.setYaw(90.0F);
		}
		else if(direction.equalsIgnoreCase("south")){
			loc.setZ(loc.getZ() + 2.0); // +1.5
			loc.setX(loc.getX() + 0.5);
			loc.setYaw(180.0F);
		}
		else if(direction.equalsIgnoreCase("west")){
			loc.setX(loc.getX() - 1.0); // -1.5
			loc.setZ(loc.getZ() + 0.5);
			loc.setYaw(-90.0F);
		}
		else if(direction.equalsIgnoreCase("south_west")){
			loc.setX(loc.getX() - 0.5);
			loc.setZ(loc.getZ() + 1.5);
			loc.setYaw(-135.0F);
		}
		else if(direction.equalsIgnoreCase("south_east")){
			loc.setX(loc.getX() + 1.5);
			loc.setZ(loc.getZ() + 1.5);
			loc.setYaw(+135.0F);
		}
		else if(direction.equalsIgnoreCase("north_east")){
			
		}
		else if(direction.equalsIgnoreCase("north_west")){
			
		}
		return loc;
	}
}
