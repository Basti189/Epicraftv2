package de.wolfsline.helpClasses;

import de.wolfsline.Epicraft.Epicraft;

public class EpicraftPlayer {
	
	private Epicraft plugin;

	public String username = "";
	
	public boolean eventMessages = true;
	public boolean chatMessages = true;
	public boolean systemMessages = true;
	public boolean chatTime = false;
	public boolean healthbar = false;
	public boolean chatWorld = false;
	public String permission = "epicraft.permission.gast";
	public boolean isAFK = false;
	
	public EpicraftPlayer(Epicraft plugin, String name, String permission, boolean event, boolean chat, boolean system, boolean time, boolean healthbar, boolean world, boolean firstEntry){
		this.plugin = plugin;
		this.username = name;
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
					"Benutzername, " +
					"Eventnachrichten, " +
					"Chatnachrichten, " +
					"Chatzeit, " +
					"Chatwelt, " +
					"Systemnachrichten, " +
					"Lebensanzeige, " +
					"Berechtigung) " +
					"VALUES (" +
					"'" + username + "', " + 
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
				"WHERE Benutzername='" + username + "'";
		plugin.getMySQL().queryUpdate(query);
	}
	
	private String tb(boolean wert){
		if(wert)
			return "1";
		return "0";
	}
	
	
}
