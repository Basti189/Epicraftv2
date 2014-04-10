package de.wolfsline.security;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LeashHitch;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import de.wolfsline.Epicraft.Epicraft;

public class HorseListener implements CommandExecutor, Listener{

	private Epicraft plugin;
	
	HashMap<String, String> map = new HashMap<String, String>();

	public HorseListener(Epicraft plugin){
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(!p.hasPermission("epicraft.horse")){
			p.sendMessage(plugin.error);
			plugin.api.sendLog("[Epicraft - Pferd] " + p.getName() + " hat versucht auf den Befehl zuzugreifen");
			return true;
		}
		if(args.length == 1){
			Player newOwner = Bukkit.getServer().getPlayer(args[0]);
			if(newOwner == null){
				p.sendMessage(plugin.namespace + ChatColor.RED + "Spieler ist nicht online!");
				return true;
			}
			p.sendMessage(plugin.namespace + ChatColor.WHITE + "Warte auf Maultier...");
			map.put(p.getName(), newOwner.getName());
			return true;
		}		
		return false;
	}
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEntityEvent event){
		Player p = event.getPlayer();
		if(event.getRightClicked() instanceof Horse){
			Horse horse = (Horse) event.getRightClicked();
			AnimalTamer tamer = horse.getOwner();
			if(tamer == null)
				return;
			if(tamer.getName().equalsIgnoreCase(p.getName())){
				if(map.containsKey(p.getName())){
					Player newOwner = Bukkit.getServer().getPlayer(map.get(p.getName()));
					map.remove(p.getName());
					if(newOwner == null){
						p.sendMessage(plugin.namespace + ChatColor.RED + "Spieler ist nicht mehr online!");
						p.sendMessage(plugin.namespace + ChatColor.RED + "Vorgang abgebrochen!");
						return;
					}
					horse.setOwner(newOwner);
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Das Maultier gehört nun " + newOwner.getName() + ".");
					newOwner.sendMessage(plugin.namespace + ChatColor.WHITE + p.getName() + " hat dich als Besitzer eines Maultieres festgelegt!");
					plugin.api.sendLog("[Epicraft - Pferd] " + p.getName() + " hat " + newOwner.getName() + " als neuen Besitzer für ein Maultier eingetragen");
					event.setCancelled(true);
				}
			}
			else{
				if(p.hasPermission("epicraft.permission.guard") || p.hasPermission("epicraft.permission.moderator") || p.hasPermission("epicraft.permission.admin") || p.isOp()){
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Zugriff auf das Maultier von " + tamer.getName() + " gewährt!");
					plugin.api.sendLog("[Epicraft - Pferd] " + p.getName() + " hat Zugriff auf das Maultier von " + tamer.getName() + " erhalten");
					if(map.containsKey(p.getName())){
						Player newOwner = Bukkit.getServer().getPlayer(map.get(p.getName()));
						map.remove(p.getName());
						if(newOwner == null){
							p.sendMessage(plugin.namespace + ChatColor.RED + "Spieler ist nicht mehr online!");
							p.sendMessage(plugin.namespace + ChatColor.RED + "Vorgang abgebrochen!");
							return;
						}
						horse.setOwner(newOwner);
						p.sendMessage(plugin.namespace + ChatColor.WHITE + "Das Maultier gehört nun " + newOwner.getName() + ".");
						newOwner.sendMessage(plugin.namespace + ChatColor.WHITE + p.getName() + " hat dich als Besitzer eines Maultieres festgelegt!");
						plugin.api.sendLog("[Epicraft - Pferd] " + p.getName() + " hat " + newOwner.getName() + " als neuen Besitzer für ein Maultier eingetragen");
						event.setCancelled(true);
					}
					return;
				}
				p.sendMessage(plugin.namespace + ChatColor.RED + "Zugriff auf das Maultier von " + tamer.getName() + " verweigert!");
				plugin.api.sendLog("[Epicraft - Pferd] " + p.getName() + " versucht auf das Maultier von " + tamer.getName() + " zuzugreifen");
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onHangingBreakEvent(HangingBreakByEntityEvent event){
		if(event.getRemover() instanceof Player){
			Player p = (Player) event.getRemover();
			if(event.getEntity() instanceof LeashHitch){
				//LeashHitch leash = (LeashHitch) event.getEntity();
				event.setCancelled(true);
				/*if(leash.getVehicle() instanceof Horse){
					Bukkit.getServer().broadcastMessage("Horse");
					Horse horse = (Horse) event.getEntity();
					if(!horse.getOwner().getName().equalsIgnoreCase(p.getName())){
						event.setCancelled(true);
						p.sendMessage(plugin.namespace + ChatColor.RED + "Zugriff auf das Maultier von " + horse.getOwner().getName() + " verweigert!");
					}
				}*/
				
			}
		}
	}
	
	@EventHandler
	public void onPlayerUnleashEntityEvent(PlayerUnleashEntityEvent event){
		Player p = event.getPlayer();
		if(event.getEntity() instanceof Horse){
			Horse horse = (Horse) event.getEntity();
			if(!horse.getOwner().getName().equalsIgnoreCase(p.getName())){
				if(p.hasPermission("epicraft.permission.guard") || p.hasPermission("epicraft.permission.moderator") || p.hasPermission("epicraft.permission.admin") || p.isOp()){
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Zugriff auf das Maultier von " + horse.getOwner().getName() + " gewährt!");
					plugin.api.sendLog("[Epicraft - Pferd] " + p.getName() + " hat Zugriff auf das Maultier von " + horse.getOwner().getName() + " erhalten");
					return;
				}
				event.setCancelled(true);
				p.sendMessage(plugin.namespace + ChatColor.RED + "Zugriff auf das Maultier von " + horse.getOwner().getName() + " verweigert!");
				plugin.api.sendLog("[Epicraft - Pferd] " + p.getName() + " versucht auf das Maultier von " + horse.getOwner().getName() + " zuzugreifen");
			}
		}
	}
	
	
	@EventHandler
	public void onEntityDamageEvent(EntityDamageByEntityEvent event){
		if(event.getEntity() instanceof Horse){
			Horse horse = (Horse) event.getEntity();
			if(event.getDamager() instanceof Player){
				Player damager = (Player) event.getDamager();
				AnimalTamer tamer = horse.getOwner();
				if(tamer != null){
					if(tamer.getName().equalsIgnoreCase(damager.getName())){
						return;
					}
					damager.sendMessage(plugin.namespace + ChatColor.RED + "Du kannst dem Maultier von " + tamer.getName() + " keinen schaden hinzufügen!");
					plugin.api.sendLog("[Epicraft - Pferd] " + damager.getName() + " versucht dem Maultier von " + tamer.getName() + " schaden zuzufügen");
					event.setCancelled(true);
				}
			}
		}
	}
}