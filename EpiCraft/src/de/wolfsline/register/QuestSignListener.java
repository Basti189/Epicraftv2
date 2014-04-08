package de.wolfsline.register;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.wolfsline.Epicraft.Epicraft;

public class QuestSignListener implements Listener{

	private Epicraft plugin;
	private HashMap<String, Integer> map;
	private Question qn;
	private String[] correctAnswer = {"a","c","b","d","b","c","a","d","b","d"};
	private String[] Questions = {"Frage1:","Frage2:","Frage3:","Frage4:","Frage5:","Frage6:","Frage7:","Frage8:","Frage9:","Frage10:"};
	private String[][] Answer;
	private Location start, raum;
	File file = new File("plugins/Epicraft/", "QuestSignLocation.yml");
	FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
	
	public QuestSignListener(Epicraft plugin) {
		this.plugin = plugin;
		this.map = new HashMap<String, Integer>();
		this.qn = new Question(plugin);
		
		String quest = "fragen.";
		for(int i = 0 ; i < 10 ; i++){
			cfg.addDefault(quest + "frage" + String.valueOf(i+1) + "." +  "frage", "Frage");
			cfg.addDefault(quest + "frage" + String.valueOf(i+1) + "." + "antwort." + "a", "");
			cfg.addDefault(quest + "frage" + String.valueOf(i+1) + "." + "antwort." + "b", "");
			cfg.addDefault(quest + "frage" + String.valueOf(i+1) + "." + "antwort." + "c", "");
			cfg.addDefault(quest + "frage" + String.valueOf(i+1) + "." + "antwort." + "d", "");
		}
		
		String pos = "raum.";
		cfg.addDefault(pos + "x", 0);
		cfg.addDefault(pos + "y", 0);
		cfg.addDefault(pos + "z", 0);
		cfg.addDefault(pos + "yaw", 0);
		cfg.addDefault(pos + "pitch", 0);
		
		String pos2 = "start.";
		cfg.addDefault(pos2 + "x", 0);
		cfg.addDefault(pos2 + "y", 0);
		cfg.addDefault(pos2 + "z", 0);
		cfg.addDefault(pos2 + "yaw", 0);
		cfg.addDefault(pos2 + "pitch", 0);
		cfg.options().copyDefaults(true);
		try {
			cfg.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Answer = new String[10][4];
		for(int i = 0 ; i < 10 ; i++){
			Questions[i] = cfg.getString(quest + "frage" + String.valueOf(i+1) + "." + "frage");
			Answer[i][0] = cfg.getString(quest + "frage" + String.valueOf(i+1) + "." + "antwort." + "a");
			Answer[i][1] = cfg.getString(quest + "frage" + String.valueOf(i+1) + "." + "antwort." + "b");
			Answer[i][2] = cfg.getString(quest + "frage" + String.valueOf(i+1) + "." + "antwort." + "c");
			Answer[i][3] = cfg.getString(quest + "frage" + String.valueOf(i+1) + "." + "antwort." + "d");
		}
		
		raum = new Location(Bukkit.getWorld("Survival"), cfg.getDouble(pos + "x"), cfg.getDouble(pos + "y"), cfg.getDouble(pos + "z"));
		raum.setPitch((float) cfg.getDouble(pos + "pitch"));
		raum.setYaw((float) cfg.getDouble(pos + "yaw"));
		
		start = new Location(Bukkit.getWorld("Survival"), cfg.getDouble(pos2 + "x"), cfg.getDouble(pos2 + "y"), cfg.getDouble(pos2 + "z"));
		start.setPitch((float) cfg.getDouble(pos2 + "pitch"));
		start.setYaw((float) cfg.getDouble(pos2 + "yaw"));
		
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent event){
		Player p = event.getPlayer();
		if(event.getLine(0).equals("[Start]") || event.getLine(0).equalsIgnoreCase("a")|| event.getLine(0).equalsIgnoreCase("b")|| event.getLine(0).equalsIgnoreCase("c")|| event.getLine(0).equalsIgnoreCase("d")){
			if(!p.hasPermission("epicraft.sign.guard")){
				event.getBlock().breakNaturally();
				p.kickPlayer(ChatColor.RED + "Nein...");
				plugin.api.sendLog("[Epicraft - Fragebogen] " + p.getName() + " hat versucht ein Fragebogenschild aufzustellen");
				return;
			}
			else if(!(p.hasPermission("epicraft.sign.mod") || p.isOp())){
				event.getBlock().breakNaturally(); //<-- Exception! Warum ?
				return;
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e){
		Player p = e.getPlayer();
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(e.getClickedBlock().getState() instanceof Sign){
				if(!p.hasPermission("epicraft.register.gast")){
					return;
				}
				Sign sign = (Sign) e.getClickedBlock().getState();
				String linie[] = sign.getLines();
				if(linie[0].equals("[Start]")){
					if(map.containsKey(p.getName())){
						map.remove(p.getName());
					}
					plugin.api.sendLog("[Epicraft - Fragebogen] " + p.getName() + " hat den Fragebogen gestartet");
					map.put(p.getName(), 1);
					qn.start(p);
					p.teleport(this.raum);
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Willkommen beim Test.");
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Ich hoffe, du hast die Regeln durchgelesen...");
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "");
					sendQuestion(p);
					return;
				}
				else if(linie[0].equalsIgnoreCase("a") || linie[0].equalsIgnoreCase("b") || linie[0].equalsIgnoreCase("c") || linie[0].equalsIgnoreCase("d")){
					if(map.containsKey(p.getName())){
						if(correctAnswer[map.get(p.getName())-1].equalsIgnoreCase(linie[0])){
							qn.update(p, map.get(p.getName()), true);
							plugin.api.sendLog("[Epicraft - Fragebogen] " + p.getName() + " hat Frage " + map.get(p.getName()) + " richtig beantwortet");
						}
						else{
							qn.update(p, map.get(p.getName()), false);
							plugin.api.sendLog("[Epicraft - Fragebogen] " + p.getName() + " hat Frage " + map.get(p.getName()) + " falsch beantwortet");
						}
						up(p);
					}
				}
			}
		}
	}
	
	private void sendQuestion(Player p){
		p.sendMessage(ChatColor.GOLD + "---------------[Frage " + map.get(p.getName()) + "]---------------");
		p.sendMessage(ChatColor.GOLD + "Frage: " + Questions[map.get(p.getName())-1]);
		p.sendMessage(ChatColor.GOLD + "Antwort a: " + Answer[map.get(p.getName())-1][0]);
		p.sendMessage(ChatColor.GOLD + "Antwort b: " + Answer[map.get(p.getName())-1][1]);
		p.sendMessage(ChatColor.GOLD + "Antwort c: " + Answer[map.get(p.getName())-1][2]);
		p.sendMessage(ChatColor.GOLD + "Antwort d: " + Answer[map.get(p.getName())-1][3]);
		p.sendMessage(ChatColor.GOLD + "---------------[Frage " + map.get(p.getName()) + "]---------------");
	}
	
	public void up(Player p){
		int i = map.get(p.getName());
		if(i == 10){
			if(qn.questionAllRight(p)){
				map.remove(p.getName());
				p.teleport(Bukkit.getServer().getWorld("Survival").getSpawnLocation());
				p.kickPlayer(ChatColor.WHITE + "Glückwunsch! Du hast alle Fragen richtig beantwortet\nDu gehörst nun zu den Spielern");
				Bukkit.getServer().broadcastMessage(plugin.namespace + ChatColor.WHITE + p.getName() + " gehört nun zu den Spielern!");
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + p.getName() + " group set Spieler");
				plugin.api.sendLog("[Epicraft - Fragebogen] " + p.getName() + " hat den Fragebogen bestanden und gehört nun zu den Spielern");
				return;
			}
			p.teleport(this.start);
			p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast mind. eine Frage falsch beantwortet");
			p.sendMessage(plugin.namespace + ChatColor.RED + "Bitte wiederhole den Test");
			plugin.api.sendLog("[Epicraft - Fragebogen] " + p.getName() + " hat den Fragebogen nicht bestande");
			map.remove(p.getName());
			return;
		}
		if(i < 10)
			i++;
		map.put(p.getName(), i);
		sendQuestion(p);
	}
	
	public void setStartLocation(Player p){
		Location loc = p.getLocation();
		this.start = loc;
		String pos = "start.";
		cfg.set(pos + "x", loc.getX());
		cfg.set(pos + "y", loc.getY());
		cfg.set(pos + "z", loc.getZ());
		cfg.set(pos + "pitch", loc.getPitch());
		cfg.set(pos + "yaw", loc.getYaw());
		try {
			this.cfg.save(file);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setRaumLocation(Player p){
		Location loc = p.getLocation();
		this.raum = loc;
		String pos = "raum.";
		cfg.set(pos + "x", loc.getX());
		cfg.set(pos + "y", loc.getY());
		cfg.set(pos + "z", loc.getZ());
		cfg.set(pos + "pitch", loc.getPitch());
		cfg.set(pos + "yaw", loc.getYaw());
		try {
			this.cfg.save(file);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void PlayerCommandListener(PlayerCommandPreprocessEvent e){
		if(map.containsKey(e.getPlayer().getName())){
			e.setCancelled(true);
			e.getPlayer().sendMessage(plugin.namespace + ChatColor.RED + "Du hast keinen Zugriff auf diesen Befehl!");
		}	
	}
	
	@EventHandler
	public void ChatEvent(AsyncPlayerChatEvent event){
		for(Entry<String, Integer> e : map.entrySet())
			event.getRecipients().remove(Bukkit.getPlayer(e.getKey()));
	}
}
