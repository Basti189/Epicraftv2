package de.wolfsline.helpClasses;

import java.io.Serializable;

import org.bukkit.Location;

public class userBlockLocation implements Serializable{
	private static final long serialVersionUID = -3198773738444185347L;
	
	private int x, y, z;
	private String playerName;
	public userBlockLocation(Location loc, String playerName){
		this.x = (int) loc.getX();
		this.y = (int) loc.getY();
		this.z = (int) loc.getZ();
		this.playerName = playerName;
	}
	
	public String equals(Location loc){
		if((int)loc.getX() == this.x && (int)loc.getY() == this.y && (int)loc.getZ() == this.z)
			return this.playerName;
		return null;
	}
}
