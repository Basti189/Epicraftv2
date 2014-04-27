package ProtocolLib;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.nbt.NbtBase;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;

import de.wolfsline.Epicraft.Epicraft;

public class SkullPacketAdapter extends PacketAdapter {
	
	private Epicraft plugin;

	public SkullPacketAdapter(Epicraft plugin, PacketType tileEntityData) {
		super(plugin, tileEntityData);
		this.plugin = plugin;
	}
	
	@Override
	public void onPacketSending(PacketEvent event){
		PacketContainer packet = event.getPacket();
		Player p = event.getPlayer();
		StructureModifier<Integer> coords = packet.getIntegers();
		int x = coords.read(0), y = coords.read(1), z = coords.read(2);
		Block block = p.getWorld().getBlockAt(x, y, z);
		p.sendMessage(block.getType().toString());
		try{
			if(block.getType() == Material.SKULL){
				Skull skull = (Skull) block.getState();
				String owner = skull.getOwner();
				if(owner == null)
					return;
				if(owner.equals("spieler")){
					NbtBase<?> packetNbtModifier = packet.getNbtModifier().read(0);
					((NbtCompound)packetNbtModifier).put("Owner", ((NbtCompound)packetNbtModifier).getCompound("Owner").put("Name", p.getName()));
					((NbtCompound)packetNbtModifier).put("Owner", ((NbtCompound)packetNbtModifier).getCompound("Owner").put("Id", p.getUniqueId().toString()));
					packet.getNbtModifier().write(0, packetNbtModifier);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
