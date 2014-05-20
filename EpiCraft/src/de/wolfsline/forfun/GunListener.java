package de.wolfsline.forfun;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
import org.bukkit.inventory.ItemStack;

import de.wolfsline.Epicraft.Epicraft;

public class GunListener implements Listener, CommandExecutor{

	private Epicraft plugin;
	private boolean createNewGun = false;
	private ArrayList<Location> map;
	File file = new File("plugins/Epicraft/tntButtons.yml");
	FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
	
	private final String WORLD = "Survival";
	
	public GunListener(Epicraft plugin) {
		this.plugin = plugin;
		map = new ArrayList<Location>();
		readList();
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e)
	{
		Player p = e.getPlayer();
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(e.getClickedBlock().getType().equals(Material.STONE_BUTTON)){
				Location loc = e.getClickedBlock().getLocation();
				if((p.hasPermission("epicraft.gun") || p.isOp())){
					if(createNewGun){
						if(map.contains(loc)){
							map.remove(loc);
							p.sendMessage("Knopf wurde der Liste entfernt!");
							return;
						}
							
						map.add(loc);
						saveList();
						p.sendMessage("Knopf wurde der Liste hinzugefügt!");
						return;
					}
				}
				if(!map.contains(loc))
					return;
				int x = (int) loc.getX();
				int y = (int) loc.getY();
				int z = (int) loc.getZ();
				ItemStack woolB = new ItemStack(Material.WOOL, 1, (short)15);
				Block b = Bukkit.getServer().getWorld(WORLD).getBlockAt(x+1, y, z);
				if(b.getType().equals(woolB.getType())){
					loc.setX(x+3);
					Bukkit.getWorld(WORLD).createExplosion(loc, 0.0f, false);
					loc.setX(x+23);
					Bukkit.getWorld(WORLD).createExplosion(loc, 0.0f, false);		
					return;
				}
				b = Bukkit.getServer().getWorld(WORLD).getBlockAt(x-1, y, z);
				if(b.getType().equals(woolB.getType())){
					loc.setX(x-3);
					Bukkit.getWorld(WORLD).createExplosion(loc, 0.0f, false);
					loc.setX(x-23);
					Bukkit.getWorld(WORLD).createExplosion(loc, 0.0f, false);
					return;
				}
				b = Bukkit.getServer().getWorld(WORLD).getBlockAt(x, y, z+1);
				if(b.getType().equals(woolB.getType())){;
					loc.setZ(z+3);
					Bukkit.getWorld(WORLD).createExplosion(loc, 0.0f, false);
					loc.setZ(z+23);
					Bukkit.getWorld(WORLD).createExplosion(loc, 0.0f, false);
					return;
				}
				b = Bukkit.getServer().getWorld(WORLD).getBlockAt(x, y, z-1);
				if(b.getType().equals(woolB.getType())){
					loc.setZ(z-3);
					Bukkit.getWorld(WORLD).createExplosion(loc, 0.0f, false);
					loc.setZ(z-23);
					Bukkit.getWorld(WORLD).createExplosion(loc, 0.0f, false);
					return;
				}
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(!(p.hasPermission("epicraft.gun") || p.isOp())){
			p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast keinen Zugriff auf diesen Befehl!");
			return true;
		}
		if(args.length == 0){
			createNewGun = !createNewGun;
			p.sendMessage("Toggled GunSaver");
			return true;
		}
		return false;
	}
	
	private void saveList(){
		int i = 1;
		for(Location loc : map){
			String pos = "Knopf" + String.valueOf(i) + ".";
			cfg.set(pos + "x", loc.getX());
			cfg.set(pos + "y", loc.getY());
			cfg.set(pos + "z", loc.getZ());
			i++;
		}
		try {
			this.cfg.save(file);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void readList(){
		for(String t : cfg.getKeys(false)){
			String pos = t + ".";
			Location loc = new Location(Bukkit.getWorld(WORLD), cfg.getInt(pos + "x"), cfg.getInt(pos + "y"), cfg.getInt(pos + "z"));
			map.add(loc);
		}
	}
}
