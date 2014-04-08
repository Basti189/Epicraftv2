package de.wolfsline.community;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.wolfsline.Epicraft.Epicraft;

public class Project implements CommandExecutor{
	
	private Epicraft plugin;
	
	public Project(Epicraft plugin){
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(!(p.hasPermission("epicraft.project.create") || p.isOp())){
			p.sendMessage(plugin.namespace + ChatColor.RED + "Du hats keinen Zugriff auf diesen Befehl!");
			return true;
		}
		if(!p.getLocation().getWorld().toString().equalsIgnoreCase("Survival")){
			p.sendMessage(plugin.namespace + ChatColor.RED + "Auf dieser Welt kˆnnen keine Groﬂprojekte erstellt werden!");
			return true;
		}
		if(args.length > 5){
			if(args[0].equalsIgnoreCase("neu") || args[0].equalsIgnoreCase("new")){
				
			}
		}
		else{
			p.sendMessage(plugin.namespace + ChatColor.RED + "Zu wenig Argumente!");
			return true;
		}
		return false;
	}

	private void markProject(Player p, int groeﬂe_x, int groeﬂe_y, String projectName){
		byte n = 0x3; // Norden
		byte s = 0x2; // S¸den
		byte w = 0x5; // Westen
		byte o = 0x4; // Osten
		int x = (int)p.getLocation().getX();
		int y = (int)p.getLocation().getZ();
		int z = (int)p.getLocation().getY();
		
		int x1 = x - (groeﬂe_x / 2)+1;
		int y1 = y + (groeﬂe_y / 2)-1;
		int z1 = z;
		ecke(x1, y1, z1, Material.WOOD);
		ecke(x1, y1, z1+1, Material.WOOD);
		ecke(x1, y1, z1+2, Material.TORCH);
		Bukkit.getServer().getWorld("Survival").getBlockAt(x1-1, z1+1, y1).setTypeIdAndData(68,o , false);
		setSign(projectName, (Sign)Bukkit.getServer().getWorld("Survival").getBlockAt(x1-1, z1+1, y1).getState());
		Bukkit.getServer().getWorld("Survival").getBlockAt(x1, z1+1, y1+1).setTypeIdAndData(68, n , false);
		setSign(projectName, (Sign)Bukkit.getServer().getWorld("Survival").getBlockAt(x1, z1+1, y1+1).getState());
		
		x1 = x+(groeﬂe_x / 2);
		y1 = y + (groeﬂe_y / 2)-1;
		z1 = z;
		ecke(x1, y1, z1, Material.WOOD);
		ecke(x1, y1, z1+1, Material.WOOD);
		ecke(x1, y1, z1+2, Material.TORCH);
		Bukkit.getServer().getWorld("Survival").getBlockAt(x1+1, z1+1, y1).setTypeIdAndData(68,w , false);
		setSign(projectName, (Sign)Bukkit.getServer().getWorld("Survival").getBlockAt(x1+1, z1+1, y1).getState());
		Bukkit.getServer().getWorld("Survival").getBlockAt(x1, z1+1, y1+1).setTypeIdAndData(68,n , false);
		setSign(projectName, (Sign)Bukkit.getServer().getWorld("Survival").getBlockAt(x1, z1+1, y1+1).getState());
		
		x1 = x-(groeﬂe_x / 2)+1;
		y1 = y - (groeﬂe_y / 2);
		z1 = z;
		ecke(x1, y1, z1, Material.WOOD);
		ecke(x1, y1, z1+1, Material.WOOD);
		ecke(x1, y1, z1+2, Material.TORCH);
		Bukkit.getServer().getWorld("Survival").getBlockAt(x1-1, z1+1, y1).setTypeIdAndData(68,o , false);
		setSign(projectName, (Sign)Bukkit.getServer().getWorld("Survival").getBlockAt(x1-1, z1+1, y1).getState());
		Bukkit.getServer().getWorld("Survival").getBlockAt(x1, z1+1, y1-1).setTypeIdAndData(68,s , false);
		setSign(projectName, (Sign)Bukkit.getServer().getWorld("Survival").getBlockAt(x1, z1+1, y1-1).getState());
		
		x1 = x+(groeﬂe_x / 2);
		y1 = y - (groeﬂe_y / 2);
		z1 = z;
		ecke(x1, y1, z1, Material.WOOD);
		ecke(x1, y1, z1+1, Material.WOOD);
		ecke(x1, y1, z1+2, Material.TORCH);
		Bukkit.getServer().getWorld("Survival").getBlockAt(x1+1, z1+1, y1).setTypeIdAndData(68,w , false);
		setSign(projectName, (Sign)Bukkit.getServer().getWorld("Survival").getBlockAt(x1+1, z1+1, y1).getState());
		Bukkit.getServer().getWorld("Survival").getBlockAt(x1, z1+1, y1-1).setTypeIdAndData(68,s , false);
		setSign(projectName, (Sign)Bukkit.getServer().getWorld("Survival").getBlockAt(x1, z1+1, y1-1).getState());
	}
	
	private void ecke(int x1, int y1, int z1, Material mat){
		Bukkit.getServer().getWorld("Survival").getBlockAt(x1, z1, y1).setType(mat);
	}
	
	private void setSign(String name, Sign sign){
		sign.setLine(0, "---------------");
		sign.setLine(1, "Groﬂprojekt");
		sign.setLine(2, name);
		sign.setLine(3, "---------------");
		sign.update();
	}

}
