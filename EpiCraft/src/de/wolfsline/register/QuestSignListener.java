package de.wolfsline.register;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.helpClasses.EpicraftPlayer;

public class QuestSignListener implements Listener{

	private Epicraft plugin;
	private final String WORLD = "Survival";
	
	private HashMap<UUID, Integer> map;
	private Question qn;
	private final String[] correctAnswer = {"a","c","b","d","b","c","a","d","b","d"};
	public String[] Questions = {"Frage1:","Frage2:","Frage3:","Frage4:","Frage5:","Frage6:","Frage7:","Frage8:","Frage9:","Frage10:"};
	private String[][] Answer;
	private Location start, raum;
	File file = new File("plugins/Epicraft/", "QuestSignLocation.yml");
	FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
	
	public QuestSignListener(Epicraft plugin) {
		this.plugin = plugin;
		this.map = new HashMap<UUID, Integer>();
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
		
		raum = new Location(Bukkit.getWorld(WORLD), cfg.getDouble(pos + "x"), cfg.getDouble(pos + "y"), cfg.getDouble(pos + "z"));
		raum.setPitch((float) cfg.getDouble(pos + "pitch"));
		raum.setYaw((float) cfg.getDouble(pos + "yaw"));
		
		start = new Location(Bukkit.getWorld(WORLD), cfg.getDouble(pos2 + "x"), cfg.getDouble(pos2 + "y"), cfg.getDouble(pos2 + "z"));
		start.setPitch((float) cfg.getDouble(pos2 + "pitch"));
		start.setYaw((float) cfg.getDouble(pos2 + "yaw"));
		
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent event){
		Player p = event.getPlayer();
		String line = event.getLine(0);
		line = ChatColor.stripColor(line);
		if(line.equals("[Fragebogen]")){
			if(!p.hasPermission("epicraft.fragebogen.team")){
				event.getBlock().breakNaturally();
				plugin.api.sendLog("[Epicraft - Fragebogen] " + p.getName() + " hat versucht ein Fragebogenschild aufzustellen");
				return;
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e){
		Player p = e.getPlayer();
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(e.getClickedBlock().getState() instanceof Sign){
				if(!p.hasPermission("epicraft.permission.gast")){
					return;
				}
				Sign sign = (Sign) e.getClickedBlock().getState();
				String linie[] = sign.getLines();
				for(int i = 0 ; i < 4 ; i++ ){
					linie[i] = ChatColor.stripColor(linie[i]);
				}
				if(!linie[0].equals("[Fragebogen]")){
					return;
				}
				if(linie[1].equalsIgnoreCase("start")){
					plugin.api.sendLog("[Epicraft - Fragebogen] " + p.getName() + " hat den Fragebogen gestartet");
					map.put(p.getUniqueId(), 1);
					qn.start(p);
					p.teleport(this.raum);
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Willkommen beim Test.");
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Ich hoffe, du hast die Regeln durchgelesen...");
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "");
					sendQuestion(p);
					return;
				}
				else if(linie[1].equalsIgnoreCase("a") || linie[1].equalsIgnoreCase("b") || linie[1].equalsIgnoreCase("c") || linie[1].equalsIgnoreCase("d")){
					if(map.containsKey(p.getUniqueId())){
						if(correctAnswer[map.get(p.getUniqueId())-1].equalsIgnoreCase(linie[1])){
							qn.update(p, map.get(p.getUniqueId()), true);
							plugin.api.sendLog("[Epicraft - Fragebogen] " + p.getName() + " hat Frage " + map.get(p.getUniqueId()) + " richtig beantwortet");
						}
						else{
							qn.update(p, map.get(p.getUniqueId()), false);
							plugin.api.sendLog("[Epicraft - Fragebogen] " + p.getName() + " hat Frage " + map.get(p.getUniqueId()) + " falsch beantwortet");
						}
						up(p);
					}
				}
			}
		}
	}
	
	private void sendQuestion(Player p){
		p.sendMessage(ChatColor.GOLD + "---------------[Frage " + map.get(p.getUniqueId()) + "]---------------");
		p.sendMessage(ChatColor.GOLD + "Frage: " + ChatColor.WHITE + Questions[map.get(p.getUniqueId())-1]);
		p.sendMessage(ChatColor.GOLD + "Antwort a: " + ChatColor.WHITE + Answer[map.get(p.getUniqueId())-1][0]);
		p.sendMessage(ChatColor.GOLD + "Antwort b: " + ChatColor.WHITE + Answer[map.get(p.getUniqueId())-1][1]);
		p.sendMessage(ChatColor.GOLD + "Antwort c: " + ChatColor.WHITE + Answer[map.get(p.getUniqueId())-1][2]);
		p.sendMessage(ChatColor.GOLD + "Antwort d: " + ChatColor.WHITE + Answer[map.get(p.getUniqueId())-1][3]);
		p.sendMessage(ChatColor.GOLD + "---------------[Frage " + map.get(p.getUniqueId()) + "]---------------");
	}
	
	public void up(Player p){
		int i = map.get(p.getUniqueId());
		if(i == 10){
			if(qn.questionAllRight(p)){
				map.remove(p.getUniqueId());
				p.teleport(this.start);
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Glückwunsch! Du hast alle Fragen richtig beantwortet");
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Du gehörst nun zu den Spielern");
				for(Player player : Bukkit.getServer().getOnlinePlayers()){
					if(player.getName().equals(p.getName()))
						continue;
					EpicraftPlayer epiPlayer = plugin.pManager.getEpicraftPlayer(player.getUniqueId());
					if(epiPlayer == null)
						continue;
					if(epiPlayer.eventMessages)
						player.sendMessage(plugin.namespace + ChatColor.WHITE + p.getName() + " gehört nun zu den Spielern!");
				}
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "permission set " + p.getName() + " Spieler");
				plugin.api.sendLog("[Epicraft - Fragebogen] " + p.getName() + " hat den Fragebogen bestanden und gehört nun zu den Spielern");
				return;
			}
			p.teleport(this.start);
			p.sendMessage(plugin.namespace + ChatColor.RED + "Du hast mind. eine Frage falsch beantwortet");
			p.sendMessage(plugin.namespace + ChatColor.RED + "Bitte wiederhole den Test");
			plugin.api.sendLog("[Epicraft - Fragebogen] " + p.getName() + " hat den Fragebogen nicht bestanden");
			map.remove(p.getUniqueId());
			return;
		}
		if(i < 10)
			i++;
		map.put(p.getUniqueId(), i);
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
		if(map.containsKey(e.getPlayer().getUniqueId())){
			e.setCancelled(true);
			e.getPlayer().sendMessage(plugin.namespace + ChatColor.RED + "Du hast keinen Zugriff auf diesen Befehl!");
		}	
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void PlayerLogoutEvent(PlayerQuitEvent event){
		Player p = event.getPlayer();
		if(!map.containsKey(p.getUniqueId()))
			return;
		map.remove(p.getUniqueId());
		p.teleport(this.start);
		EpicraftPlayer epiPlayer = plugin.pManager.getEpicraftPlayer(p.getUniqueId());
		if(epiPlayer != null){
			epiPlayer.chatMessages = true;
			epiPlayer.update();
		}
		plugin.api.sendLog("[Epicraft - Fragebogen] " + p.getName() + " hat sich ausgeloggt und wurde aus dem Fragebogen geworfen");
	}
}
