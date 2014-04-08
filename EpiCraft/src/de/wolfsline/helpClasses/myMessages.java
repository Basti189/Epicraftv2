package de.wolfsline.helpClasses;

import java.io.Serializable;

public class myMessages implements Serializable{

	private static final long serialVersionUID = 1457244917549870960L;
	
	public String sender = "";
	public String message = "";
	
	public myMessages(String sender, String message){
		this.sender = sender;
		this.message = message;
	}

}
