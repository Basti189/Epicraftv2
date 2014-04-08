package de.wolfsline.administration;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
			p.sendMessage("Anfrage wird verarbeitet - Bitte warten...");
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
							p.sendMessage(plugin.namespace + ChatColor.WHITE + "Informationen zum Spieler: " + player.getName());
							p.sendMessage("Welt: " + player.getLocation().getWorld().getName());
							p.sendMessage("Position: " + (int)p.getLocation().getX() + " " + (int)p.getLocation().getY() + " " + (int)p.getLocation().getZ());
							SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyy HH:mm:ss");
							SimpleDateFormat formater = new SimpleDateFormat("HH:mm:ss");
							long firstPlayed = player.getFirstPlayed();
							long lastPlayed = player.getLastPlayed();
							long timePlayed = player.getPlayerTime();
							Date dateFirstPlayed = new Date(firstPlayed);
							Date dateLastPlayed = new Date(lastPlayed);
							Date dateTimePlayed = new Date(timePlayed);
							p.sendMessage("Erster Login: " + formatter.format(dateFirstPlayed));
							p.sendMessage("Letzer Login: " + formatter.format(dateLastPlayed));
							//p.sendMessage("Akt. Spielzeit: " + formater.format(dateTimePlayed)); <-- Fixen!!
							p.sendMessage("Standort: " + country + " - " + city);
						}
					}
				}).start();
			}
			else{
				OfflinePlayer offPlayer = Bukkit.getServer().getOfflinePlayer(args[0]);
				if(!offPlayer.hasPlayedBefore()){
					p.sendMessage(ChatColor.RED + "Dieser Spieler ist auf dem Server nicht bekannt!");
					return true;
				}
				SimpleDateFormat formatter = new SimpleDateFormat("dd:MM:yyy HH:mm:ss");
				long firstPlayed = offPlayer.getFirstPlayed();
				long lastPlayed = offPlayer.getLastPlayed();
				Date dateFirstPlayed = new Date(firstPlayed);
				Date dateLastPlayed = new Date(lastPlayed);
				p.sendMessage("Erster Login: " + formatter.format(dateFirstPlayed));
				p.sendMessage("Letzer Login: " + formatter.format(dateLastPlayed));
				p.sendMessage("Gebannt: " + (offPlayer.isBanned() ? "Ja" : "Nein"));
				
			}
			return true;
		}
		else{
			p.sendMessage(plugin.namespace + ChatColor.RED + "/whois <spielername>");
			return true;
		}
	}
}