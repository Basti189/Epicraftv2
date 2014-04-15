package de.wolfsline.forfun;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.wolfsline.Epicraft.Epicraft;

public class PVP implements CommandExecutor, Listener {
	private List<UUID> list = new ArrayList<UUID>();
	private Epicraft plugin;

	public PVP(Epicraft plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(!p.hasPermission("epicraft.pvp")){
			p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast keinen Zugriff auf diesen Befehl!");
			plugin.api.sendLog("[Epicraft - PVP] " + p.getName() + " hat versucht auf den PVP-Befehl zuzugreifen");
			return true;
		}
		if(args.length == 0){
			if(list.contains(p.getUniqueId())){
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "PVP wurde deaktiviert");
				plugin.api.sendLog("[Epicraft - PVP] " + p.getName() + " hat sein PVP deaktiviert");
				list.remove(p.getUniqueId());
				return true;
			}
			else{
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "PVP wurde aktiviert");
				plugin.api.sendLog("[Epicraft - PVP] " + p.getName() + " hat sein PVP aktiviert");
				list.add(p.getUniqueId());
				return true;
			}
		}
		else{
			p.sendMessage(plugin.namespace + ChatColor.RED + "/pvp");
			return true;
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDamage(EntityDamageByEntityEvent event){
		Entity e = event.getEntity();
		Entity d = event.getDamager();
		if(!(e instanceof Player && (d instanceof Player || d instanceof Arrow || d instanceof Fish || d instanceof Egg || d instanceof Snowball))){
			return;
		}
		
		Player victim = (Player) e;
		Player damager = null;
		if(d instanceof Player){
			damager = (Player) d;
		}
		else if(d instanceof Arrow){
			Arrow arrow = (Arrow) d;
			if(arrow.getShooter() instanceof Player){
				damager = (Player) arrow.getShooter();
			}
			else{
				return;
			}
		}
		else if(d instanceof Fish){
			Fish fish = (Fish) d;
			if(fish.getShooter() instanceof Player){
				damager = (Player) fish.getShooter();
			}
			else{
				return;
			}
		}
		else if(d instanceof Egg){
			Egg egg = (Egg) d;
			if(egg.getShooter() instanceof Player){
				damager = (Player) egg.getShooter();
			}
			else{
				return;
			}
		}
		
		else if (d instanceof Snowball){ // macht keinen schaden, spieler simuliert schaden
			Snowball snow = (Snowball) d;
			if(snow.getShooter() instanceof Player){
				damager = (Player) snow.getShooter();
			}
			else{
				return;
			}
		}
		else{
			return;
		}
			
		if(list.contains(damager.getUniqueId())){
			if(list.contains(victim.getUniqueId())){
				//Füge schaden hinzu, OK!
			}
			else{
				event.setCancelled(true);
				damager.sendMessage(plugin.namespace + ChatColor.RED + victim.getName() + " hat kein PVP aktiviert!");
				return;
			}
		}
		else{
			event.setCancelled(true);
			damager.sendMessage(plugin.namespace + ChatColor.RED + "Du hast kein PVP aktiviert!");
			return;
		}
	}
	
	@EventHandler
	public void onSlpashPotionEvent(PotionSplashEvent event){
		if(event.getPotion().getShooter() instanceof Player){
			Player damager = (Player) event.getPotion().getShooter();
			for(LivingEntity victims : event.getAffectedEntities()){
				if(victims instanceof Player){
					Player victim = (Player) victims;
					if(victim.getUniqueId().equals(damager.getUniqueId()))
						return;
					for(PotionEffect effect : event.getPotion().getEffects()){
						if(!isBadSplashPotion(effect.getType()))
							return;
					}
					if(list.contains(damager.getUniqueId())){
						if(list.contains(victim.getUniqueId())){
							//Füge schaden hinzu, OK!
						}
						else{
							event.setCancelled(true);
							damager.sendMessage(plugin.namespace + ChatColor.RED + victim.getName() + " hat kein PVP aktiviert!");
							return;
						}
					}
					else{
						event.setCancelled(true);
						damager.sendMessage(plugin.namespace + ChatColor.RED + "Du hast kein PVP aktiviert!");
						return;
					}
				}
			}
		}
	}
	
	private boolean isBadSplashPotion(PotionEffectType effect){
		if(effect.equals(PotionEffectType.POISON) ||
		   effect.equals(PotionEffectType.WEAKNESS) ||
		   effect.equals(PotionEffectType.SLOW) ||
		   effect.equals(PotionEffectType.HARM) ||
		   effect.equals(PotionEffectType.INCREASE_DAMAGE))
			return true;
		return false;
	}
	
	//public void onProjectileHit(ProjectileHitEvent event){} snowball knokcback ?! <--
	
	@EventHandler
	public void OnQuit(PlayerQuitEvent event){
		if(list.contains(event.getPlayer().getUniqueId()))
			list.remove(event.getPlayer().getUniqueId());
	}
}


