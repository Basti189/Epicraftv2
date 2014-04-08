package de.wolfsline.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredServiceProvider;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.data.MySQL;

public class ClientHandler extends Thread{

	private Socket socket = null;
	private Epicraft plugin;
	private Server server = null;
	private boolean wasloggedIn = false;
	private String username;
	
	public ClientHandler(Socket clientSocket, Epicraft plugin, Server server) {
		this.socket = clientSocket;
		this.plugin = plugin;
		this.server = server;
		try {
			socket.setSoTimeout(10000);
		}
		catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run(){
		System.out.println("[EpiCraft - DEBUG] " + socket.getInetAddress().toString() + " verbunden");
		System.out.println("[EpiCraft - DEBUG] " + "Warte auf Anfrage");
		String what = "";
		try {
			what = leseNachricht(socket);
			System.out.println("[EpiCraft - DEBUG] " + "Client möchte: " + what);
		}
		catch (IOException e1) {
		}
		String player = login();
		this.username = player;
		boolean connected = true;
		if(player.equalsIgnoreCase("")){
			connected = false;
			try {
				schreibeNachricht(socket, "badlogin");
			} 
			catch (IOException e) {
			}
		}
		else{
			Player p = Bukkit.getServer().getPlayer(player);
			if(p != null){
				connected = false;
				try {
					schreibeNachricht(socket, "playerisonline");
				} 
				catch (IOException e) {
				}
			}
		}
		if(what.equalsIgnoreCase("bank")){
			if(!player.equalsIgnoreCase("")){
				String bank = showBank(player);
				try {
					if(connected)
						schreibeNachricht(socket, "loginAccept");
					Thread.sleep(100);
					schreibeNachricht(socket, bank);
				}
				catch (IOException e) {
				} 
				catch (InterruptedException e) {
				}
			}
			connected = false;
		}
		
		else if(what.equalsIgnoreCase("details")){
			if(!player.equalsIgnoreCase("")){
				String details = showDetails(player);
				try {
					if(connected)
						schreibeNachricht(socket, "loginAccept");
					Thread.sleep(100);
					schreibeNachricht(socket, details);
				}
				catch (IOException e) {
				} 
				catch (InterruptedException e) {
				}
			}
			connected = false;
		}
		
		else if(what.equalsIgnoreCase("player")){
			if(!player.equalsIgnoreCase("")){
				String details = showPlayer(player);
				try {
					if(connected)
						schreibeNachricht(socket, "loginAccept");
					Thread.sleep(100);
					schreibeNachricht(socket, details);
				}
				catch (IOException e) {
				} 
				catch (InterruptedException e) {
				}
			}
			connected = false;
		}
		
		/*if(connected){
			try {
				schreibeNachricht(socket, "Server Der Server(Chat) steht zurzeit nicht zur Verfügung!");
				Thread.sleep(250);
				schreibeNachricht(socket, "Server Wir bitten um Entschuldigung.");
				Thread.sleep(250);
				schreibeNachricht(socket, "Server Verbindung getrennt!");
			} catch (IOException e1) {
			} catch (InterruptedException e) {
			}
			connected = false;
		}*/
		
		if(connected){
			Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "***  " + player + " ist dem Chat mit Android beigetreten  ***");
			for(de.wolfsline.tcp.ClientHandler client : server.getClients()){
				try {
					schreibeNachricht(client.getSocket(), player + " joinApp");
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				schreibeNachricht(socket, "loginAccept");
				
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			wasloggedIn = true;
			server.addClient(this);
			this.username = player;
		}
        while(connected){
        	try {
				String msg = "";
				msg = leseNachricht(socket);
				if(msg.startsWith("/vote"))
					schreibeNachricht(socket, "Server Vote für uns unter http://vote.epicraft.de");
				else if(msg.startsWith("/")){
					schreibeNachricht(this.socket, "Server Befehle werden nicht unterstützt!");
				}
				else if(!msg.equalsIgnoreCase("")){
					Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "[Android] " + player + ChatColor.WHITE + ": " + msg);
					for(de.wolfsline.tcp.ClientHandler client : server.getClients()){
						schreibeNachricht(client.getSocket(), player + " " + msg);
					}
				}
			}
        	catch(StringIndexOutOfBoundsException e){
        		connected = false;
        		server.removeClient(this);
        	}
        	catch (IOException e) {
        		//connected = false;
			}
        }
        if((!player.equalsIgnoreCase("")) && wasloggedIn){
        	Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "***  " + player + " hat den Chat mit Android verlassen  ***");
        	for(de.wolfsline.tcp.ClientHandler client : server.getClients()){
				try {
					schreibeNachricht(client.getSocket(), player + " leaveApp");
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
        }
        System.out.println("[EpiCraft - DEBUG] " + socket.getInetAddress().toString() + " getrennt");
        server.removeClient(this);
        try {
			socket.close();
			
		}
        catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String leseNachricht(Socket socket) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(socket.getInputStream()));
        char[] buffer = new char[256];
        int anzahlZeichen = bufferedReader.read(buffer, 0, 256); // blockiert bis Nachricht empfangen
        String nachricht = new String(buffer, 0, anzahlZeichen);
        return nachricht;
    }
	
	void schreibeNachricht(Socket socket, String nachricht) throws IOException {
	    PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
	    printWriter.print(nachricht);
	    printWriter.flush();
	}
	
	private String login(){
		String player = "";
		String password = "";
		try {
			System.out.println("[EpiCraft - DEBUG] Frage Logindaten ab");
			player = leseNachricht(socket);
			System.out.println("[EpiCraft - DEBUG] Spieler: " + player);
			password = leseNachricht(socket);
			System.out.println("[EpiCraft - DEBUG] Passwort: " + "*****");//password);
			String convertedPassword = StringToSHA256(password);
			if(!loginPlayer(player, convertedPassword)){
				player = "";
			}
		} 
		catch (IOException e) {
			player = "";
		}
		catch(StringIndexOutOfBoundsException e){
    		player = "";
    	}
		return player;
	}
	
	private String StringToSHA256(String password){
		try{
	        MessageDigest digest = MessageDigest.getInstance("SHA-256");
	        byte[] hash = digest.digest(password.getBytes("UTF-8"));
	        StringBuffer hexString = new StringBuffer();

	        for (int i = 0; i < hash.length; i++) {
	            String hex = Integer.toHexString(0xff & hash[i]);
	            if(hex.length() == 1) hexString.append('0');
	            hexString.append(hex);
	        }

	        return hexString.toString();
	    } 
		catch(Exception ex){
	    }
		return password;
	}
	
	private boolean loginPlayer(String name, String convertedPassword){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		boolean login = false;
		try {
			st = conn.prepareStatement("SELECT password FROM auth WHERE username='" + name + "'");
			rs = st.executeQuery();
			rs.next();
			if(rs.getString(1).equals(convertedPassword))
				login = true;
			sql.closeRessources(rs, st);
			return login;
		} 
		catch (SQLException e) {
			//e.printStackTrace();
			return false;
		}
	}
	
	private String showBank(String username){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT * FROM `ep-Bank` WHERE username='" + username + "'");
			rs = st.executeQuery();
			String bank = "";
			while(rs.next()){
				//block = rs.getString(3);
				//amount = rs.getString(5);
				bank += rs.getString(3) + " " + rs.getString(5) + ":" + rs.getString(4) + " ";
			}
			sql.closeRessources(rs, st);
			return bank;
		}
		catch(SQLException e){
			return "error";
		}
	}
	
	private String showDetails(String username){
		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		Economy economy = null;
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
        String money = "Fehler";
        String onlinetime = "Fehler";
        if(economy != null){
        	Double myCoins = economy.getBalance(username);
        	money = String.valueOf(myCoins) + " Coins";
        }
        int seconds = getOnlineTime(username);
        if(seconds != -1){
        	int day = (int)TimeUnit.SECONDS.toDays(seconds);        
   		 	long hours = TimeUnit.SECONDS.toHours(seconds) - (day *24);
   		 	long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
   		 	long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);
   		 	onlinetime = String.valueOf(day) + " Tag(e) " + String.valueOf(hours) + " Stunde(n) " + String.valueOf(minute) + " Minute(n) " + String.valueOf(second) + " Sekunde(n)";
        }
        return (money + "_" + onlinetime);
	}
	
	private String showPlayer(String username){
		String player = "";
		player = "Spieler ";
		for(Player p : Bukkit.getServer().getOnlinePlayers()){
			player += p.getName() + " ";
		}
		player += "Appuser ";
		for(ClientHandler handler : server.getClients()){
			player += handler.getUsername() + " ";
		}
		return player;
	}
	
	private int getOnlineTime(String username){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		int gsX = 0;
		int gsY = 0;
		try {
			st = conn.prepareStatement("SELECT onlinetime FROM `lb-players` WHERE playername='" + username + "'");
			rs = st.executeQuery();
			rs.next();
			int onlineTime = rs.getInt(1);
			sql.closeRessources(rs, st);
			return onlineTime;
		} 
		catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public void kickUser(){
		try {
			schreibeNachricht(socket, "Server kicked");
		} catch (IOException e1) {
		}
		server.removeClient(this);
		Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "***  " + this.username + " hat den Chat mit Android unfreiwillig verlassen  ***");
		for(de.wolfsline.tcp.ClientHandler client : server.getClients()){
			try {
				schreibeNachricht(client.getSocket(), this.username+ " kickApp");
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.stop();
	}
	
	@EventHandler
	public void ChatEvent(AsyncPlayerChatEvent event){
		if(!wasloggedIn)
			return;
		String player = event.getPlayer().getName();
		String msg = event.getMessage();
		try {
			schreibeNachricht(socket, player + " " + msg);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void onLogin(PlayerJoinEvent event){
		if(!wasloggedIn)
			return;
		String player = event.getPlayer().getName();
		try {
			schreibeNachricht(socket, player + " join");
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event){
		if(!wasloggedIn)
			return;
		String player = event.getPlayer().getName();
		try {
			schreibeNachricht(socket, player + " leave");
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getUsername(){
		return this.username;
	}
	
	public Socket getSocket(){
		return this.socket;
	}
}
