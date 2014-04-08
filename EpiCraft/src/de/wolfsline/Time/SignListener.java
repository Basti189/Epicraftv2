package de.wolfsline.Time;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.wolfsline.Epicraft.Epicraft;

public class SignListener implements Listener{
	private Epicraft plugin;
	
	public SignListener(Epicraft plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event){
		Player p = event.getPlayer();
		if(event.getLine(0).equals("[Hexer]")){
			if(p.hasPermission("epicraft.sign.mod") || p.isOp()){
				String money = event.getLine(2);
				money = money.replaceAll(" ", "");
				money = money.replaceAll("Coins", "");
				money = money.replaceAll("Coin", "");
				money = money.replaceAll("C", "");
				double coins = 0.0D;
				try{
					coins = Double.valueOf(money);
				}
				catch(NumberFormatException nfe){
					event.setLine(2, ChatColor.DARK_RED + "Keine Zahl");
					Sign sign = (Sign)event.getBlock().getState();
					sign.update();
				}
				return;
			}
			if(!p.hasPermission("epicraft.sign.guard")){
				p.kickPlayer(ChatColor.RED + "Das wäre wohl zu einfach...");
			}
			event.getBlock().breakNaturally();
		}
	}
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e){
		Player p = e.getPlayer();
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(e.getClickedBlock().getState() instanceof Sign){
				Sign sign = (Sign) e.getClickedBlock().getState();
				if(sign.getLine(0).equals("[Hexer]")){
					String money = sign.getLine(2);
					money = money.replaceAll(" ", "");
					money = money.replaceAll("Coins", "");
					money = money.replaceAll("Coin", "");
					money = money.replaceAll("C", "");
					double coins = 0.0D;
					try{
						coins = Double.valueOf(money);
					}
					catch(NumberFormatException nfe){
						return;
					}
					if(!plugin.economy.has(p.getName(), coins)){
							p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast leider nicht genug Geld!");
							return;
					}
					this.plugin.economy.withdrawPlayer(p.getName(), coins);
					if(sign.getLine(1).equalsIgnoreCase("day") || sign.getLine(1).equalsIgnoreCase("tag")){
						Bukkit.broadcastMessage(ChatColor.BLUE + "[Hexer] " + p.getName() + ChatColor.WHITE + " Tag Spruch fehlt");
						Bukkit.broadcastMessage(ChatColor.BLUE + "[Hexer] " + p.getName() + ChatColor.WHITE + " Tag Spruch fehlt");
						plugin.api.sendLog("[Epicraft - Zeit] " + p.getName() + " hat die Zeit auf Tag gestellt");
						Bukkit.getServer().getWorld("Survival").setTime(0);
					}
					else if(sign.getLine(1).equalsIgnoreCase("night") || sign.getLine(1).equalsIgnoreCase("nacht")){
						Bukkit.broadcastMessage(ChatColor.BLUE + "[Hexer] " + p.getName() + ChatColor.WHITE + " Sonne, Mond und Sterne.");
						Bukkit.broadcastMessage(ChatColor.BLUE + "[Hexer] " + p.getName() + ChatColor.WHITE + " nun leuchtet die Laterne!");
						plugin.api.sendLog("[Epicraft - Zeit] " + p.getName() + " hat die Zeit auf Nacht gestellt");
						Bukkit.getServer().getWorld("Survival").setTime(12500);
					}
					else if(sign.getLine(1).equalsIgnoreCase("sun") || sign.getLine(1).equalsIgnoreCase("sonne")){
						Bukkit.broadcastMessage(ChatColor.BLUE + "[Hexer] " + p.getName() + ChatColor.WHITE + " Elemente Feuer, Erde, Wasser und Luft - Steht mir bei.");
						Bukkit.broadcastMessage(ChatColor.BLUE + "[Hexer] " + p.getName() + ChatColor.WHITE + " Sonnengott, schenk uns dein strahlendes Lächeln!");
						plugin.api.sendLog("[Epicraft - Wetter] " + p.getName() + " hat das Wetter auf Sonne gestellt");
						Bukkit.getServer().getWorld("Survival").setStorm(false);
					}
					else if(sign.getLine(1).equalsIgnoreCase("rain") || sign.getLine(1).equalsIgnoreCase("regen")){
						Bukkit.broadcastMessage(ChatColor.BLUE + "[Hexer] " + p.getName() + ChatColor.WHITE + " Elemente Feuer, Erde, Wasser und Luft - Steht mir bei.");
						Bukkit.broadcastMessage(ChatColor.BLUE + "[Hexer] " + p.getName() + ChatColor.WHITE + " Regengott, schenk uns deine Tränen!");
						plugin.api.sendLog("[Epicraft - Wetter] " + p.getName() + " hat das Wetter auf Regen gestellt");
						Bukkit.getServer().getWorld("Survival").setStorm(true);
					}
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Dir wurden dafür " + money + " Coins berechnet.");
					
				}
			}
		}
	}
}
