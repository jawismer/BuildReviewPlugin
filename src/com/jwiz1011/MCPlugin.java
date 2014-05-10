// Created by John Wismer © 2013
// Plugin for Nashborough Minecraft Server

package com.jwiz1011;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;

public final class MCPlugin extends JavaPlugin {
	
	public static ArrayList<String> submittedPlayerList = new ArrayList<String>();
	public static HashMap<String, Location> submittedBuilds = new HashMap<String, Location>();
	public static HashMap<String, Boolean> approvedBuilds = new HashMap<String, Boolean>();
	
	public static ArrayList<String> reviewers = new ArrayList<String>();
	public static ArrayList<String> submitters = new ArrayList<String>();
	public static ArrayList<String> timestamps = new ArrayList<String>();
	public static ArrayList<String> reviewStatus = new ArrayList<String>();
	
	public static String approvalMessage = "Your build has been approved!";
	public static String rejectionMessage = "Your build has not been approved at this time."
			+ " The mods encourage you to keep working on it and resubmit it at a later time.";
	
	// Plugin enable handler
	// still needs handler for reload
	// onEnable is not compatible with reloads
    public void onEnable(){
		this.getServer().getPluginManager().registerEvents(new EventListener(), this);
		this.getServer().getPluginManager().registerEvents(new Economy(), this);
        getLogger().info("MCPlugin is working properly.");
    }
    
    // command handler
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	
    	// Submit build for review
    	if (cmd.getName().equalsIgnoreCase("submitbuild")){
    		if (sender instanceof Player){
    			Player player = (Player) sender;
    			if(!submittedPlayerList.contains(player.getName())){
    				submittedPlayerList.add(player.getName());
    			}
    			submittedBuilds.put(player.getName(), player.getLocation());
    			sender.sendMessage(ChatColor.AQUA+"Your build has been submitted for approval");
    			for(int i = 0; i < Bukkit.getServer().getOnlinePlayers().length; i++){
    				if(Bukkit.getServer().getOnlinePlayers()[i].isOp()){
    					Bukkit.getServer().getOnlinePlayers()[i].sendMessage(ChatColor.DARK_AQUA+"[MOD MESSAGE] "
    							+ChatColor.AQUA+"New builds submitted for approval. Use /reviewlist");
    				}
    			}
    			return true;
    		}else {
    	    	sender.sendMessage(ChatColor.RED+"You must be a player to use that command.");
    	    	return false;
    	    }
    	}
    	
    	// displays builds submitted for review (op only)
    	if(cmd.getName().equalsIgnoreCase("reviewlist")){
    		if (sender instanceof Player){
    			sender.sendMessage(ChatColor.WHITE+"----- "+ChatColor.DARK_AQUA+"Builds Submitted for Review"+ChatColor.WHITE+" -----");
    			if(submittedBuilds.isEmpty()){
    				sender.sendMessage(ChatColor.AQUA+" No builds");
    			}else{
	    			for(int i = 0; i < submittedPlayerList.size(); i++){
	    				sender.sendMessage(ChatColor.WHITE+" - "+ChatColor.AQUA+submittedPlayerList.get(i));
	    			}
    			}
    			return true;
    		}else {
    	    	sender.sendMessage(ChatColor.RED+"You must be a player to use that command.");
    	    	return false;
    	    }
    	}
    	
    	// displays review log (op only)
    	if(cmd.getName().equalsIgnoreCase("reviewlog")){
    		if (sender instanceof Player){
    			if(submitters.isEmpty()){
    				sender.sendMessage(ChatColor.WHITE+"----- "+ChatColor.DARK_AQUA+"Builds Reviewed (page 1/"+(int)(Math.ceil((double)submitters.size()/3))+")"+ChatColor.WHITE+" -----");
    				sender.sendMessage(ChatColor.AQUA+" No builds");
    			}else{
    				if(args.length == 0){
    					sender.sendMessage(ChatColor.WHITE+"----- "+ChatColor.DARK_AQUA+"Builds Reviewed (page 1/"+(int)(Math.ceil((double)submitters.size()/3))+")"+ChatColor.WHITE+" -----");
		    			for(int i = submitters.size()-1; i >= ((submitters.size()-3) + Math.abs(submitters.size()-3))/2; i--){
		    				sender.sendMessage(ChatColor.WHITE+" - "+ChatColor.AQUA+reviewers.get(i)+reviewStatus.get(i)+submitters.get(i)+"'s build on "+timestamps.get(i));
		    			}
    				}else{
    					int pageNum = Integer.parseInt(args[0]);
    					sender.sendMessage(ChatColor.WHITE+"----- "+ChatColor.DARK_AQUA+"Builds Reviewed (page "+pageNum+"/"+(int)(Math.ceil((double)submitters.size()/3))+")"+ChatColor.WHITE+" -----");
    					for(int i = (submitters.size()-1)-((pageNum-1)*3); i >= ((((submitters.size()-3)-((pageNum-1)*3)) + Math.abs((submitters.size()-3)-((pageNum-1)*3)))/2); i--){
		    				sender.sendMessage(ChatColor.WHITE+" - "+ChatColor.AQUA+reviewers.get(i)+reviewStatus.get(i)+submitters.get(i)+"'s build on "+timestamps.get(i));
		    			}
    				}
    			}
    			return true;
    		}else {
    	    	sender.sendMessage(ChatColor.RED+"You must be a player to use that command.");
    	    	return false;
    	    }
    	}
    	
    	
    	//reviews build (op only)
    	if(cmd.getName().equalsIgnoreCase("reviewbuild")){
    		if (sender instanceof Player){
    			if(args.length == 0){
    				sender.sendMessage(ChatColor.RED+ "Usage: /reviewbuild [player name] [-i]");
    				return true;
    			}
    			String playerName = args[0];
    			Player reviewer = (Player) sender;
				if(args.length == 2){
					if(args[1].equalsIgnoreCase("i")){
						if(Bukkit.getServer().getOfflinePlayer(playerName).isOnline()){
							sender.sendMessage(ChatColor.LIGHT_PURPLE+" "+ChatColor.ITALIC+"You are now hidden from "+playerName);
							Bukkit.getServer().getPlayer(playerName).hidePlayer(reviewer);
						}
					}
    			}
    			if(submittedBuilds.containsKey(playerName)){
    				reviewer.teleport(submittedBuilds.get(playerName));
    				sender.sendMessage(ChatColor.DARK_AQUA+playerName+"'s Build");
    			}else{
    				sender.sendMessage(ChatColor.RED+ "Player does not exist.");
    				return true;
    			}
    			return true;
    		}else {
    	    	sender.sendMessage(ChatColor.RED+"You must be a player to use that command.");
    	    	return false;
    	    }
    	}
    	
    	// approve build (op only)	
    	if(cmd.getName().equalsIgnoreCase("approvebuild")){
    		if (sender instanceof Player){
    			if(args.length == 0){
    				sender.sendMessage(ChatColor.RED+ "Usage: /approvebuild [player name]");
    				return true;
    			}
    			String playerName = args[0];
    			Player reviewer = (Player) sender;
    			Date reviewDate = new Date();
    			if(submittedBuilds.containsKey(playerName)){
    				sender.sendMessage(ChatColor.DARK_AQUA+playerName+"'s build was approved");
    				reviewers.add(sender.getName());
    				submitters.add(playerName);
    				timestamps.add(reviewDate.toString());
    				reviewStatus.add(" approved ");
    				submittedBuilds.remove(playerName);
    				submittedPlayerList.remove(playerName);
    				approvedBuilds.put(playerName, true);
    				if(Bukkit.getServer().getOfflinePlayer(playerName).isOnline()){
    					Bukkit.getServer().getPlayer(playerName).sendMessage(ChatColor.DARK_AQUA+"[MESSAGE FROM THE MODS] "+ChatColor.AQUA+approvalMessage);
    					Bukkit.getServer().getPlayer(playerName).setGameMode(GameMode.CREATIVE);
    					Bukkit.getServer().getPlayer(playerName).showPlayer(reviewer);
    					sender.sendMessage(ChatColor.LIGHT_PURPLE+" "+ChatColor.ITALIC+"You are now visible to "+playerName);
    				}
    				return true;
    			}else{
    				sender.sendMessage(ChatColor.RED+ "Player does not exist.");
    				return true;
    			}
    		}else {
    	    	sender.sendMessage(ChatColor.RED+"You must be a player to use that command.");
    	    	return false;
    	    }
    	}
    	//reject build (op only)
    	if(cmd.getName().equalsIgnoreCase("rejectbuild")){
    		if (sender instanceof Player){
    			if(args.length == 0){
    				sender.sendMessage(ChatColor.RED+ "Usage: /rejectbuild [player name]");
    				return true;
    			}
    			String playerName = args[0];
    			Player reviewer = (Player) sender;
    			Date reviewDate = new Date();
    			if(submittedBuilds.containsKey(playerName)){
    				sender.sendMessage(ChatColor.DARK_AQUA+playerName+"'s build was denied.");
    				reviewers.add(sender.getName());
    				submitters.add(playerName);
    				timestamps.add(reviewDate.toString());
    				reviewStatus.add(" rejected ");
    				submittedBuilds.remove(playerName);
    				submittedPlayerList.remove(playerName);
    				approvedBuilds.put(playerName, false);
    				if(Bukkit.getServer().getOfflinePlayer(playerName).isOnline()){
    					Bukkit.getServer().getPlayer(playerName).sendMessage(ChatColor.DARK_AQUA+"[MESSAGE FROM THE MODS] "+ChatColor.AQUA+rejectionMessage);
    					Bukkit.getServer().getPlayer(playerName).showPlayer(reviewer);
    					sender.sendMessage(ChatColor.LIGHT_PURPLE+" "+ChatColor.ITALIC+"You are now visible to "+playerName);
    				}
    				return true;
    			}else{
    				sender.sendMessage(ChatColor.RED+ "Player does not exist.");
    				return true;
    			}
    		}else {
    	    	sender.sendMessage(ChatColor.RED+"You must be a player to use that command.");
    	    	return false;
    	    }
    	}
    	return true;
    }
    
    
}
