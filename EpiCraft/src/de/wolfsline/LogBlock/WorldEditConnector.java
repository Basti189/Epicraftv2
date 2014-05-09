package de.wolfsline.LogBlock;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

import de.wolfsline.Epicraft.Epicraft;

public class WorldEditConnector {

	private Epicraft plugin;
	
	private WorldEditPlugin wePlugin;
	
	public WorldEditConnector(Epicraft plugin){
		this.plugin = plugin;
		try{
			this.wePlugin = plugin.getWorldGuard().getWorldEdit();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public WorldEditPlugin getWorldEdit(){
		return this.wePlugin;
	}
	
	public Location[] getSelection(UUID targetUUID){
		Player player = Bukkit.getServer().getPlayer(targetUUID);
		if(player == null){
			return null;
		}
		if(wePlugin == null){
			return null;
		}
		Selection sel = wePlugin.getSelection(player);
		Location[] loc = new Location[2];
		loc[0] = sel.getMinimumPoint();
		loc[1] = sel.getMaximumPoint();
		return loc;
	}
	
	public Location[] getSelection(Player player){
		if(player == null){
			return null;
		}
		if(wePlugin == null){
			return null;
		}
		Selection sel = wePlugin.getSelection(player);
		Location[] loc = new Location[2];
		loc[0] = sel.getMinimumPoint();
		loc[1] = sel.getMaximumPoint();
		return loc;
	}
	
	public boolean isCoorInsideSelection(Player p, int x, int y, int z){
		Location[] loc = getSelection(p);
		if(loc[0] == null || loc[1] == null)
			return false;
		boolean isX = isVarBetweenVars(loc[0].getBlockX(), x, loc[1].getBlockX());
		boolean isY = isVarBetweenVars(loc[0].getBlockY(), y, loc[1].getBlockY());
		boolean isZ = isVarBetweenVars(loc[0].getBlockZ(), z, loc[1].getBlockZ());
		if(isX && isY && isZ){
			return true;
		}
		return false;
		
	}
	
	private boolean isVarBetweenVars(int arg1, int arg2, int arg3){
		if(arg1 > arg3){
			if(arg1 >= arg2 && arg2 >= arg3){
				
			}
			else{
				return false;
			}
		}
		else{
			if(arg3 >= arg2 && arg2 >= arg1){
				
			}
			else{
				return false;
			}
		}
		return true;
	}
	
}
