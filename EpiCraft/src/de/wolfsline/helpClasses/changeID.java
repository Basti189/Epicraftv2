package de.wolfsline.helpClasses;

import java.util.HashMap;

public class changeID {

	private HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
	
	public changeID(int srcID, int destID){
		map.put(srcID, destID);
	}
	
	public void add(int srcID, int destID){
		map.put(srcID, destID);
	}
	
	public void remove(int srcID){
		map.remove(srcID);
	}
	
	public void clear(){
		map.clear();
		map = null;
	}
	
	public int getChangeID(int srcID){
		if(map.containsKey(srcID)){
			return map.get(srcID);
		}
		return srcID;
	}
	
	public int count(){
		return map.size();
	}
	
	public HashMap<Integer, Integer> getList(){
		return this.map;
	}
}