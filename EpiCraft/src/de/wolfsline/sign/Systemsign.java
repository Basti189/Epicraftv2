package de.wolfsline.sign;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import de.wolfsline.Epicraft.Epicraft;

public class Systemsign implements CommandExecutor, Listener {

	private final String WORLD = "Survival";
	
	private Epicraft plugin;
	
	private File file = new File("plugins/Epicraft/systemsign.yml");
	private FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
	private World world;
	private Location loc[];
	
	private HashMap<UUID, String> map = new HashMap<UUID, String>();
	
	
	
	public Systemsign(Epicraft plugin){
		this.plugin = plugin;
		world = Bukkit.getServer().getWorld(WORLD);
		loc = new Location[6];
		init();
		initSign();
		
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, runnable, 60L, 20L);
	}
	
	private void init(){
		String pos = "Location.ram.";
		cfg.addDefault(pos + "x", 0);
		cfg.addDefault(pos + "y", 0);
		cfg.addDefault(pos + "z", 0);
		
		loc[0] = new Location(world, cfg.getInt(pos + "x"), cfg.getInt(pos + "y"), cfg.getInt(pos + "z"));
		
		pos = "Location.onlinespieler.";
		cfg.addDefault(pos + "x", 0);
		cfg.addDefault(pos + "y", 0);
		cfg.addDefault(pos + "z", 0);
		
		loc[1] = new Location(world, cfg.getInt(pos + "x"), cfg.getInt(pos + "y"), cfg.getInt(pos + "z"));
		
		pos = "Location.uhrzeit.";
		cfg.addDefault(pos + "x", 0);
		cfg.addDefault(pos + "y", 0);
		cfg.addDefault(pos + "z", 0);
		
		loc[2] = new Location(world, cfg.getInt(pos + "x"), cfg.getInt(pos + "y"), cfg.getInt(pos + "z"));
		
		pos = "Location.hauptwelt.";
		cfg.addDefault(pos + "x", 0);
		cfg.addDefault(pos + "y", 0);
		cfg.addDefault(pos + "z", 0);
		
		loc[3] = new Location(world, cfg.getInt(pos + "x"), cfg.getInt(pos + "y"), cfg.getInt(pos + "z"));
		
		pos = "Location.farmwelt.";
		cfg.addDefault(pos + "x", 0);
		cfg.addDefault(pos + "y", 0);
		cfg.addDefault(pos + "z", 0);
		
		loc[4] = new Location(world, cfg.getInt(pos + "x"), cfg.getInt(pos + "y"), cfg.getInt(pos + "z"));
		
		pos = "Location.nether.";
		cfg.addDefault(pos + "x", 0);
		cfg.addDefault(pos + "y", 0);
		cfg.addDefault(pos + "z", 0);
		
		loc[5] = new Location(world, cfg.getInt(pos + "x"), cfg.getInt(pos + "y"), cfg.getInt(pos + "z"));
		cfg.options().copyDefaults(true);
		try {
			cfg.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void initSign(){
		if(world == null){
			return;
		}
		for(int i = 0 ; i <= 5 ; i++){
			if(loc[i].getBlock().getType() == Material.WALL_SIGN || loc[i].getBlock().getType() == Material.SIGN_POST){
				Sign sign = (Sign) loc[i].getBlock().getState();
				sign.setLine(0, ChatColor.DARK_RED + "█████████");
				sign.setLine(1, ChatColor.WHITE + "Server online");
				sign.setLine(2, ChatColor.WHITE + "Initialisiere");
				sign.setLine(3, ChatColor.DARK_RED + "█████████");
				sign.update();
			}
		}
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command cms, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(!p.hasPermission("epicraft.sign.system")){
			p.sendMessage(plugin.error);
			return true;
		}
		if(args.length == 1){
			if(args[0].equalsIgnoreCase("ram")){
				map.put(p.getUniqueId(), "ram");
			}
			else if(args[0].equalsIgnoreCase("onlinespieler")){
				map.put(p.getUniqueId(), "onlinespieler");
			}
			else if(args[0].equalsIgnoreCase("uhrzeit")){
				map.put(p.getUniqueId(), "uhrzeit");
			}
			else if(args[0].equalsIgnoreCase("hautpwelt")){
				map.put(p.getUniqueId(), "hauptwelt");
			}
			else if(args[0].equalsIgnoreCase("farmwelt")){
				map.put(p.getUniqueId(), "farmwelt");
			}
			else if(args[0].equalsIgnoreCase("nether")){
				map.put(p.getUniqueId(), "nether");
			}
			if(map.containsKey(p.getUniqueId())){
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Bitte das Schild anklicken!");
				return true;
			}
		}
		return false;
	}
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event){
		Player p = event.getPlayer();
		if(map.containsKey(p.getUniqueId())){
			if (!(event.getClickedBlock() instanceof Block))
				return;
			if ((event.getClickedBlock().getType() == Material.WALL_SIGN || event.getClickedBlock().getType() == Material.SIGN_POST) && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				Location loc = event.getClickedBlock().getLocation();
				//Sign sign = (Sign) event.getClickedBlock().getState();
				cfg.set("Location." + map.get(p.getUniqueId()) + ".x", loc.getBlockX());
				cfg.set("Location." + map.get(p.getUniqueId()) + ".y", loc.getBlockY());
				cfg.set("Location." + map.get(p.getUniqueId()) + ".z", loc.getBlockZ());
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Schild wurde hinzugefügt!");
				try {
					cfg.save(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
				map.remove(p.getUniqueId());
				return;
			}
			p.sendMessage(plugin.namespace + ChatColor.RED + "Das ist kein Schild!");
		}
	}
	
	Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			if(world == null){
				return;
			}
			int hauptwelt = 0, farmwelt = 0, nether = 0;
			for(Player player : Bukkit.getServer().getOnlinePlayers()){
				if(player.getLocation().getWorld().getName().equalsIgnoreCase("Survival")){
					hauptwelt++;
				}
				else if(player.getLocation().getWorld().getName().equalsIgnoreCase("Minemap")){
					farmwelt++;
				}
				else if(player.getLocation().getWorld().getName().equalsIgnoreCase("Survival_nether")){
					nether++;
				}
			}
			if(loc[0].getBlock().getType() == Material.WALL_SIGN || loc[0].getBlock().getType() == Material.SIGN_POST){
				long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				String used = String.valueOf(usedMemory / 1024 / 1024);
				String max = String.valueOf(Runtime.getRuntime().maxMemory() / 1024 / 1024);
				Sign sign = (Sign) loc[0].getBlock().getState();
				sign.setLine(0, ChatColor.GOLD + "Benutzt:");
				sign.setLine(1, ChatColor.WHITE  + used + " MB");
				sign.setLine(2, ChatColor.GOLD + "Maximal:");
				sign.setLine(3, ChatColor.WHITE  + max + "MB");
				sign.update();
				
			}
			if(loc[1].getBlock().getType() == Material.WALL_SIGN || loc[1].getBlock().getType() == Material.SIGN_POST){
				Sign sign = (Sign) loc[1].getBlock().getState();
				sign.setLine(0, ChatColor.GOLD + "Spieler");
				sign.setLine(1, "");
				sign.setLine(2, ChatColor.WHITE + String.valueOf(Bukkit.getServer().getOnlinePlayers().length) + ChatColor.DARK_GREEN + " online");
				sign.setLine(3, "");
				
				sign.update();
			}
			if(loc[2].getBlock().getType() == Material.WALL_SIGN || loc[2].getBlock().getType() == Material.SIGN_POST){
				Sign sign = (Sign) loc[2].getBlock().getState();
				Date date = new Date();
				SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.GERMANY);
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
				sign.setLine(0, ChatColor.GOLD + "Uhrzeit:");
				sign.setLine(1, ChatColor.WHITE + timeFormat.format(date));
				sign.setLine(2, ChatColor.GOLD + "Datum:");
				sign.setLine(3, ChatColor.WHITE + dateFormat.format(date));
				sign.update();
				
			}
			if(loc[3].getBlock().getType() == Material.WALL_SIGN || loc[3].getBlock().getType() == Material.SIGN_POST){
				Sign sign = (Sign) loc[3].getBlock().getState();
				sign.setLine(0, ChatColor.GOLD + "Hauptwelt");
				sign.setLine(1, "");
				sign.setLine(2, ChatColor.WHITE + String.valueOf(hauptwelt) + " Spieler");
				sign.setLine(3, "");
				sign.update();
				
			}
			if(loc[4].getBlock().getType() == Material.WALL_SIGN || loc[4].getBlock().getType() == Material.SIGN_POST){
				Sign sign = (Sign) loc[4].getBlock().getState();
				sign.setLine(0, ChatColor.GOLD + "Farmwelt");
				sign.setLine(1, "");
				sign.setLine(2, ChatColor.WHITE + String.valueOf(farmwelt) + " Spieler");
				sign.setLine(3, "");
				sign.update();
				
			}
			if(loc[5].getBlock().getType() == Material.WALL_SIGN || loc[5].getBlock().getType() == Material.SIGN_POST){
				Sign sign = (Sign) loc[5].getBlock().getState();
				sign.setLine(0, ChatColor.GOLD + "Nether");
				sign.setLine(1, "");
				sign.setLine(2, ChatColor.WHITE + String.valueOf(nether) + " Spieler");
				sign.setLine(3, "");
				sign.update();
			}
			
		}
	};
}
