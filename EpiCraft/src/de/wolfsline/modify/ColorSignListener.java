package de.wolfsline.modify;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class ColorSignListener implements Listener{
	
	@EventHandler
	public void onSignChange(SignChangeEvent event){
		Player p = event.getPlayer();
		if(p.hasPermission("epicraft.sign.color") || p.isOp()){
			for(int i = 0 ; i < 4 ; i++){
				String tmp = ChatColor.translateAlternateColorCodes('$', event.getLine(i).toString());
				if(tmp != null)
					event.setLine(i,tmp);
			}
			Sign sign = (Sign)event.getBlock().getState();
			sign.update();
		}
	}
}
