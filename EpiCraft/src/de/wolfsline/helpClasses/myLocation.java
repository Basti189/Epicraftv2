package de.wolfsline.helpClasses;

import java.io.Serializable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
 
public class myLocation implements Serializable {
    private static final long serialVersionUID = -8100514952085724461L;
 
    private String worldname;
    private int x;
    private int y;
    private int z;
 
    public myLocation(Location loc) {
        this.worldname = loc.getWorld().getName().toString();
        this.x = (int) loc.getX();
        this.y = (int) loc.getY();
        this.z = (int) loc.getZ();
    }
 
    public Location unpack() {
        Location location = new Location(Bukkit.getServer().getWorld(this.worldname), this.x, this.y, this.z);
        return location;
    }
    
    public boolean equals(Location loc){
    	int x = (int) loc.getX();
        int y = (int) loc.getY();
        int z = (int) loc.getZ();
        if((int)loc.getX() == this.x && (int)loc.getY() == this.y && (int)loc.getZ() == this.z && loc.getWorld().getName().toString().equalsIgnoreCase(this.worldname))
        	return true;
        return false;
    }
}