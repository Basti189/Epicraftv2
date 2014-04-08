package de.wolfsline.system;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import de.wolfsline.Epicraft.Epicraft;

public class ShowSystemSign implements CommandExecutor, Listener{
	
	private Epicraft plugin;
	private Location ramLocation, timeLocation, SWorld, MWorld, NWorld, PWorld, cpuLocation, playerTodayLocation;
	private List<String> todayPlayer = new ArrayList<String>();
	private String whichBlock = "";
	private int welt = 0;
	private String date = "";
	
	public ShowSystemSign(Epicraft plugin){
		this.plugin = plugin;
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					try { Thread.sleep(1000); } catch (InterruptedException e) {}
					updateRamSign();
					updateTimeSign();
					updateWorldSign();
					//updateCPUSign();
					updatePlayerTodaySign();
				}
				
			}
		});
		thread.start();
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler");
			return true;
		}
		Player p = (Player) cs;
		if(!p.isOp()){
			p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast keinen Zugriff auf diesen Befehl!");
			return true;
		}
		if(args.length == 2){
			String block = args[0];
			String what = args[1];
			if(block.equalsIgnoreCase("sign")){
				if(what.equalsIgnoreCase("ram")){
					whichBlock = "RAM";
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Warte auf Schild für Ram!");
				}
				else if(what.equalsIgnoreCase("time")){
					whichBlock = "TIME";
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Warte auf Schild für Zeit!");
				}
				else if(what.equalsIgnoreCase("world")){
					welt = 0;
					whichBlock = "WORLD";
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Warte auf Schild für Survival!");
				}
				else if(what.equalsIgnoreCase("cpu")){
					whichBlock = "CPU";
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Warte auf Schild für CPU!");
				}
				else if(what.equalsIgnoreCase("player")){
					whichBlock = "PLAYER";
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Warte auf Schild für Spieler!");
				}
			}
			else{
				p.sendMessage(plugin.namespace + ChatColor.RED + block + " gibt es nicht");
				return true;
			}
		}
		else{
			p.sendMessage(plugin.namespace + ChatColor.RED + "Zu wenig argumente");
			return true;
		}
		return false;
	}
	
	private void updateRamSign(){
		if(ramLocation == null)
			return;
		if(!ramLocation.getChunk().isLoaded()){
			return;
		}
		long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		String used = String.valueOf(usedMemory / 1024 / 1024);
		String max = String.valueOf(Runtime.getRuntime().maxMemory() / 1024 / 1024);
		Sign ramSign = (Sign) Bukkit.getServer().getWorld(ramLocation.getWorld().getName()).getBlockAt(ramLocation).getState();
		if(ramSign == null){
			ramLocation = null;
			return;
		}
		ramSign.setLine(0, "In Benutzung:");
		ramSign.setLine(1, used + " MB");
		ramSign.setLine(2, "Zur Verfügung:");
		ramSign.setLine(3, max + " MB");
		ramSign.update();
	}
	
	private void updateTimeSign(){
		if(timeLocation == null)
			return;
		if(!timeLocation.getChunk().isLoaded()){
			return;
		}
		Sign timeSign = (Sign) Bukkit.getServer().getWorld(timeLocation.getWorld().getName()).getBlockAt(timeLocation).getState();
		if(timeSign == null){
			timeLocation = null;
			Bukkit.broadcastMessage("TimeSchild gelöscht");
			return;
		}
		SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
		SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy");
		Date currentTime = new Date();
		timeSign.setLine(1, time.format(currentTime));
		timeSign.setLine(2, date.format(currentTime));
		timeSign.update();
		if(!this.date.equalsIgnoreCase(date.format(currentTime))){
			this.date = date.format(currentTime);
			todayPlayer.clear();
		}
	}
	
	private void updateWorldSign(){
		if(SWorld == null || MWorld == null || NWorld == null || PWorld == null)
			return;
		int PlayerInSWorld = 0;
		int PlayerInMWorld = 0;
		int PlayerInNWorld = 0;
		int PlayerInPWorld = 0;
		for(Player p : Bukkit.getServer().getOnlinePlayers()){
			String world = p.getLocation().getWorld().getName();
			if(world.equalsIgnoreCase("survival"))
				PlayerInSWorld++;
			else if(world.equalsIgnoreCase("minemap"))
				PlayerInMWorld++;
			else if(world.equalsIgnoreCase("survival_nether"))
				PlayerInNWorld++;
			else if(world.equalsIgnoreCase("plots"))
				PlayerInPWorld++;
		}
		Sign sign = (Sign) Bukkit.getServer().getWorld(SWorld.getWorld().getName()).getBlockAt(SWorld).getState();
		sign.setLine(0, "Survival");
		sign.setLine(2, String.valueOf(PlayerInSWorld) + " Spieler");
		sign.update();
		
		sign = (Sign) Bukkit.getServer().getWorld(MWorld.getWorld().getName()).getBlockAt(MWorld).getState();
		sign.setLine(0, "Minemap");
		sign.setLine(2, String.valueOf(PlayerInMWorld) + " Spieler");
		sign.update();
		
		sign = (Sign) Bukkit.getServer().getWorld(NWorld.getWorld().getName()).getBlockAt(NWorld).getState();
		sign.setLine(0, "Nether");
		sign.setLine(2, String.valueOf(PlayerInNWorld) + " Spieler");
		sign.update();
		
		sign = (Sign) Bukkit.getServer().getWorld(PWorld.getWorld().getName()).getBlockAt(PWorld).getState();
		sign.setLine(0, "Plots");
		sign.setLine(2, String.valueOf(PlayerInPWorld) + " Spieler");
		sign.update();
	}

	private void updateCPUSign(){
		if(cpuLocation == null)
			return;
		Sign sign = (Sign) Bukkit.getServer().getWorld(cpuLocation.getWorld().getName()).getBlockAt(cpuLocation).getState();
		String usage = String.valueOf(getCPUUsage());
		sign.setLine(0, usage);
		sign.update();
		
	}
	
	private double getCPUUsage(){
		OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
	    return operatingSystemMXBean.getSystemLoadAverage();
	}
	
	private void updatePlayerTodaySign(){
		if(playerTodayLocation == null)
			return;
		int count = todayPlayer.size();
		Sign sign = (Sign) Bukkit.getServer().getWorld(playerTodayLocation.getWorld().getName()).getBlockAt(playerTodayLocation).getState();
		sign.setLine(0, ">> Statistik <<");
		if(count != 1)
			sign.setLine(1, "Heute waren");
		else
			sign.setLine(1, "Heute war");
		sign.setLine(2, String.valueOf(count) + " Spieler");
		sign.setLine(3, ChatColor.DARK_GREEN + "online");
		sign.update();
	}
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event){
		if(!event.getPlayer().isOp())
			return;
		if(!(event.getClickedBlock() instanceof Block))
			return;
		if(event.getClickedBlock().getType() == Material.SIGN || event.getClickedBlock().getType() == Material.WALL_SIGN || event.getClickedBlock().getType() == Material.SIGN_POST){
			Player p = event.getPlayer();
			if(whichBlock == "RAM"){
				ramLocation = event.getClickedBlock().getLocation();
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "RAMSchild wurde gesetzt!");
				whichBlock = "";
			}
			else if(whichBlock == "TIME"){
				timeLocation = event.getClickedBlock().getLocation();
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Zeitschild wurde gesetzt!");
				whichBlock = "";
			}
			else if(whichBlock == "CPU"){
				cpuLocation = event.getClickedBlock().getLocation();
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "CPUSchild wurde gesetzt!");
				whichBlock = "";
			}
			else if(whichBlock == "WORLD"){
				if(welt == 0){
					welt++;
					SWorld = event.getClickedBlock().getLocation();
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Warte auf Schild für Minemap!");
				}
				else if(welt == 1){
					welt++;
					MWorld = event.getClickedBlock().getLocation();
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Warte auf Schild für Nether!");
				}
				else if(welt == 2){
					welt++;
					NWorld = event.getClickedBlock().getLocation();
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Warte auf Schild für Plots!");
				}
				else if(welt == 3){
					welt = 0;
					PWorld = event.getClickedBlock().getLocation();
					whichBlock = "";
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Weltschilder gesetzt!");
				}
			}
			else if(whichBlock == "PLAYER"){
				playerTodayLocation = event.getClickedBlock().getLocation();
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Spielerschild wurde gesetzt!");
				whichBlock = "";
			}
			
		}
	}
	
	@EventHandler
	public void onPlayerLoginEvent(PlayerLoginEvent event){
		String p = event.getPlayer().getName();
		if(!todayPlayer.contains(p))
			todayPlayer.add(p);
	}
}
