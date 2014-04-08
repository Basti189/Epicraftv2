package de.wolfsline.gs;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.wolfsline.Epicraft.Epicraft;


public class GSCommand implements CommandExecutor, Listener{

	private Economy econ;
	private Epicraft plugin;
	private Data data;
	private HashMap<String, String> map = new HashMap<String, String>();
	
	public GSCommand(Epicraft plugin){
		this.plugin = plugin;
		this.data = new Data(plugin);
		this.econ = plugin.economy;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		//Prüfe ob es ein Spieler ist
		if(!(cs instanceof Player)){
			cs.sendMessage(plugin.namespace + ChatColor.RED + "Der Befehl steht nur Spielern zur Verfügung!");
			return true;
		}
		Player p = (Player) cs;
		//Prüfe ob er die Permission hat
		if(!p.hasPermission("epicraft.gs.use")){
			p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast keinen Zugriff auf diesen Befehl!");
			plugin.api.sendLog("[Epicraft - Grundstück] " + p.getName() + " versucht auf den Grundstücks-Befehl zuzugreifen");
			return true;
		}
		//Prüfe ob der Spieler in der richtigen Welt ist
		if(!(p.getLocation().getWorld() == Bukkit.getServer().getWorld("Survival"))){
			p.sendMessage(plugin.namespace + ChatColor.RED + "Du kannst dir auf dieser Welt kein Grundstück anlegen!");
			plugin.api.sendLog("[Epicraft - Grundstück] " + p.getName() + " versucht auf der Welt " + p.getLocation().getWorld().getName() + " ein Grundstück anzulegen");
			return true;
		}
		//user
		if(args.length == 0){
			if(data.countGsFromPlayer(p) == 0){
				p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast keine Grundstücke!");
				return true;
			}
			data.showGSfromPlayer(p, p.getName());
			return true;
		}
		if(args.length == 2){
			if(args[0].equalsIgnoreCase("neu") || args[0].equalsIgnoreCase("new")){
				int anzahl = 0;
				String gsname = args[1];
				anzahl = data.countGsFromPlayer(p);
				if(anzahl == 0){ //1. GS
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Dies ist dein 1. Grundstück.");
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Für dieses Grundstück werden dir 0 Coins berechnet.");
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Erstelle Grundstück: " + gsname + " mit den Maßen: 50*50.");
					data.newGS((int)p.getLocation().getX(), (int)p.getLocation().getY(), (int)p.getLocation().getZ(), gsname, p.getName(), 50, 50);
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Grundstück wurde erstellt.");
					markGS(p, 50, 50);
					starterKit(p);
					plugin.api.sendLog("[Epicraft - Grundstück] " + p.getName() + " hat sein 1. Grundstück erstellt");
					return true;
				}
				else{//2. Gs oder höher
					if(!data.hasplayerGSwithName(p.getName(), gsname)){
						if(!econ.has(p.getName(), 5000)){
							p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast keine 5000 Coins!");
							return true;
						}
						p.sendMessage(plugin.namespace + ChatColor.WHITE + "Dies ist dein " + String.valueOf(anzahl+1) + ". Grundstück.");
						p.sendMessage(plugin.namespace + ChatColor.WHITE + "Für dieses Grundstück werden dir 5000 Coins berechnet.");
						p.sendMessage(plugin.namespace + ChatColor.WHITE + "Erstelle Grundstück: " + gsname + " mit den Maßen: 25*25.");
						data.newGS((int)p.getLocation().getX(), (int)p.getLocation().getY(), (int)p.getLocation().getZ(), gsname, p.getName(), 25, 25);
						p.sendMessage(plugin.namespace + ChatColor.WHITE + "Grundstück wurde erstellt.");
						p.sendMessage(plugin.namespace + ChatColor.WHITE + "Wir wünschen dir weiterhin viel Spaß...");
						econ.withdrawPlayer(p.getName(), 5000.0D);
						markGS(p, 25, 25);
						plugin.api.sendLog("[Epicraft - Grundstück] " + p.getName() + " hat sein " + String.valueOf(anzahl+1) + ". Grundstück erstellt");
						return true;
					}
					else{
						p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast schon ein Grundstück mit dem Namen: " + gsname + "!");
						return true;
					}
				}
			}
		}
		if(args.length == 4){
			if(args[0].equalsIgnoreCase("+")){
				int x = 0;
				int y = 0;
				String gsname = args[1];
				try{
					x = Integer.valueOf(args[2]);
					y = Integer.valueOf(args[3]);
				}
				catch(NumberFormatException nfe){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Bitte gib gültige Zahlen an!");
					return true;
				}
				if(x < 0 || y < 0){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Bitte gib nur positive Zahlen an!");
					return true;
				}
				if(!data.hasplayerGSwithName(p.getName(), gsname)){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast kein Grundstück mit dem Namen: " + gsname + "!");
					return true;
				}
				if(data.updateGS(p, gsname, x, y, this.econ, true)){
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Dein Grundstück " + gsname+ " wurde um " + x + "*" + y + " erweitert!");
					plugin.api.sendLog("[Epicraft - Grundstück] " + p.getName() + " hat sein Grundstück mit dem Namen " + gsname + " um " + x + "*" + y + " erweitert");
					return true;
				}
				else
				{
					return true;
				}
				
			}
			if(args[0].equalsIgnoreCase("price") || args[0].equalsIgnoreCase("preis")){
				int x = 0;
				int y = 0;
				String gsname = args[1];
				try{
					x = Integer.valueOf(args[2]);
					y = Integer.valueOf(args[3]);
				}
				catch(NumberFormatException nfe){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Bitte gib gültige Zahlen an!");
					return true;
				}
				if(x < 0 || y < 0){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Bitte gib nur positive Zahlen an!");
					return true;
				}
				if(!data.hasplayerGSwithName(p.getName(), gsname)){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast kein Grundstück mit dem Namen: " + gsname + "!");
					return true;
				}
				if(data.updateGS(p, gsname, x, y, this.econ, false)){
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Dein Grundstück " + gsname+ " wurde um " + x + "*" + y + " erweitert!");
					return true;
				}
				else{
					return true;
				}
				
			}
		}
		//Admiistration
		if(args.length == 2){
			if(!(p.hasPermission("epicraft.gs.remove") || p.isOp())){
				p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast keinen Zugriff auf diesen Befehl!");
				return true;
			}
			if(args[0].equalsIgnoreCase("show") || args[0].equalsIgnoreCase("zeige")){
				data.showGSfromPlayer(p, args[1]);
				return true;
			}
			else if(args[0].equalsIgnoreCase("warp")){
				String name = args[1];
				List<String> gs = data.getGSFromPlayer(name);
				if(gs.isEmpty()){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Spieler hat keine Grundstücke!");
					return true;
				}
				Inventory inv = Bukkit.createInventory(null, 2*9, name + "'s Grundstücke");
				int i = 0;
				map.put(p.getName(), name);
				for(String gsname : gs){
					ItemStack stack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
					ItemMeta meta = stack.getItemMeta();
					meta.setDisplayName("Teleport");
					List<String> list = new ArrayList<String>();
					list.add("Grundstück: " + gsname);
					meta.setLore(list);
					stack.setItemMeta(meta);
					list.clear();
					inv.setItem(i, stack);
					i++;
				}
				p.openInventory(inv);
				return true;
			}
		}
		if(args.length == 3){
			//Prüfe auf permissions
			if(!(p.hasPermission("epicraft.gs.remove") || p.isOp())){
				p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast keinen Zugriff auf diesen Befehl!");
				return true;
			}
			if(args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("delete")){
				String name = args[1];
				String gsname = args[2];
				//Prüfe ob Grundstück existiert
				if(data.hasplayerGSwithName(name, gsname)){
					data.delGS(name, gsname);
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Grundstück von " + name + " wurde gelöscht.");
					plugin.api.sendLog("[Epicraft - Grundstück] " + p.getName() + " löscht von " + name + " das Grundstück: " + gsname);
					return true;
				}
				else{
					p.sendMessage(plugin.namespace + ChatColor.RED + name + " hat kein Grundstück mit dem Namen: " + gsname + "!");
					return true;
				}
			}
		}
		return false;
	}
	
	private void markGS(Player p, int groeße_x, int groeße_y){
		byte n = 0x3; // Norden
		byte s = 0x2; // Süden
		byte w = 0x5; // Westen
		byte o = 0x4; // Osten
		int x = (int)p.getLocation().getX();
		int y = (int)p.getLocation().getZ();
		int z = (int)p.getLocation().getY();
		
		int x1 = x - (groeße_x / 2)+1;
		int y1 = y + (groeße_y / 2)-1;
		int z1 = getZonGround(x1, y1, z);
		ecke(x1, y1, z1, Material.WOOD);
		ecke(x1, y1, z1+1, Material.WOOD);
		ecke(x1, y1, z1+2, Material.TORCH);
		Bukkit.getServer().getWorld("Survival").getBlockAt(x1-1, z1+1, y1).setTypeIdAndData(68,o , false);
		setSign(p, (Sign)Bukkit.getServer().getWorld("Survival").getBlockAt(x1-1, z1+1, y1).getState());
		Bukkit.getServer().getWorld("Survival").getBlockAt(x1, z1+1, y1+1).setTypeIdAndData(68, n , false);
		setSign(p, (Sign)Bukkit.getServer().getWorld("Survival").getBlockAt(x1, z1+1, y1+1).getState());
		
		x1 = x+(groeße_x / 2);
		y1 = y + (groeße_y / 2)-1;
		z1 = getZonGround(x1, y1, z);
		ecke(x1, y1, z1, Material.WOOD);
		ecke(x1, y1, z1+1, Material.WOOD);
		ecke(x1, y1, z1+2, Material.TORCH);
		Bukkit.getServer().getWorld("Survival").getBlockAt(x1+1, z1+1, y1).setTypeIdAndData(68,w , false);
		setSign(p, (Sign)Bukkit.getServer().getWorld("Survival").getBlockAt(x1+1, z1+1, y1).getState());
		Bukkit.getServer().getWorld("Survival").getBlockAt(x1, z1+1, y1+1).setTypeIdAndData(68,n , false);
		setSign(p, (Sign)Bukkit.getServer().getWorld("Survival").getBlockAt(x1, z1+1, y1+1).getState());
		
		x1 = x-(groeße_x / 2)+1;
		y1 = y - (groeße_y / 2);
		z1 = getZonGround(x1, y1, z);
		ecke(x1, y1, z1, Material.WOOD);
		ecke(x1, y1, z1+1, Material.WOOD);
		ecke(x1, y1, z1+2, Material.TORCH);
		Bukkit.getServer().getWorld("Survival").getBlockAt(x1-1, z1+1, y1).setTypeIdAndData(68,o , false);
		setSign(p, (Sign)Bukkit.getServer().getWorld("Survival").getBlockAt(x1-1, z1+1, y1).getState());
		Bukkit.getServer().getWorld("Survival").getBlockAt(x1, z1+1, y1-1).setTypeIdAndData(68,s , false);
		setSign(p, (Sign)Bukkit.getServer().getWorld("Survival").getBlockAt(x1, z1+1, y1-1).getState());
		
		x1 = x+(groeße_x / 2);
		y1 = y - (groeße_y / 2);
		z1 = getZonGround(x1, y1, z);
		ecke(x1, y1, z1, Material.WOOD);
		ecke(x1, y1, z1+1, Material.WOOD);
		ecke(x1, y1, z1+2, Material.TORCH);
		Bukkit.getServer().getWorld("Survival").getBlockAt(x1+1, z1+1, y1).setTypeIdAndData(68,w , false);
		setSign(p, (Sign)Bukkit.getServer().getWorld("Survival").getBlockAt(x1+1, z1+1, y1).getState());
		Bukkit.getServer().getWorld("Survival").getBlockAt(x1, z1+1, y1-1).setTypeIdAndData(68,s , false);
		setSign(p, (Sign)Bukkit.getServer().getWorld("Survival").getBlockAt(x1, z1+1, y1-1).getState());
	}
	
	private void starterKit(Player p){
		p.sendMessage(this.plugin.namespace + ChatColor.WHITE + "Zahle Starterpack aus...");
		Inventory inv = p.getInventory();
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
		inv.addItem(new ItemStack(Material.SIGN, 24));
		inv.addItem(new ItemStack(Material.WORKBENCH, 1));
		p.sendMessage(this.plugin.namespace + ChatColor.WHITE + "Du hast nun alles, was du für den Anfang brauchst!");
		p.sendMessage(plugin.namespace + ChatColor.WHITE + "Wir wünschen dir viel Spaß...");
	}
	
	private void ecke(int x1, int y1, int z1, Material mat){
		Bukkit.getServer().getWorld("Survival").getBlockAt(x1, z1, y1).setType(mat);
	}
	
	private int getZonGround(int x, int y, int z){
		int tmpZ = z+100;
		for( ; tmpZ >= 0 ; tmpZ--){
			Block block = Bukkit.getServer().getWorld("Survival").getBlockAt(x, tmpZ, y);
			if(block.getType() != Material.AIR && block.getType() != Material.RED_ROSE && block.getType() != Material.YELLOW_FLOWER && block.getType() != Material.LONG_GRASS){
				return tmpZ+1;
			}
		}
		return z;
	}
	
	private void setSign(Player p, Sign sign){
		sign.setLine(0, "---------------");
		sign.setLine(1, "Grundstück von");
		sign.setLine(2, p.getName());
		sign.setLine(3, "---------------");
		sign.update();
	}

	@EventHandler
	public void onClickInventory(InventoryClickEvent e){
		Player p = (Player) e.getWhoClicked();
		if(!map.containsKey(p.getName()))
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
				gsname = gsname.replaceAll("Grundstück", "");
				String name = map.get(p.getName());
				p.closeInventory();
				if(!data.warpPlayerto(p, name, gsname)){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Es ist ein fehler aufgetreten!");
					return;
				}
				p.setAllowFlight(true);
				p.setFlying(true);
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Du wurdest teleportiert!");
				plugin.api.sendLog("[Epicraft - Grundstück] " + p.getName() + " wurde zum Grundstück " + gsname + " von " + name + " teleportiert");
				map.remove(p.getName());
			}
		}
	}

}