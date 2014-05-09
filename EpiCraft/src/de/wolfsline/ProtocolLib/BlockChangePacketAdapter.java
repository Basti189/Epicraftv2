package de.wolfsline.ProtocolLib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.ProtocolLib.BlockChanger.Calculations;

public class BlockChangePacketAdapter extends PacketAdapter{

	private Calculations calc;
	
	public BlockChangePacketAdapter(Epicraft plugin, PacketType blockchange, Calculations calc){
		super(plugin, blockchange);
		this.calc = calc;
	}
	
	@Override
	public void onPacketSending(PacketEvent event){
		calc.translateBlockChange(event.getPacket(), event.getPlayer());
	}
	
}
