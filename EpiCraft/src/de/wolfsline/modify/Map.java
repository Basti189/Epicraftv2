package de.wolfsline.modify;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;

public class Map implements CommandExecutor, Listener{

	@Override
	public boolean onCommand(CommandSender cs, Command arg1cmd, String label, String[] args) {
		if(!cs.isOp())
			return true;
		Player p = (Player) cs;
		ItemStack[] is = p.getInventory().getContents();
		for (ItemStack i : is){
		if (i.getType() == Material.MAP){
		short d = i.getDurability();
		MapView map = Bukkit.getServer().getMap(d);
		 for (MapRenderer renderer : map.getRenderers()) {
	            map.removeRenderer(renderer);
	        }
		map.addRenderer(new MapRenderer() {
			
			@Override
			public void render(MapView map, MapCanvas canvas, Player p) {
				for (int x = 0; x < 128; ++x) {
		            for (int y = 0; y < 128; ++y) {
		                byte color = (byte) (4 + (x+y) * 52 / 256);
		                if (canvas.getBasePixel(x, y) == 0) {
		                    canvas.setPixel(x, y, color);
		                } else {
		                    canvas.setPixel(x, y, (byte) -1);
		                }
		            }
		        }
			}
		});
		p.sendMap(map);
		}
		
		return true;
	}
		return false;
	}
	@EventHandler
	 public void onMapInitialize(MapInitializeEvent event) {
        MapView map = event.getMap();
        //Bukkit.getServer().broadcastMessage("[MapListener] Map " + map.getId() + " initialized.");
        for (MapRenderer renderer : map.getRenderers()) {
            map.removeRenderer(renderer);
        }
        map.addRenderer(new MapRenderer() {
			@Override
			public void render(MapView map, MapCanvas canvas, Player p) {
				/*for (int x = 0; x < 128; ++x) {
		            for (int y = 0; y < 128; ++y) {
		                byte color = (byte) (4 + (x+y) * 52 / 256);
		                if (canvas.getBasePixel(x, y) == 0) {
		                    canvas.setPixel(x, y, color);
		                } else {
		                    canvas.setPixel(x, y, (byte) -1);
		                }
		            }
		        }*/
			canvas.drawText(0, 0, MinecraftFont.Font, "Hier koennte deine\nWerbung stehen\n" + p.getName());
			}
		});
        //event..sendMap(map);
    }
}
