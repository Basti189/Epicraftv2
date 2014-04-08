package de.wolfsline.helpClasses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class myPlayer implements Serializable{
	private static final long serialVersionUID = -5082806277890146996L;
	
	public String username = "";
	
	public boolean eventMessages = true;
	public boolean chatMessages = true;
	public boolean autoMessages = true;
	public boolean chatTime = false;
	public boolean moneyForVote = true;
	public boolean chatWorld = true;
	
	public List<myMessages> msg = new ArrayList<>();
	
	public myPlayer(String username) {
		this.username = username;
	}
	
}
