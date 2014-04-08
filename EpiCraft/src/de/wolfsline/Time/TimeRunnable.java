package de.wolfsline.Time;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class TimeRunnable extends BukkitRunnable{
	private static long ticks = 0;
	private static int sec = 0;
	public void run() {
		//Bukkit.getWorld("Games").setTime(6000L);
		World gamesWorld = Bukkit.getWorld("Games");
		if(gamesWorld != null)
			gamesWorld.setTime(6000L);
		//Bukkit.getWorld("Creative").setTime(6000L);
		World plotsWorld = Bukkit.getWorld("Plots");
		if(plotsWorld != null)
			plotsWorld.setTime(6000L);
		//Bukkit.getWorld("Team").setTime(6000L);
		World teamWorld = Bukkit.getWorld("Team");
		if(teamWorld != null)
			teamWorld.setTime(6000L);
		World survivalWorld = Bukkit.getWorld("Survival");
		if(survivalWorld != null){
			if(survivalWorld.getTime() >= ticks+100)
				ticks = survivalWorld.getTime();
			else if(survivalWorld.getTime() <= ticks-100)
				ticks = survivalWorld.getTime();
			survivalWorld.setTime(ticks);
		}
		World minemapWorld = Bukkit.getWorld("Minemap");
		if(minemapWorld != null)
			minemapWorld.setTime(ticks);
		//Bukkit.broadcastMessage("Ticks: " + String.valueOf(ticks) + "  -  Sek: " + String.valueOf(sec));
		if(sec == 14)
			ticks+=4.15;
		else if(sec == 30)
			ticks+=4.15;
		else if(sec == 46)
			ticks+=4.15;
		else if(sec == 58){
			ticks+=4.15;
			sec = 0;
		}
		sec+=2;
	}

}
