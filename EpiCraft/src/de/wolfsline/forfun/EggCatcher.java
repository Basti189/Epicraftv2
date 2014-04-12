package de.wolfsline.forfun;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import de.wolfsline.Epicraft.Epicraft;

public class EggCatcher implements Listener{
	
	private Epicraft plugin;
	
	public EggCatcher(Epicraft plugin){
		this.plugin = plugin;
	}

	@EventHandler
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event){
		if(event.getDamager() instanceof Egg){
			Egg egg = (Egg) event.getDamager();
			if(egg.getShooter() instanceof Player){
				Player p = (Player) egg.getShooter();
				if(p.hasPermission("epicraft.egg.catch")){
					Entity targetEntity = event.getEntity();
					ItemStack stack = null;
					if(targetEntity instanceof Player){
						return;
					}
					else if(targetEntity instanceof Chicken){//93
						if(!canPlayerCatchEntity(p, targetEntity))
							return;
						event.setCancelled(true);
						targetEntity.remove();
						stack = new ItemStack(Material.MONSTER_EGG, 1, (byte)93);
					}
					else if(targetEntity instanceof Cow){//92
						if(!canPlayerCatchEntity(p, targetEntity))
							return;
						event.setCancelled(true);
						targetEntity.remove();
						stack = new ItemStack(Material.MONSTER_EGG, 1, (byte)92);
					}
					else if(targetEntity instanceof Horse){//100
						Horse horse = (Horse) targetEntity;
						AnimalTamer tamer = horse.getOwner();
						if(tamer != null){
							if(!horse.getOwner().getName().equals(p.getName())){
								p.sendMessage(plugin.namespace + ChatColor.RED + "Du kannst das Mautlier von " + horse.getOwner().getName() + " nicht einfangen!");
								plugin.api.sendLog("[Epicraft - EggCatcher] " + p.getName() + " versucht das Maultier von " + horse.getOwner().getName() + " einzufangen");
								return;
							}
						}
						if(!canPlayerCatchEntity(p, targetEntity))
							return;
						if(horse.isLeashed()){
							p.sendMessage(plugin.namespace + ChatColor.RED + "Bitte binde das Maultier los, bevor du es einfängst!");
							return;
						}
						for(ItemStack invStack : horse.getInventory()){
							if(invStack == null)
								continue;
							targetEntity.getLocation().getWorld().dropItemNaturally(targetEntity.getLocation(), invStack);
						}
						event.setCancelled(true);
						targetEntity.remove();
						stack = new ItemStack(Material.MONSTER_EGG, 1, (byte)100);
					}
					else if(targetEntity instanceof Ocelot){//98
						if(!canPlayerCatchEntity(p, targetEntity))
							return;
						event.setCancelled(true);
						targetEntity.remove();
						stack = new ItemStack(Material.MONSTER_EGG, 1, (byte)98);
					}
					else if(targetEntity instanceof Pig){//90
						if(!canPlayerCatchEntity(p, targetEntity))
							return;
						event.setCancelled(true);
						targetEntity.remove();
						stack = new ItemStack(Material.MONSTER_EGG, 1, (byte)90);
					}
					else if(targetEntity instanceof Sheep){//91
						if(!canPlayerCatchEntity(p, targetEntity))
							return;
						event.setCancelled(true);
						targetEntity.remove();
						stack = new ItemStack(Material.MONSTER_EGG, 1, (byte)91);
					}
					if(stack == null)
						return;
					p.getInventory().addItem(stack);
				}
			}
		}
	}
	
	private boolean canPlayerCatchEntity(Player p, Entity entity){
		Location loc = entity.getLocation();
		WorldGuardPlugin wgPlugin = plugin.getWorldGuard();
		if(wgPlugin == null){
			return true;
		}
		boolean canCatch = wgPlugin.canBuild(p, loc.getBlock());
		if(canCatch){
			return true;
		}
		else{
			p.sendMessage(plugin.namespaceBeta + ChatColor.RED + "Du kannst auf diesem Grundstück keine Tiere einfangen!");
			plugin.api.sendLog("[Epicraft - EggCatcher] " + p.getName() + " versucht ein Tier auf einem anderen Grundstück einzufangen");
			return false;
		}
	}
}
