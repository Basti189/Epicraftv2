package de.wolfsline.Epicraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import ProtocolLib.SignPacketAdapter;
import ProtocolLib.SkullPacketAdapter;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.ListenerPriority;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import de.wolfsline.API.EventAPI;
import de.wolfsline.Ticketsystem.Ticketsystem;
import de.wolfsline.Ticketsystem.Ticketsystem_Schild;
import de.wolfsline.UUID.MyUUID;
import de.wolfsline.administration.ChestAccess;
import de.wolfsline.administration.Wartungsmodus;
import de.wolfsline.administration.DoorAccess;
import de.wolfsline.administration.EnderChestCommand;
import de.wolfsline.administration.FlyCommand;
import de.wolfsline.administration.InventarCommand;
import de.wolfsline.administration.RestartCommand;
import de.wolfsline.administration.SpawnCommand;
import de.wolfsline.administration.TeleportCommand;
import de.wolfsline.administration.UnHideCommand;
import de.wolfsline.administration.WhoIsCommand;
import de.wolfsline.afk.AFK;
import de.wolfsline.blocksecure.BlockSecure;
import de.wolfsline.data.MySQL;
import de.wolfsline.epimaster.EpiMaster;
import de.wolfsline.forfun.ChatFakerCommand;
import de.wolfsline.forfun.EggCatcher;
import de.wolfsline.forfun.FunEffects;
import de.wolfsline.forfun.GunListener;
import de.wolfsline.forfun.HeadCommand;
import de.wolfsline.forfun.LightningCommand;
import de.wolfsline.forfun.More;
import de.wolfsline.forfun.PVP;
import de.wolfsline.forfun.TntCommand;
import de.wolfsline.gs.Grundstück;
import de.wolfsline.gs.SignName;
import de.wolfsline.healthbar.DamageListener;
import de.wolfsline.home.HomeCommand;
import de.wolfsline.message.ChatListener;
import de.wolfsline.message.WhisperExecuter;
import de.wolfsline.microblock.Microblock;
import de.wolfsline.modify.ColorSignListener;
import de.wolfsline.modify.CommandListener;
import de.wolfsline.modify.DeathListener;
import de.wolfsline.modify.EventBlocker;
import de.wolfsline.modify.JoinQuitListener;
import de.wolfsline.modify.MECommand;
import de.wolfsline.permission.PermissionManager;
import de.wolfsline.register.AuthCommand;
import de.wolfsline.register.QuestSignCommand;
import de.wolfsline.register.QuestSignListener;
import de.wolfsline.restriction.BanCommand;
import de.wolfsline.restriction.KickCommand;
import de.wolfsline.restriction.RestrictionCommand;
import de.wolfsline.security.CreatureSpawnListener;
import de.wolfsline.security.HorseListener;
import de.wolfsline.security.MapSizeControll;
import de.wolfsline.security.Region;
import de.wolfsline.settings.Settings;
import de.wolfsline.sign.SaveSign;
import de.wolfsline.sign.Systemsign;
import de.wolfsline.statistics.KillCounter;
import de.wolfsline.teleport.SignLift;
import de.wolfsline.teleport.TeleportBack;
import de.wolfsline.teleport.WorldManager;
import de.wolfsline.worldgenerator.CleanRoomChunkGenerator;

public class Epicraft extends JavaPlugin{
	private int restartTask;
	
	public final String namespace = ChatColor.GOLD + "[" + ChatColor.GRAY + "EpiMaster" + ChatColor.GOLD + "] ";
	public final String error = ChatColor.GOLD + "[" + ChatColor.GRAY + "EpiMaster" + ChatColor.GOLD + "] " + ChatColor.RED + "Du hast keinen Zugriff auf diesen Befehl!";
	public final String namespaceBeta = ChatColor.GOLD + "[" + ChatColor.GRAY + "EpiMaster - Beta" + ChatColor.GOLD + "] ";
	
	private MySQL sql;
	public EventAPI api;
	public PermissionManager pManager;
	public MyUUID uuid;
	public ProtocolManager protocolManager;
	
	
	@Override
	public void onDisable(){
		Bukkit.getScheduler().cancelTask(restartTask);
		sql.closeConnection();
		System.out.println("[Epicraft] Epicraft wurde beendet");
	}
	
	@Override
	public void onEnable(){
		//Logger log = Bukkit.getServer().getLogger();
		//log.addHandler(new EventAPI());
		
		//PluginManager
		PluginManager pm = this.getServer().getPluginManager();
		
		//EventAPI
		api = new EventAPI();
		this.getCommand("api").setExecutor(api);
		pm.registerEvents(api, this);
		
		//PermissionManager
		pManager = new PermissionManager(this);
		this.getCommand("permission").setExecutor(pManager);
		
		//SQL-Datenbank
		this.sql = new MySQL();
		
		//UUID
		uuid = new MyUUID(this);
		this.getCommand("uuid").setExecutor(uuid);
		pm.registerEvents(uuid, this);
		
		//ProtocolManager
		this.protocolManager = ProtocolLibrary.getProtocolManager();
		SkullPacketAdapter skullPacketAdapter = new SkullPacketAdapter(this, PacketType.Play.Server.TILE_ENTITY_DATA);
		protocolManager.addPacketListener(skullPacketAdapter);
		SignPacketAdapter signPacketAdapter = new SignPacketAdapter(this, PacketType.Play.Server.UPDATE_SIGN);
		protocolManager.addPacketListener(signPacketAdapter);
		
		//Spawn
		SpawnCommand spawn = new SpawnCommand(this);
		this.getCommand("spawn").setExecutor(spawn);
		
		//Grundstückssystem
		Grundstück gs = new Grundstück(this);
		this.getCommand("gs").setExecutor(gs);
		pm.registerEvents(gs, this);
		
		//Neustart des Servers
		RestartCommand restart = new RestartCommand(this);
		this.getCommand("restart").setExecutor(restart);
		pm.registerEvents(restart, this);
		
		//Beschriftung der Grundstücksgrenzenschilder
		SignName sign = new SignName(this);
		this.getCommand("sign").setExecutor(sign);
		pm.registerEvents(sign, this);
		
		//Fragebogen
		QuestSignListener qsl = new QuestSignListener(this);
		this.getCommand("fragebogen").setExecutor(new QuestSignCommand(this, qsl));
		pm.registerEvents(qsl, this);
		
		//Verwarnung
		RestrictionCommand restriction = new RestrictionCommand(this);
		this.getCommand("warn").setExecutor(restriction);
		pm.registerEvents(restriction, this);
		
		//Kick
		KickCommand kick = new KickCommand(this);
		this.getCommand("kick").setExecutor(kick);
		
		//Ban
		BanCommand ban = new BanCommand(this);
		this.getCommand("ban").setExecutor(ban);
		
		//Home
		HomeCommand home = new HomeCommand(this);
		this.getCommand("home").setExecutor(home);
		
		//Hide / Unhide
		UnHideCommand unhide = new UnHideCommand(this);
		this.getCommand("hide").setExecutor(unhide);
		pm.registerEvents(unhide, this);
		
		//Inventar gucken
		InventarCommand inv = new InventarCommand(this);
		this.getCommand("invsee").setExecutor(inv);
		
		//Blitze erzeugen
		LightningCommand lightning = new LightningCommand(this);
		this.getCommand("lightning").setExecutor(lightning);
		
		//Erzeugen von Kanonen
		GunListener gun = new GunListener(this);
		this.getCommand("gun").setExecutor(gun);
		pm.registerEvents(gun, this);
		
		//TNT platzieren
		TntCommand tnt = new TntCommand(this);
		this.getCommand("grenade").setExecutor(tnt);
		
		//Blick in die Enderchest eines Spielers werfen
		EnderChestCommand enderchest = new EnderChestCommand(this);
		this.getCommand("ensee").setExecutor(enderchest);
		
		//Spielernachricht erzeugen
		ChatFakerCommand fakeChat = new ChatFakerCommand(this);
		this.getCommand("chat").setExecutor(fakeChat);
		
		//Block auf den Kopf setzen
		HeadCommand head = new HeadCommand(this);
		this.getCommand("head").setExecutor(head);
		
		//Registrierung und Einloggen
		AuthCommand auth = new AuthCommand(this);
		this.getCommand("login").setExecutor(auth);
		pm.registerEvents(auth, this);
		
		//Private Nachrichten
		WhisperExecuter whisper = new WhisperExecuter(this);
		this.getCommand("w").setExecutor(whisper);
		
		//PVP
		PVP pvp = new PVP(this);
		this.getCommand("pvp").setExecutor(pvp);
		pm.registerEvents(pvp, this);
		
		//Fly
		FlyCommand fly = new FlyCommand(this);
		this.getCommand("fly").setExecutor(fly);
		
		//Info <-- Wird überarbeitet
		//InfoCommand info = new InfoCommand(this);
		//this.getCommand("ts").setExecutor(info);
		
		//DEBUG - Wartungsmodus
		Wartungsmodus wartung = new Wartungsmodus(this);
		this.getCommand("wartung").setExecutor(wartung);
		pm.registerEvents(wartung, this);
		
		//Einstellungen
		Settings settings = new Settings(this);
		this.getCommand("settings").setExecutor(settings);
		pm.registerEvents(settings, this);
		
		//Wichtig Befehl
		MECommand me = new MECommand(this);
		this.getCommand("me").setExecutor(me);
		
		//Teleport
		TeleportCommand teleport = new TeleportCommand(this);
		this.getCommand("tp").setExecutor(teleport);
		
		//Sichert Truhen und Öfen
		/*ChestPassword chestPasswort = new ChestPassword(this);
		this.getCommand("secure").setExecutor(chestPasswort);
		pm.registerEvents(chestPasswort, this);*/
		BlockSecure blockSecure = new BlockSecure(this);
		this.getCommand("secure").setExecutor(blockSecure);
		pm.registerEvents(blockSecure, this);
		
		//Sichert die Pferde eines Spielers
		HorseListener horse = new HorseListener(this);
		this.getCommand("horse").setExecutor(horse);
		pm.registerEvents(horse, this);
		
		//Kisten aus der ferne Öffnen
		ChestAccess chest = new ChestAccess(this);
		this.getCommand("chest").setExecutor(chest);
		
		//Informationen zu einem Spieler
		WhoIsCommand whois = new WhoIsCommand(this);
		this.getCommand("whois").setExecutor(whois);
		
		//Channel
		ChatListener myChat = new ChatListener(this);
		this.getCommand("channel").setExecutor(myChat);
		pm.registerEvents(myChat, this);
		
		//Ticketsystem
		Ticketsystem ticket = new Ticketsystem(this);
		this.getCommand("ticket").setExecutor(ticket);
		
		Ticketsystem_Schild ticketSchild = new Ticketsystem_Schild(this);
		this.getCommand("ticketschild").setExecutor(ticketSchild);
		pm.registerEvents(ticketSchild, this);
		
		//Weltsystem
		WorldManager world = new WorldManager(this);
		this.getCommand("welt").setExecutor(world);
		pm.registerEvents(world, this);
		
		//Afk
		AFK afk = new AFK(this);
		this.getCommand("afk").setExecutor(afk);
		pm.registerEvents(afk, this);
		
		//Teleportiert dicvh zurück zum Todesort
		TeleportBack back = new TeleportBack(this);
		this.getCommand("back").setExecutor(back);
		pm.registerEvents(back, this);
		
		//Microblocks
		Microblock microblock = new Microblock(this);
		this.getCommand("microblock").setExecutor(microblock);
		pm.registerEvents(microblock, this);
		
		//More Stack das ausgewählte Item auf 64
		More more = new More(this);
		this.getCommand("more").setExecutor(more);
		
		//Kopiert ein Schild
		SaveSign savesign = new SaveSign(this);
		this.getCommand("savesign").setExecutor(savesign);
		pm.registerEvents(savesign, this);
		
		
		//Login Listener
		JoinQuitListener jqlistener = new JoinQuitListener(this);	
		pm.registerEvents(jqlistener, this);
		
		//Färbt ein Schild
		ColorSignListener colorSign = new ColorSignListener(this);
		pm.registerEvents(colorSign, this);
		
		//Blockiert Befehle, wenn Spieler Berechtigung nicht hat
		CommandListener cmd = new CommandListener(this);
		pm.registerEvents(cmd, this);
		
		//Speichert die Kills in eine Datenbank
		KillCounter killCounter = new KillCounter(this);
		pm.registerEvents(killCounter, this);
		
		//Benachrictigt die Spieler bei eintreten des Todes
		DeathListener death = new DeathListener(this);
		pm.registerEvents(death, this);
		
		//Gibt dem Stammspieler ein SpawnEgg von einem gefangenen Tier
		EggCatcher catcher = new EggCatcher(this);
		pm.registerEvents(catcher, this);
		
		//Sichert die Welten vor Feuerausbreitung
		Region region = new Region(this);
		pm.registerEvents(region, this);
		
		//Öffnen von Eisentüren
		DoorAccess doorAccess = new DoorAccess(this);
		pm.registerEvents(doorAccess, this);
		
		//Kontrolliert die größe der Map
		MapSizeControll mapSizeControll = new MapSizeControll(this);
		pm.registerEvents(mapSizeControll, this);
		
		//Kontrolliert das Spawnen von Kreaturen
		CreatureSpawnListener creatureSpawn = new CreatureSpawnListener(this);
		pm.registerEvents(creatureSpawn, this);
		
		//Fahrstuhl über Schilder
		SignLift lift = new SignLift(this);
		pm.registerEvents(lift, this);
		
		//EpiMaster
		EpiMaster epimaster = new EpiMaster(this);
		pm.registerEvents(epimaster, this);
		
		//Lebensanzeige der Mobs
		DamageListener healthbar = new DamageListener(this);
		pm.registerEvents(healthbar, this);
		
		//Systemschilder
		Systemsign sysSign = new Systemsign(this);
		this.getCommand("system").setExecutor(sysSign);
		pm.registerEvents(sysSign, this);
		
		//Effecte
		FunEffects effecte = new FunEffects(this);
		this.getCommand("effect").setExecutor(effecte);
		
		//EventBlocker
		EventBlocker eBlocker = new EventBlocker(this);
		this.getCommand("event").setExecutor(eBlocker);
		pm.registerEvents(eBlocker, this);
		
		//Lädt die Spieler die während eines Reloads online sind auf der Datenbank
		for(Player player : Bukkit.getServer().getOnlinePlayers()){
			pManager.triggerEpicraftPlayerList(player, true);
			myChat.mapChannel.put(player.getUniqueId(), 0);
		}
		System.out.println("[Epicraft] Epicraft wurde gestartet");
	}
	
	public MySQL getMySQL(){ //Löst nach ein paar Stunden fehler aus -> Lösung?
		if(!sql.hasConnection()){ //Wenn keine Verbindung
			this.sql.closeConnection();
			this.sql = new MySQL();
			this.api.sendLog("[Epicraft - SQL] Die Verbindung wird neu aufgebaut");
		}
		return this.sql;
	}
	
	public WorldGuardPlugin getWorldGuard() {
	    Plugin wg = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
	    if (wg == null || !(wg instanceof WorldGuardPlugin)) {
	        return null;
	    }
	    return (WorldGuardPlugin) wg;
	}
	
	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id){
		return new CleanRoomChunkGenerator(id);
	}
}
