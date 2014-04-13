package de.wolfsline.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.helpClasses.userBlockLocation;

public class ChestPassword implements CommandExecutor, Listener{

	private Epicraft plugin;
	//private Economy econ;
	private List<userBlockLocation> myChest = new ArrayList<userBlockLocation>();
	private List<String> list = new ArrayList<String>();
	
	public ChestPassword(Epicraft plugin){//, Economy economy){
		this.plugin = plugin;
		//this.econ = economy;
		try {
			readData();
		} catch (ClassNotFoundException e) {
			System.out.println("[EpiCraft] ChestPassword Klasse nicht gefunden");
		} catch (IOException e) {
			System.out.println("[EpiCraft] ChestPassword Fehler");
		}
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler");
			return true;
		}
		Player p = (Player) cs;
		if(!p.hasPermission("epicraft.blocksecure")){
			p.sendMessage(plugin.error);
			plugin.api.sendLog("[Epicraft - Secure] " + p.getName() + " hat versucht auf den Secure-Befehl zuzugreifen");
			return true;
		}
		if(!p.getLocation().getWorld().getName().equalsIgnoreCase("Survival")){
			p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast keinen Zugriff auf diesen Befehl!");
			plugin.api.sendLog("[Epicraft - Secure] " + p.getName() + " hat versucht auf der Welt " + p.getLocation().getWorld().getName() + " eine Kiste oder einen Ofen zu sichern");
			return true;
		}
		if(!list.contains(p.getName())){
			p.sendMessage(plugin.namespace + ChatColor.WHITE + "Truhe/Ofen zum Sichern anklicken!");
			plugin.api.sendLog("[Epicraft - Secure] " + p.getName() + " ist im Sicherungsmodus");
			list.add(p.getName());
			return true;
		}
		else{
			p.sendMessage(plugin.namespace + ChatColor.WHITE + "Truhen/Öfen fertig gesichert!");
			plugin.api.sendLog("[Epicraft - Secure] " + p.getName() + " hat den Sicherungsmodus verlassen");
			list.remove(p.getName());
			return true;
		}
	}
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event){
		Player p = event.getPlayer();
		if(!list.contains(p.getName()))
			return;
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(event.getClickedBlock().getType().equals(Material.CHEST) || event.getClickedBlock().getType().equals(Material.TRAPPED_CHEST) || event.getClickedBlock().getType().equals(Material.FURNACE)){
				Location loc = event.getClickedBlock().getLocation();
				for(userBlockLocation ubl: myChest){
					String playerName = ubl.equals(loc);
					if(playerName != null){
						if(!playerName.equalsIgnoreCase(p.getName())){
							p.sendMessage(plugin.namespace + ChatColor.RED + "Du kannst " + getBlockName(event.getClickedBlock().getType()) + " nicht sichern!");
							plugin.api.sendLog("[Epicraft - Secure] " + p.getName() + " hat versucht einen fremden Ofen oder eine fremde Truhe zu sichern");
							return;
						}
						myChest.remove(ubl);
						p.sendMessage(plugin.namespace + ChatColor.WHITE + "Schutz entfernt.");
						saveData();
						event.setCancelled(true);
						return;
					}
				}
				if(p.hasPermission("epicraft.blocksecure.team")){
					/*if(!econ.has(p.getName(), 10.0D)){
						p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast nicht genug Geld!");
						return;
					}*/
				}
				/*else if(!econ.has(p.getName(), 25.0D)){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast nicht genug Geld!");
					return;
				}*/
				myChest.add(new userBlockLocation(loc, p.getName()));
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Schutz hinzugefügt.");
				if(p.hasPermission("epicraft.blocksecure.team")){
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Dir wurden dafür 10 Coins berechnet - Teamprozente.");
					//econ.withdrawPlayer(p.getName(), 10.0D);
				}
				else{
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Dir wurden dafür 25 Coins berechnet.");
					//econ.withdrawPlayer(p.getName(), 25.0D);
				}
				saveData();
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteractEventChest(PlayerInteractEvent event){
		Player p = event.getPlayer();
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(event.getClickedBlock().getType().equals(Material.CHEST) || event.getClickedBlock().getType().equals(Material.TRAPPED_CHEST) || event.getClickedBlock().getType().equals(Material.FURNACE)){
				Location loc = event.getClickedBlock().getLocation();
				if(!loc.getWorld().getName().equalsIgnoreCase("Survival"))
					return;
				for(userBlockLocation ubl: myChest){
					String playerName = ubl.equals(loc);
					if(playerName != null){
						if(!playerName.equalsIgnoreCase(p.getName())){
							if(p.hasPermission("epicraft.blocksecure.team")){
								p.sendMessage(plugin.namespace + ChatColor.WHITE + "Zugriff auf " + getBlockName(event.getClickedBlock().getType()) + " von " + playerName + " gewährt!");
								plugin.api.sendLog("[Epicraft - Secure] " + p.getName() + " hat Zugriff auf eine gesicherte Truhe oder einen gesicherten Ofen von " + playerName + " erhalten");
								return;
							}
							p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast kein Zugriff auf " + getBlockName(event.getClickedBlock().getType()) + "!");
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onDestroyChest(BlockBreakEvent event){
		if(event.getBlock().getType().equals(Material.CHEST) || event.getBlock().getType().equals(Material.TRAPPED_CHEST) || event.getBlock().getType().equals(Material.FURNACE)){
			Location loc = event.getBlock().getLocation();
			if(!loc.getWorld().getName().equalsIgnoreCase("Survival"))
				return;
			Player p = event.getPlayer();
			for(userBlockLocation ubl: myChest){
				String playerName = ubl.equals(loc);
				if(playerName != null){
					if(!playerName.equalsIgnoreCase(p.getName())){
						p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast kein Zugriff auf " + getBlockName(event.getBlock().getType()) + "!");
						plugin.api.sendLog("[Epicraft - Secure] " + p.getName() + " hat versucht eine gesicherte Truhe oder einen gesicherten Ofen von " + playerName + " abzubauen");
						event.setCancelled(true);
						return;
					}
					myChest.remove(ubl);
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Schutz entfernt.");
					saveData();
					return;
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event){
		if(!event.getLocation().getWorld().getName().equalsIgnoreCase("Survival"))
			return;
		List<Block> destroyed = event.blockList();
		Iterator<Block> it = destroyed.iterator();
		while(it.hasNext()){
			Block block = it.next();
			for(userBlockLocation ubl: myChest){
				String playerName = ubl.equals(block.getLocation());
				if(playerName != null){
					it.remove();
					break;
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event){
		Player p = event.getPlayer();
		if(list.contains(p.getName()))
			list.remove(p.getName());
	}
	
	private String getBlockName(Material m){
		if(m.toString().contains("CHEST"))
			return "diese Kiste";
		else if(m.toString().equalsIgnoreCase("furnace"))
			return "diesen Ofen";
		return "?";
		
	}
	
	private void saveData(){
		File file = new File("plugins/EpiCraft/", "ChestPassword.dat");
		try {
			FileOutputStream f = new FileOutputStream(file);
			ObjectOutputStream s = new ObjectOutputStream(f);
	        s.writeObject(myChest);
	        s.close();
		} catch (FileNotFoundException e) {
			System.out.println("[EpiCraft] ChestPassword Datei nicht gefunden");
		} catch (IOException e) {
			System.out.println("[EpiCraft] ChestPassword Fehler");
			e.printStackTrace();
		}
        
	}
	
	@SuppressWarnings("unchecked")
	private void readData() throws IOException, ClassNotFoundException{
		File file = new File("plugins/EpiCraft/", "ChestPassword.dat");
		FileInputStream f = new FileInputStream(file);
	    ObjectInputStream s = new ObjectInputStream(f);
		myChest = (List<userBlockLocation>) s.readObject();
		s.close();
	}
}
