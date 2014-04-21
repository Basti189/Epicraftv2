package de.wolfsline.blocksecure;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Hopper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.material.Chest;

import de.wolfsline.Epicraft.Epicraft;

public class BlockSecure implements CommandExecutor, Listener {
	
	private Epicraft plugin;
	private BlockSecure_Data data;
	
	private HashMap<UUID, String> map = new HashMap<UUID, String>();
	
	public BlockSecure(Epicraft plugin){
		this.plugin = plugin;
		this.data = new BlockSecure_Data(plugin);
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(!p.hasPermission("epicraft.blocksecure")){
			p.sendMessage(plugin.error);
			plugin.api.sendLog("[Epicraft - Blocksicherheit] " + p.getName() + " hat versucht auf den Befgehl zuzugreifen");
			return true;
		}
		if(args.length == 1){
			if(args[0].equalsIgnoreCase("+")){
				map.put(p.getUniqueId(), "add");
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Bitte klicke den Block zum Sichern an");
			}
			else if(args[0].equalsIgnoreCase("-")){
				map.put(p.getUniqueId(), "remove");
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Bitte klicke den Block zum Entsichern an");
			}
			return true;
		}
		else if(args.length == 2){
			if(args[0].equalsIgnoreCase("+")){
				map.put(p.getUniqueId(), "add:" + args[1]);
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Bitte klicke den Block zum Hinzufügen des Spielers " + args[1] + " an");
			}
			else if(args[0].equalsIgnoreCase("-")){
				map.put(p.getUniqueId(), "remove:" + args[1]);
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Bitte klicke den Block zum Entfernen des Spielers " + args[1] + " an");
			}
			return true;
		}
		return false;
	}
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event){
		Player p = event.getPlayer();
		if(!(event.getClickedBlock() instanceof Block))
			return;
		Material blockType = event.getClickedBlock().getType();
		if ((blockType == Material.WOOD_DOOR || blockType == Material.IRON_DOOR_BLOCK || blockType == Material.CHEST || blockType == Material.TRAPPED_CHEST || blockType == Material.FURNACE) && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
			if(map.containsKey(p.getUniqueId())){
				String result = map.get(p.getUniqueId());
				if(!result.contains(":")){
					String action = result;
					BlockSecure_Block secureBlock = data.getBlockSecure(block);
					if(action.equals("add")){
						if(secureBlock == null){
							if(!data.canPlayerProtectBlock(p, block)){
								p.sendMessage(plugin.namespace + ChatColor.RED + "Du kannst auf diesem Grundstück keine Blöcke sichern!");
								event.setCancelled(true);
								map.remove(p.getUniqueId());
								return;
							}
							data.secureBlock(p.getUniqueId(), block, true);
							p.sendMessage(plugin.namespace + ChatColor.WHITE + "D" + materialToSting(blockType) + " wurde gesichert");
							event.setCancelled(true);
							map.remove(p.getUniqueId());
							return;
						}
						p.sendMessage(plugin.namespace + ChatColor.RED + "D" + materialToSting(blockType) + " ist bereits gesichert!");
						event.setCancelled(true);
						map.remove(p.getUniqueId());
						return;
					}
					else if(action.equals("remove")){
						if(secureBlock == null){
							p.sendMessage(plugin.namespace + ChatColor.RED + "D" + materialToSting(blockType) + " ist nicht gesichert!");
							map.remove(p.getUniqueId());
							event.setCancelled(true);
							return;
						}
						if(p.getUniqueId().equals(secureBlock.getOwner())){
							data.unSecureBlock(p.getUniqueId(), block);
							p.sendMessage(plugin.namespace + ChatColor.WHITE + "D" + materialToSting(blockType) + " ist nun nicht mehr gesichert");
							map.remove(p.getUniqueId());
							event.setCancelled(true);
							return;
						}
						else{
							p.sendMessage(plugin.namespace + ChatColor.RED + "D" + materialToSting(blockType) + " kann nicht entsichern werden!");
							map.remove(p.getUniqueId());
							event.setCancelled(true);
							return;
						}
					}
				}
				else {
					String action[] = result.split(":");
					BlockSecure_Block secureBlock = data.getBlockSecure(block);
					UUID targetUUID = plugin.uuid.getUUIDFromPlayer(action[1]);
					if(targetUUID == null){
						p.sendMessage(plugin.uuid.ERROR);
						map.remove(p.getUniqueId());
						event.setCancelled(true);
						return;
					}
					if(action[0].equals("add")){
						if(secureBlock == null){
							p.sendMessage(plugin.namespace + ChatColor.RED + "D" + materialToSting(blockType) + " ist nicht gesichert!");
							map.remove(p.getUniqueId());
							event.setCancelled(true);
							return;
						}
						else{
							if(p.getUniqueId().equals(secureBlock.getOwner())){
								if(secureBlock.getMember().contains(targetUUID)){
									p.sendMessage(plugin.namespace + ChatColor.RED + "D" + materialToSting(blockType) + " kann bereits von " + action[1] + " benutzt werden!");
									map.remove(p.getUniqueId());
									event.setCancelled(true);
									return;
								}
								data.secureBlock(targetUUID, block, false);
								p.sendMessage(plugin.namespace + ChatColor.WHITE + "D" + materialToSting(blockType) + " kan nun von " + action[1] + " benutzt werden");
								map.remove(p.getUniqueId());
								event.setCancelled(true);
								return;
							}
							p.sendMessage(plugin.namespace + ChatColor.RED + "D" + materialToSting(blockType) + " kann von dir nicht verändert werden!");
						}
					}
					else if(action[0].equals("remove")){
						if(secureBlock == null){
							p.sendMessage(plugin.namespace + ChatColor.RED + "D" + materialToSting(blockType) + " ist nicht gesichert!");
							map.remove(p.getUniqueId());
							event.setCancelled(true);
							return;
						}
						else{
							if(p.getUniqueId().equals(secureBlock.getOwner())){
								if(!secureBlock.getMember().contains(targetUUID)){
									p.sendMessage(plugin.namespace + ChatColor.RED + "D" + materialToSting(blockType) + " kann von " + action[1] + " nicht benutzt werden!");
									map.remove(p.getUniqueId());
									event.setCancelled(true);
									return;
								}
								data.unSecureBlock(targetUUID, block);
								p.sendMessage(plugin.namespace + ChatColor.WHITE + "D" + materialToSting(blockType) + " kann nun nicht mehr von " + action[1] + " benutzt werden!");
								map.remove(p.getUniqueId());
								event.setCancelled(true);
								return;
							}
							p.sendMessage(plugin.namespace + ChatColor.RED + "D" + materialToSting(blockType) + " kann von dir nicht verändert werden!");
							map.remove(p.getUniqueId());
							event.setCancelled(true);
							return;
						}
					}
				}
			map.remove(p.getUniqueId());
			}
		}
		else{
			if(map.containsKey(p.getUniqueId())){
				p.sendMessage(plugin.namespace + ChatColor.RED + "Dieser Block kann nicht gesichert werden!");
				map.remove(p.getUniqueId());
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteractEventWithSecureBlock(PlayerInteractEvent event){
		Player p = event.getPlayer();
		if(map.containsKey(p.getUniqueId()))
			return;
		if(!(event.getClickedBlock() instanceof Block))
			return;
		Material blockType = event.getClickedBlock().getType();
		if ((blockType == Material.WOOD_DOOR || blockType == Material.IRON_DOOR_BLOCK || blockType == Material.CHEST || blockType == Material.TRAPPED_CHEST || blockType == Material.FURNACE) && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
			BlockSecure_Block secureBlock = data.getBlockSecure(block);
			if(secureBlock != null){
				if(secureBlock.getOwner().equals(p.getUniqueId())){
					return;
				}
				else if(secureBlock.getMember().contains(p.getUniqueId())){
					return;
				}
				event.setCancelled(true);
				p.sendMessage(plugin.namespace + ChatColor.RED + "Du kannst auf d" + materialToSting(blockType) + " nicht zugreifen!");
			}
		}
	}
	
	@EventHandler
	public void onPlayerDestroyBlock(BlockBreakEvent event){
		Player p = event.getPlayer();
		if(map.containsKey(p.getUniqueId()))
			return;
		if(!(event.getBlock() instanceof Block))
			return;
		Material blockType = event.getBlock().getType();
		if (blockType == Material.WOOD_DOOR || blockType == Material.IRON_DOOR_BLOCK || blockType == Material.CHEST || blockType == Material.TRAPPED_CHEST || blockType == Material.FURNACE) {
			Block block = event.getBlock();
			BlockSecure_Block secureBlock = data.getBlockSecure(block);
			if(secureBlock != null){
				if(secureBlock.getOwner().equals(p.getUniqueId())){
					data.unSecureBlock(p.getUniqueId(), block);
					p.sendMessage(plugin.namespace + ChatColor.RED + "Sicherung wurde entfernt!");
					return;
				}
				event.setCancelled(true);
				p.sendMessage(plugin.namespace + ChatColor.RED + "D" + materialToSting(blockType) + " kann nicht von dir abgebaut werden!");
			}
		}
	}
	
	@EventHandler
	public void onBlockPlaceNextToSecureBlockEvent(BlockPlaceEvent event){
		Player p = event.getPlayer();
		if(!(event.getBlock() instanceof Block))
			return;
		Material blockType = event.getBlock().getType();
		if (blockType == Material.CHEST || blockType == Material.TRAPPED_CHEST) {
			Block block = event.getBlock();
			Block nextBlock = data.isDoubleChest(block);
			if(nextBlock != null){
				if(data.getBlockSecure(nextBlock) != null){
					p.sendMessage(plugin.namespace + ChatColor.RED + "D" + materialToSting(blockType) + " kann nicht neben einen gesicherten Block gesetzt werden!");
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event){
		List<Block> destroyed = event.blockList();
		Iterator<Block> it = destroyed.iterator();
		while(it.hasNext()){
			Block block = it.next();
			BlockSecure_Block secureBlock = data.getBlockSecure(block);
			if(secureBlock != null){
				it.remove();
			}
		}
	}
	
	@EventHandler
	public void onInventoryMoveItemevent(InventoryMoveItemEvent event){
		InventoryHolder sourceHolder = event.getSource().getHolder();
		
		Location loc = null;
		
		if(sourceHolder instanceof BlockState){
			BlockState blockState = (BlockState) sourceHolder;
			loc = blockState.getLocation();
		}
		else if (sourceHolder instanceof DoubleChest) {
			DoubleChest chest = (DoubleChest) sourceHolder;
        	loc = chest.getLocation();
		}
		if (loc != null) {
            Block block = loc.getBlock();
            BlockSecure_Block secureBlock = data.getBlockSecure(block);
            if(secureBlock != null){
            	event.setCancelled(!data.getBlockSecure(block).isHopperAllow());
            }
		}
	}

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event){
		Player p = event.getPlayer();
		if(map.containsKey(p.getUniqueId()))
			map.remove(p.getUniqueId());
	}
	
	private String materialToSting(Material mat){
		if(mat == Material.WOOD_DOOR){
			return "ie Holztüre";
		}
		else if(mat == Material.IRON_DOOR_BLOCK){
			return "ie Eisentüre";
		}
		else if(mat == Material.CHEST){
			return "ie Holztruhe";
		}
		else if(mat == Material.TRAPPED_CHEST){
			return "ie Redstonetruhe";
		}
		else if(mat == Material.FURNACE){
			return "er Ofen";
		}
		else if(mat == Material.TRAP_DOOR){
			return "ie Falltüre";
		}
		else{
			return mat.toString();
		}
	}
}
