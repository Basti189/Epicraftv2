package de.wolfsline.Sign;

import java.util.HashMap;

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
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import de.wolfsline.Epicraft.Epicraft;

public class SaveSign implements CommandExecutor, Listener{
	
	private Epicraft plugin;
	private HashMap<String, String> map = new HashMap<String, String>();
	
	public SaveSign(Epicraft plugin){
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(!p.hasPermission("epicraft.sign.save")){
			p.sendMessage(plugin.error);
			return true;
		}
		if(args.length == 1){
			if(args[0].equalsIgnoreCase("fertig")){
				map.remove(p.getUniqueId() + "_1");
				map.remove(p.getUniqueId() + "_2");
				map.remove(p.getUniqueId() + "_3");
				map.remove(p.getUniqueId() + "_4");
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Schilder fertig bearbeitet");
				return true;
			}
		}
		else if(args.length > 1){
			String signLine = args[0];
			String msg = "";
			if(!(signLine.equals("1") || signLine.equals("2") || signLine.equals("3") || signLine.equals("4"))){
				p.sendMessage(plugin.namespace + ChatColor.RED + "Es sind maximal 4 Zeilen erlaubt!");
				return true;
			}
			for(int i = 1 ; i < args.length ; i++){
				msg += args[i] + " ";
			}
			if(msg.length() > 16){
				p.sendMessage(plugin.namespace + ChatColor.RED + "Es sind maximal 16 Zeichen erlaubt!");
				return true;
			}
			map.put(p.getUniqueId() + "_" + signLine, msg);
			p.sendMessage(plugin.namespace + ChatColor.WHITE + "Schildzeile[" + signLine + "] wurde gespeichert");
			return true;
		}
		return false;
	}
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event){
		Player p = event.getPlayer();
		if(!(event.getClickedBlock() instanceof Block))
			return;
		if ((event.getClickedBlock().getType() == Material.WALL_SIGN || event.getClickedBlock().getType() == Material.SIGN_POST) && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Sign sign = (Sign) event.getClickedBlock().getState();
			if(map.containsKey(p.getUniqueId() + "_1")){
				sign.setLine(0, ChatColor.translateAlternateColorCodes('$', map.get(p.getUniqueId() + "_1")));
			}
			if(map.containsKey(p.getUniqueId() + "_2")){
				sign.setLine(1, ChatColor.translateAlternateColorCodes('$', map.get(p.getUniqueId() + "_2")));			
			}
			if(map.containsKey(p.getUniqueId() + "_3")){
				sign.setLine(2, ChatColor.translateAlternateColorCodes('$', map.get(p.getUniqueId() + "_3")));;
			}
			if(map.containsKey(p.getUniqueId() + "_4")){
				sign.setLine(3, ChatColor.translateAlternateColorCodes('$', map.get(p.getUniqueId() + "_4")));
			}
			sign.update();
		}
	}
}
