package de.wolfsline.helpClasses;

import de.wolfsline.Epicraft.Epicraft;

public class EpicraftPlayer {
	
	private Epicraft plugin;

	public String username = "";
	
	public boolean eventMessages = true;
	public boolean chatMessages = true;
	public boolean systemMessages = true;
	public boolean chatTime = false;
	public boolean moneyForVote = true;
	public boolean chatWorld = false;
	public String permission = "epicraft.permission.gast";
	
	public EpicraftPlayer(Epicraft plugin, String name, String permission, boolean event, boolean chat, boolean system, boolean time, boolean money, boolean world, boolean firstEntry){
		this.plugin = plugin;
		this.username = name;
		this.eventMessages = event;
		this.chatMessages = chat;
		this.systemMessages = system;
		this.chatTime = time;
		this.moneyForVote = money;
		this.chatWorld = world;
		this.permission = permission;
		if(firstEntry){
			//Datenbankeintrag anlegen
			String query = "INSERT INTO Einstellungen (" +
					"Name, " +
					"Eventnachrichten, " +
					"Chatnachrichten, " +
					"Chatzeit, " +
					"Chatwelt, " +
					"Systemnachrichten, " +
					"Votegeld, " +
					"Berechtigung) " +
					"VALUES (" +
					"'" + username + "', " + 
					"'" + tb(eventMessages) + "', " + 
					"'" + tb(chatMessages) + "', " + 
					"'" + tb(chatTime) + "', " + 
					"'" + tb(chatWorld) + "', " + 
					"'" + tb(systemMessages) + "', " + 
					"'" + tb(moneyForVote) + "', " +
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
				"Votegeld='" + tb(moneyForVote) + "', " +
				"Berechtigung='" + permission + "' " +
				"WHERE Name='" + username + "'";
		plugin.getMySQL().queryUpdate(query);
	}
	
	private String tb(boolean wert){
		if(wert)
			return "1";
		return "0";
	}
	
	
}
