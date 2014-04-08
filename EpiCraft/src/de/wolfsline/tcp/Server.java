package de.wolfsline.tcp;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.data.MySQL;


public class Server extends Thread implements Listener{
	private Epicraft plugin = null;
	private List<de.wolfsline.tcp.ClientHandler> list = new ArrayList<de.wolfsline.tcp.ClientHandler>();

	public Server(Epicraft plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run(){
		ServerSocket serverSocket = null;
        boolean listeningSocket = true;
        try {
            serverSocket = new ServerSocket(1234);
        } 
        catch (IOException e) {
            System.err.println("Port blockiert?: 1234");
        }
        System.out.println("[EpiCraft - DEBUG] Server gestartet an: " + serverSocket.getInetAddress().toString() + ":1234");

        while(listeningSocket){
        	
            Socket clientSocket = null;
			try {
				System.out.println("[EpiCraft - DEBUG] Warte auf Client");
				clientSocket = serverSocket.accept();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
			finally{
				de.wolfsline.tcp.ClientHandler mini = new ClientHandler(clientSocket, plugin, this);
				mini.start();
			}
        }
        try {
			serverSocket.close();
		}
        catch (IOException e) {
			e.printStackTrace();
		}       
    }
	
	public List<de.wolfsline.tcp.ClientHandler> getClients(){
		return this.list;
	}
	
	public ClientHandler getClientFromUser(String name){
		for(ClientHandler handler : list){
			if(handler.getUsername().equalsIgnoreCase(name))
				return handler;
		}
		return null;
	}
	
	public void removeClient(de.wolfsline.tcp.ClientHandler client){
		this.list.remove(client);
	}
	
	public void addClient(de.wolfsline.tcp.ClientHandler client){
		this.list.add(client);
	}
	
	@EventHandler
	public void ChatEvent(AsyncPlayerChatEvent event){
		String player = event.getPlayer().getName();
		String msg = event.getMessage();
		for(de.wolfsline.tcp.ClientHandler client : list){
			try {
				schreibeNachricht(client.getSocket(), player + " " + msg);
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@EventHandler
	public void onLogin(PlayerJoinEvent event){
		String player = event.getPlayer().getName();
		String onlineUser = "";
		for(de.wolfsline.tcp.ClientHandler client : list){
			try {
				schreibeNachricht(client.getSocket(), player + " join");
				onlineUser += client.getUsername() + ", ";
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		event.getPlayer().sendMessage(plugin.namespace + ChatColor.WHITE + "Appuser online:");
		event.getPlayer().sendMessage(plugin.namespace + ChatColor.WHITE + onlineUser);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event){
		String player = event.getPlayer().getName();
		for(de.wolfsline.tcp.ClientHandler client : list){
			try {
				schreibeNachricht(client.getSocket(), player + " leave");
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void schreibeNachricht(Socket socket, String nachricht) throws IOException {
	    PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
	    printWriter.print(nachricht);
	    printWriter.flush();
	}
	
	public void sendStopServer(){
		for(de.wolfsline.tcp.ClientHandler client : list){
			try {
				schreibeNachricht(client.getSocket(), "Server closed");
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
