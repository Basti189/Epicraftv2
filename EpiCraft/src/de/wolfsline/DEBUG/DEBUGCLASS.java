package de.wolfsline.DEBUG;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.data.MySQL;

public class DEBUGCLASS implements Listener, CommandExecutor{

	private Epicraft plugin;
	
	private List<tmpClass> ListOfBank = new ArrayList<tmpClass>();
	
	public DEBUGCLASS(Epicraft plugin){
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!cs.isOp())
			return true;
		Player p = (Player) cs;
		if(args.length == 0)
			return false;
		else if(args.length == 1){
			if(args[0].equalsIgnoreCase("info")){
				ItemStack stack = p.getItemInHand();
				String type = stack.getType().toString();
				String damage = String.valueOf(stack.getDurability());
				p.sendMessage("Typ: " + type);
				p.sendMessage("Durability: " + damage);
				for(Entry<Enchantment, Integer> myentry : stack.getEnchantments().entrySet()){
					Enchantment tmpE = myentry.getKey();
					int tmpI = myentry.getValue();
					p.sendMessage("Enchanment: " + tmpE.toString());
					p.sendMessage("Wert: " + String.valueOf(tmpI));
				}
				if(stack.getType() == Material.ENCHANTED_BOOK){
					p.sendMessage("Verzaubertes Buch!");
					EnchantmentStorageMeta meta = (EnchantmentStorageMeta) stack.getItemMeta();
					for(Entry<Enchantment, Integer> myentry : meta.getStoredEnchants().entrySet()){
						int ID = myentry.getKey().getId();
						int tmpI = myentry.getValue();
						p.sendMessage("Enchanment: " + ID);
						p.sendMessage("Stufe: " + String.valueOf(tmpI));
					}
				}
			}
			else if(args[0].equalsIgnoreCase("umzug")){
				MySQL sql = this.plugin.getMySQL();
				Connection conn = sql.getConnection();
				ResultSet rs = null;
				PreparedStatement st = null;
				int i = 0;
				try {
					st = conn.prepareStatement("SELECT * FROM banksystem");
					rs = st.executeQuery();
					
					while(rs.next()){
						i++;
						tmpClass tmpBank = new tmpClass(rs.getString(2), rs.getString(3), rs.getInt(4), rs.getInt(5));
						ListOfBank.add(tmpBank);
					}
				}
				catch(SQLException e){
					
				}
				finally{
					sql.closeRessources(rs, st);
				}
				p.sendMessage("Einträge: " + String.valueOf(i));
				sql.queryUpdate("CREATE TABLE IF NOT EXISTS `ep-Bank` (id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(16), itemTyp VARCHAR(25), data INT, amount INT, enchantment VARCHAR(30))");
				for(tmpClass tmpBank : ListOfBank){
					String update = "INSERT INTO `ep-Bank` (username, itemTyp, data, amount, enchantment) VALUES ('" + tmpBank.username + "', '" + tmpBank.itemTyp + "', '" + tmpBank.data + "', '" + tmpBank.amount + "', '')";
					sql.queryUpdate(update);
				}
			}
		}
		return false;
	}


}
