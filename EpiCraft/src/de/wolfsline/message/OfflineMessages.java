package de.wolfsline.message;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.helpClasses.myMessages;
import de.wolfsline.helpClasses.myPlayer;

public class OfflineMessages implements CommandExecutor, Listener{
	
	private Epicraft plugin;
	private List<String> list = new ArrayList<String>();
	
	public OfflineMessages(Epicraft plugin){
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(args.length >= 2){
			String receiver = args[0];
			String message = "";
			for(int i = 1 ; i < args.length ; i++){
				message += args[i] + " ";
			}
			Player p = Bukkit.getServer().getPlayer(receiver);
			if(p != null){
				cs.sendMessage(plugin.namespace + ChatColor.RED + "Der Spieler ist online. Bitte nutze /w <Spieler>");
				return true;
			}
			else{
				if(Bukkit.getServer().getOfflinePlayer(receiver).isBanned()){
					cs.sendMessage(plugin.namespace + ChatColor.RED + "Konnte nachricht nicht senden!");
					return true;
				}
					
				for(myPlayer player : this.plugin.player){
					if(player.username.equalsIgnoreCase(receiver)){
						player.msg.add(new myMessages(cs.getName(), message));
						cs.sendMessage(plugin.namespace + ChatColor.WHITE + "Nachricht wurde gesendet!");
						return true;
					}
				}
			}
		}
		else if(args.length == 0){
			if(!(cs instanceof Player)){
				cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
				return true;
			}
			Player p = (Player) cs;
			cs.sendMessage(plugin.namespace + ChatColor.WHITE + "Nachrichten werden abgerufen");
			Inventory inv = Bukkit.createInventory(null, 1*9, "EpiMail");
			for(myPlayer player : this.plugin.player){
				if(player.username.equalsIgnoreCase(cs.getName())){
					if(player.msg.isEmpty()){
						cs.sendMessage(plugin.namespace + ChatColor.WHITE + "Du hast keine Nachrichten");
						return true;
					}
					for(myMessages msg : player.msg){
						inv.addItem(createMessage(msg.sender, msg.message));
					}
					player.msg.clear();
					list.add(cs.getName());
					p.openInventory(inv);
					return true;
				}
			}
		}
		else{
			cs.sendMessage(plugin.namespace + ChatColor.RED + "Zu wenig Argumente");
		}
		return false;
	}
	
	private ItemStack createMessage(String sender, String message){
		ItemStack item = new ItemStack(Material.WALL_SIGN, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(sender);
		List<String> list = new ArrayList<String>();
		list.add(message);
		meta.setLore(list);
		item.setItemMeta(meta);
		return item;
		
	}
	
	@EventHandler
	public void onPlayerLoginEvent(PlayerLoginEvent event){
		for(myPlayer player : this.plugin.player){
			if(player.username.equalsIgnoreCase(event.getPlayer().getName())){
				if(!player.msg.isEmpty()){
					event.getPlayer().sendMessage(plugin.namespace + ChatColor.WHITE + "Du hast " + player.msg.size() + " ungelesene Nachricht(en)");
					return;
				}
				return;
			}
		}
		this.plugin.player.add(new myPlayer(event.getPlayer().getName()));
	}
	
	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent event){
		if(list.contains(event.getWhoClicked().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event){
		if(list.contains(event.getPlayer().getName()))
			list.remove(event.getPlayer().getName());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event){
		list.remove(event.getPlayer().getName());
	}
}