package de.wolfsline.healthbar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.scheduler.BukkitScheduler;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.helpClasses.EpicraftPlayer;

public class DamageListener implements Listener{
	

	private static  	Epicraft plugin;
	private static		BukkitScheduler scheduler = Bukkit.getScheduler();
	private static		String[] barArray;
	protected static	long mobHideDelay = 40L;
	private static		Map<Integer,Integer> mobTable = new HashMap<Integer, Integer>();

	public DamageListener(Epicraft plugin){
		this.plugin = plugin;
	}

	@EventHandler (ignoreCancelled = true, priority = EventPriority.LOW)
	 public void onEntityDeath(EntityDeathEvent event) {
		 if (event.getEntity() instanceof LivingEntity) {
			 hideBar(event.getEntity());
		 }
	 }
	
	@EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onEntityDamageEvent(EntityDamageByEntityEvent event) {
		Entity entity = event.getEntity();
		Entity damageEntity = event.getDamager();
		if(damageEntity == null ) return;
		if(!(damageEntity instanceof Player)) return;
		Player p = (Player) damageEntity;
		EpicraftPlayer epiPlayer = plugin.pManager.getEpicraftPlayer(p.getUniqueId());
		if(epiPlayer != null){
			if(!epiPlayer.healthbar) return;
		}
		if(entity instanceof Horse)
			return;
		if(entity instanceof LivingEntity){
			LivingEntity living = (LivingEntity) entity;
			if (living.getNoDamageTicks() > living.getMaximumNoDamageTicks() / 2F) 
				return;
			parseMobHit(living, event instanceof EntityDamageByEntityEvent);
		} 
	}

	@EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onEntityRegain(EntityRegainHealthEvent event) {
		Entity entity = event.getEntity();
		if(entity instanceof Horse)
			return;
		if (entity instanceof LivingEntity) {
			parseMobHit((LivingEntity) entity, true);
		}
	}

	protected static void parseMobHit (LivingEntity mob, boolean damagedByEntity) {
		Integer eventualTaskID = mobTable.remove(mob.getEntityId());
		if (eventualTaskID != null) {
			 scheduler.cancelTask(eventualTaskID);
		 }
		showMobHealthBar(mob);
		hideMobBarLater(mob);
	}

	private static void showMobHealthBar (final LivingEntity mob) {

		scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
	    	  public void run() {

	    		  //check for compatibility
	    		  double health = mob.getHealth();
	    		  double max = mob.getMaxHealth();


	    		  //if the health is 0
	    		  if (health <= 0.0) {
			    	  return;
	    		  }
	    		  barArray = MobBarUtil.getDefaultsBars(3);
	    		  mob.setCustomName("§r" + barArray[Utils.roundUpPositiveWithMax(((health/max) * 20.0), 20)]);
			      
			      //check for visibility
			      mob.setCustomNameVisible(true);
	    	  }
		});
	}

	private static void hideMobBarLater(final LivingEntity mob) {
		final int id = mob.getEntityId();
		mobTable.put(id, scheduler.scheduleSyncDelayedTask(plugin, new Runnable() 
		{ 
			public void run() {
	    		hideBar(mob);

	    }}, mobHideDelay));
	}

	public static void hideBar(LivingEntity mob) {
		String cname = mob.getCustomName();
		if (cname != null && !cname.startsWith("§r")) {
			//it's a real name! Don't touch it!
			return;
		}

		//cancel eventual tasks
		Integer id = mobTable.remove(mob.getEntityId());
		if (id != null) {
			scheduler.cancelTask(id);
		}

		//not a custom named mob, use default method (hide the name)
		mob.setCustomName("");
		mob.setCustomNameVisible(false);
	}

	public static void removeAllMobHealthBars() {
		scheduler.cancelTasks(plugin);
		mobTable.clear();
		List<World> worldsList = plugin.getServer().getWorlds();
		for (World w : worldsList) {
			List<LivingEntity> entityList = w.getLivingEntities();
			for (LivingEntity e : entityList) {
				if (e.getType() != EntityType.PLAYER) {
					hideBar(e);
				}
			}
		}
	}	
}
