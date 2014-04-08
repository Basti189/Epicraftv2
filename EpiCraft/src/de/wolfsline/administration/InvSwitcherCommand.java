package de.wolfsline.administration;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.helpClasses.PlayerSave;

public class InvSwitcherCommand implements CommandExecutor, Listener{

	private HashMap<String, PlayerSave> spList = new HashMap<String, PlayerSave>();
	private Epicraft plugin;
	public InvSwitcherCommand(Epicraft plugin) {
		this.plugin = plugin;
	}
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(!p.hasPermission("epicraft.inv.switch")){
			p.sendMessage(plugin.error);
			plugin.api.sendLog("[Epicraft - Supportmodus] " + p.getName() + " versuchte auf den Supportmodus zuzugreifen");
			return true;
		}
		p.sendMessage(plugin.namespace + ChatColor.RED + "Der Modus steht nicht zur Verfügung!");
		boolean test = true;
		if(test)
			return true;
		if(spList.containsKey(p.getName())){
			p.getInventory().clear();
			PlayerSave ps = spList.get(p.getName());
			ps.restoreInventory(p);
			p.updateInventory();
			p.sendMessage(plugin.namespace + ChatColor.WHITE + "Dein Inventar wurde wiederhergestellt.");
			plugin.api.sendLog("[Epicraft - Supportmodus] " + p.getName() + " deaktiviert den Supportmodus");
			spList.remove(p.getName());
			return true;
		}
		else{
			PlayerSave ps = new PlayerSave();
			ps.saveInventory(p);
			spList.put(p.getName(), ps);
			p.sendMessage(plugin.namespace + ChatColor.WHITE + "Inventar wurde gespeichert.");
			plugin.api.sendLog("[Epicraft - Supportmodus] " + p.getName() + " aktiviert den Supportmodus");
			return true;
		}
	}
	
	public void onServerShutdown(){
		for(Player p : Bukkit.getServer().getOnlinePlayers()){
			if(spList.containsKey(p.getName())){
				p.getInventory().clear();
				PlayerSave ps = spList.get(p.getName());
				ps.restoreInventory(p);
				p.updateInventory();
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Dein Inventar wurde wiederhergestellt.");
				spList.remove(p.getName());
			}
		}
	}
	/*
	@EventHandler
	public void onDamageTool(PlayerInteractEvent event){
		Player p = event.getPlayer();
		if(!spList.containsKey(p.getName()))
			return;
		ItemStack stack = p.getItemInHand();
		stack.setDurability((short)-1);
		p.updateInventory();
	}*/
	
	@EventHandler
	public void onPlayerHungerEvent(FoodLevelChangeEvent event){
		if(!(event.getEntity() instanceof Player))
			return;
		Player p = (Player) event.getEntity();
		if(!spList.containsKey(p.getName()))
			return;
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerDamageEvent(EntityDamageEvent event){
		if(!(event.getEntity() instanceof Player))
			return;
		Player p = (Player) event.getEntity();
		if(!spList.containsKey(p.getName()))
			return;
		event.setCancelled(true);
	}
	/*
	@EventHandler
	public void onBlockPlaceEvent(BlockPlaceEvent event){
		Player p = event.getPlayer();
		if(!spList.containsKey(p.getName()))
			return;
		ItemStack handItem = p.getItemInHand();
		handItem.setAmount(2);
		p.updateInventory();
	}*/
	
	@EventHandler
	public void onDropItem(PlayerDropItemEvent e){
		Player p = e.getPlayer();
		if(spList.containsKey(p.getName())){
			e.setCancelled(true);
			p.sendMessage(plugin.namespace + ChatColor.RED + "Ist im Supporter-Modus nicht nöglich!");
		}	
	}
	
	@EventHandler
	public void onPlayerDead(PlayerDeathEvent e){
		final Player p = e.getEntity();
		if(!this.spList.containsKey(p.getName()))
			return;
		e.setKeepLevel(true);
		e.setDroppedExp(0);
		final ItemStack[] armor = p.getInventory().getArmorContents();
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				p.getInventory().setArmorContents(armor);
			}

		});
		for (ItemStack is : armor) {
			e.getDrops().remove(is);
		}
		final ItemStack[] inventory = p.getInventory().getContents();
		for (int i = 0; i < inventory.length; i++) {
			ItemStack is = inventory[i];
				e.getDrops().remove(is);
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				p.getInventory().setContents(inventory);
			}
		});
	}
	
	@EventHandler
	public void onEntityTargetEvent(EntityTargetEvent event){
		if(event.getTarget() instanceof Player){
			Player p = (Player) event.getTarget();
			if(spList.containsKey(p.getName())){
				EntityType type = event.getEntity().getType();
				if (type == EntityType.ENDERMAN || type == EntityType.WOLF || type == EntityType.PIG_ZOMBIE || type == EntityType.BLAZE || type == EntityType.CAVE_SPIDER || type == EntityType.CREEPER || type == EntityType.GHAST || type == EntityType.MAGMA_CUBE || type == EntityType.SILVERFISH || type == EntityType.SKELETON || type == EntityType.SLIME || type == EntityType.SPIDER || type == EntityType.WITCH || type == EntityType.WITHER_SKULL || type == EntityType.ZOMBIE || type == EntityType.IRON_GOLEM || type == EntityType.ENDER_DRAGON || type == EntityType.WITHER){
					event.setCancelled(true);
				}
			}
		}
	}
}
