/*
 Factoid: Lands and Factions plugin for Minecraft server
 Copyright (C) 2014 Kaz00, Tabinol

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.
 */ 

package me.tabinol.factoid.minecraft.bukkit;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.FactoidBukkit;
import me.tabinol.factoid.lands.areas.Point;
import me.tabinol.factoid.listeners.ChatListener;
import me.tabinol.factoid.listeners.CommonListener.Click;
import me.tabinol.factoid.listeners.LandListener;
import me.tabinol.factoid.listeners.PlayerListener;
import me.tabinol.factoid.listeners.PvpListener;
import me.tabinol.factoid.listeners.WorldListener;
import me.tabinol.factoid.minecraft.FPlayer;
import me.tabinol.factoid.minecraft.Listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import com.google.common.base.Optional;
import com.sk89q.worldedit.blocks.ItemType;

// TODO: Put Citizens bugfix

public class ListenerBukkit implements Listener, org.bukkit.event.Listener {

    private final ChatListener chatListener;
    private final LandListener landListener;
    private final PlayerListener playerListener;
    private final PvpListener pvpListener;
    private final WorldListener worldListener;
    
    public ListenerBukkit(FactoidBukkit plugin) {
    	
    	chatListener = new ChatListener();
    	landListener = new LandListener();
    	playerListener = new PlayerListener();
    	pvpListener = new PvpListener();
    	worldListener = new WorldListener();
    	
    	plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onWorldLoadMonitor(WorldLoadEvent event) {
		
		// Add world to list
		Factoid.getServer().addWorld(new FWorldBukkit(event.getWorld()));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onWorldUnloadMonitor(WorldUnloadEvent event) {
		
		// Remove world to list
		Factoid.getServer().removeWorld(new FWorldBukkit(event.getWorld()));
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoinMonitor(PlayerJoinEvent event) {
		
		FPlayer player = new FPlayerBukkit(event.getPlayer());
		
		Factoid.getServer().addPlayer(player); // Add player in the list
		playerListener.onPlayerJoinMonitor(player);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuitMonitor(PlayerQuitEvent event) {

		FPlayer player = Factoid.getServer().getPlayer(event.getPlayer().getUniqueId());
		
		Factoid.getServer().removePlayer(player); // Remove player from the list
		playerListener.onPlayerQuitMonitor(player);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerTeleportMonitor(PlayerTeleportEvent event) {
		
		FPlayer player = Factoid.getServer().getPlayer(event.getPlayer().getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

		if(playerListener.onPlayerTeleport(player, toPoint(event.getTo()),
				event.getCause() == TeleportCause.ENDER_PEARL)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerMoveMonitor(PlayerMoveEvent event) {

		FPlayer player = Factoid.getServer().getPlayer(event.getPlayer().getUniqueId());

		// Real player?
		if(player == null) {
			return;
		}

		playerListener.onPlayerMoveMonitor(player, 
				toPoint(event.getFrom()), toPoint(event.getTo()));
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
    	
		FPlayer player = new FPlayerBukkit(event.getPlayer());
		
		if(chatListener.onAsyncPlayerChat(player, event.getMessage())) {
			event.setCancelled(true);
		}
    }
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
			
		// get click
		Click click;
		if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
			click = Click.LEFT;
		} else if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			click = Click.RIGHT;
		} else if(event.getAction() == Action.PHYSICAL) {
			click = Click.NONE;
		} else {
			return;
		}
		
		// Check item in hand
		String itemInHand = event.getPlayer().getItemInHand().getType().name();
		
    	FPlayer player = Factoid.getServer().getPlayer(event.getPlayer().getUniqueId());
		if(playerListener.onPlayerInteract(player, click, itemInHand, event.getClickedBlock().getType().name(),
				toPoint(event.getClickedBlock().getLocation()))) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		
		FPlayer player = Factoid.getServer().getPlayer(event.getPlayer().getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

		if(playerListener.onBlockPlace(player, event.getBlockPlaced().getType().name(),
				toPoint(event.getBlockPlaced().getLocation()))) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		
		FPlayer player = Factoid.getServer().getPlayer(event.getPlayer().getUniqueId());
		
		if(playerListener.onPlayerInteractEntity(player, event.getRightClicked().getType().name(),
				toPoint(event.getRightClicked().getLocation()))) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		
		FPlayer player = Factoid.getServer().getPlayer(((Player) event.getPlayer()).getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

		if(playerListener.onBlockBreak(player, event.getBlock().getType().name(),
				toPoint(event.getBlock().getLocation()))) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		
		FPlayer player = Factoid.getServer().getPlayer(event.getPlayer().getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

		if(playerListener.onPlayerDropItem(player, event.getItemDrop().getType().name(),
				toPoint(event.getItemDrop().getLocation()))) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		
		FPlayer player = Factoid.getServer().getPlayer(event.getPlayer().getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

		if(playerListener.onPlayerPickupItem(player, event.getItem().getType().name(),
				toPoint(event.getItem().getLocation()))) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerBedEnter(PlayerBedEnterEvent event) {
		
		FPlayer player = Factoid.getServer().getPlayer(event.getPlayer().getUniqueId());
		
		if(playerListener.onPlayerBedEnter(player, toPoint(event.getBed().getLocation()))) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		
		// Get the source player
		Player mplayer = getSourcePlayer(event.getDamager());
		
		// No player found?
		if(mplayer == null) {
			return;
		}
		
		Entity entity = event.getEntity();
		boolean isAnimal = entity instanceof Animals;
		boolean isMonster = entity instanceof Monster;
		boolean isTamedAndNotOwner = false;
		
		// Is tamed and the player is not owner?
		if(entity instanceof Tameable && ((Tameable) entity).isTamed()
				&& ((Tameable) entity).getOwner() != mplayer) {
			isTamedAndNotOwner = true;
		}
		
		FPlayer player = Factoid.getServer().getPlayer(mplayer.getUniqueId());

		if(playerListener.onEntityDamageByEntity(player, entity.getType().name(),
				toPoint(event.getEntity().getLocation()),
				isAnimal, isMonster, isTamedAndNotOwner)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerBucketFill(PlayerBucketFillEvent event) {
		
		FPlayer player = Factoid.getServer().getPlayer(event.getPlayer().getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

		if(playerListener.onPlayerBucketFill(player, event.getBlockClicked().getType().name(),
				toPoint(event.getBlockClicked().getLocation()))) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {

		FPlayer player = Factoid.getServer().getPlayer(event.getPlayer().getUniqueId());
		Block block = event.getBlockClicked().getRelative(event.getBlockFace());
		
		// Real player?
		if(player == null) {
			return;
		}

		if(playerListener.onPlayerBucketEmpty(player, event.getBucket().name(),
				toPoint(block.getLocation()))) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		
		if(!(event.getEntity() instanceof Player)) {
			return;
		}
		
		FPlayer player = Factoid.getServer().getPlayer(((Player) event.getEntity()).getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

    	if(playerListener.onPlayerChangeBlock(player, event.getBlock().getType().name(),
    			event.getTo().name(), toPoint(event.getBlock().getLocation()))) {
    		event.setCancelled(true);
    	}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		
    	FPlayer player = Factoid.getServer().getPlayer(event.getPlayer().getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

    	Point newLoc = playerListener.onPlayerRespawn(player, toPoint(event.getRespawnLocation()));
    	
    	if(newLoc != null) {
    		event.setRespawnLocation(toLocation(Bukkit.getWorld(newLoc.getWorldName()), newLoc));
    	}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	// For land listener
	public void onPlayerRespawnMonitor(PlayerRespawnEvent event) {
		
    	FPlayer player = Factoid.getServer().getPlayer(event.getPlayer().getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

    	playerListener.onPlayerRespawnMonitor(player, toPoint(event.getRespawnLocation()));
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockIgnite(BlockIgniteEvent event) {
		
		if(event.getPlayer() == null) {
			return;
		}
		
		FPlayer player = Factoid.getServer().getPlayer(event.getPlayer().getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

		if(playerListener.onBlockIgnite(player, toPoint(event.getBlock().getLocation()))) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPotionSplash(PotionSplashEvent event) {
		
		// Not a player?
		if (event.getEntity() == null
				|| !(event.getEntity().getShooter() instanceof Player)) {
			return;
		}
		
		FPlayer player = Factoid.getServer().getPlayer(((Player) event.getEntity().getShooter()).getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

		if(playerListener.onPotionSplash(player, toPoint(event.getEntity().getLocation()))) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityRegainHealth(EntityRegainHealthEvent event) {
		
		// Not a player?
		if (event.getEntity() == null
				|| !(event.getEntity() instanceof Player)
				|| !(event.getRegainReason() == RegainReason.REGEN
				|| event.getRegainReason() == RegainReason.SATIATED)) {
			return;
		}
		
		FPlayer player = Factoid.getServer().getPlayer(((Player) event.getEntity()).getUniqueId());

		// Real player?
		if(player == null) {
			return;
		}

		if(playerListener.onEntityRegainHealth(player, toPoint(event.getEntity().getLocation()))) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		
		FPlayer player = Factoid.getServer().getPlayer(event.getPlayer().getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

		if(playerListener.onPlayerItemConsume(player, toPoint(event.getPlayer().getLocation()))) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		
		FPlayer player = Factoid.getServer().getPlayer(event.getPlayer().getUniqueId());
		String commandTyped = event.getMessage().substring(1).split(" ")[0];

		if(playerListener.onPlayerCommandPreprocess(player, toPoint(event.getPlayer().getLocation()),
				commandTyped)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		
		// Not a player
		if(!(event.getEntity() instanceof Player)) {
			return;
		}
		
		FPlayer player = Factoid.getServer().getPlayer(event.getEntity().getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

		if(playerListener.onPlayerDamage(player, toPoint(event.getEntity().getLocation()))) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
		
		FPlayer player = Factoid.getServer().getPlayer(event.getPlayer().getUniqueId());

		// Check if item in hand is present
		String itemInHandType = null;
		if(event.getPlayer().getItemInHand().getType() != Material.AIR) {
			itemInHandType = event.getPlayer().getItemInHand().getType().name();
		}
		
		if(playerListener.onPlayerInteractAtEntity(player, event.getRightClicked().getType().name(),
				itemInHandType, toPoint(event.getPlayer().getLocation()))) {
			event.setCancelled(true);
		}
	}


	/**************************************************************************
	 * Private methods
	 *************************************************************************/
	
	private Point toPoint(Location loc) {
		
		return new Point(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ());
	}
	
	private Location toLocation(World world, Point loc) {
		
		return new Location(world, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
	}

	/**
	 * Gets the source player from entity
	 *
	 * @param entity the entity
	 * @return the source player
	 */
	private Player getSourcePlayer(Entity entity) {
		
		Projectile damagerProjectile;

		// Check if the damager is a player
		if (entity instanceof Player) {
			return (Player) entity;
		} else if (entity instanceof Projectile
				&& entity.getType() != EntityType.EGG
				&& entity.getType() != EntityType.SNOWBALL) {
			damagerProjectile = (Projectile) entity;
			if (damagerProjectile.getShooter() instanceof Player) {
				return (Player) damagerProjectile.getShooter();
			}
		}
		
		return null;
	}
}
