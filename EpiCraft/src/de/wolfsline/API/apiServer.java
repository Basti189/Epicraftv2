package de.wolfsline.API;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import de.wolfsline.Epicraft.Epicraft;

public class apiServer extends Thread{
	
	private boolean threadRun = true;
	private Epicraft plugin;
	private EventAPI api;
	
	public apiServer(Epicraft plugin, EventAPI api){
		this.plugin = plugin;
		this.api = api;
	}

	@Override
	public void run(){
		ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(2894);
        } 
        catch (IOException e) {
            System.err.println("Port blockiert?: 2894");
        }
        System.out.println("[EpiCraft - ApiServer] Server gestartet an: " + serverSocket.getInetAddress().toString() + ":2894");

        while(threadRun){
            Socket clientSocket = null;
			try {
				System.out.println("[EpiCraft - ApiServer] Warte auf Client");
				clientSocket = serverSocket.accept();
			} 
			catch (IOException e) {
				System.out.println("[EpiCraft - ApiServer] Ein unbekannter Fehler ist mit einem Client aufgetreten");
			}
			finally{
				apiClientHandler apiClient = new apiClientHandler(clientSocket, plugin, api);
				apiClient.start();
			}
        }
        try {
			serverSocket.close();
		}
        catch (IOException e) {
			e.printStackTrace();
		}       
    }
	
	public void stopServer(){
		threadRun = false;
	}
}
