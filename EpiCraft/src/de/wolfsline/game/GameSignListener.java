package de.wolfsline.game;

import java.util.HashMap;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import de.wolfsline.Epicraft.Epicraft;

public class GameSignListener implements Listener{
	private Economy econ;
	private HashMap<String, Long> map;
	private Epicraft plugin;
	public GameSignListener(Epicraft plugin) {
		this.econ = plugin.economy;
		this.map = new HashMap<String,Long>();
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e){
		Player p = e.getPlayer();
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(e.getClickedBlock().getState() instanceof Sign){
				Sign s = (Sign) e.getClickedBlock().getState();
				if(s.getLine(0).equalsIgnoreCase("Belohnung:")){
					if(s.getLocation().getWorld() == Bukkit.getWorld("Games")){
						String money = s.getLine(1);
						if(map.containsKey(p.getName())){
							long time = map.get(p.getName());
							if(time+1000 >= System.currentTimeMillis()){
								p.sendMessage(plugin.namespace + ChatColor.RED + "War das ein Betrugsversuch?...");
								return;
							}
						}
						map.put(p.getName(), System.currentTimeMillis());
						money = money.replaceAll(" ", "");
						money = money.replaceAll("Coins", "");
						money = money.replaceAll("Coin", "");
						money = money.replaceAll("C", "");
						double coins = 0.0D;
						try{
							coins = Double.valueOf(money);
						}
						catch(NumberFormatException nfe){
							p.sendMessage(plugin.namespace + ChatColor.RED + "Es ist ein Fehler aufgetreten!\n" + plugin.namespace + ChatColor.WHITE + "Bitte einen Guard, Moderator oder Admin kontaktieren!");
						}
						if(coins >= 0.0D && coins <= 40.0D){
							this.econ.depositPlayer(p.getName(), coins);
							if(s.getLine(2).contains("w:")){
								String tmp = s.getLine(2);
								tmp = tmp.replaceAll("w:", "");
								Bukkit.getServer().dispatchCommand(p, "warp " + tmp);
							}
							else
								Bukkit.getServer().dispatchCommand(p, "spawn");
							p.sendMessage(plugin.namespace +  ChatColor.WHITE + "Dir wurden " + money + " Coins gutgeschrieben.");
						}
						else
							p.sendMessage(plugin.namespace + ChatColor.RED + "Der ausgewählte Betrag ist ungültig!");
					}
				}
			}
		}
	}

}
