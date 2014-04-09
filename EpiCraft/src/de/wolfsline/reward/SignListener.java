package de.wolfsline.reward;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.data.MySQL;

public class SignListener implements Listener {

	private Epicraft plugin;

	public SignListener(Epicraft plugin) {
		this.plugin = plugin;
		MySQL sql = this.plugin.getMySQL();
		sql.queryUpdate("CREATE TABLE IF NOT EXISTS Events (Benutzername VARCHAR(16), Event VARCHAR(20), Zeit VARCHAR(10), Datum VARCHAR(10))");
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event){
		Player p = event.getPlayer();
		if(!(event.getLine(1).equalsIgnoreCase("Truhe") && event.getLine(2).equalsIgnoreCase("gefunden"))){
			return;
		}
		if(!(p.hasPermission("epicraft.reward.create") || p.isOp())){
			p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast keinen Zugriff auf das Belohnsystem!");
			event.getBlock().breakNaturally();
			return;
		}
		if(event.getLine(0).equalsIgnoreCase("")){
			p.sendMessage(plugin.namespace + ChatColor.RED + "Beim erstellen des Schildes ist ein Fehler aufgetreten");
			event.getBlock().breakNaturally();
			return;
		}
	}
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event){
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(event.getClickedBlock().getState() instanceof Sign){
				Player p = event.getPlayer();
				Sign s = (Sign)event.getClickedBlock().getState();
				if(!(s.getLine(1).equalsIgnoreCase("Truhe") && s.getLine(2).equalsIgnoreCase("gefunden"))){
					return;
				}
				if(!(p.hasPermission("epicraft.reward.use") || p.isOp())){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast keinen Zugriff auf das Belohnsystem!");
					return;
				}
				if(doPlayerEventBefor(p, s.getLine(0))){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast diese Truhe schon gefunden");
					return;
				}
				else{
					Potion potion = new Potion(randomPotion());
					ItemStack stack = new ItemStack(potion.toItemStack(1));
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Glückwunsch, du hast eine von vielen Truhen gefunden");
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Zur Belohnung bekommst du ein Geschenk");
					Bukkit.getServer().getWorld("Survival").dropItemNaturally(p.getLocation(), stack);
					if(p.isOp())
						return;
					newEvent(p, s.getLine(0));
				}
			}
		}
	}
	
	@SuppressWarnings("resource")
	private boolean doPlayerEventBefor(Player p, String event){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT Event FROM Events WHERE Benutzername='" + p.getName() + "'");
			rs = st.executeQuery();
			while(rs.next()){
				if(rs.getString(1).equalsIgnoreCase(event)){
					sql.closeRessources(rs, st);
					return true;
				}
			}
		} 
		catch (SQLException e) {
			return false;
		}
		finally{
			sql.closeRessources(rs, st);
		}
		return false;
	}
	
	private void newEvent(Player p, String event){
		MySQL sql = this.plugin.getMySQL();
		String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
		String date = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
		String update = "INSERT INTO Events (Benutzername, Event, Zeit, Datum) VALUES ('" + p.getName() + "', '" + event + "', '" + time + "', '" + date + "')";
		sql.queryUpdate(update);
	}
	
	private PotionType randomPotion(){
		Random rand = new Random();
		int zahl = rand.nextInt(11 - 1 + 1) + 1;
		switch(zahl){
			case 1: return PotionType.FIRE_RESISTANCE;
			case 2: return PotionType.INSTANT_DAMAGE;
			case 3: return PotionType.INSTANT_HEAL;
			case 4: return PotionType.INVISIBILITY;
			case 5: return PotionType.NIGHT_VISION;
			case 6: return PotionType.POISON;
			case 7: return PotionType.REGEN;
			case 8: return PotionType.SLOWNESS;
			case 9: return PotionType.SPEED;
			case 10: return PotionType.STRENGTH;
			case 11: return PotionType.WEAKNESS;
		}
		return PotionType.SPEED;
	}
}
