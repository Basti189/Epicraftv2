package de.wolfsline.blocksecure;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;

import de.wolfsline.Epicraft.Epicraft;

public class BlockSecure_Block {
	
	private Epicraft plugin;

	private Location location;
	private List<UUID> member;
	private UUID owner;
	private boolean allowHopper;
	
	
	public BlockSecure_Block(Epicraft plugin, Location location, UUID owner, List<UUID> member, boolean allowHopper){
		this.plugin = plugin;
		this.location = location;
		this.owner = owner;
		this.member = member;
		this.allowHopper = allowHopper;
	}

	public Location getLocation() {
		return location;
	}

	public List<UUID> getMember() {
		return member;
	}

	public UUID getOwner() {
		return owner;
	}
	
	public World getWorld(){
		return this.location.getWorld();
	}

	public boolean isHopperAllow() {
		return allowHopper;
	}
}
