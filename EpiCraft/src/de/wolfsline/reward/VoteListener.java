package de.wolfsline.reward;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.data.MySQL;
import de.wolfsline.helpClasses.EpicraftPlayer;

public class VoteListener implements CommandExecutor, Listener{
	
	Epicraft plugin;
	Economy econ;
	
	public VoteListener(Epicraft plugin){
		this.plugin = plugin;
		this.econ = plugin.economy;
		
		MySQL sql = this.plugin.getMySQL();
		sql.queryUpdate("CREATE TABLE IF NOT EXISTS `ep-Vote` (id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(16), amount INT)");
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		cs.sendMessage(plugin.namespace + ChatColor.WHITE + "Vote für uns unter:");
		cs.sendMessage(plugin.namespace + ChatColor.WHITE + "http://vote.epicraft.de");
		return true;
	}

	@EventHandler
	public void onVotifierEvent(VotifierEvent event){
		Vote vote = event.getVote();
		Player p = Bukkit.getServer().getPlayer(vote.getUsername());
		EpicraftPlayer player = plugin.pManager.getEpicraftPlayer(vote.getUsername());
		if(p != null){
			p.sendMessage(plugin.namespace + ChatColor.WHITE + "Vielen Dank für deinen Vote!");
			if(player == null){
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Dir wurden 100 Coins gutgeschrieben!");
				econ.depositPlayer(p.getName(), 100.0D);
			}
			else{
				if(player.moneyForVote){
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Dir wurden 100 Coins gutgeschrieben!");
					econ.depositPlayer(p.getName(), 100.0D);
				}
			}
		}
		else{
			if(econ.hasAccount(vote.getUsername())){
				if(player != null){
					if(player.moneyForVote)
						econ.depositPlayer(vote.getUsername(), 100.0D);
				}
				else
					econ.depositPlayer(vote.getUsername(), 100.0D);
			}
				
		}
		this.plugin.api.sendLog("[Epicraft - Vote] " + vote.getUsername() + " hat einen Vote für den Server abgegeben");
		updateDatabase(vote.getUsername());
		//Bukkit.getServer().broadcastMessage(plugin.namespace + ChatColor.WHITE + vote.getUsername() + " hat für unseren Server einen Vote abgegeben!");
	}
	
	private void updateDatabase(String username){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT amount FROM `ep-Vote` WHERE username='" + username + "'");
			rs = st.executeQuery();
			if(rs.next()){//eintrag vorhanden?
				int amountInDatabase = rs.getInt(1);
				sql.closeRessources(rs, st);
				amountInDatabase++;
				String update = "UPDATE `ep-Vote` SET amount='" + String.valueOf(amountInDatabase) + "' WHERE username='" + username + "'";
                sql.queryUpdate(update);
			}
			else{
				sql.closeRessources(rs, st);
				String update = "INSERT INTO `ep-Vote` (username, amount) VALUES ('" + username + "', '1')";
				sql.queryUpdate(update);
			}
		}
		catch(SQLException e){
			
		}
	}
}
