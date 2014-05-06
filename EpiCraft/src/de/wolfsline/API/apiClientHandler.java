package de.wolfsline.API;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.data.MySQL;

public class apiClientHandler extends Thread{
	
	private Socket client;
	private String username;
	private Epicraft plugin;
	private EventAPI api;
	private boolean threadRun = true;
	private List<String> list = new ArrayList<String>();

	public apiClientHandler(Socket client, Epicraft plugin, EventAPI api){
		this.client = client;
		this.plugin = plugin;
		this.api = api;
		
	}
	
	public void addToList(String result){
		list.add(result);
	}
	
	@Override
	public void run(){
		try {
			client.setSoTimeout(500);
		} catch (SocketException e2) {
		}
		boolean verifyed = false;
		try {
			 verifyed = verifyClient();
		} catch (IOException e) {
			writeToClient("error");
			threadRun = false;
		} catch (IndexOutOfBoundsException e){
			System.out.println("[Epicraft - apiClient ] Verbindung beendet");
			threadRun = false;
		}
		if(!verifyed){
			writeToClient("badlogin");
			threadRun = false;
		}
		else if(!api.isPlayerVerifyed(this.username)){
			writeToClient("nopermission");
			threadRun = false;
		}
		else{
			writeToClient("loginaccept");
			try {
				client.setSoTimeout(100);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		api.addClientToList(this);
		try { Thread.sleep(250); } catch (InterruptedException e1) {}
		List<String> tmpList = new ArrayList<String>();
		if(threadRun){
			String result = "player ";
			for(Player p : Bukkit.getServer().getOnlinePlayers()){
				result += p.getName() + " ";
			}
			writeToClient(result);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		while(threadRun){
			tmpList.addAll(list);
			for(String tmp : tmpList){
				writeToClient(tmp);
				list.remove(tmp);
				try { Thread.sleep(200); } catch (InterruptedException e) {}
			}
			tmpList.clear();
			try {
				readFromClient();
			} catch (IndexOutOfBoundsException e) {
				threadRun = false;
			} catch (IOException e) {
			}
		}
		api.removeClientFromList(this);
		if(client != null)
			try { client.close(); } catch (IOException e) {}
		System.out.println("[Epicraft - apiClient ] Verbindung beendet");
	}
	
	private boolean verifyClient() throws IOException, IndexOutOfBoundsException{
		this.username = readFromClient();
		String password = readFromClient();
		
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		boolean login = false;
		try {
			UUID targetUUID = plugin.uuid.getUUIDFromPlayer(username);
			if(targetUUID == null){
				return false;
			}
			st = conn.prepareStatement("SELECT Passwort FROM Auth WHERE UUID='" + targetUUID + "'");
			rs = st.executeQuery();
			rs.next();
			if(rs.getString(1).equals(password))
				login = true;
			sql.closeRessources(rs, st);
			return login;
		} 
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private String readFromClient() throws IOException, IndexOutOfBoundsException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(this.client.getInputStream()));
        char[] buffer = new char[256];
        int anzahlZeichen = bufferedReader.read(buffer, 0, 256); // blockiert bis Nachricht empfangen
        String nachricht = new String(buffer, 0, anzahlZeichen);
        return nachricht;
    }
	
	void writeToClient(String nachricht) {
		try {
			PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(this.client.getOutputStream()));
			printWriter.print(nachricht);
		    printWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IndexOutOfBoundsException ex){
			System.out.println("Client verbindung geschlossen?");
			threadRun = false;
		}
	}
}
