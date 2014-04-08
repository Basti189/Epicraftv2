package de.wolfsline.game;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import de.wolfsline.Epicraft.Epicraft;

public class AdvnetureInTheSky implements CommandExecutor, Listener{

	File file = new File("plugins/EpiCraft/", "AdvnetureInTheSky.yml");
	FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
	private Location[] chest;
	private List<String> listPlayer = new ArrayList<String>();
	private List<Location> listLoation = new ArrayList<Location>();
	private Location startLocation, lobby, signLocation;
	private Epicraft plugin;
	private boolean runGame = false;
	private int Task = 1000;
	private boolean setSign = false;
	HashMap<String, Integer[]> map = new HashMap<String, Integer[]>();
	
	public AdvnetureInTheSky(Epicraft plugin){
		this.plugin = plugin;
		chest = new Location[5];
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(!p.hasPermission("epicraft.sky.use")){
			p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast keinen Zugriff auf diesen Befehl!");
			return true;
		}
		if(args.length == 1){
			String command = args[0];
			if(command.equalsIgnoreCase("join")){
				checkStart(p);
				return true;
			}
			else if(command.equalsIgnoreCase("leave")){
				listPlayer.remove(p.getName());
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Du hast das Spiel verlassen!");
				for(String name : listPlayer){
					Player player = Bukkit.getServer().getPlayer(name);
					player.sendMessage(plugin.namespace + ChatColor.GOLD + p.getName() + " hat das Spiel verlassen");
					if(listPlayer.size() == 1 && runGame){
						String winner = listPlayer.get(0);
						portToLobby("Spiel zu Ende");
						listPlayer.clear();
						Player pWin = Bukkit.getServer().getPlayer(winner);
						pWin.sendMessage(plugin.namespace + ChatColor.GOLD + "Glückwunsch!" + ChatColor.WHITE + " Du hast gewonnen!");
						portToLobby("Spiel zu Ende");
						resetField();
						runGame = false;
					}
					else if(listPlayer.size() == 1){
						Bukkit.getScheduler().cancelTask(this.Task);
						p.teleport(lobby);
						p.sendMessage("Du hast das Spiel verlassen");
						runGame = false;
					}
				}
				return true;
			}
			else if(command.equalsIgnoreCase("create")){
				if(!p.hasPermission("epicraft.sky.create")){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast keinen Zugriff auf diesen Befehl!");
					return true;
				}
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Erstelle Spielfeld...");
				createGameField(p);	
				return true;
			}
			else if(command.equalsIgnoreCase("reset")){
				if(!p.hasPermission("epicraft.sky.create")){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast keinen Zugriff auf diesen Befehl!");
					return true;
				}
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Resete Spielfeld...");
				resetField();
				return true;
			}
			else if(command.equalsIgnoreCase("setLobby")){
				if(!p.hasPermission("epicraft.sky.create")){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast keinen Zugriff auf diesen Befehl!");
					return true;
				}
				lobby = p.getLocation();
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "SkyLobby wurde gesetzt!");
				return true;
			}
			else if(command.equalsIgnoreCase("setSign")){
				setSign = true;
			}
		}
		return false;
	}
	
	private void portToLobby(String msg) {
		for(String name : listPlayer){
			Player player = Bukkit.getServer().getPlayer(name);
			player.teleport(lobby);
			player.sendMessage(plugin.namespace + ChatColor.WHITE + msg);
		}
	}

	private void checkStart(Player p)
	{
		Sign sign = (Sign) signLocation.getBlock().getState();
		sign.setLine(0, ChatColor.DARK_GREEN + "[Beitreten]");
		sign.setLine(1, "Warte...");
		sign.setLine(3, ChatColor.DARK_GRAY + String.valueOf(listPlayer.size()) + "/4");
		sign.update();
		if(runGame){
			p.sendMessage(plugin.namespace + ChatColor.RED + "Es läuft gerade ein Spiel!");
			return;
		}
		else if(listPlayer.size() == 4){
			p.sendMessage(plugin.namespace + ChatColor.RED + "Diese Arena ist voll");
			return;
		}
		if(listPlayer.contains(p.getName())){
			p.sendMessage(plugin.namespace + ChatColor.RED + "Du bist dem Spiel bereits beigetreten!");
			return;
		}
		p.teleport(lobby);
		if(listPlayer.size() == 0){
			listPlayer.add(p.getName());
			p.sendMessage(plugin.namespace + ChatColor.WHITE + "Du bist dem Spiel beigetreten!");
			p.sendMessage(plugin.namespace + ChatColor.WHITE + "Es müssen mind. zwei Spieler beitreten..");
		}
		else if(listPlayer.size() == 1){
			p.sendMessage(plugin.namespace + ChatColor.WHITE + "Du bist dem Spiel beigetreten!");
			for(String name : listPlayer){
				Player player = Bukkit.getServer().getPlayer(name);
				player.sendMessage(plugin.namespace + ChatColor.GOLD + p.getName() + ChatColor.WHITE + " ist dem Spiel beigetreten");
			}
			listPlayer.add(p.getName());
			this.Task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){
				@Override
				public void run() {
					int i = 1;
					if(listPlayer.size() < 2){
						for(String name : listPlayer){
							Player player = Bukkit.getServer().getPlayer(name);
							player.sendMessage(plugin.namespace + ChatColor.RED + "Es sind nicht genügend Spieler in der Arena");
							Bukkit.getScheduler().cancelTask(Task);
							return;
						}
					}
					//resetField();
					for(String name : listPlayer){
						Player player = Bukkit.getServer().getPlayer(name);
						player.teleport(chest[i]);
						Integer[] tmp = {(int) chest[i].getX(), (int) chest[i].getY(), (int) chest[i].getZ()}; 
						map.put(player.getName(), tmp);
						i++;
					}
					runGame=true;
					for(String name : listPlayer){
						Player player = Bukkit.getServer().getPlayer(name);
						player.sendMessage(plugin.namespace + ChatColor.GOLD + "Spiel gestartet!");
					}
					Bukkit.getScheduler().cancelTask(Task);
				}
				
			}, 60*20L, 100L);
			for(String name : listPlayer){
				Player player = Bukkit.getServer().getPlayer(name);
				player.sendMessage(plugin.namespace + ChatColor.WHITE + "Spiel startet in 60 Sekunden");
			}
		}
		else if(listPlayer.size() >= 2){
			p.sendMessage(plugin.namespace + ChatColor.WHITE + "Du bist dem Spiel beigetreten!");
			p.sendMessage(plugin.namespace + ChatColor.WHITE + "Spiel startet in wenigen Sekunden!");
			for(String name : listPlayer){
				Player player = Bukkit.getServer().getPlayer(name);
				player.sendMessage(plugin.namespace + ChatColor.GOLD + p.getName() + ChatColor.WHITE + " ist dem Spiel beigetreten");
			}
			listPlayer.add(p.getName());
		}
	}	
	
	private void createGameField(Player p){
		if(p != null)
			startLocation = p.getLocation();
		if(startLocation == null)
			return;
		int x = (int) startLocation.getX();
		int y = (int) startLocation.getY(); // höhe
		int z = (int) startLocation.getZ();
		
		createPlayerIsland(x-40, y-3, z+40, 1); // an der stelle soll ne kiste hin
		createPlayerIsland(x+40, y-3, z+40, 2);
		createPlayerIsland(x-40, y-3, z-40, 3);
		createPlayerIsland(x+40, y-3, z-40, 4);
		createMiddleIsland(x, y-1, z);
		fillChest();
		
		
	}
	
	private void fillChest(){
		for(int i = 1 ; i < 5 ; i++){
			Location loc = chest[i];
			Chest chest = (Chest) loc.getBlock().getState();
			chest.getInventory().clear();
			chest.getInventory().addItem(new ItemStack(Material.ICE, 1));
			chest.getInventory().addItem(new ItemStack(Material.LAVA_BUCKET, 1));
			chest.getInventory().addItem(new ItemStack(Material.BREAD, 32));
			chest.getInventory().addItem(new ItemStack(Material.BOW, 1));
			chest.getInventory().addItem(new ItemStack(Material.ARROW, 32));
			chest.getInventory().addItem(new ItemStack(Material.LOG, 4));
		}
		Location loc = chest[0];
		Chest chest = (Chest)loc.getBlock().getState();
		chest.getInventory().clear();
		chest.getInventory().addItem(new ItemStack(Material.LAPIS_BLOCK, 1));
	}
	
	private void createPlayerIsland(int x, int y, int z, int count) {
		World w = Bukkit.getServer().getWorld("Survival");
		if(w == null){
			Bukkit.getServer().broadcastMessage("Fehler!");
			return;
		}
		chest[count] = new Location(w, x, y, z);
		chest[count].getBlock().setType(Material.CHEST);
		Location tmp = new Location(w, x, y+5, z);
		tmp.getBlock().setType(Material.WOOL);
		tmp.getBlock().setData((byte) count, true);
		y -= 1;
		for(int h = 0 ; h < 5 ; h++){
			for(int i = 0 ; i < 5 ; i++){
				for(int j = 0 ; j < 5 ; j++){
					Location loc = new Location(w, (x-2)+i, y-h, (z-2)+j);
					loc.getBlock().setType(Material.DIRT);
				}
			}
		}
	}

	private void createMiddleIsland(int x, int y, int z){
		World w = Bukkit.getServer().getWorld("Survival");
		chest[0] = new Location(w, x, y, z);
		chest[0].getBlock().setType(Material.CHEST);
		Location tmp = new Location(w, x, y+5, z);
		tmp.getBlock().setType(Material.GLOWSTONE);
		y -= 1;
		for(int i = 0 ; i < 5 ; i++){
			for(int j = 0 ; j < 5 ; j++){
				Location loc = new Location(w, (x-2)+i, y, (z-2)+j);
				loc.getBlock().setType(Material.WOOD);
			}
		}
		y -= 1;
		for(int i = 0 ; i < 7 ; i++){
			for(int j = 0 ; j < 7 ; j++){
				Location loc = new Location(w, (x-3)+i, y, (z-3)+j);
				loc.getBlock().setType(Material.WOOD);
			}
		}
		y -= 1;
		for(int i = 0 ; i < 17 ; i++){
			for(int j = 0 ; j < 17 ; j++){
				Location loc = new Location(w, (x-8)+i, y, (z-8)+j);
				loc.getBlock().setType(Material.LOG);
			}
		}
		for(int i = 0 ; i < 15 ; i++){
			for(int j = 0 ; j < 15 ; j++){
				Location loc = new Location(w, (x-7)+i, y, (z-7)+j);
				loc.getBlock().setType(Material.WOOD);
			}
		}
	}
	
	private void resetField(){
		Sign sign = (Sign) signLocation.getBlock().getState();
		sign.setLine(0, ChatColor.DARK_RED + "■■■■■■■■■■■■■■■■");
		sign.setLine(1, "Neustart...");
		sign.setLine(3, ChatColor.DARK_RED + "■■■■■■■■■■■■■■■■");;
		sign.update();
		for(Location loc : listLoation){
			loc.getBlock().setType(Material.AIR);
		}
		listLoation.clear();
		map.clear();
		createGameField(null);
		sign.setLine(0, ChatColor.DARK_GREEN + "[Beitreten]");
		sign.setLine(1, "Warte...");
		sign.setLine(3, ChatColor.DARK_GRAY + "0/4");
		sign.update();
	}
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event){
		Player p = event.getPlayer();
		if(setSign){
			signLocation = event.getClickedBlock().getLocation();
			p.sendMessage(plugin.namespace + ChatColor.WHITE + "Schild wurde gesetzt!");
			Sign sign = (Sign) signLocation.getBlock().getState();
			sign.setLine(0, ChatColor.DARK_GREEN + "[Beitreten]");
			sign.setLine(1, "Warte...");
			sign.setLine(3, ChatColor.DARK_GRAY + "0/4");
			sign.update();
			setSign = false;
		}
		if(event.getClickedBlock() instanceof Sign){
			if(event.getClickedBlock().getLocation().equals(signLocation)){
				Sign sign = (Sign) signLocation.getBlock().getState();
				String state = ChatColor.stripColor(sign.getLine(0));
				if(state.equalsIgnoreCase("[beitreten]")){
					checkStart(p);
				}
			}
		}
	}
	
	@EventHandler
	private void onPlaceBock(BlockPlaceEvent event){
		if(listPlayer.contains(event.getPlayer().getName())){
			if(event.getBlock().getType().equals(Material.LAPIS_BLOCK)){
				Location loc = event.getBlock().getLocation();
				Player p = event.getPlayer();
				for(Map.Entry<String, Integer[]> e : map.entrySet()){
					Player player = Bukkit.getServer().getPlayer(e.getKey());
					Integer[] co = e.getValue();
					Location tmpLocation = new Location(Bukkit.getWorld("Survival"), co[0], co[1], co[2]);
					if((int)tmpLocation.getX() == (int)loc.getX() && (int)tmpLocation.getY() == (int)loc.getY() && (int)tmpLocation.getZ() == (int)loc.getZ() && player.getName().equalsIgnoreCase(p.getName())){
						p.sendMessage(plugin.namespace + ChatColor.GOLD + "Glückwunsch!" + ChatColor.WHITE + " Du hast gewonnen!");
						portToLobby("Spiel zu Ende");
						listPlayer.remove(p.getName());
						for(String name : listPlayer){
							Player playerl = Bukkit.getServer().getPlayer(name);
							playerl.sendMessage(plugin.namespace + ChatColor.WHITE + "Du hast leider verloren.");
							playerl.sendMessage(plugin.namespace + ChatColor.GOLD + p.getName() + ChatColor.WHITE + " hat gewonnen!");
						}
						runGame = false;
						listPlayer.clear();
						resetField();
						return;
					}
				}
			}
			listLoation.add(event.getBlock().getLocation());
		}
	}
	
	@EventHandler
	private void onDeath(PlayerDeathEvent event){
		Player p = event.getEntity();
		if(listPlayer.contains(p.getName()) && runGame){
			listPlayer.remove(p.getName());
			for(String name : listPlayer){
				Player player = Bukkit.getServer().getPlayer(name);
				player.sendMessage(plugin.namespace + ChatColor.GOLD + p.getName() + ChatColor.WHITE + " ist ausgeschieden!");
			}
			if(listPlayer.size() == 1){
				String winner = listPlayer.get(0);
				portToLobby("Spiel zu Ende");
				listPlayer.clear();
				Player pWin = Bukkit.getServer().getPlayer(winner);
				pWin.sendMessage(plugin.namespace + ChatColor.GOLD + "Glückwunsch!" + ChatColor.WHITE + " Du hast gewonnen!");
				resetField();
				runGame = false;
				return;
			}
		}
	}
}
