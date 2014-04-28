package de.wolfsline.ProtocolLib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.ProtocolLib.BlockChanger.Calculations;

public class MapChunkPacketAdapter extends PacketAdapter { //Wird benutzt bei PlayerMovement
	
	private Calculations calc;
	
	public MapChunkPacketAdapter(Epicraft plugin, PacketType mapChunk, Calculations calc) {
		super(plugin, mapChunk);
		this.calc = calc;
	}
	
	@Override
	public void onPacketSending(PacketEvent event){
		calc.translateMapChunk(event.getPacket(), event.getPlayer());
	}
}
