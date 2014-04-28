package ProtocolLib;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;

import de.wolfsline.Epicraft.Epicraft;

public class BlockPacketAdapter extends PacketAdapter{

	public BlockPacketAdapter(Epicraft plugin, PacketType blockChange) {
		super(plugin, blockChange);
	}
	
	@Override
	public void onPacketSending(PacketEvent event){
		PacketContainer packet = event.getPacket();
		Player p = event.getPlayer();
		//p.sendMessage("Sende Block");
		StructureModifier<Integer> coords = packet.getIntegers();
		int x = coords.read(0), y = coords.read(1), z = coords.read(2);
		/*if(!p.getName().equals("Basti189"))
			return;
		if(packet.getBlocks().read(0).getId() != 0){
			packet.getBlocks().write(0, Material.GLASS);
			p.sendMessage(String.valueOf(packet.getBlocks().read(0).getId()));
		}*/
	}

}
