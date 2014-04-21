package de.wolfsline.security;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LeashHitch;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;

import de.wolfsline.Epicraft.Epicraft;

public class HorseListener implements CommandExecutor, Listener{

	private Epicraft plugin;
	
	HashMap<UUID, UUID> map = new HashMap<UUID, UUID>();

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
			UUID targetUUID = plugin.uuid.getUUIDFromPlayer(args[0]);
			if(targetUUID == null){
				p.sendMessage(plugin.uuid.ERROR);
				return true;
			}
			Player newOwner = Bukkit.getServer().getPlayer(targetUUID);
			if(newOwner == null){
				p.sendMessage(plugin.namespace + ChatColor.RED + "Spieler ist nicht online!");
				return true;
			}
			p.sendMessage(plugin.namespace + ChatColor.WHITE + "Warte auf Maultier...");
			map.put(p.getUniqueId(), newOwner.getUniqueId());
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
			if(tamer.getUniqueId().equals(p.getUniqueId())){
				if(map.containsKey(p.getUniqueId())){
					Player newOwner = Bukkit.getServer().getPlayer(map.get(p.getUniqueId()));
					map.remove(p.getUniqueId());
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
				if(p.hasPermission("epicraft.horse.team") || p.isOp()){
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Zugriff auf das Maultier von " + tamer.getName() + " gewährt!");
					plugin.api.sendLog("[Epicraft - Pferd] " + p.getName() + " hat Zugriff auf das Maultier von " + tamer.getName() + " erhalten");
					if(map.containsKey(p.getUniqueId())){
						Player newOwner = Bukkit.getServer().getPlayer(map.get(p.getUniqueId()));
						map.remove(p.getUniqueId());
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
			//Player p = (Player) event.getRemover();
			if(event.getEntity() instanceof LeashHitch){
				event.setCancelled(true);
				/*LeashHitch leash = (LeashHitch) event.getEntity();
				if(leash.getVehicle() instanceof Horse){
					Bukkit.getServer().broadcastMessage("Horse");
					Horse horse = (Horse) event.getEntity();
					AnimalTamer tamer = horse.getOwner();
					if(tamer == null){
						return;
					}
					if(!tamer.getUniqueId().equals(p.getUniqueId())){
						event.setCancelled(true);
						p.sendMessage(plugin.namespace + ChatColor.RED + "Zugriff auf das Maultier von " + tamer.getName() + " verweigert!");
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
			AnimalTamer tamer = horse.getOwner();
			if(tamer == null){
				return;
			}
			if(!tamer.getUniqueId().equals(p.getUniqueId())){
				if(p.hasPermission("epicraft.horse.team") || p.isOp()){
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Zugriff auf das Maultier von " + tamer.getName() + " gewährt!");
					plugin.api.sendLog("[Epicraft - Pferd] " + p.getName() + " hat Zugriff auf das Maultier von " + tamer.getName() + " erhalten");
					return;
				}
				event.setCancelled(true);
				p.sendMessage(plugin.namespace + ChatColor.RED + "Zugriff auf das Maultier von " + tamer.getName() + " verweigert!");
				plugin.api.sendLog("[Epicraft - Pferd] " + p.getName() + " versucht auf das Maultier von " + tamer.getName() + " zuzugreifen");
			}
		}
	}
	
	
	@EventHandler
	public void onEntityDamageEvent(EntityDamageByEntityEvent event){
		if(event.getEntity() instanceof Horse){
			Horse horse = (Horse) event.getEntity();
			Entity d = event.getDamager();
			Player damager = null;
			AnimalTamer tamer = horse.getOwner();
			if(tamer == null){
				return;
			}
			if(event.getDamager() instanceof Player){
				damager = (Player) event.getDamager();
				if(tamer.getUniqueId().equals(damager.getUniqueId())){
					return;
				}
				damager.sendMessage(plugin.namespace + ChatColor.RED + "Du kannst dem Maultier von " + tamer.getName() + " keinen schaden hinzufügen!");
				plugin.api.sendLog("[Epicraft - Pferd] " + damager.getName() + " versucht dem Maultier von " + tamer.getName() + " schaden zuzufügen");
				event.setCancelled(true);
			}
			else if(d instanceof Arrow){
				Arrow arrow = (Arrow) d;
				if(arrow.getShooter() instanceof Player){
					damager = (Player) arrow.getShooter();
					if(tamer != null){
						if(tamer.getUniqueId().equals(damager.getUniqueId())){
							return;
						}
					}
					damager.sendMessage(plugin.namespace + ChatColor.RED + "Du kannst dem Maultier von " + tamer.getName() + " keinen schaden hinzufügen!");
					plugin.api.sendLog("[Epicraft - Pferd] " + damager.getName() + " versucht dem Maultier von " + tamer.getName() + " schaden zuzufügen");
				}
				else{
					return;
				}
			}
			else if(d instanceof Fish){
				Fish fish = (Fish) d;
				if(fish.getShooter() instanceof Player){
					damager = (Player) fish.getShooter();
					if(tamer != null){
						if(tamer.getUniqueId().equals(damager.getUniqueId())){
							return;
						}
					}
					damager.sendMessage(plugin.namespace + ChatColor.RED + "Du kannst dem Maultier von " + tamer.getName() + " keinen schaden hinzufügen!");
					plugin.api.sendLog("[Epicraft - Pferd] " + damager.getName() + " versucht dem Maultier von " + tamer.getName() + " schaden zuzufügen");
				}
				else{
					return;
				}
			}
			else if(d instanceof Egg){
				Egg egg = (Egg) d;
				if(egg.getShooter() instanceof Player){
					damager = (Player) egg.getShooter();
					if(tamer != null){
						if(tamer.getUniqueId().equals(damager.getUniqueId())){
							return;
						}
					}
					damager.sendMessage(plugin.namespace + ChatColor.RED + "Du kannst dem Maultier von " + tamer.getName() + " keinen schaden hinzufügen!");
					plugin.api.sendLog("[Epicraft - Pferd] " + damager.getName() + " versucht dem Maultier von " + tamer.getName() + " schaden zuzufügen");
				}
				else{
					return;
				}
			}
			
			else if (d instanceof Snowball){ // macht keinen schaden, spieler simuliert schaden
				Snowball snow = (Snowball) d;
				if(snow.getShooter() instanceof Player){
					damager = (Player) snow.getShooter();
					if(tamer != null){
						if(tamer.getUniqueId().equals(damager.getUniqueId())){
							return;
						}
					}
					damager.sendMessage(plugin.namespace + ChatColor.RED + "Du kannst dem Maultier von " + tamer.getName() + " keinen schaden hinzufügen!");
					plugin.api.sendLog("[Epicraft - Pferd] " + damager.getName() + " versucht dem Maultier von " + tamer.getName() + " schaden zuzufügen");
				}
				else{
					return;
				}
			}
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerFishEvent(PlayerFishEvent event){
		Player p = event.getPlayer();
		Entity targetEntity = event.getCaught();
		if(targetEntity instanceof Horse){
			Horse horse = (Horse) targetEntity;
			AnimalTamer tamer = horse.getOwner();
			if(tamer != null){
				if(!tamer.getUniqueId().equals(p.getUniqueId())){
					event.setCancelled(true);
					p.sendMessage(plugin.namespace + ChatColor.RED + "Du kannst dem Maultier von " + tamer.getName() + " keinen schaden hinzufügen!");
					plugin.api.sendLog("[Epicraft - Pferd] " + p.getName() + " versucht dem Maultier von " + tamer.getName() + " schaden zuzufügen");
				}
			}
		}
	}
	
	@EventHandler
	public void onSplashPotionEvent(PotionSplashEvent event){
		if(event.getPotion().getShooter() instanceof Player){
			Player damager = (Player) event.getPotion().getShooter();
			for(LivingEntity victims : event.getAffectedEntities()){
				if(victims instanceof Horse){
					Horse horse = (Horse) victims;
					AnimalTamer tamer = horse.getOwner();
					if(tamer != null){
						if(!tamer.getUniqueId().equals(damager.getUniqueId())){
							event.setIntensity(victims, 0.0D);
							damager.sendMessage(plugin.namespace + ChatColor.RED + "Du kannst dem Maultier von " + tamer.getName() + " keinen schaden hinzufügen!");
							plugin.api.sendLog("[Epicraft - Pferd] " + damager.getName() + " versucht dem Maultier von " + tamer.getName() + " schaden zuzufügen");
						}
					}
				}
			}
		}
	}
}