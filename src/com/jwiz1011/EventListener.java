// Created by John Wismer © 2013
// Plugin for Nashborough Minecraft Server

package com.jwiz1011;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.util.Vector;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.*;
import org.bukkit.entity.Minecart;
import org.bukkit.event.player.PlayerInteractEvent;
//import org.bukkit.entity.Player;

public class EventListener implements Listener {
	// Minecart move
	@EventHandler
	public void onVehicleMove(VehicleMoveEvent event){
		if(event.getVehicle() instanceof Minecart){
			Minecart cart = (Minecart) event.getVehicle();
			cart.setSlowWhenEmpty(false);
			//cart.setMaxSpeed(0.8);
			if(event.getVehicle().getLocation().getBlock().getType() == Material.DETECTOR_RAIL && event.getVehicle().isEmpty()){
				event.getVehicle().remove();
			}

			//Vector maxVel = cart.getVelocity();
			if(event.getVehicle().getLocation().getBlock().getType() == Material.RAILS){
				cart.setMaxSpeed(0.1);
				//maxVel = cart.getVelocity();
			}
			else if(event.getVehicle().getLocation().getBlock().getType() == Material.ACTIVATOR_RAIL){
				cart.setMaxSpeed(0.4);
			}
			else{
				cart.setMaxSpeed(0.95);
			}
		}
	}
	
	@EventHandler
	public void onVehicleExit(VehicleExitEvent event){
		if(event.getVehicle() instanceof Minecart){
			event.getVehicle().remove();
		}
	}
	
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		if(event.getPlayer().isOp() && !MCPlugin.submittedPlayerList.isEmpty()){
			event.getPlayer().sendMessage(ChatColor.DARK_AQUA+"[MOD MESSAGE] "+ChatColor.AQUA+"New builds submitted for approval. Use /reviewlist");
		}
		if(MCPlugin.approvedBuilds.containsKey(event.getPlayer().getName())){
			if(MCPlugin.approvedBuilds.get(event.getPlayer().getName()) == true){
				event.getPlayer().sendMessage(ChatColor.DARK_AQUA+"[MESSAGE FROM THE MODS] "+ChatColor.AQUA+MCPlugin.approvalMessage);
				MCPlugin.approvedBuilds.remove(event.getPlayer().getName());
				event.getPlayer().setGameMode(GameMode.CREATIVE);
			}
			else if(MCPlugin.approvedBuilds.get(event.getPlayer().getName()) == false){
				event.getPlayer().sendMessage(ChatColor.DARK_AQUA+"[MESSAGE FROM THE MODS] "+ChatColor.AQUA+MCPlugin.rejectionMessage);
				MCPlugin.approvedBuilds.remove(event.getPlayer().getName());
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		if(event.getPlayer().getGameMode() == GameMode.ADVENTURE){
			if(event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK){
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED+"You do not have permission for that. Apply to become an official member to build.");
			}
		}
	}
	
	
}
