package de.wolfsline.ProtocolLib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.ProtocolLib.BlockChanger.Calculations;

public class MapChunkBulkPacketAdapter extends PacketAdapter{
	
	private Calculations calc;
	
	public MapChunkBulkPacketAdapter(Epicraft plugin, PacketType mapChunkBulk, Calculations calc) {
		super(plugin, mapChunkBulk);
		this.calc = calc;
	}
	
	@Override
	public void onPacketSending(PacketEvent event){
		calc.translateMapChunkBulk(event.getPacket(), event.getPlayer());
	}

}
