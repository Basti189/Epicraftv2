package de.wolfsline.ProtocolLib;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import de.wolfsline.Epicraft.Epicraft;

public class SoundPacketAdapter extends PacketAdapter{

	public SoundPacketAdapter(Epicraft plugin, PacketType namedSoundEffect) {
		super(plugin, namedSoundEffect);
	}
	
	@Override
	public void onPacketSending(PacketEvent event){
		PacketContainer packet = event.getPacket();
		Player p = event.getPlayer();
		String soundName = packet.getStrings().read(0);
		//Kann Sounds Modifizeren
		
	}

}
