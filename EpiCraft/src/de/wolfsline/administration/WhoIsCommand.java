package de.wolfsline.administration;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.helpClasses.JSONReader;

public class WhoIsCommand implements CommandExecutor{
	
	private Epicraft plugin;
	
	public WhoIsCommand(Epicraft plugin){
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(!p.hasPermission("epicraft.whois")){
			p.sendMessage(plugin.error);
			plugin.api.sendLog("[Epicraft - Whois] " + cs.getName() + " hat versucht auf den Befehl zuzugreifen");
			return true;
		}
		if(args.length == 1){
			p.sendMessage(plugin.namespace + ChatColor.WHITE + "Anfrage wird verarbeitet - Bitte warten...");
			final String who = args[0];
			final Player whoP = p;
			Player player = Bukkit.getServer().getPlayer(who);
			if(player != null){
				new Thread(new Runnable() {
					@Override
					public void run() {
						Player p = whoP;
						Player player = Bukkit.getServer().getPlayer(who);
						if(player != null){
							String country = "Unbekannt";
							String city = "Unbekannt";
							try {
								JSONObject jsonObject = JSONReader.readJsonFromUrl("http://freegeoip.net/json/" + player.getAddress().getHostString());
								country = (String) jsonObject.get("country_name");
								city = (String) jsonObject.get("city");
							} catch (IOException | ParseException e) {
							}
							
							long firstPlayed = player.getFirstPlayed();
							long lastPlayed = player.getLastPlayed();
							long timePlayed = player.getPlayerTime();
							Date dateFirstPlayed = new Date(firstPlayed);
							Date dateLastPlayed = new Date(lastPlayed);
							Date dateTimePlayed = new Date(timePlayed);
							
							SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.GERMANY);
							SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
							
							p.sendMessage(ChatColor.GOLD + "---------------[Information]---------------");
							p.sendMessage(ChatColor.GOLD + "Spieler: " + ChatColor.WHITE + player.getName());
							p.sendMessage(ChatColor.GOLD + "UUID: " + ChatColor.WHITE + player.getUniqueId());
							p.sendMessage(ChatColor.GOLD + "Welt: " + ChatColor.WHITE + player.getLocation().getWorld().getName());
							p.sendMessage(ChatColor.GOLD + "Position: " + ChatColor.WHITE + (int)p.getLocation().getX() + " " + (int)p.getLocation().getY() + " " + (int)p.getLocation().getZ());
							p.sendMessage(ChatColor.GOLD + "Erster Login: am " + ChatColor.WHITE + dateFormat.format(dateFirstPlayed) + ChatColor.GOLD + " um " + ChatColor.WHITE + timeFormat.format(dateFirstPlayed));
							p.sendMessage(ChatColor.GOLD + "Letzter Login: am " + ChatColor.WHITE + dateFormat.format(dateLastPlayed) + ChatColor.GOLD + " um " + ChatColor.WHITE + timeFormat.format(dateLastPlayed));
							p.sendMessage(ChatColor.GOLD + "Spielzeit: " + ChatColor.WHITE + timePlayed); //<-- Muss umgerechnet werden
							p.sendMessage(ChatColor.GOLD + "Standort: " + ChatColor.WHITE + country.replace("Germany", "Deutschland"));
							p.sendMessage(ChatColor.GOLD + "---------------[Information]---------------");
						}
					}
				}).start();
			}
			else{
				OfflinePlayer offPlayer = Bukkit.getServer().getOfflinePlayer(args[0]);
				if(!offPlayer.hasPlayedBefore()){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Dieser Spieler ist auf dem Server nicht bekannt!");
					return true;
				}
				SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.GERMANY);
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
				
				long firstPlayed = offPlayer.getFirstPlayed();
				long lastPlayed = offPlayer.getLastPlayed();
				Date dateFirstPlayed = new Date(firstPlayed);
				Date dateLastPlayed = new Date(lastPlayed);
				
				p.sendMessage(ChatColor.GOLD + "---------------[Information]---------------");
				p.sendMessage(ChatColor.GOLD + "Spieler: " + ChatColor.WHITE + offPlayer.getName());
				p.sendMessage(ChatColor.GOLD + "Erster Login: am " + ChatColor.WHITE + dateFormat.format(dateFirstPlayed) + ChatColor.GOLD + " um " + ChatColor.WHITE + timeFormat.format(dateFirstPlayed));
				p.sendMessage(ChatColor.GOLD + "Letzter Login: am " + ChatColor.WHITE + dateFormat.format(dateLastPlayed) + ChatColor.GOLD + " um " + ChatColor.WHITE + timeFormat.format(dateLastPlayed));
				p.sendMessage(ChatColor.GOLD + "Gebannt: " + ChatColor.WHITE + (offPlayer.isBanned() ? "Ja" : "Nein"));
				p.sendMessage(ChatColor.GOLD + "---------------[Information]---------------");	
			}
			return true;
		}
		else{
			p.sendMessage(plugin.namespace + ChatColor.RED + "/whois <spielername>");
			return true;
		}
	}
}