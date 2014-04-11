package de.wolfsline.teleport;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.wolfsline.Epicraft.Epicraft;

public class World implements CommandExecutor, Listener{
	
	private Epicraft plugin;
	
	public World(Epicraft plugin){
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(args.length == 0){
			String worldName = p.getLocation().getWorld().getName();
			p.sendMessage(plugin.namespace + ChatColor.WHITE + "Du befindest dich " + getNameOfWorld(worldName));
			return true;
		}
		if(args.length == 2){
			if(args[0].equalsIgnoreCase("tp")){
				if(!p.hasPermission("epicraft.world.change")){//Wenn permission nicht
					p.sendMessage(plugin.error);
					plugin.api.sendLog("[Epicraft - Welt] " + p.getName() + " hat versucht auf den TP-Befehl zuzugreifen!");
					return true;
				}
				String targetWorld = args[1];
				org.bukkit.World world = Bukkit.getServer().getWorld(targetWorld);
				if(world == null){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Die Welt \"" + targetWorld + "\" exsistiert nicht!");
					return true;
				}
				if(p.isInsideVehicle()){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Du kannst nicht teleportiert werden!");
					return true;
				}
				p.teleport(world.getSpawnLocation());
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Du wurdest auf die Welt \"" + targetWorld + "\" teleportiert!");
				plugin.api.sendLog("[Epicraft - Welt] " + p.getName() + " hat sich auf die Welt " + targetWorld + " teleportiert");
				return true;
			}
		}
		else if(args.length == 3){
			if(args[0].equalsIgnoreCase("tp")){
				if(!p.hasPermission("epicraft.world.change.other")){//Wenn permission nicht
					p.sendMessage(plugin.error);
					plugin.api.sendLog("[Epicraft - Welt] " + p.getName() + " hat versucht auf den TP-Befehl zuzugreifen!");
					return true;
				}
				String targetWorld = args[2];
				String targetPlayer = args[1];
				org.bukkit.World world = Bukkit.getServer().getWorld(targetWorld);
				Player player = Bukkit.getServer().getPlayer(targetPlayer);
				if(player == null){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Der Spieler ist nicht online!");
					return true;
				}
				if(world == null){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Die Welt \"" + targetWorld + "\" exsistiert nicht!");
					return true;
				}
				if(player.isInsideVehicle()){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Du kannst nicht teleportiert werden!");
					return true;
				}
				p.teleport(world.getSpawnLocation());
				p.sendMessage(plugin.namespace + ChatColor.WHITE + targetPlayer + " wurde auf die Welt \"" + targetWorld + "\" teleportiert!");
				player.sendMessage(plugin.namespace + ChatColor.WHITE + "Du wurdest auf die Welt \"" + targetWorld + "\" teleportiert!");
				plugin.api.sendLog("[Epicraft - Welt] " + p.getName() + " hat den Spieler " + targetPlayer + " auf die Welt " + targetWorld + " teleportiert");
				return true;
			}
		}
		p.sendMessage(plugin.namespace + ChatColor.RED + "/welt");
		return true;
	}
	
	//Listener
	//---------------------------------------------------------------------------------------------------------------//
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event){
		Player p = event.getPlayer();
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(event.getClickedBlock().getState() instanceof Sign){
				Sign sign = (Sign) event.getClickedBlock().getState();
				String line[] = sign.getLines();
				for(int i = 0 ; i < 4 ; i++ ){
					line[i] = ChatColor.stripColor(line[i]);
				}
				if(line[0].equals("[Welt]")){
					if(!p.hasPermission("epicraft.world.sign")){
						return;
					}
					String targetWorld = line[1];
					org.bukkit.World world = Bukkit.getServer().getWorld(targetWorld);
					if(world == null){
						p.sendMessage(plugin.namespace + ChatColor.RED + "Die Welt \"" + targetWorld + "\" exsistiert nicht!");
						return;
					}
					if(p.isInsideVehicle()){
						p.sendMessage(plugin.namespace + ChatColor.RED + "Du kannst nicht teleportiert werden!");
						return;
					}
					p.teleport(world.getSpawnLocation());
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Du wurdest auf die Welt \"" + targetWorld + "\" teleportiert!");
				}
			}
		}
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent event){
		Player p = event.getPlayer();
		String line = event.getLine(0);
		line = ChatColor.stripColor(line);
		if(line.equals("[Welt]")){
			if(p.hasPermission("epicraft.world.sign.create")){
				return;
			}
			p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast keine Berechtigung ein Weltenschild zu erstellen");
			plugin.api.sendLog("[Epicraft - Welt] " + p.getName() + " hat versucht ein Weltenschild zu erstellen");
			event.getBlock().breakNaturally();
		}
	}
	
	//Eigene Methoden
	//---------------------------------------------------------------------------------------------------------------//
	
	private String getNameOfWorld(String worldName){
		if(worldName.equalsIgnoreCase("world"))
			return "auf der Hauptwelt";
		return worldName;
	}

}
