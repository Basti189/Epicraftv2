package de.wolfsline.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.data.MySQL;
import de.wolfsline.helpClasses.EpicraftPlayer;

public class Settings implements CommandExecutor, Listener{
	
	private Epicraft plugin;
	private List<UUID> inInventory = new ArrayList<UUID>();

	public Settings(Epicraft plugin) {
		this.plugin = plugin;
		MySQL sql = plugin.getMySQL();
		sql.queryUpdate("CREATE TABLE IF NOT EXISTS Einstellungen (UUID VARCHAR(36), Eventnachrichten SMALLINT, Chatnachrichten SMALLINT, Chatzeit SMALLINT, Chatwelt SMALLINT, Systemnachrichten SMALLINT, Lebensanzeige SMALLINT, Berechtigung VARCHAR(30))");
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(p.hasPermission("epicraft.permission.gast") && (!p.isOp())){
			p.sendMessage(plugin.error);
			plugin.api.sendLog("[Epicraft - Einstellungen] " + p.getName() + " versucht auf die Einstellungen zuzugreifen");
			return true;
		}
		p.sendMessage(plugin.namespace + ChatColor.WHITE + "Einstellungen werden geöffnet");
		plugin.api.sendLog("[Epicraft - Einstellungen] " + p.getName() + " hat die Einstellungen geöffnet");
		
		openInv(p);
		return true;
	}
	
	
	private void openInv(Player p){
		int lines = 1;
		EpicraftPlayer player = plugin.pManager.getEpicraftPlayer(p.getUniqueId());
		if(player == null){
			p.sendMessage(plugin.namespace + ChatColor.RED + "Deine Einstellungen können nicht aufgerufen werden!");
			p.sendMessage(plugin.namespace + ChatColor.RED + "Besteht das Problem weiterhin, wende dich bitte an einen Teamler");
			return;
		}
		Inventory inv = Bukkit.createInventory(null, lines*9, "Einstellungen");
		ItemMeta meta = null;
		List<String> tmpList = null;
		ItemStack eventMessages = new ItemStack(Material.WOOL, 1, (short) 0);
		meta = eventMessages.getItemMeta();
		meta.setDisplayName("Eventnachrichten");
		tmpList = new ArrayList<String>();
		if(player.eventMessages)
			tmpList.add("Status: An");
		else
			tmpList.add("Status: Aus");
		meta.setLore(tmpList);
		eventMessages.setItemMeta(meta);
		
		ItemStack chatMessages = new ItemStack(Material.WOOL, 1, (short) 1);
    	meta = chatMessages.getItemMeta();
		meta.setDisplayName("Chatnachrichten");
		tmpList = new ArrayList<String>();
		if(player.chatMessages)
			tmpList.add("Status: An");
		else
			tmpList.add("Status: Aus");
		meta.setLore(tmpList);
		chatMessages.setItemMeta(meta);
		
		ItemStack chatTime = new ItemStack(Material.WOOL, 1, (short) 2);
    	meta = chatTime.getItemMeta();
		meta.setDisplayName("Uhrzeit im Chat");
		tmpList = new ArrayList<String>();
		if(player.chatTime)
			tmpList.add("Status: An");
		else
			tmpList.add("Status: Aus");
		meta.setLore(tmpList);
		chatTime.setItemMeta(meta);
		
		ItemStack chatWorld = new ItemStack(Material.WOOL, 1, (short) 4);
    	meta = chatWorld.getItemMeta();
		meta.setDisplayName("Welt im Chat");
		tmpList = new ArrayList<String>();
		if(player.chatWorld)
			tmpList.add("Status: An");
		else
			tmpList.add("Status: Aus");
		meta.setLore(tmpList);
		chatWorld.setItemMeta(meta);
		
		/*ItemStack moneyForVote = new ItemStack(Material.WOOL, 1, (short) 5);
    	meta = moneyForVote.getItemMeta();
		meta.setDisplayName("Coins fürs Voten");
		tmpList = new ArrayList<String>();
		if(player.moneyForVote)
			tmpList.add("Status: An");
		else
			tmpList.add("Status: Aus");
		meta.setLore(tmpList);
		moneyForVote.setItemMeta(meta);*/
		
		ItemStack healthbar = new ItemStack(Material.WOOL, 1, (short) 5);
    	meta = chatWorld.getItemMeta();
		meta.setDisplayName("Herzen über Mobs");
		tmpList = new ArrayList<String>();
		if(player.healthbar)
			tmpList.add("Status: An");
		else
			tmpList.add("Status: Aus");
		meta.setLore(tmpList);
		healthbar.setItemMeta(meta);
		
		inv.setItem(0, eventMessages);
		inv.setItem(1, chatMessages);
		inv.setItem(2, chatTime);
		inv.setItem(3, chatWorld);
		inv.setItem(4, healthbar);
		//inv.setItem(4, moneyForVote);
		/*for(int i = 5 ; i < 9 ; i++){
			ItemStack tmpStack = new ItemStack(Material.WOOL, 1, (short) i);
			ItemMeta tmpMeta = tmpStack.getItemMeta();
			tmpMeta.setDisplayName("< Keine Einstellung vorhanden >");
			tmpList = new ArrayList<String>();
			tmpList.add("Status: Unbekannt");
			tmpMeta.setLore(tmpList);
			tmpStack.setItemMeta(tmpMeta);
			inv.setItem(i, tmpStack);
		}*/
		this.inInventory.add(p.getUniqueId());
		p.openInventory(inv);
	}
	
	@EventHandler
	public void onQuitEvent(PlayerQuitEvent e){
		Player p = e.getPlayer();
		this.inInventory.remove(p.getUniqueId());
	}
	
	
	@EventHandler
	public void OnPickUpItem(PlayerPickupItemEvent event){
		if(inInventory.contains(event.getPlayer().getUniqueId()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent event){
		try{
			Player p = (Player) event.getWhoClicked();
			if(inInventory.contains(p.getUniqueId())){
				event.setCancelled(true);
				if(event.getSlot() == event.getRawSlot()){
					int slot = event.getSlot();
					ItemStack stack = event.getCurrentItem();
					ItemMeta meta = stack.getItemMeta();
					List<String> tmpList = new ArrayList<String>();
					EpicraftPlayer player = plugin.pManager.getEpicraftPlayer(p.getUniqueId());
					if(player == null){
						p.sendMessage(plugin.namespace + ChatColor.RED + "Deine Einstellungen können nicht verändert werden!");
						p.sendMessage(plugin.namespace + ChatColor.RED + "Besteht das Problem weiterhin, wende dich bitte an einen Teamler");
						return;
					}
					if(slot == 0){ //Eventnachrichten
						if(player.eventMessages){
							tmpList.add("Status: Aus");
							player.eventMessages = false;
							player.update();
							plugin.api.sendLog("[Epicraft - Einstellungen] " + p.getName() + " empfängt nun keine Eventnachrichten mehr");
						}
						else{
							tmpList.add("Status: An");
							player.eventMessages = true;
							player.update();
							plugin.api.sendLog("[Epicraft - Einstellungen] " + p.getName() + " empfängt nun wieder Eventnachrichten");
						}
					}
					else if(slot == 1){ //Chatnachrichten
						if(player.chatMessages){
							player.chatMessages = false;
							player.update();
							tmpList.add("Status: Aus");
							plugin.api.sendLog("[Epicraft - Einstellungen] " + p.getName() + " empfängt nun keine Chatnachrichten mehr");
						}
						else{
							player.chatMessages = true;
							player.update();
							tmpList.add("Status: An");
							plugin.api.sendLog("[Epicraft - Einstellungen] " + p.getName() + " empfängt nun wieder Chatnachrichten");
						}
					}
					else if(slot == 2){ //Uhrzeit im Chat
						if(player.chatTime){
							tmpList.add("Status: Aus");
							player.chatTime = false;
							player.update();
							plugin.api.sendLog("[Epicraft - Einstellungen] " + p.getName() + " bekommt nun keine Zeit im Chat angezeigt");
						}
						else{
							tmpList.add("Status: An");
							player.chatTime = true;
							player.update();
							plugin.api.sendLog("[Epicraft - Einstellungen] " + p.getName() + " bekommt nun wieder die Zeit im Chat angezeigt");
						}
					}
					else if(slot == 3){ //Welt im Chat
						if(player.chatWorld){
							tmpList.add("Status: Aus");
							player.chatWorld = false;
							player.update();
							plugin.api.sendLog("[Epicraft - Einstellungen] " + p.getName() + " bekommt nun keine Welt im Chat angezeigt");
						}
						else{
							tmpList.add("Status: An");
							player.chatWorld = true;
							player.update();
							plugin.api.sendLog("[Epicraft - Einstellungen] " + p.getName() + " bekommt nun wieder die Welt im Chat angezeigt");
						}
					}
					/*else if(slot == 4){//Geld fürs Voten
						if(player.moneyForVote){
							tmpList.add("Status: Aus");
							player.moneyForVote = false;
							player.update();
							plugin.api.sendLog("[Epicraft - Einstellungen] " + p.getName() + " bekommt nun keine Coins fürs Voten");
						}
						else{
							tmpList.add("Status: An");
							player.moneyForVote = true;
							player.update();
							plugin.api.sendLog("[Epicraft - Einstellungen] " + p.getName() + " bekommt nun wieder Coins fürs Voten");
						}
					}*/
					else if(slot == 4){//Lebensanzeige
						if(player.healthbar){
							tmpList.add("Status: Aus");
							player.healthbar = false;
							player.update();
							plugin.api.sendLog("[Epicraft - Einstellungen] " + p.getName() + " bekommt nicht mehr die Lebensanzeige angezeigt");
						}
						else{
							tmpList.add("Status: An");
							player.healthbar = true;
							player.update();
							plugin.api.sendLog("[Epicraft - Einstellungen] " + p.getName() + " bekommt die Lebensanzeige angezeigt");
						}
					}
					else{
						p.sendMessage(plugin.namespace + ChatColor.RED + "Keine weiteren Einstellungen vorhanden!");
					}
					meta.setLore(tmpList);
					stack.setItemMeta(meta);
				}
			}
		}
		catch(NullPointerException e){
			
		}
		catch(IndexOutOfBoundsException e){
			
		}
	}
	
	@EventHandler
	public void onInventoryCloseEvent(InventoryCloseEvent event){
		if(inInventory.contains(event.getPlayer().getUniqueId())){
			inInventory.remove(event.getPlayer().getUniqueId());
			plugin.api.sendLog("[Epicraft - Einstellungen] " + event.getPlayer().getName() + " hat die Einstellungen geschlossen");
		}
		
	}
}
