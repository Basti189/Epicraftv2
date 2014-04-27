package de.wolfsline.forfun;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.wolfsline.Epicraft.Epicraft;

public class FunEffects implements CommandExecutor{
	
	private Epicraft plugin;
	
	public FunEffects(Epicraft plugin){
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage(ChatColor.RED + "Du bist kein Spieler!");
			return true;
		}
		Player p = (Player) cs;
		if(args.length == 1){
			if(args[0].equalsIgnoreCase("drunk")){
				if(!p.hasPermission("epicraft.effect.drunk")){
					p.sendMessage(plugin.error);
					return true;
				}
				p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 1200, 1));
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Du bist nun betrunken");
				return true;
			}
			else if(args[0].equalsIgnoreCase("jump")){
				if(!p.hasPermission("epicraft.effect.jump")){
					p.sendMessage(plugin.error);
					return true;
				}
				p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1200, 50));
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Du kannst nun Superjumps ausführen");
				return true;
			}
			else if(args[0].equalsIgnoreCase("blind")){
				if(!p.hasPermission("epicraft.effect.blind")){
					p.sendMessage(plugin.error);
					return true;
				}
				p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1200, 10));
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Du bist nun blind");
				return true;
			}
			else if(args[0].equalsIgnoreCase("clear")){
				if(!p.hasPermission("epicraft.effect.clear")){
					p.sendMessage(plugin.error);
					return true;
				}
				for(PotionEffect effect : p.getActivePotionEffects()){
					p.removePotionEffect(effect.getType());
				}
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Alle Effecte wurde entfernt");
				return true;
			}
			else{
				p.sendMessage(plugin.namespace + ChatColor.RED + "Der Effect ist nicht bekannt!");
				return true;
			}
		}
		else if(args.length == 2){
			String name = args[1];
			UUID targetUUID = plugin.uuid.getUUIDFromPlayer(name);
			if(targetUUID == null){
				p.sendMessage(plugin.uuid.ERROR);
				return true;
			}
			Player targetPlayer = Bukkit.getServer().getPlayer(targetUUID);
			if(targetPlayer == null){
				p.sendMessage(plugin.namespace + ChatColor.RED + "Spieler ist nicht online!");
				return true;
			}
			if(targetPlayer.hasPermission("epicraft.permission.admin")){
				p.kickPlayer(ChatColor.RED + "Aber nur in deinen Träumen! :D");
				return true;
			}
			if(args[0].equalsIgnoreCase("drunk")){
				if(!p.hasPermission("epicraft.effect.drunk.other")){
					p.sendMessage(plugin.error);
					return true;
				}
				targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 1200, 1));
				p.sendMessage(plugin.namespace + ChatColor.WHITE + targetPlayer.getName() +  " ist nun betrunken");
				return true;
			}
			else if(args[0].equalsIgnoreCase("jump")){
				if(!p.hasPermission("epicraft.effect.jump.other")){
					p.sendMessage(plugin.error);
					return true;
				}
				targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1200, 50));
				p.sendMessage(plugin.namespace + ChatColor.WHITE + targetPlayer.getName() +  " kann nun Superjumps ausführen");
				return true;
			}
			else if(args[0].equalsIgnoreCase("blind")){
				if(!p.hasPermission("epicraft.effect.blind.other")){
					p.sendMessage(plugin.error);
					return true;
				}
				targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1200, 10));
				p.sendMessage(plugin.namespace + ChatColor.WHITE + targetPlayer.getName() +  " ist nun blind");
				return true;
			}
			else if(args[0].equalsIgnoreCase("clear")){
				if(!p.hasPermission("epicraft.effect.clear.other")){
					p.sendMessage(plugin.error);
					return true;
				}
				for(PotionEffect effect : targetPlayer.getActivePotionEffects()){
					targetPlayer.removePotionEffect(effect.getType());
				}
				p.sendMessage(plugin.namespace + ChatColor.WHITE + "Alle Effecte wurde entfernt");
				return true;
			}
			else{
				p.sendMessage(plugin.namespace + ChatColor.RED + "Der Effect ist nicht bekannt!");
				return true;
			}
		}
		p.sendMessage(plugin.namespace + ChatColor.RED + "/effect <Effet> [Spieler]");
		return true;
	}

}
