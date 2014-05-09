package de.wolfsline.LogBlock;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import de.wolfsline.Epicraft.Epicraft;

public class PlayerInteractListener implements Listener{

	private Epicraft plugin;
	private LogBlock lb;
	
	private final Material tool = Material.WOOD_SPADE;
	
	public PlayerInteractListener(Epicraft plugin, LogBlock lb){
		this.plugin = plugin;
		this.lb = lb;
		
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteractEvent(PlayerInteractEvent event){
		Player p = event.getPlayer();
		if(p.hasPermission("epicraft.logblock.view") && event.getAction() == Action.RIGHT_CLICK_BLOCK){
			ItemStack itemInHand = p.getInventory().getItemInHand();
			if(itemInHand.getType() == tool){
				showLogs(event);
				return;
			}
		}
		this.logInteract(event);
	}
	
	private void logInteract(PlayerInteractEvent event){
		Block clicked = event.getClickedBlock();
		if (clicked == null) 
			return;
		Material type = clicked.getType();
		byte blockData = clicked.getData();
		Player player = event.getPlayer();
		Location loc = clicked.getLocation();
		
		switch (type) {
		case LEVER:
		case WOOD_BUTTON:
		case STONE_BUTTON:
		case FENCE_GATE:
		case WOODEN_DOOR:
		case TRAP_DOOR:
		case NOTE_BLOCK:
		case DIODE_BLOCK_OFF:
		case DIODE_BLOCK_ON:
		case REDSTONE_COMPARATOR_OFF:
		case REDSTONE_COMPARATOR_ON:
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
				queueBlock(player.getUniqueId(), loc, type, type, blockData);
			break;
		case CHEST:
		case TRAPPED_CHEST:
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK){
				Block doubleChest = isDoubleChest(clicked);
				queueBlock(player.getUniqueId(), loc, type, type, blockData);
				if(doubleChest != null){
					queueBlock(player.getUniqueId(), doubleChest.getLocation(), type, type, blockData);
				}
			}
			break;
		case CAKE_BLOCK:
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK && player.getFoodLevel() < 20)
				queueBlock(player.getUniqueId(), loc, type, type, blockData);
			break;
		case WOOD_PLATE:
		case STONE_PLATE:
		case IRON_PLATE:
		case GOLD_PLATE:
		case TRIPWIRE:
			if (event.getAction() == Action.PHYSICAL) {
				queueBlock(player.getUniqueId(), loc, type, type, blockData);
			}
		break;
		default:
			break;
		}
	}
	
	private void queueBlock(UUID uuid, Location loc, Material m, Material m1, byte data){
		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();
		
		String query = "INSERT INTO " + loc.getWorld().getName() + " (" +
				"UUID," +
				" Zeitstempel," +
				" `alter Block`," +
				" `neuer Block`," +
				" Datenwert," +
				" X," +
				" Y," +
				" Z)" +
				" VALUES (" +
				"'" + uuid + "'," +
				"'" + System.currentTimeMillis() + "'," +
				"'" + m.toString() + "'," +
				"'" + m.toString() + "'," +
				"'" + String.valueOf(data) + "'," +
				"'" + x + "'," +
				"'" + y + "'," +
				"'" + z + "')";
		
		this.plugin.getMySQL().queryUpdate(query);
	}
	
	public Block isDoubleChest(Block block){
		if(block.getType() == Material.CHEST){
			Location location = block.getLocation();

			location.setX(location.getX() + 1.0D);
			Block newBlock = location.getBlock();
			if(newBlock.getType() == Material.CHEST){
				return newBlock;
			}
			location.setX(location.getX() - 1.0D);
			
			location.setX(location.getX() - 1.0D);
			newBlock = location.getBlock();
			if(newBlock.getType() == Material.CHEST){
				return newBlock;
			}
			location.setX(location.getX() + 1.0D);
			
			location.setZ(location.getZ() + 1.0D);
			newBlock = location.getBlock();
			if(newBlock.getType() == Material.CHEST){
				return newBlock;
			}
			location.setZ(location.getZ() - 1.0D);
			
			location.setZ(location.getZ() - 1.0D);
			newBlock = location.getBlock();
			if(newBlock.getType() == Material.CHEST){
				return newBlock;
			}
			location.setZ(location.getZ() + 1.0D);
		}
		else if(block.getType() == Material.TRAPPED_CHEST){
			Location location = block.getLocation();

			location.setX(location.getX() + 1.0D);
			Block newBlock = location.getBlock();
			if(newBlock.getType() == Material.TRAPPED_CHEST){
				return newBlock;
			}
			
			location.setX(location.getX() - 2.0D);
			newBlock = location.getBlock();
			if(newBlock.getType() == Material.TRAPPED_CHEST){
				return newBlock;
			}
			
			location.setZ(location.getZ() + 1.0D);
			newBlock = location.getBlock();
			if(newBlock.getType() == Material.TRAPPED_CHEST){
				return newBlock;
			}
			
			location.setZ(location.getZ() - 2.0D);
			newBlock = location.getBlock();
			if(newBlock.getType() == Material.TRAPPED_CHEST){
				return newBlock;
			}
		}
		return null;
	}
	
	private void showLogs(PlayerInteractEvent event){
		Player p = event.getPlayer();
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
			event.setCancelled(true);
			Location loc = event.getClickedBlock().getLocation();
			lb.showLocForTool(p, loc);
		}
	}
	
}
