package de.wolfsline.helpClasses;

import java.util.UUID;

import de.wolfsline.Epicraft.Epicraft;

public class EpicraftPlayer {
	
	private Epicraft plugin;

	public UUID uuid;
	
	public boolean eventMessages = true;
	public boolean chatMessages = true;
	public boolean systemMessages = true;
	public boolean chatTime = false;
	public boolean healthbar = false;
	public boolean chatWorld = false;
	public String permission = "epicraft.permission.gast";
	public boolean isAFK = false;
	
	public EpicraftPlayer(Epicraft plugin, UUID uuid, String permission, boolean event, boolean chat, boolean system, boolean time, boolean healthbar, boolean world, boolean firstEntry){
		this.plugin = plugin;
		this.uuid = uuid;
		this.eventMessages = event;
		this.chatMessages = chat;
		this.systemMessages = system;
		this.chatTime = time;
		this.healthbar = healthbar;
		this.chatWorld = world;
		this.permission = permission;
		if(firstEntry){
			//Datenbankeintrag anlegen
			String query = "INSERT INTO Einstellungen (" +
					"UUID, " +
					"Eventnachrichten, " +
					"Chatnachrichten, " +
					"Chatzeit, " +
					"Chatwelt, " +
					"Systemnachrichten, " +
					"Lebensanzeige, " +
					"Berechtigung) " +
					"VALUES (" +
					"'" + uuid.toString() + "', " + 
					"'" + tb(eventMessages) + "', " + 
					"'" + tb(chatMessages) + "', " + 
					"'" + tb(chatTime) + "', " + 
					"'" + tb(chatWorld) + "', " + 
					"'" + tb(systemMessages) + "', " + 
					"'" + tb(healthbar) + "', " +
					"'" + permission + "')";
			plugin.getMySQL().queryUpdate(query);
		}
	}
	
	public void update(){
		String query = "UPDATE Einstellungen SET " +
				"Eventnachrichten='" + tb(eventMessages) + "', " +
				"Chatnachrichten='" + tb(chatMessages) + "', " +
				"Chatzeit='" + tb(chatTime) + "', " +
				"Chatwelt='" + tb(chatWorld) + "', " +
				"Systemnachrichten='" + tb(systemMessages) + "', " +
				"Lebensanzeige='" + tb(healthbar) + "', " +
				"Berechtigung='" + permission + "' " +
				"WHERE UUID='" + uuid + "'";
		plugin.getMySQL().queryUpdate(query);
	}
	
	private String tb(boolean wert){
		if(wert)
			return "1";
		return "0";
	}
	
	
}
