package ProtocolLib;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import de.wolfsline.Epicraft.Epicraft;

public class SignPacketAdapter extends PacketAdapter{

	public SignPacketAdapter(Epicraft plugin, PacketType updateSign) {
		super(plugin, updateSign);
	}
	
	@Override
	public void onPacketSending(PacketEvent event){
		PacketContainer packet = event.getPacket();
		Player p = event.getPlayer();
		String[] array = packet.getStringArrays().getValues().get(0);
		for(int i = 0 ; i < 4 ; i++){
			array[i] = array[i].replace("[PLAYER]", p.getName());
		}
		packet.getStringArrays().write(0, array);
	}

}
