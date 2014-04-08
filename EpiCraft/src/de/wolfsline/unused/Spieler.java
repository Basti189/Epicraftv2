package de.wolfsline.unused;

public class Spieler {
	private Long timestamp;
	private String name;
	private int anzahl;
	private String chatMessage;
	private int anzahlKick;
	private Long timestampForKickCount;
	
	public Spieler(){
		timestamp = (long) 0;
		name = "";
		anzahl = 0;
		chatMessage = "";
	}
	
	public Spieler(long timestamp, String chatMessage, long timestampkick){
		this.timestamp = timestamp;
		this.anzahl = 1;
		this.chatMessage = chatMessage;
		this.anzahlKick = 0;
	}
	
	public void setTimestampKick(Long timestamp){
		this.timestampForKickCount = timestamp;
	}
	public Long getTimestampKick(){
		return timestampForKickCount;
	}
	
	public void setTimestamp(Long timestamp){
		this.timestamp = timestamp;
	}
	public Long getTimestamp(){
		return timestamp;
	}
	
	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return name;
	}
	
	public void setAnzahl(int anzahl){
		this.anzahl = anzahl;
	}
	public int getAnzahl(){
		return anzahl;
	}
	
	public void setAnzahlKick(int anzahl){
		this.anzahlKick = anzahl;
	}
	public int getAnzahlKick(){
		return anzahlKick;
	}
	
	public void setChatMessage(String chatMessage){
		this.chatMessage = chatMessage;
	}
	public String getChatMessage(){
		return chatMessage;
	}
	
	public boolean isSameMessage(String message){
		if(chatMessage.equalsIgnoreCase(message)){
			anzahl++;
			return true;
		}
		anzahl = 1;
		return false;
	}
	
	public void reset(){
		timestamp = (long) 0;
		anzahl = 1;
		chatMessage = "";
	}
}
