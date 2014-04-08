package de.wolfsline.Epicraft;

import java.util.HashMap;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import de.wolfsline.API.EventAPI;
import de.wolfsline.DEBUG.DEBUGCLASS;
import de.wolfsline.DEBUG.PlayerBLock;
import de.wolfsline.Time.TimeRunnable;
import de.wolfsline.administration.ChestAccess;
import de.wolfsline.administration.DebugCommand;
import de.wolfsline.administration.EnderChestCommand;
import de.wolfsline.administration.FlyCommand;
import de.wolfsline.administration.InvSwitcherCommand;
import de.wolfsline.administration.JailCommand;
import de.wolfsline.administration.RestartCommand;
import de.wolfsline.administration.SkullPlayer;
import de.wolfsline.administration.SpawnCommand;
import de.wolfsline.administration.TeleportCommand;
import de.wolfsline.administration.TimePlayer;
import de.wolfsline.administration.UnHideCommand;
import de.wolfsline.administration.WhoIsCommand;
import de.wolfsline.data.MySQL;
import de.wolfsline.epimaster.EpiMaster;
import de.wolfsline.forfun.ChatFakerCommand;
import de.wolfsline.forfun.GunListener;
import de.wolfsline.forfun.HeadCommand;
import de.wolfsline.forfun.LightningCommand;
import de.wolfsline.forfun.PVP;
import de.wolfsline.forfun.TntCommand;
import de.wolfsline.forfun.VelocityShooter;
import de.wolfsline.game.ArenaSignListener;
import de.wolfsline.game.GameSignListener;
import de.wolfsline.gs.GSCommand;
import de.wolfsline.home.HomeCommand;
import de.wolfsline.info.TimeCommand;
import de.wolfsline.info.infoCommand;
import de.wolfsline.message.ChatListener;
import de.wolfsline.message.WhisperExecuter;
import de.wolfsline.modify.BedListener;
import de.wolfsline.modify.ColorSignListener;
import de.wolfsline.modify.CommandListener;
import de.wolfsline.modify.DeathListener;
import de.wolfsline.modify.GamemodeListener;
import de.wolfsline.modify.JoinQuitListener;
import de.wolfsline.modify.MECommand;
import de.wolfsline.permission.PermissionManager;
import de.wolfsline.register.AuthCommand;
import de.wolfsline.register.QuestSignCommand;
import de.wolfsline.register.QuestSignListener;
import de.wolfsline.restriction.AutoKickListener;
import de.wolfsline.restriction.RestrictionCommand;
import de.wolfsline.reward.VoteListener;
import de.wolfsline.security.ChestPassword;
import de.wolfsline.security.CreatureSpawnListener;
import de.wolfsline.security.HorseListener;
import de.wolfsline.security.IronGolemDropControll;
import de.wolfsline.security.MapSizeControll;
import de.wolfsline.settings.Settings;
import de.wolfsline.statistics.KillCounter;
import de.wolfsline.system.ShowSystemSign;
import de.wolfsline.teleport.SignLift;
import de.wolfsline.worldgenerator.CleanRoomChunkGenerator;

public class Epicraft extends JavaPlugin{
	private int messageTask;
	private int restartTask;
	public HashMap<String, Boolean> signmap;
	public static Economy economy = null;
	public final String namespace = ChatColor.GOLD + "[" + ChatColor.GRAY + "EpiMaster" + ChatColor.GOLD + "] ";
	public final String error = ChatColor.GOLD + "[" + ChatColor.GRAY + "EpiMaster" + ChatColor.GOLD + "] " + ChatColor.RED + "Du hast keinen Zugriff auf diesen Befehl!";
	public final String namespaceBeta = ChatColor.GOLD + "[" + ChatColor.GRAY + "EpiMaster - Beta" + ChatColor.GOLD + "] ";;
	private InvSwitcherCommand invswitch;
	
	private MySQL sql;
	public EventAPI api;
	public PermissionManager pManager;
	
	
	@Override
	public void onDisable(){
		Bukkit.getScheduler().cancelTask(restartTask);
		Bukkit.getScheduler().cancelTask(messageTask);
		invswitch.onServerShutdown();
		sql.closeConnection();
		System.out.println("[Epicraft] Epicraft wurde beendet");
	}
	
	@Override
	public void onEnable(){
		//Logger log = Bukkit.getServer().getLogger();
		//log.addHandler(new EventAPI());
		api = new EventAPI();
		this.sql = new MySQL();
		this.setupEconomy();
		
		QuestSignListener qsl = new QuestSignListener(this);
		GunListener gun = new GunListener(this);
		AuthCommand auth = new AuthCommand(this);
		UnHideCommand unhide = new UnHideCommand(this);
		GSCommand gs = new GSCommand(this);
		TimePlayer timeplayer = new TimePlayer(this);
		PVP pvp = new PVP(this);
		DebugCommand debugCommand = new DebugCommand(this);
		JailCommand myJail = new JailCommand(this);
		JoinQuitListener jqlistener = new JoinQuitListener(this);
		ChestPassword chestPasswort = new ChestPassword(this, economy);
		invswitch = new InvSwitcherCommand(this);
		HorseListener horse = new HorseListener(this);
		Settings set = new Settings(this);
		SkullPlayer skp = new SkullPlayer(this);
		VoteListener voteListener = new VoteListener(this);
		ShowSystemSign systemSign = new ShowSystemSign(this);
		RestrictionCommand restriction = new RestrictionCommand(this);
		RestartCommand restart = new RestartCommand(this);
		
		PlayerBLock pb = new PlayerBLock();
		
		this.getCommand("spawn").setExecutor(new SpawnCommand(this));
		this.getCommand("gs").setExecutor(gs);
		this.getCommand("hide").setExecutor(unhide);
		this.getCommand("restart").setExecutor(restart);
		this.getCommand("sign").setExecutor(new de.wolfsline.gs.SignCommand(this));
		this.getCommand("ep").setExecutor(new QuestSignCommand(this, qsl));
		this.getCommand("warn").setExecutor(restriction);
		this.getCommand("kick").setExecutor(new de.wolfsline.restriction.KickCommand(this));
		this.getCommand("ban").setExecutor(new de.wolfsline.restriction.BanCommand(this));
		this.getCommand("home").setExecutor(new HomeCommand(this));
        this.getCommand("invsee").setExecutor(new de.wolfsline.administration.InventarCommand(this));
        this.getCommand("lightning").setExecutor(new LightningCommand());
        this.getCommand("gun").setExecutor(gun);
        this.getCommand("grenade").setExecutor(new TntCommand(this));
        this.getCommand("ensee").setExecutor(new EnderChestCommand(this));
        this.getCommand("jail").setExecutor(myJail);
        this.getCommand("chat").setExecutor(new ChatFakerCommand(this));
        this.getCommand("head").setExecutor(new HeadCommand(this));
        this.getCommand("login").setExecutor(auth);
        this.getCommand("w").setExecutor(new WhisperExecuter(this));
        this.getCommand("support").setExecutor(invswitch);
        this.getCommand("onlinetime").setExecutor(timeplayer);
        this.getCommand("pvp").setExecutor(pvp);
        this.getCommand("fly").setExecutor(new FlyCommand(this));
        this.getCommand("ts").setExecutor(new infoCommand(this));
        this.getCommand("debug").setExecutor(debugCommand);
        this.getCommand("me").setExecutor(new MECommand());
        this.getCommand("tp").setExecutor(new TeleportCommand(this));
        this.getCommand("settings").setExecutor(set);
        this.getCommand("secure").setExecutor(chestPasswort);
        this.getCommand("horse").setExecutor(horse);
        this.getCommand("api").setExecutor(api);
        this.getCommand("epicraft").setExecutor(new DEBUGCLASS(this));
        this.getCommand("system").setExecutor(systemSign);
        this.getCommand("jump").setExecutor(new VelocityShooter());
        this.getCommand("uhr").setExecutor(new TimeCommand(this));
        this.getCommand("skull").setExecutor(skp);
        this.getCommand("vote").setExecutor(voteListener);
        this.getCommand("block").setExecutor(pb);
        this.getCommand("chest").setExecutor(new ChestAccess(this));
        this.getCommand("whois").setExecutor(new WhoIsCommand(this));
        
		PluginManager pm = this.getServer().getPluginManager();
		
		pm.registerEvents(jqlistener, this);
		pm.registerEvents(new ArenaSignListener(), this);
		pm.registerEvents(new BedListener(this), this);
		pm.registerEvents(new GameSignListener(this), this);
		pm.registerEvents(new de.wolfsline.gs.SignListener(this), this);
		pm.registerEvents(new de.wolfsline.Time.SignListener(this), this);
		pm.registerEvents(qsl, this);
		pm.registerEvents(new ChatListener(this), this);
		pm.registerEvents(new ColorSignListener(), this);
		pm.registerEvents(new de.wolfsline.reward.SignListener(this), this);
		pm.registerEvents(gun, this);
		pm.registerEvents(new CommandListener(this), this);
		pm.registerEvents(auth, this);
		pm.registerEvents(unhide, this);
		pm.registerEvents(gs, this);
		pm.registerEvents(invswitch, this);
		pm.registerEvents(timeplayer, this);
		pm.registerEvents(new KillCounter(this), this);
		pm.registerEvents(pvp, this);
		pm.registerEvents(myJail, this);
		pm.registerEvents(new DeathListener(), this);
		pm.registerEvents(debugCommand, this);
		pm.registerEvents(new AutoKickListener(this), this);
		pm.registerEvents(chestPasswort, this);
		pm.registerEvents(new MapSizeControll(this), this);
		pm.registerEvents(new IronGolemDropControll(), this);
		pm.registerEvents(horse, this);
		pm.registerEvents(new GamemodeListener(), this);
		pm.registerEvents(set, this);
		pm.registerEvents(restriction, this);
		pm.registerEvents(api, this);
		pm.registerEvents(new CreatureSpawnListener(), this);
		pm.registerEvents(systemSign, this);
		pm.registerEvents(skp, this);
		pm.registerEvents(voteListener, this);
		pm.registerEvents(new SignLift(this), this);
		pm.registerEvents(pb, this);
		pm.registerEvents(new EpiMaster(this), this);
		pm.registerEvents(restart, this);
		this.messageTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new TimeRunnable(), 0L, 2*20L);
		System.out.println("[Epicraft] Epicraft wurde gestartet");
	}
	
	public MySQL getMySQL(){
		this.sql.getConnection();
		return this.sql;
	}
	
	private boolean setupEconomy(){
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
	
	public WorldGuardPlugin getWorldGuard() {
	    Plugin wg = getServer().getPluginManager().getPlugin("WorldGuard");
	 
	    // WorldGuard may not be loaded
	    if (wg == null || !(wg instanceof WorldGuardPlugin)) {
	        return null; // Maybe you want throw an exception instead
	    }
	 
	    return (WorldGuardPlugin) wg;
	}
	
	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id){
		return new CleanRoomChunkGenerator(id);
	}
}
