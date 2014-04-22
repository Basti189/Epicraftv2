package de.wolfsline.Ticketsystem;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.wolfsline.Epicraft.Epicraft;

public class Ticketsystem implements CommandExecutor{
	
	private Epicraft plugin;
	private Ticketsystem_Daten data;
	
	public Ticketsystem(Epicraft plugin){
		this.plugin = plugin;
		this.data = new Ticketsystem_Daten(plugin);
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(!p.hasPermission("epicraft.ticket")){
			p.sendMessage(plugin.error);
			plugin.api.sendLog("[Epicraft - Ticketsystem] " + p.getName() + " hat versucht auf das Ticketsystem zuzugreifen!");
			return true;
		}
		if(args.length == 0){//Zeige Tickets an, welche noch offen sind
			if(p.hasPermission("epicraft.ticket.team")){
				int ID = data.isTeamOnTicket(p);
				if(ID > 0){
					p.sendMessage(plugin.namespace + ChatColor.RED + "Du bearbeitest gerade das Ticket[" + ID + "]!");
					return true;
				}
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Nächstes offene Ticket wird gesucht...");
				data.TicketForTeam(p);
			}
			else{
				data.showTicketsWithStateOpen(p);
			}
			return true;
		}
		else if(args.length >= 1){
			if(p.hasPermission("epicraft.ticket.team")){
				if(args[0].equalsIgnoreCase("release")){
					if(data.setTicketState(p, args[0])){
						p.sendMessage(plugin.namespace + ChatColor.WHITE + "Das Ticket wurde als \"offen\" markiert");
					}	
					return true;
				}
				else if(args[0].equalsIgnoreCase("finish")){
					if(data.setTicketState(p, args[0])){
						
					}
					return true;
				}
				else if(args[0].equalsIgnoreCase("show")){
					int ID = 0;
					try{
						ID = Integer.valueOf(args[1]);
					}
					catch (NumberFormatException nfe){
						p.sendMessage(plugin.namespace + ChatColor.RED + args[1] + " ist keine Zahl!");
						p.sendMessage(plugin.namespace + ChatColor.RED + "/ticket show <ZAHL>");
						return true;
					}
					catch (IndexOutOfBoundsException ioobe){
						p.sendMessage(plugin.namespace + ChatColor.RED + "Keine Zahl eingegeben!");
						p.sendMessage(plugin.namespace + ChatColor.RED + "/ticket show <ZAHL>");
						return true;
					}
					data.showTicketWithNumber(p, ID, false);
					return true;
				}
				else if(args[0].equalsIgnoreCase("warp")){
					int ID = 0;
					try{
						ID = Integer.valueOf(args[1]);
					}
					catch (NumberFormatException nfe){
						p.sendMessage(plugin.namespace + ChatColor.RED + args[1] + " ist keine Zahl!");
						p.sendMessage(plugin.namespace + ChatColor.RED + "/ticket show <ZAHL>");
						return true;
					}
					data.showTicketWithNumber(p, ID, true);
					return true;
				}
				else if(args[0].equalsIgnoreCase("back")){
					if(p.isInsideVehicle()){
						p.sendMessage(plugin.namespace + ChatColor.RED + "Du kannst nicht teleportiert werden!");
						return true;
					}
					Location lastLocation = data.getLastLocationFromPlayer(p.getUniqueId());
					if(lastLocation == null){
						p.sendMessage(plugin.namespace + ChatColor.RED + "Es wurde keine gespeicherte Location gefunden!");
						return true;
					}
					p.teleport(lastLocation);
					p.sendMessage(plugin.namespace + ChatColor.WHITE + "Du wurdest zurück teleportiert");
					return true;
				}
			}
			String ticket = "";
			for(String tmp : args){
				ticket += tmp + " ";
			}
			if(ticket.length() > 99){
				p.sendMessage(plugin.namespace + ChatColor.RED + "Dein Ticket ist zu lang, bitte kürzen!");
			}
			if(data.createTicket(p, ticket)){
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Wir haben dein Ticket erhalten. Vielen dank für deine Meldung!");
			}
			else{
				p.sendMessage(plugin.namespace + ChatColor.RED + "Ticket konnte nicht erstellt werden");
			}
			return true;
		}
		else {
			p.sendMessage(plugin.namespace + ChatColor.RED + "Bitte gib ein Grund für das Ticket an!");
			return true;
		}
	}

}
