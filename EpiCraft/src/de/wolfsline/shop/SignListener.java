package de.wolfsline.shop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.data.MySQL;

public class SignListener implements Listener {

	private Economy econ;
	private Epicraft plugin;

	public SignListener(Epicraft plugin) {
		this.econ = plugin.economy;
		this.plugin = plugin;
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event){
		Player p = event.getPlayer();
		if(!event.getLine(0).equalsIgnoreCase("[Shop]")){
			return;
		}
		if(!(p.hasPermission("epicraft.shop.create") || p.isOp())){
			p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast keinen Zugriff auf das Shopsystem!");
			event.getBlock().breakNaturally();
		}
		String money = event.getLine(2);
		money = money.replaceAll(" ", "");
		money = money.replaceAll("Coins", "");
		money = money.replaceAll("Coin", "");
		money = money.replaceAll("C", "");
		String amount = money.substring(0, money.indexOf(":"));
		money = money.substring(money.indexOf(":")+1, money.length());
		String item = event.getLine(3);
		Double dprice = 0.0D;
		try{
			dprice = Double.valueOf(money);
		}
		catch(NumberFormatException nfe){
			p.sendMessage(plugin.namespace + ChatColor.RED + money + " ist keine gültige Zahl!");
			event.getBlock().breakNaturally();
			return;
		}
		Material m = Material.getMaterial(item.toUpperCase());
		if(m == null){
			p.sendMessage(plugin.namespace + ChatColor.RED + item + " gibt es nicht!");
			event.getBlock().breakNaturally();
			return;
		}
		event.setLine(1,p.getName());
		event.setLine(3, m.name());
		Sign sign = (Sign)event.getBlock().getState();
		sign.update();
	}
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event){
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(event.getClickedBlock().getState() instanceof Sign){
				Player p = event.getPlayer();
				Sign s = (Sign)event.getClickedBlock().getState();
				if(!s.getLine(0).equalsIgnoreCase("[Shop]")){
					return;
				}
				if(!(p.hasPermission("epicraft.shop.use") || p.isOp())){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast keinen Zugriff auf das Shopsystem!");
				}
				String shopowner = s.getLine(1);
				String money = s.getLine(2);
				money = money.replaceAll(" ", "");
				money = money.replaceAll("Coins", "");
				money = money.replaceAll("Coin", "");
				money = money.replaceAll("C", "");
				String amount = money.substring(0, money.indexOf(":"));
				money = money.substring(money.indexOf(":")+1, money.length());
				String item = s.getLine(3);
				item = item.replaceAll(ChatColor.RED.toString(), "");
				item = item.replaceAll(ChatColor.GREEN.toString(), "");
				double dprice = 0.0D;
				int iamount = 0;
				try{
					dprice = Double.valueOf(money);
					iamount = Integer.valueOf(amount);
					
				}
				catch(NumberFormatException nfe){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Bitte gültige Zahlen angeben!");
					return;
				}
				if(item.equalsIgnoreCase(ChatColor.RED + "Ausverkauft")){
					return;
				}
				Material m = Material.getMaterial(item.toUpperCase());
				if(m == null){
					p.sendMessage(plugin.namespace + ChatColor.RED + item + " gibt es nicht!");
					return;
				}
				if(!econ.has(p.getName(), dprice)){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast nicht genug Geld!");
				}
				ItemStack stack = new ItemStack(m, iamount);
				if((p.getInventory().getSize() + iamount) > 2304){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast nicht genug Platz in deinem Inventar!");
				}
				if(updateItemId(shopowner, item, iamount)){
					s.setLine(3, ChatColor.GREEN + item.toUpperCase());
					s.update();
					econ.withdrawPlayer(p.getName(), dprice);
					econ.depositPlayer(shopowner, dprice);
					p.getInventory().addItem(stack);
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Du hast " + amount + " " + item + " von " + shopowner + " gekauft");
				}
				else{
					p.sendMessage(plugin.namespace + ChatColor.RED + shopowner + " hat nicht genug Items auf Lager!");
					s.setLine(3, ChatColor.RED + item.toUpperCase());
					s.update();
				}
			}
		}
	}
	
	public boolean updateItemId(String name, String itemTyp, int amount){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT * FROM inventory WHERE username='" + name + "'");
			rs = st.executeQuery();
			while(rs.next()){
				if(rs.getString(3).equalsIgnoreCase(itemTyp)){
					if(rs.getInt(4) - amount < 0){
						sql.closeRessources(rs, st);
						return false;
					}
                    String update = "UPDATE inventory SET amount='" + String.valueOf(rs.getInt(4) - amount) + "' WHERE username='" + name + "' and itemTyp='" + itemTyp + "'";
                    sql.queryUpdate(update);
                    sql.closeRessources(rs, st);
                    return true;
				}
			}
			sql.closeRessources(rs, st);
		} 
		catch (SQLException e) {
			e.printStackTrace();
            return false;
		}
        return false;
	}
}
