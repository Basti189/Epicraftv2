package de.wolfsline.gs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import de.wolfsline.Epicraft.Epicraft;

public class Grundst�ck implements CommandExecutor, Listener{
	
	private Epicraft plugin;
	private final String WORLD = "Survival";
	
	private Data data;
	private HashMap<UUID, String> map = new HashMap<UUID, String>();
	
	public Grundst�ck(Epicraft plugin){
		this.plugin = plugin;
		this.data = new Data(plugin);
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		//Pr�fe Berechtigung
		if(!(p.hasPermission("epicraft.gs") || p.isOp())){
			p.sendMessage(plugin.error);
			plugin.api.sendLog("[Epicraft - Grundst�ck] " + p.getName() + " hat versucht auf den Befehl zuzugreifen!");
			return true;
		}
		//Spieler in der richtigen Welt?
		if(!p.getLocation().getWorld().getName().equals(WORLD)){
			p.sendMessage(plugin.namespace + ChatColor.RED + "Du kannst auf dieser Welt nicht auf die Grundst�cksverwaltung zugreifen!");
			return true;
		}
		if(args.length == 0){//Zeige die Grundst�cke des Spielers
			if(data.countGsFromPlayer(p) == 0){
				p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast keine Grundst�cke!");
				return true;
			}
			data.showGSfromPlayer(p, p.getUniqueId());
			return true;
		}
		else if(args.length == 1){
			/*if(args[0].equalsIgnoreCase("test")){
				Location loc = p.getTargetBlock(null, 200).getLocation();
				int x = (int) loc.getX();
				int y = (int) loc.getZ();
				int z = getZonGround(x, y, (int)loc.getY());
				ecke(x, y, z, Material.WOOD);
				ecke(x, y, z+1, Material.WOOD);
				ecke(x, y, z+2, Material.TORCH);
				return true;
			}*/
		}
		else if(args.length == 2){
			if(args[0].equalsIgnoreCase("neu") || args[0].equalsIgnoreCase("new")){//Neues Grundst�ck anlegen //gs neu GSNAME
				String gsname = args[1];
				int anzahlGS = data.countGsFromPlayer(p);
				if(anzahlGS == 0){ //<-- Spieler hat noch kein Grundst�ck
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Dies ist dein 1. Grundst�ck.");
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Erstelle Grundst�ck: " + gsname + " mit den Ma�en: 50*50.");
					if(!markGS(p, 50, 50, gsname)){
						p.sendMessage(plugin.namespace + ChatColor.RED + "Dein Grundst�ck schneidet ein anderes Grundsrt�ck!");
						p.sendMessage(plugin.namespace + ChatColor.RED + "Vorgang abgebrochen");
						return true;
					}
					data.newGS((int)p.getLocation().getX(), (int)p.getLocation().getY(), (int)p.getLocation().getZ(), gsname, p, 50, 50);
					starterKit(p);
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Grundst�ck wurde erstellt.");
					plugin.api.sendLog("[Epicraft - Grundst�ck] " + p.getName() + " hat sein 1. Grundst�ck erstellt");
					return true;
				}
				else{ //<-- Spieler hat schon ein Grundst�ck
					if(!data.hasPlayerGSwithName(p.getUniqueId(), gsname)){
						/*if(!econ.has(p.getName(), 5000)){
							p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast keine 5000 Coins!");
							return true;
						}*/
						if(!data.PlayerGSSizeOk(p.getUniqueId())){
							p.sendMessage(plugin.namespace + ChatColor.RED + "Jedes Grundst�ck muss mind. 50*50 Bl�cke gro� sein, bevor du ein weiteres kaufen kannst!");
							return true;
						}
						p.sendMessage(plugin.namespace + ChatColor.WHITE + "Dies ist dein " + String.valueOf(anzahlGS+1) + ". Grundst�ck.");
						//p.sendMessage(plugin.namespace + ChatColor.WHITE + "F�r dieses Grundst�ck werden dir 5000 Coins berechnet.");
						p.sendMessage(plugin.namespace + ChatColor.WHITE + "Erstelle Grundst�ck: " + gsname + " mit den Ma�en: 25*25.");
						if(!markGS(p, 25, 25, gsname)){
							p.sendMessage(plugin.namespace + ChatColor.RED + "Dein Grundst�ck schneidet ein anderes Grundsrt�ck!");
							p.sendMessage(plugin.namespace + ChatColor.RED + "Vorgang abgebrochen");
							return true;
						}
						data.newGS((int)p.getLocation().getX(), (int)p.getLocation().getY(), (int)p.getLocation().getZ(), gsname, p, 25, 25);
						p.sendMessage(plugin.namespace + ChatColor.WHITE + "Grundst�ck wurde erstellt.");
						p.sendMessage(plugin.namespace + ChatColor.WHITE + "Wir w�nschen dir weiterhin viel Spa�...");
						//econ.withdrawPlayer(p.getName(), 5000.0D);
						
						plugin.api.sendLog("[Epicraft - Grundst�ck] " + p.getName() + " hat sein " + String.valueOf(anzahlGS+1) + ". Grundst�ck erstellt");
						return true;
					}
					else{
						p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast schon ein Grundst�ck mit dem Namen: " + gsname + "!");
						return true;
					}
				}
			}
			else if(args[0].equalsIgnoreCase("warp")){ //gs warp SPIELERNAME
				if(!(p.hasPermission("epicraft.gs.team"))){
					p.sendMessage(plugin.error);
					plugin.api.sendLog("[Epicraft - Grundst�ck] " + p.getName() + " wollte auf den Warp-Befehl zugreifen!");
					return true;
				}
				String name = args[1];
				UUID targetUUID = plugin.uuid.getUUIDFromPlayer(name);
				//Hole UUID
				List<String> gs = data.getGSFromPlayer(targetUUID);
				if(gs.isEmpty()){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Spieler hat keine Grundst�cke!");
					return true;
				}
				Inventory inv = Bukkit.createInventory(null, 2*9, name + "'s Grundst�cke");
				int i = 0;
				map.put(p.getUniqueId(), name);
				for(String gsname : gs){
					ItemStack stack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
					ItemMeta meta = stack.getItemMeta();
					meta.setDisplayName("Teleport");
					List<String> list = new ArrayList<String>();
					list.add("Grundst�ck: " + gsname);
					meta.setLore(list);
					stack.setItemMeta(meta);
					list.clear();
					inv.setItem(i, stack);
					i++;
				}
				p.openInventory(inv);
				return true;
			}
			if(args[0].equalsIgnoreCase("show") || args[0].equalsIgnoreCase("zeige")){ //gs show SPIELERNAME
				if(!(p.hasPermission("epicraft.gs.team"))){
					p.sendMessage(plugin.error);
					plugin.api.sendLog("[Epicraft - Grundst�ck] " + p.getName() + " wollte auf den Show-Befehl zugreifen!");
					return true;
				}
				UUID targetUUID = plugin.uuid.getUUIDFromPlayer(args[1]);
				if(targetUUID == null){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Die UUID des Spielers konnte nicht gefunden werden!");
					return true;
				}
				data.showGSfromPlayer(p, targetUUID);
				return true;
			}
		}
		else if(args.length == 3){
			if(args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("delete")){ //gs del SPIELERNAME GSNAME
				if(!(p.hasPermission("epicraft.gs.team"))){
					p.sendMessage(plugin.error);
					plugin.api.sendLog("[Epicraft - Grundst�ck] " + p.getName() + " wollte auf den L�schen-Befehl zugreifen!");
					return true;
				}
				UUID targetUUID = plugin.uuid.getUUIDFromPlayer(args[1]);
				int iid = plugin.uuid.getIIDFromUUID(targetUUID);
				if(targetUUID == null){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Die UUID des Spielers konnte nicht gefunden werden!");
					return true;
				}
				String gsname = args[2];
				//Pr�fe ob Grundst�ck existiert
				if(data.hasPlayerGSwithName(targetUUID, gsname)){
					data.delGS(targetUUID, gsname);
					WorldGuardPlugin wgp = plugin.getWorldGuard();
					if(wgp != null){
						RegionManager rm = wgp.getRegionManager(Bukkit.getServer().getWorld(WORLD));
						rm.removeRegion(iid + "_" + gsname);
						p.sendMessage(plugin.namespace + ChatColor.WHITE + "Protection wurde entfernt");
					}
					else{
						p.sendMessage(plugin.namespace + ChatColor.RED + "Protection konnte nicht entfernt werden!");
						p.sendMessage(plugin.namespace + ChatColor.RED + "Bitte Region \"" + args[1] + "_" + gsname + "\" manuell entfernen");
					}
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Grundst�ck von " + args[1] + " wurde gel�scht.");
					plugin.api.sendLog("[Epicraft - Grundst�ck] " + p.getName() + " l�scht von " + args[1] + " das Grundst�ck: " + gsname);
					return true;
				}
				else{
					p.sendMessage(plugin.namespace + ChatColor.RED + args[1] + " hat kein Grundst�ck mit dem Namen: " + gsname + "!");
					return true;
				}
			}
		}
		else{
			p.sendMessage(plugin.namespace + ChatColor.RED + "/gs -> Bitte �berpr�fe deine Eingabe!");
			return true;
		}
		p.sendMessage(plugin.namespace + ChatColor.RED + "/gs -> Bitte �berpr�fe deine Eingabe!");
		return true;
	}
	
	//----------------------------------------------------------------------------------------------------------------------//
	
	private void starterKit(Player p){
		//p.sendMessage(this.plugin.namespace + ChatColor.WHITE + "Zahle Starterpack aus...");
		Location loc = p.getLocation();
		loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()).setType(Material.CHEST);
		p.teleport(new Location(loc.getWorld(), loc.getX(), loc.getY() + 1, loc.getZ()));
		Chest chest = ((Chest) loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()).getState());
		Inventory inv = chest.getInventory();
		inv.addItem(new ItemStack(Material.WOOD_AXE, 1));
		inv.addItem(new ItemStack(Material.WOOD_PICKAXE, 1));
		inv.addItem(new ItemStack(Material.WOOD_HOE, 1));
		inv.addItem(new ItemStack(Material.WOOD_SWORD, 1));
		inv.addItem(new ItemStack(Material.WOOD_SPADE, 1));
		inv.addItem(new ItemStack(Material.BREAD, 32));
		inv.addItem(new ItemStack(Material.TORCH, 10));
		inv.addItem(new ItemStack(Material.MINECART, 1));
		inv.addItem(new ItemStack(Material.FENCE, 191));
		inv.addItem(new ItemStack(Material.FENCE_GATE, 1));
		inv.addItem(new ItemStack(Material.WORKBENCH, 1));
		p.sendMessage(this.plugin.namespace + ChatColor.WHITE + "Du hast nun alles, was du f�r den Anfang brauchst!");
		p.sendMessage(plugin.namespace + ChatColor.WHITE + "Wir w�nschen dir viel Spa�...");
	}
	
	private boolean markGS(Player p, int groe�e_x, int groe�e_y, String gsname){
		byte n = 0x3; // Norden
		byte s = 0x2; // S�den
		byte w = 0x5; // Westen
		byte o = 0x4; // Osten
		int x = (int)p.getLocation().getX();
		int y = (int)p.getLocation().getZ();
		int z = (int)p.getLocation().getY();
		
		//Pr�fe jede Ecke des Grundst�ckes
		int myX = x - (groe�e_x / 2)+1;
		int myY = y + (groe�e_y / 2)-1;
		int myZ = getZonGround(myX, myY, z);
		if(isRegionOnLocation(myX, myZ, myY)){
			return false;
		}
		
		myX = x+(groe�e_x / 2);
		myY = y + (groe�e_y / 2)-1;
		myZ = getZonGround(myX, myY, z);
		if(isRegionOnLocation(myX, myZ, myY)){
			return false;
		}
		
		myX = x-(groe�e_x / 2)+1;
		myY = y - (groe�e_y / 2);
		myZ = getZonGround(myX, myY, z);
		if(isRegionOnLocation(myX, myZ, myY)){
			return false;
		}
		
		myX = x+(groe�e_x / 2);
		myY = y - (groe�e_y / 2);
		myZ = getZonGround(myX, myY, z);
		if(isRegionOnLocation(myX, myZ, myY)){
			return false;
		}
		
		x = (int)p.getLocation().getX();
		y = (int)p.getLocation().getZ();
		z = (int)p.getLocation().getY();
		
		int x1 = x - (groe�e_x / 2)+1;
		int y1 = y + (groe�e_y / 2)-1;
		int z1 = getZonGround(x1, y1, z);
		ecke(x1, y1, z1, Material.WOOD);
		ecke(x1, y1, z1+1, Material.WOOD);
		ecke(x1, y1, z1+2, Material.TORCH);
		Bukkit.getServer().getWorld(WORLD).getBlockAt(x1-1, z1+1, y1).setTypeIdAndData(68,o , false);
		setSign(p, (Sign)Bukkit.getServer().getWorld(WORLD).getBlockAt(x1-1, z1+1, y1).getState());
		Bukkit.getServer().getWorld(WORLD).getBlockAt(x1, z1+1, y1+1).setTypeIdAndData(68, n , false);
		setSign(p, (Sign)Bukkit.getServer().getWorld(WORLD).getBlockAt(x1, z1+1, y1+1).getState());
		createFirework(x1, y1, z1+2);
		createFirework(x1, y1, z1+2);
		BlockVector b1 = new BlockVector(x1, 0, y1);
		
		x1 = x+(groe�e_x / 2);
		y1 = y + (groe�e_y / 2)-1;
		z1 = getZonGround(x1, y1, z);
		ecke(x1, y1, z1, Material.WOOD);
		ecke(x1, y1, z1+1, Material.WOOD);
		ecke(x1, y1, z1+2, Material.TORCH);
		Bukkit.getServer().getWorld(WORLD).getBlockAt(x1+1, z1+1, y1).setTypeIdAndData(68,w , false);
		setSign(p, (Sign)Bukkit.getServer().getWorld(WORLD).getBlockAt(x1+1, z1+1, y1).getState());
		Bukkit.getServer().getWorld(WORLD).getBlockAt(x1, z1+1, y1+1).setTypeIdAndData(68,n , false);
		setSign(p, (Sign)Bukkit.getServer().getWorld(WORLD).getBlockAt(x1, z1+1, y1+1).getState());
		createFirework(x1, y1, z1+2);
		createFirework(x1, y1, z1+2);
		
		x1 = x-(groe�e_x / 2)+1;
		y1 = y - (groe�e_y / 2);
		z1 = getZonGround(x1, y1, z);
		ecke(x1, y1, z1, Material.WOOD);
		ecke(x1, y1, z1+1, Material.WOOD);
		ecke(x1, y1, z1+2, Material.TORCH);
		Bukkit.getServer().getWorld(WORLD).getBlockAt(x1-1, z1+1, y1).setTypeIdAndData(68,o , false);
		setSign(p, (Sign)Bukkit.getServer().getWorld(WORLD).getBlockAt(x1-1, z1+1, y1).getState());
		Bukkit.getServer().getWorld(WORLD).getBlockAt(x1, z1+1, y1-1).setTypeIdAndData(68,s , false);
		setSign(p, (Sign)Bukkit.getServer().getWorld(WORLD).getBlockAt(x1, z1+1, y1-1).getState());
		createFirework(x1, y1, z1+2);
		createFirework(x1, y1, z1+2);
		
		x1 = x+(groe�e_x / 2);
		y1 = y - (groe�e_y / 2);
		z1 = getZonGround(x1, y1, z);
		ecke(x1, y1, z1, Material.WOOD);
		ecke(x1, y1, z1+1, Material.WOOD);
		ecke(x1, y1, z1+2, Material.TORCH);
		Bukkit.getServer().getWorld(WORLD).getBlockAt(x1+1, z1+1, y1).setTypeIdAndData(68,w , false);
		setSign(p, (Sign)Bukkit.getServer().getWorld(WORLD).getBlockAt(x1+1, z1+1, y1).getState());
		Bukkit.getServer().getWorld(WORLD).getBlockAt(x1, z1+1, y1-1).setTypeIdAndData(68,s , false);
		setSign(p, (Sign)Bukkit.getServer().getWorld(WORLD).getBlockAt(x1, z1+1, y1-1).getState());
		createFirework(x1, y1, z1+2);
		createFirework(x1, y1, z1+2);
		BlockVector b2 = new BlockVector(x1, 256, y1);
		
		WorldGuardPlugin wgp = plugin.getWorldGuard();
		if(wgp != null){
			RegionManager rm = wgp.getRegionManager(p.getLocation().getWorld());
			ProtectedCuboidRegion pr = new ProtectedCuboidRegion((plugin.uuid.getIIDFromUUID(p.getUniqueId()) + "_" + gsname), b1, b2);
			DefaultDomain dd = new DefaultDomain();
			dd.addPlayer(p.getName());
			pr.setOwners(dd);
			rm.addRegion(pr);
			try{
	            rm.save();
	        }
	        catch (Exception exp)
	        { }
		}
		else{
			p.sendMessage(plugin.namespace + ChatColor.RED + "Grundst�ck konnte nicht gesichert werden!");
			p.sendMessage(plugin.namespace + ChatColor.RED + "Bitte wende dich an einen Teamler");
		}
		return true;
	}
	
	private void ecke(int x1, int y1, int z1, Material mat){
		Bukkit.getServer().getWorld(WORLD).getBlockAt(x1, z1, y1).setType(mat);
	}
	
	private int getZonGround(int x, int y, int z){
		int tmpZ = z+100;
		for( ; tmpZ >= 0 ; tmpZ--){
			Block block = Bukkit.getServer().getWorld(WORLD).getBlockAt(x, tmpZ, y);
			if(block.getType() != Material.AIR 
					&& block.getType() != Material.RED_ROSE 
					&& block.getType() != Material.YELLOW_FLOWER 
					&& block.getType() != Material.LONG_GRASS
					&& block.getType() != Material.CACTUS
					&& block.getType() != Material.LEAVES
					&& block.getType() != Material.LEAVES_2
					&& block.getType() != Material.DOUBLE_PLANT
					&& block.getType() != Material.SUGAR_CANE_BLOCK){
				return tmpZ+1;
			}
		}
		return z;
	}
	
	private void setSign(Player p, Sign sign){
		sign.setLine(0, "---------------");
		sign.setLine(1, "Grundst�ck von");
		sign.setLine(2, p.getName());
		sign.setLine(3, "---------------");
		sign.update();
	}
	
	private synchronized void createFirework(int x, int y, int z){
		Location loc = new Location(Bukkit.getServer().getWorld(WORLD), x, z, y);
		for(int i = 0 ; i < 2 ; i++){
			Firework fw = loc.getWorld().spawn(loc, Firework.class);
			FireworkMeta data = fw.getFireworkMeta();
		    data.addEffects(FireworkEffect.builder().withColor(Color.RED).with(Type.BALL_LARGE).build());
		    data.setPower(3);
		    fw.setFireworkMeta(data);
		}
	}
	
	@EventHandler
	public void onClickInventory(InventoryClickEvent e){
		Player p = (Player) e.getWhoClicked();
		if(!map.containsKey(p.getUniqueId()))
			return;
		if(e.getSlot() == e.getRawSlot()){
			ItemStack stack = e.getCurrentItem();
			if(stack.getType().equals(Material.SKULL_ITEM)){
				e.setCancelled(true);
				ItemMeta meta = stack.getItemMeta();
				List<String> list = meta.getLore();
				String gsname = list.get(0);
				gsname = gsname.replaceAll(" ", "");
				gsname = gsname.replaceAll(":", "");
				gsname = gsname.replaceAll("Grundst�ck", "");
				String name = map.get(p.getUniqueId());
				UUID targetUUID = plugin.uuid.getUUIDFromPlayer(name);
				p.closeInventory();
				if(!data.warpPlayerto(p, targetUUID, gsname)){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Es ist ein Fehler aufgetreten!");
					return;
				}
				p.setAllowFlight(true);
				p.setFlying(true);
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Du wurdest teleportiert!");
				plugin.api.sendLog("[Epicraft - Grundst�ck] " + p.getName() + " wurde zum Grundst�ck " + gsname + " von " + name + " teleportiert");
				map.remove(p.getUniqueId());
			}
		}
	}
	
	private boolean isRegionOnLocation(int x, int y, int z){
		Location loc = new Location(Bukkit.getServer().getWorld(WORLD), x, y, z);
		ApplicableRegionSet set = getWGSet(loc);
		if(set == null){
			return true;
		}
		for(ProtectedRegion rg : set){
			return true;
		}
		return false;
	}
	
	private ApplicableRegionSet getWGSet(Location loc) {
		WorldGuardPlugin wg = plugin.getWorldGuard();
		if (wg == null) {
			return null;
		}
		RegionManager rm = wg.getRegionManager(loc.getWorld());
		if (rm == null) {
			return null;
		}
		return rm.getApplicableRegions(loc);
	}
}
