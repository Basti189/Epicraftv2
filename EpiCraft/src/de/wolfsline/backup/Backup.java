package de.wolfsline.backup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import de.wolfsline.Epicraft.Epicraft;

public class Backup implements CommandExecutor, Listener {

	private Epicraft plugin;
	
	private boolean wasOnline = false;
	
	private double interval;
    private Double startHour;

    private boolean broadcast = true;

    private String dateFormat = "yyyy-MM-dd-HH-mm-ss";
    private String backupFile = "plugins/Epicraft/backups/";

    private List<String> backupWorlds = new ArrayList<String>();
    private List<String> additionalFolders = new ArrayList<String>();
    private IBackupFileManager backupFileManager;
    private DeleteSchedule deleteSchedule;
	
	public Backup(final Epicraft plugin) {
		this.plugin = plugin;
		this.dateFormat = "dd-MM-yyyy HH-mm";
		this.interval = 1.0D;
		this.backupWorlds.add("Survival");
		
		List<String> intervalsStr = new ArrayList<String>();
		List<String> frequenciesStr = new ArrayList<String>();
		backupFileManager = new ZipBackup(backupFile, dateFormat, plugin);
		deleteSchedule = new DeleteSchedule(intervalsStr, frequenciesStr, backupFileManager, plugin);
        
        long ticks = (long) (72000 * this.interval);
        if (ticks > 0) {
            long delay = this.startHour != null ? syncStart(this.startHour) : ticks;
            // Add the repeating task, set it to repeat the specified time
            Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                @Override
                public void run() {
                    // When the task is run, start the map backup
                    if (Bukkit.getServer().getOnlinePlayers().length > 0 || wasOnline) {
                    	new Thread(new Runnable() {
							
							@Override
							public void run() {
								doBackup(true);
							}
						}).start();
                        
                    } else {
                        plugin.api.sendLog("[Epicraft - Backup] Abgebrochen -> Keiner Online");
                    }
                }
            }, delay, ticks);
            plugin.api.sendLog("[Epicraft - Backup] Backup wird in " + delay / 72000. + " Std. erstellt und alle " + this.interval + " Std. wiederholt");
        }
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!cs.hasPermission("epicraft.backup")) {
        	cs.sendMessage(plugin.error);
        	plugin.api.sendLog("[Epicraft - Backup] " + cs.getName() + " hat versucht auf den Befehl zuzugreifen");
        	return true;
        }
		 new Thread(new Runnable() {
             @Override
             public void run() {
             	doBackup(false);
             }
         }).start();
		 return true;
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.wasOnline = true;
    }
	
	/*------------------------
    This runs the map backup
    -------------------------*/
   public synchronized void doBackup(boolean auto) {
       // Begin backup of worlds
       // Broadcast the backup initialization if enabled
       if (broadcast) {
    	   if(auto){
    		   Bukkit.getServer().broadcastMessage(plugin.namespace + ChatColor.WHITE + "Erstelle automatisches Backup");
    	   }
    	   else{
    		   Bukkit.getServer().broadcastMessage(plugin.namespace + ChatColor.WHITE + "Erstelle manuelles Backup");
    	   }
       }
       plugin.api.sendLog("[Epicraft - Backup] Erstellen");
       
       // Loop through all the specified worlds and save them
       List<File> foldersToBackup = new ArrayList<File>();
       for (final World world : worldsForBackup()) {
    	   plugin.api.sendLog("[Epicraft - Backup] Deaktiviere AutoSave -> " + world.getName());
           world.setAutoSave(false);
           try {
               Bukkit.getServer().getScheduler().callSyncMethod(this.plugin, new Callable<Object>() {
                   @Override
                   public Object call() throws Exception {
                       world.save();
                       plugin.api.sendLog("[Epicraft - Backup] Speichern -> " + world.getName());
                       return null;
                   }
               }).get();
               foldersToBackup.add(world.getWorldFolder());
           } catch (Exception e) {
        	   plugin.api.sendLog("[Epicraft - Backup] Speichern fehlgeschlagen -> " + world.getName());
           }
       }
       // additional folders, e.g. "plugins/"
       foldersToBackup.addAll(foldersForBackup());

       // zip/copy world folders
       try {
           backupFileManager.createBackup(foldersToBackup);
       } catch (IOException e) {
    	   plugin.api.sendLog("[Epicraft - Backup] Zippen/Kopieren fehlgeschlagen");
       }

       // re-enable auto-save
       for (World world : worldsForBackup()) {
           world.setAutoSave(true);
           plugin.api.sendLog("[Epicraft - Backup] Aktiviere AutoSave -> " + world.getName());
       }

       // delete old backups
       try {
    	   plugin.api.sendLog("[Epicraft - Backup] Bereinige Backup-Ordner");
           deleteSchedule.deleteOldBackups();
           
       } catch (IOException e) {
           plugin.api.sendLog("[Epicraft - Backup] Bereinigen des Backup-Ordners fehlgeschlagen");
       }

       // Broadcast the backup completion if enabled
       if (broadcast) {
           Bukkit.getServer().broadcastMessage(plugin.namespace + ChatColor.WHITE + "Backup erfolgreich erstellt");
       }
       plugin.api.sendLog("[Epicraft - Backup] Erfolgreich erstellt");
       if(Bukkit.getServer().getOnlinePlayers().length == 0){
    	   this.wasOnline = false;
       }
   }

   private Collection<File> foldersForBackup() {
       List<File> result = new ArrayList<File>();
       for (String additionalFolder : additionalFolders) {
           File f = new File(".", additionalFolder);
           if (f.exists()) {
               result.add(f);
           }
       }
       return result;
   }
   
   private Collection<World> worldsForBackup() {
       List<World> worlds = new ArrayList<World>();
       for (World world : Bukkit.getServer().getWorlds()) {
           if (backupWorlds.contains(world.getName())) {
               worlds.add(world);
           }
       }
       return worlds;
   }

   private double hoursOf(Date parsedTime) {
       return parsedTime.getHours() + parsedTime.getMinutes() / 60. + parsedTime.getSeconds() / 3600.;
   }

   private long syncStart(double startHour) {
       double now = hoursOf(new Date());
       double diff = now - startHour;
       if (diff < 0) {
           diff += 24;
       }
       double intervalPart = diff - Math.floor(diff / interval) * interval;
       double remaining = interval - intervalPart;
       return (long) (remaining * 72000);
   }


}
