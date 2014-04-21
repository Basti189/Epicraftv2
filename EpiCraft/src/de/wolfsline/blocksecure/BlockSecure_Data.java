package de.wolfsline.blocksecure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.data.MySQL;

public class BlockSecure_Data {

	private Epicraft plugin;
	
	public BlockSecure_Data(Epicraft plugin){
		this.plugin = plugin;													//1					2					3					4				5					6		7		8
		plugin.getMySQL().queryUpdate("CREATE TABLE IF NOT EXISTS Blocksicherheit (UUID VARCHAR(36), Inhaber VARCHAR(6), Typ VARCHAR(20), Hopper VARCHAR(5), Welt VARCHAR(50), X INT, Y INT, Z INT)");
	}
	
	public BlockSecure_Block getBlockSecure(Block block){
		Location location = block.getLocation();
		List<UUID> member = new ArrayList<UUID>();
		UUID owner = null;
		boolean hopperAllow = false;
		MySQL sql = plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT * FROM Blocksicherheit WHERE Welt='" + location.getWorld().getName() + "' AND X='" + location.getBlockX() + "' AND Y='" + location.getBlockY() + "' AND Z='" + location.getBlockZ() + "'");
			rs = st.executeQuery();
			while(rs.next()){
				if(rs.getString(2).equals("Owner")){//Owner
					owner = UUID.fromString(rs.getString(1));
					if(rs.getString(4).equals("ALLOW")){
						hopperAllow = true;
					}
				}
				else{//Member
					member.add(UUID.fromString(rs.getString(1)));
				}
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			sql.closeRessources(rs, st);
			if(owner != null){
				return new BlockSecure_Block(this.plugin, location, owner, member, hopperAllow);
			}
		}
		return null;
	}
	
	public boolean secureBlock(UUID uuid, Block block, boolean isOwner){
		Location loc = block.getLocation();
		plugin.getMySQL().queryUpdate("INSERT INTO Blocksicherheit (UUID, Inhaber, Typ, Hopper, Welt, X, Y, Z) VALUES ('" + uuid + "', '" + getInhaber(isOwner) + "', '" + block.getType().toString() + "', 'DENY', '" + loc.getWorld().getName() + "', '" + loc.getBlockX() + "', '" + loc.getBlockY() + "', '" + loc.getBlockZ() + "')");
		Block doubleChest = isDoubleChest(block);
		if(doubleChest != null){
			loc = doubleChest.getLocation();
			plugin.getMySQL().queryUpdate("INSERT INTO Blocksicherheit (UUID, Inhaber, Typ, Hopper, Welt, X, Y, Z) VALUES ('" + uuid + "', '" + getInhaber(isOwner) + "', '" + doubleChest.getType().toString() + "', 'DENY', '" + loc.getWorld().getName() + "', '" + loc.getBlockX() + "', '" + loc.getBlockY() + "', '" + loc.getBlockZ() + "')");
		}
		return true;
	}
	
	public boolean unSecureBlock(UUID uuid, Block block){
		BlockSecure_Block secureBlock = getBlockSecure(block);
		if(secureBlock != null){
			if(secureBlock.getOwner().equals(uuid)){
				Location location = block.getLocation();
				plugin.getMySQL().queryUpdate("DELETE FROM Blocksicherheit WHERE Welt='" + location.getWorld().getName() + "' AND X='" + location.getBlockX() + "' AND Y='" + location.getBlockY() + "' AND Z='" + location.getBlockZ() + "'");
			}
			else{
				Location location = block.getLocation();
				plugin.getMySQL().queryUpdate("DELETE FROM Blocksicherheit WHERE UUID='" + uuid + "' AND Welt='" + location.getWorld().getName() + "' AND X='" + location.getBlockX() + "' AND Y='" + location.getBlockY() + "' AND Z='" + location.getBlockZ() + "'");
			}
		}
		return false;
	}
	
	public Block isDoubleChest(Block block){
		if(block.getType() == Material.CHEST){
			Location location = block.getLocation();

			location.setX(location.getX() + 1.0D);
			Block newBlock = location.getBlock();
			if(newBlock.getType() == Material.CHEST){
				return newBlock;
			}
			location.setX(location.getX() - 1.0D);
			
			location.setX(location.getX() - 1.0D);
			newBlock = location.getBlock();
			if(newBlock.getType() == Material.CHEST){
				return newBlock;
			}
			location.setX(location.getX() + 1.0D);
			
			location.setZ(location.getZ() + 1.0D);
			newBlock = location.getBlock();
			if(newBlock.getType() == Material.CHEST){
				return newBlock;
			}
			location.setZ(location.getZ() - 1.0D);
			
			location.setZ(location.getZ() - 1.0D);
			newBlock = location.getBlock();
			if(newBlock.getType() == Material.CHEST){
				Bukkit.getServer().broadcastMessage("Z - 1");
				return newBlock;
			}
			location.setZ(location.getZ() + 1.0D);
		}
		else if(block.getType() == Material.TRAPPED_CHEST){
			Location location = block.getLocation();

			location.setX(location.getX() + 1.0D);
			Block newBlock = location.getBlock();
			if(newBlock.getType() == Material.TRAPPED_CHEST){
				return newBlock;
			}
			
			location.setX(location.getX() - 2.0D);
			newBlock = location.getBlock();
			if(newBlock.getType() == Material.TRAPPED_CHEST){
				return newBlock;
			}
			
			location.setZ(location.getZ() + 1.0D);
			newBlock = location.getBlock();
			if(newBlock.getType() == Material.TRAPPED_CHEST){
				return newBlock;
			}
			
			location.setZ(location.getZ() - 2.0D);
			newBlock = location.getBlock();
			if(newBlock.getType() == Material.TRAPPED_CHEST){
				return newBlock;
			}
		}
		return null;
	}
	
	public boolean isBlockSave(Block block){
		Location location = block.getLocation();
		boolean isSave = false;
		MySQL sql = plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT Welt FROM Blocksicherheit WHERE Welt='" + location.getWorld().getName() + "' AND X='" + location.getBlockX() + "' AND Y='" + location.getBlockY() + "' AND Z='" + location.getBlockZ() + "'");
			rs = st.executeQuery();
			if(rs.next()){
				isSave = true;
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			sql.closeRessources(rs, st);
		}
		return isSave;
	}
	
	private String getInhaber(boolean isOwner){
		if(isOwner){
			return "Owner";
		}
		return "Member";
	}
}
