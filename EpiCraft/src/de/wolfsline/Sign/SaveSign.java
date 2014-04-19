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
			plugin.api.sendLog("[Epicraft - Schild] " + p.getName() + " wollte auf den Befehl zuzugreifen");
			return true;
		}
		if(args.length == 1){
			if(args[0].equalsIgnoreCase("kopieren")){
				map.put(p.getUniqueId().toString(), "kopieren");
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Bitte das Schild zum Kopieren anklicken");
				return true;
			}
			else if(args[0].equalsIgnoreCase("fertig")){
				map.put(p.getUniqueId().toString(), "fertig");
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Es wird nun kein Schild mehr verändert");
				return true;
			}
			else if(args[0].equalsIgnoreCase("löschen")){
				map.remove(p.getUniqueId().toString());
				map.remove(p.getUniqueId() + "_1");
				map.remove(p.getUniqueId() + "_2");
				map.remove(p.getUniqueId() + "_3");
				map.remove(p.getUniqueId() + "_4");
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Die Kopie wurde gelöscht");
				return true;
			}
			else if(args[0].equalsIgnoreCase("einfügen")){
				map.put(p.getUniqueId().toString(), "einfügen");
				p.sendMessage(plugin.namespace + ChatColor.RED + "Bitte Schilder zum Einfügen anklicken");
				return true;
			}
		}
		p.sendMessage(plugin.namespace + ChatColor.RED + "/savesign <kopieren>, <einfügen>, <fertig>, <löschen>");
		return true;
	}
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event){
		Player p = event.getPlayer();
		if(!(event.getClickedBlock() instanceof Block))
			return;
		if ((event.getClickedBlock().getType() == Material.WALL_SIGN || event.getClickedBlock().getType() == Material.SIGN_POST) && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Sign sign = (Sign) event.getClickedBlock().getState();
			if(map.containsKey(p.getUniqueId())){
				String state = map.get(p.getUniqueId());
				if(state.equals("fertig")){
					return;
				}
				else if(state.equals("kopieren")){
					map.put(p.getUniqueId() + "_1", sign.getLine(0));
					map.put(p.getUniqueId() + "_2", sign.getLine(1));
					map.put(p.getUniqueId() + "_3", sign.getLine(2));
					map.put(p.getUniqueId() + "_4", sign.getLine(3));
					map.put(p.getUniqueId().toString(), "fertig");
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Das Schild wurde kopiert");
				}
				else if(state.equals("einfügen")){
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
	}
}
