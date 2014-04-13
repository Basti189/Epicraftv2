package de.wolfsline.administration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.helpClasses.myLocation;

public class JailCommand implements CommandExecutor, Listener{

	private Epicraft plugin;
	private final String WORLD = "Survival";
	
	private Location jailLoc;
	private boolean newObsidian = false;
	
	File file = new File("plugins/EpiCraft/", "Jail.yml");
	FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
	List<myLocation> mylocation = new ArrayList<myLocation>();
	HashMap<String, Integer> map = new HashMap<String, Integer>();
	
	public JailCommand(Epicraft plugin) {
		this.plugin = plugin;
		String pos = "Jail.";
		cfg.addDefault(pos + "x", 0);
		cfg.addDefault(pos + "y", 0);
		cfg.addDefault(pos + "z", 0);
		cfg.addDefault(pos + "yaw", 0);
		cfg.addDefault(pos + "pitch", 0);
		
		jailLoc = new Location(Bukkit.getWorld(WORLD), cfg.getDouble(pos + "x"), cfg.getDouble(pos + "y"), cfg.getDouble(pos + "z"));
		jailLoc.setPitch((float) cfg.getDouble(pos + "pitch"));
		jailLoc.setYaw((float) cfg.getDouble(pos + "yaw"));
		try {
			readData();
			readObsidianLocations();
		} 
		catch (ClassNotFoundException | IOException e) {
		}
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(!(p.hasPermission("epicraft.jail"))){
			p.sendMessage(plugin.error);
			plugin.api.sendLog("[Epicraft - Gefängnis] " + p.getName() + " versuchte den Befehl zu benutzen");
			return true;
		}
		if(label.equalsIgnoreCase("jail")){
			if(args.length == 1){
				Player jailPlayer = Bukkit.getPlayer(args[0]);
				if(jailPlayer == null){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Spieler ist nicht online!");
					return true;
				}
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "p set " + jailPlayer.getName() + " Inhaftierter");
				jailPlayer.teleport(jailLoc);
				jailPlayer.sendMessage(plugin.namespace + ChatColor.WHITE + "Du wurdest in das Gefängnis gesperrt!");
				for(Player team : Bukkit.getServer().getOnlinePlayers()){
					if(team.hasPermission("epicraft.jail.info")){
						team.sendMessage(plugin.namespace + ChatColor.WHITE + jailPlayer.getName() + " wurde eingebuchtet!");
						plugin.api.sendLog("[Epicraft - Gefängnis] " + p.getName() + " sperrte " + jailPlayer.getName() + " in das Gefängnis");
					}
				}
				map.put(jailPlayer.getName(), -1);
				return true;
			}
			else if(args.length == 2){
				Player jailPlayer = Bukkit.getPlayer(args[0]);
				if(jailPlayer == null){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Spieler ist nicht online!");
					return true;
				}
				int obsidianAmount = 0;
				try{
					obsidianAmount = Integer.valueOf(args[1]);
				}
				catch(NumberFormatException nfe){
					p.sendMessage(plugin.namespace + ChatColor.RED + args[1] + " ist keine Zahl!");
					p.sendMessage(plugin.namespace + ChatColor.RED + "Vorgang abgebrochen...");
					return true;
				}
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "p set " + jailPlayer.getName() + " Inhaftierter");
				jailPlayer.teleport(jailLoc);
				jailPlayer.sendMessage(plugin.namespace + ChatColor.WHITE + "Du wurdest in das Gefängnis gesperrt!");
				jailPlayer.sendMessage(plugin.namespace + ChatColor.WHITE + "Du musst " + obsidianAmount + " Obsidianblöcke mit der Hand abbauen!");
				for(Player team : Bukkit.getServer().getOnlinePlayers()){
					if(team.hasPermission("epicraft.jail.info")){
						team.sendMessage(plugin.namespace + ChatColor.WHITE + jailPlayer.getName() + " wurde eingebuchtet!");
						team.sendMessage(plugin.namespace + ChatColor.WHITE + jailPlayer.getName() + " muss " + obsidianAmount + " Obsidianblöcke abbauen!");
						plugin.api.sendLog("[Epicraft - Gefängnis] " + p.getName() + " sperrte " + jailPlayer.getName() + " in das Gefängnis");
						plugin.api.sendLog("[Epicraft - Gefängnis] " + jailPlayer.getName() + " muss " + obsidianAmount + " Obsidianbläcke abbauen");
					}
				}
				map.put(jailPlayer.getName(), obsidianAmount);
				return true;
			}
		}
		else if(label.equalsIgnoreCase("unjail")){
			if(args.length == 1){
				Player jailPlayer = Bukkit.getPlayer(args[0]);
				if(jailPlayer == null){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Spieler ist nicht online!");
					return true;
				}
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "p set " + jailPlayer.getName() + " Spieler");
				jailPlayer.teleport(Bukkit.getServer().getWorld(WORLD).getSpawnLocation());
				jailPlayer.sendMessage(plugin.namespace + ChatColor.WHITE + "Du wurdest entlassen!");
				for(Player team : Bukkit.getServer().getOnlinePlayers()){
					if(team.hasPermission("epicraft.jail.info")){
						team.sendMessage(plugin.namespace + ChatColor.WHITE + jailPlayer.getName() + " wurde entlassen!");
						plugin.api.sendLog("[Epicraft - Gefängnis] " + p.getName() + " hat " + jailPlayer.getName() + " aus dem Gefängnis entlassen");
						
					}
				}
				return true;
			}
		}
		else if(label.equalsIgnoreCase("tpjail")){
			p.teleport(jailLoc);
			p.sendMessage(plugin.namespace + ChatColor.WHITE + "Du wurdest in das Gefängnis teleportiert!");
			plugin.api.sendLog("[Epicraft - Gefängnis] " + p.getName() + " hat sich in das Gefängnis teleportiert");
			return true;
		}
		else if(label.equalsIgnoreCase("setjail")){
			if(!(p.hasPermission("epicraft.jail.set"))){
				p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast keinen Zugriff auf diesen Befehl!");
				return true;
			}
			if(args.length == 0){
				setJail(p);
				p.sendMessage(plugin.namespace + ChatColor.RED + "Jailposition wurde gesetzt!");
				return true;
			}
		}
		else if(label.equalsIgnoreCase("setObsi")){
			newObsidian = !newObsidian;
			if(newObsidian){
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Neue Obsidianblöcke werden nun registriert!");
				return true;
			}
			else{
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Obsidianblöcke werden nun nicht mehr registriert!");
				return true;
			}
				
		}
		return false;
	}
	
	public void setJail(Player p){
		Location loc = p.getLocation();
		this.jailLoc = loc;
		String pos = "Jail.";
		cfg.set(pos + "x", loc.getX());
		cfg.set(pos + "y", loc.getY());
		cfg.set(pos + "z", loc.getZ());
		cfg.set(pos + "pitch", loc.getPitch());
		cfg.set(pos + "yaw", loc.getYaw());
		try {
			this.cfg.save(file);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//-----------------------------------------------------------------------------------------------------------
	
	@EventHandler
	public void onDestroyBlock(BlockBreakEvent event){
		if(!event.getBlock().getType().equals(Material.OBSIDIAN))
			return;
		boolean isInList = false;
		for(int i = 0 ; i < mylocation.size() ; i++){
			myLocation myLoc = mylocation.get(i);
			if(myLoc.equals(event.getBlock().getLocation())){
				isInList = true;
			}
		}
		if(!isInList){
			return;
		}
			
		event.setCancelled(true);
		Player p = event.getPlayer();
		if(!p.getItemInHand().getType().equals(Material.AIR))
			return;
		
		for(Entry<String, Integer> myMap : map.entrySet()){
			if(myMap.getKey().equalsIgnoreCase(p.getName())){
				int amount = myMap.getValue();
				if(amount == -1){
					return;
				}
				amount -= 1;
				if(amount == 0){
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Du hast deine Strafe erfolgreich erledigt.");
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user " + p.getName() + " group set Spieler");
					p.teleport(Bukkit.getServer().getWorld(WORLD).getSpawnLocation());
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Du wurdest entlassen!");
					for(Player team : Bukkit.getServer().getOnlinePlayers()){
						if(team.hasPermission("epicraft.jail.info")){
							team.sendMessage(plugin.namespace + ChatColor.WHITE + p.getName() + " wurde entlassen!");
							plugin.api.sendLog("[Epicraft - Gefängnis] " + p.getName() + " wurde entlassen");
						}
					}
					map.remove(p.getName());
				}
				else{
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Noch " + amount + " Blöcke, bis zur Entlassung!");
					plugin.api.sendLog("[Epicraft - Gefängnis] " + p.getName() + " muss noch " + amount + " Obsidianblöcke abbauen, um entlassen zu werden");
					map.put(p.getName(), amount);
					try {
						saveData();
					} 
					catch (IOException e) {
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e)
	{
		if(!newObsidian)
			return;
		Player p = e.getPlayer();
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(e.getClickedBlock().getType().equals(Material.OBSIDIAN)){
				Location loc = e.getClickedBlock().getLocation();
				if(p.isOp()){
					myLocation myLoc = new myLocation(loc);
					for(myLocation tmp : mylocation){
						if(tmp.equals(loc)){
							mylocation.remove(tmp);
							p.sendMessage(plugin.namespace + ChatColor.WHITE + "Block wurde aus der Liste entfernt!");
							try {
								saveObsidianLocation();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
							return;
						}
					}
					mylocation.add(myLoc);
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Block wurde der Liste hinzugefügt!");
					try {
						saveObsidianLocation();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					return;
				}
			}
		}
	}
	
	private void saveObsidianLocation() throws IOException{      
        File file1 = new File("plugins/EpiCraft/", "JailObsidianLocation.dat");
		FileOutputStream f1 = new FileOutputStream(file1);
        ObjectOutputStream s1 = new ObjectOutputStream(f1);
        s1.writeObject(mylocation);
        s1.close();
	}
	
	private void readObsidianLocations() throws IOException, ClassNotFoundException{
		File file1 = new File("plugins/EpiCraft/", "JailObsidianLocation.dat");
		FileInputStream f1 = new FileInputStream(file1);
	    ObjectInputStream s1 = new ObjectInputStream(f1);
		mylocation = (List<myLocation>) s1.readObject();
		s1.close();
	}
	
	private void saveData() throws IOException{
		File file = new File("plugins/EpiCraft/", "JailUserdata.dat");
		FileOutputStream f = new FileOutputStream(file);
        ObjectOutputStream s = new ObjectOutputStream(f);
        s.writeObject(map);
        s.close();
	}
	
	private void readData() throws IOException, ClassNotFoundException{
		File file = new File("plugins/EpiCraft/", "JailUserdata.dat");
		FileInputStream f = new FileInputStream(file);
	    ObjectInputStream s = new ObjectInputStream(f);
		map = (HashMap<String, Integer>) s.readObject();
		s.close();
	}

}
