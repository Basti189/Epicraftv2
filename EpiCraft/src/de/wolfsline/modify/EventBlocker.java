package de.wolfsline.modify;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

import de.wolfsline.Epicraft.Epicraft;

public class EventBlocker implements CommandExecutor, Listener{

	private Epicraft plugin;
	
	private boolean mBlockRedstoneEvent = true;
	
	public EventBlocker(Epicraft plugin){
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!cs.hasPermission("epicraft.eventblocker")){
			cs.sendMessage(plugin.error);
			return true;
		}
		if(args[0].equalsIgnoreCase("BlockRedstoneEvent")){
			if(mBlockRedstoneEvent){
				mBlockRedstoneEvent = false;
				cs.sendMessage(plugin.namespace + ChatColor.RED + "RedstoneEvents werden nun blockiert");
				return true;
			}
			else{
				mBlockRedstoneEvent = true;
				cs.sendMessage(plugin.namespace + ChatColor.RED + "RedstoneEvents werden nun wieder erlaubt");
				return true;
			}
		}
		return false;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockRedstoneEvent(BlockRedstoneEvent event){
		Block block = event.getBlock();
		if(!mBlockRedstoneEvent)
			event.setNewCurrent(15);
	}

}
