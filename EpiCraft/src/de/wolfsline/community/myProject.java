package de.wolfsline.community;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class myProject {
	private String projectName;
	Location middle, e1, e3;
	public myProject(String projectName, Location middle, Location e1, Location e3){
		this.projectName = projectName;
		this.middle = middle;
		this.e1 = e1;
		this.e3 = e3;
	}
	
	public boolean isPlayerAtProject(Player p){
		int x = (int)p.getLocation().getX();
		int y = (int)p.getLocation().getZ();
		int z = (int)p.getLocation().getY();
		return false;
	}

}
