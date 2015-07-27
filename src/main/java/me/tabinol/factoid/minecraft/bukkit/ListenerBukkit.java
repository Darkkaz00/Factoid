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

import java.util.Iterator;
import java.util.List;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.event.bukkit.PlayerContainerAddNoEnterEvent;
import me.tabinol.factoid.event.bukkit.PlayerContainerLandBanEvent;
import me.tabinol.factoid.event.bukkit.PlayerLandChangeEvent;
import me.tabinol.factoid.lands.areas.Point;
import me.tabinol.factoid.listeners.ChatListener;
import me.tabinol.factoid.listeners.CommonListener.Click;
import me.tabinol.factoid.listeners.LandListener;
import me.tabinol.factoid.listeners.PlayerListener;
import me.tabinol.factoid.listeners.PvpListener;
import me.tabinol.factoid.listeners.WorldListener;
import me.tabinol.factoid.minecraft.FPlayer;
import me.tabinol.factoid.minecraft.Item;
import me.tabinol.factoid.minecraft.Listener;
import me.tabinol.factoid.parameters.FlagList;
import me.tabinol.factoid.parameters.FlagType;
import me.tabinol.factoid.parameters.FlagValue;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Flying;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Tameable;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
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
import org.bukkit.plugin.java.JavaPlugin;

// TODO Put Citizens bugfix

public class ListenerBukkit implements Listener, org.bukkit.event.Listener {

    private final ChatListener chatListener;
    private final LandListener landListener;
    private final PlayerListener playerListener;
    private final PvpListener pvpListener;
    private final WorldListener worldListener;
    
    public ListenerBukkit(JavaPlugin plugin) {
    	
    	chatListener = new ChatListener();
    	landListener = new LandListener();
    	playerListener = new PlayerListener();
    	pvpListener = new PvpListener();
    	worldListener = new WorldListener();
    	
    	plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
	
	/**************************************************************************
	 * Bukkit events
	 *************************************************************************/
    
    @EventHandler(priority = EventPriority.MONITOR)
	public void onWorldLoadMonitor(WorldLoadEvent event) {
		
		// Add world to list
		Factoid.getServerCache().addWorld(new FWorldBukkit(event.getWorld()));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onWorldUnloadMonitor(WorldUnloadEvent event) {
		
		// Remove world to list
		Factoid.getServerCache().removeWorld(new FWorldBukkit(event.getWorld()));
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoinMonitor(PlayerJoinEvent event) {
		
		FPlayer player = new FPlayerBukkit(event.getPlayer());
		
		Factoid.getServerCache().addPlayer(player); // Add player in the list
		playerListener.onPlayerJoinMonitor(player);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuitMonitor(PlayerQuitEvent event) {

		FPlayer player = Factoid.getServerCache().getPlayer(event.getPlayer().getUniqueId());

		landListener.onPlayerQuitMonitor(player);
		Factoid.getServerCache().removePlayer(player); // Remove player from the list
		playerListener.onPlayerQuitMonitor(player);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerTeleportMonitor(PlayerTeleportEvent event) {
		
		FPlayer player = Factoid.getServerCache().getPlayer(event.getPlayer().getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

		if(playerListener.onPlayerTeleport(player, BukkitUtils.toPoint(event.getTo()),
				event.getCause() == TeleportCause.ENDER_PEARL)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerMoveMonitor(PlayerMoveEvent event) {

		FPlayer player = Factoid.getServerCache().getPlayer(event.getPlayer().getUniqueId());

		// Real player?
		if(player == null) {
			return;
		}

		playerListener.onPlayerMoveMonitor(player, 
				BukkitUtils.toPoint(event.getFrom()), BukkitUtils.toPoint(event.getTo()));
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
		Item itemInHand = new ItemBukkit(event.getPlayer().getItemInHand().getType());
		
    	FPlayer player = Factoid.getServerCache().getPlayer(event.getPlayer().getUniqueId());
		if(playerListener.onPlayerInteract(player, click, itemInHand, 
				new ItemBukkit(event.getClickedBlock().getType()),
				BukkitUtils.toPoint(event.getClickedBlock().getLocation()))) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		
		FPlayer player = Factoid.getServerCache().getPlayer(event.getPlayer().getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

		if(playerListener.onBlockPlace(player, new ItemBukkit(event.getBlockPlaced().getType()),
				BukkitUtils.toPoint(event.getBlockPlaced().getLocation()))) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlaceMonitor(BlockPlaceEvent event) {
		
		FPlayer player = Factoid.getServerCache().getPlayer(event.getPlayer().getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

		pvpListener.onBlockPlaceMonitor(player, new ItemBukkit(event.getBlockPlaced().getType()),
				BukkitUtils.toPoint(event.getBlockPlaced().getLocation()));
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		
		FPlayer player = Factoid.getServerCache().getPlayer(event.getPlayer().getUniqueId());
		
		if(playerListener.onPlayerInteractEntity(player, event.getRightClicked().getType().name(),
				BukkitUtils.toPoint(event.getRightClicked().getLocation()))) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		
		FPlayer player = Factoid.getServerCache().getPlayer(((Player) event.getPlayer()).getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

		if(playerListener.onBlockBreak(player, new ItemBukkit(event.getBlock().getType()),
				BukkitUtils.toPoint(event.getBlock().getLocation()))) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		
		FPlayer player = Factoid.getServerCache().getPlayer(event.getPlayer().getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

		if(playerListener.onPlayerDropItem(player, new ItemBukkit(event.getItemDrop().getType()),
				BukkitUtils.toPoint(event.getItemDrop().getLocation()))) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		
		FPlayer player = Factoid.getServerCache().getPlayer(event.getPlayer().getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

		if(playerListener.onPlayerPickupItem(player, new ItemBukkit(event.getItem().getType()),
				BukkitUtils.toPoint(event.getItem().getLocation()))) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerBedEnter(PlayerBedEnterEvent event) {
		
		FPlayer player = Factoid.getServerCache().getPlayer(event.getPlayer().getUniqueId());
		
		if(playerListener.onPlayerBedEnter(player, BukkitUtils.toPoint(event.getBed().getLocation()))) {
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
		boolean isPlayer = entity instanceof Player;
		boolean isAnimal = entity instanceof Animals;
		boolean isMonster = entity instanceof Monster;
		boolean isTamedAndNotOwner = false;
		
		// Is tamed and the player is not owner?
		if(entity instanceof Tameable && ((Tameable) entity).isTamed()
				&& ((Tameable) entity).getOwner() != mplayer) {
			isTamedAndNotOwner = true;
		}
		
		FPlayer player = Factoid.getServerCache().getPlayer(mplayer.getUniqueId());
		Point locPlayer = BukkitUtils.toPoint(mplayer.getLocation());
		Point locVictime = BukkitUtils.toPoint(entity.getLocation());

		// Real player?
		if(player == null) {
			return;
		}

		if(playerListener.onEntityDamageByEntity(player, new ItemBukkit(entity.getType()),
				locVictime,
				isAnimal, isMonster, isTamedAndNotOwner)) {
			event.setCancelled(true);
		
		} else if(isPlayer) { 
			
			// PVP
			FPlayer victime = Factoid.getServerCache().getPlayer(entity.getUniqueId());
			
			if(victime != null && pvpListener.onPlayerDamageByPlayer(player, victime, locPlayer, locVictime)) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerBucketFill(PlayerBucketFillEvent event) {
		
		FPlayer player = Factoid.getServerCache().getPlayer(event.getPlayer().getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

		if(playerListener.onPlayerBucketFill(player, new ItemBukkit(event.getBlockClicked().getType()),
				BukkitUtils.toPoint(event.getBlockClicked().getLocation()))) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {

		FPlayer player = Factoid.getServerCache().getPlayer(event.getPlayer().getUniqueId());
		Block block = event.getBlockClicked().getRelative(event.getBlockFace());
		
		// Real player?
		if(player == null) {
			return;
		}

		if(playerListener.onPlayerBucketEmpty(player, new ItemBukkit(event.getBucket()),
				BukkitUtils.toPoint(block.getLocation()))) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		
		
		// All entities section
		if(worldListener.onEntityChangeBlock(BukkitUtils.toPoint(event.getBlock().getLocation()),
				event.getEntityType().name(),
				new ItemBukkit(event.getBlock().getType()), new ItemBukkit(event.getTo()))) {
			event.setCancelled(true);
			return;
		}
		
		// Players only
		if(!(event.getEntity() instanceof Player)) {
			return;
		}

		FPlayer player = Factoid.getServerCache().getPlayer(event.getEntity().getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

    	if(playerListener.onPlayerChangeBlock(player, new ItemBukkit(event.getBlock().getType()),
    			new ItemBukkit(event.getTo()), BukkitUtils.toPoint(event.getBlock().getLocation()))) {
    		event.setCancelled(true);
    	}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		
    	FPlayer player = Factoid.getServerCache().getPlayer(event.getPlayer().getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

    	Point newLoc = playerListener.onPlayerRespawn(player, BukkitUtils.toPoint(event.getRespawnLocation()));
    	
    	if(newLoc != null) {
    		event.setRespawnLocation(BukkitUtils.toLocation(Bukkit.getWorld(newLoc.getWorldName()), newLoc));
    	}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	// For land listener
	public void onPlayerRespawnMonitor(PlayerRespawnEvent event) {
		
    	FPlayer player = Factoid.getServerCache().getPlayer(event.getPlayer().getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

    	playerListener.onPlayerRespawnMonitor(player, BukkitUtils.toPoint(event.getRespawnLocation()));
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockIgnite(BlockIgniteEvent event) {
		
		Point loc = BukkitUtils.toPoint(event.getBlock().getLocation());

		// Natural cause
		if (event.getCause() == IgniteCause.SPREAD || event.getCause() == IgniteCause.LAVA
				&& worldListener.onBlockIgniteNatural(loc)) {
			event.setCancelled(true);
			return;
		}
		
		// Player cause
		if(event.getPlayer() == null) {
			return;
		}
		
		FPlayer player = Factoid.getServerCache().getPlayer(event.getPlayer().getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

		if(playerListener.onBlockIgnite(player, loc)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockIgniteMonitor(BlockIgniteEvent event) {
		
		if(event.getPlayer() == null) {
			return;
		}
		
		FPlayer player = Factoid.getServerCache().getPlayer(event.getPlayer().getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

		pvpListener.onBlockIgniteMonitor(player, BukkitUtils.toPoint(event.getBlock().getLocation()));
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPotionSplash(PotionSplashEvent event) {
		
		// Not a player?
		if (event.getEntity() == null
				|| !(event.getEntity().getShooter() instanceof Player)) {
			return;
		}
		
		FPlayer player = Factoid.getServerCache().getPlayer(((Player) event.getEntity().getShooter()).getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

		if(playerListener.onPotionSplash(player, BukkitUtils.toPoint(event.getEntity().getLocation()))) {
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
		
		// Players only
		if(!(event.getEntity() instanceof Player)) {
			return;
		}
		
		FPlayer player = Factoid.getServerCache().getPlayer(event.getEntity().getUniqueId());

		// Real player?
		if(player == null) {
			return;
		}

		if(playerListener.onEntityRegainHealth(player, BukkitUtils.toPoint(event.getEntity().getLocation()))) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		
		FPlayer player = Factoid.getServerCache().getPlayer(event.getPlayer().getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

		if(playerListener.onPlayerItemConsume(player, BukkitUtils.toPoint(event.getPlayer().getLocation()))) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		
		FPlayer player = Factoid.getServerCache().getPlayer(event.getPlayer().getUniqueId());
		String commandTyped = event.getMessage().substring(1).split(" ")[0];

		if(playerListener.onPlayerCommandPreprocess(player, BukkitUtils.toPoint(event.getPlayer().getLocation()),
				commandTyped)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		
		Point loc = BukkitUtils.toPoint(event.getEntity().getLocation());
		
		// Hanging break
		if(event.getEntity() instanceof Hanging
                && (event.getCause() == DamageCause.BLOCK_EXPLOSION 
                || event.getCause() == DamageCause.ENTITY_EXPLOSION
                || event.getCause() == DamageCause.PROJECTILE)
                && worldListener.onHangingBreakExplosion(loc)) {
			event.setCancelled(true);
			return;
		}
		
		// Not a player
		if(!(event.getEntity() instanceof Player)) {
			
			return;
		}
		
		FPlayer player = Factoid.getServerCache().getPlayer(event.getEntity().getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

		if(playerListener.onPlayerDamage(player, loc)) {
			event.setCancelled(true);
		} else {
			
			Block block = event.getEntity().getLocation().getBlock();
			
			if(pvpListener.onPlayerDamage(player, loc, event.getCause().name(), 
					new ItemBukkit(block.getType()))) {
				block.setType(Material.AIR);
				event.getEntity().setFireTicks(0);
				event.setDamage(0);
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
		
		FPlayer player = Factoid.getServerCache().getPlayer(event.getPlayer().getUniqueId());

		// Check if item in hand is present
		Item itemInHandType = null;
		if(event.getPlayer().getItemInHand().getType() != Material.AIR) {
			itemInHandType = new ItemBukkit(event.getPlayer().getItemInHand().getType());
		}
		
		if(playerListener.onPlayerInteractAtEntity(player, event.getRightClicked().getType().name(),
				itemInHandType, BukkitUtils.toPoint(event.getPlayer().getLocation()))) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockSpreadMonitor(BlockSpreadEvent event) {
		
		pvpListener.onBlockSpreadMonitor(BukkitUtils.toPoint(event.getSource().getLocation()),
				BukkitUtils.toPoint(event.getBlock().getLocation()));
	}

    /**************************************************************************
	 * World events
	 *************************************************************************/

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onExplosionPrime(ExplosionPrimeEvent event) {
    	
		Entity entity = event.getEntity();
		
		if(entity == null) {
			return;
		}
		
		if(worldListener.onExplosionPrime(BukkitUtils.toPoint(entity.getLocation()), 
				entity.getType().name())) {
			event.setCancelled(true);
            if (entity.getType() == EntityType.CREEPER) {
                event.getEntity().remove();
            }
		}
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
    	
        if (event.getEntity() == null) {
            return;
        }

        if (Factoid.getConf().isOverrideExplosions()) {

            float power;

            // Creeper Explosion
            if (event.getEntityType() == EntityType.CREEPER) {
               	power = 0L;
                ExplodeBlocks(event, event.blockList(), FlagList.CREEPER_DAMAGE.getFlagType(), event.getLocation(),
                        event.getYield(), power, false, true);

                //  Wither
            } else if (event.getEntityType() == EntityType.WITHER_SKULL) {
                ExplodeBlocks(event, event.blockList(), FlagList.WITHER_DAMAGE.getFlagType(), event.getLocation(),
                        event.getYield(), 1L, false, true);
            } else if (event.getEntityType() == EntityType.WITHER) {
                ExplodeBlocks(event, event.blockList(), FlagList.WITHER_DAMAGE.getFlagType(), event.getLocation(),
                        event.getYield(), 7L, false, true);

                // Ghast
            } else if (event.getEntityType() == EntityType.FIREBALL) {
                ExplodeBlocks(event, event.blockList(), FlagList.GHAST_DAMAGE.getFlagType(), event.getLocation(),
                        event.getYield(), 1L, true, true);

                // TNT
            } else if (event.getEntityType() == EntityType.MINECART_TNT
                    || event.getEntityType() == EntityType.PRIMED_TNT) {
                ExplodeBlocks(event, event.blockList(), FlagList.TNT_DAMAGE.getFlagType(), event.getLocation(),
                        event.getYield(), 4L, false, true);
            } else if (event.getEntityType() == EntityType.ENDER_DRAGON) {
                ExplodeBlocks(event, event.blockList(), FlagList.ENDERDRAGON_DAMAGE.getFlagType(), event.getLocation(),
                        event.getYield(), 4L, false, false);
            }
        }
    }
    
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onHangingBreak(HangingBreakEvent event) {
    	
		Entity entity = event.getEntity();
		
		if(entity == null) {
			return;
		}
		
		if(event.getCause() == RemoveCause.EXPLOSION
				&& worldListener.onHangingBreakExplosion(BukkitUtils.toPoint(entity.getLocation()))) {
			event.setCancelled(true);
		}
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event) {
    	
    	if(worldListener.onBlockBurn(BukkitUtils.toPoint(event.getBlock().getLocation()))) {
			event.setCancelled(true);
		}
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
    	
    	Entity entity = event.getEntity();
    	boolean isAnimal = false;
    	boolean isMob = false;
    	
    	if(entity instanceof Animals) {
    		isAnimal = true;
    	}
    	
    	if(event.getEntity() instanceof Monster
                || event.getEntity() instanceof Slime
                || event.getEntity() instanceof Flying) {
    		isMob = true;
    	}
    	
    	if(worldListener.onCreatureSpawn(BukkitUtils.toPoint(event.getLocation()), 
    			isAnimal, isMob)) {
    		event.setCancelled(true);
    	}
    }
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onLeavesDecay(LeavesDecayEvent event) {
    	
    	if(worldListener.onLeavesDecay(BukkitUtils.toPoint(event.getBlock().getLocation()))) {
    		event.setCancelled(true);
    	}
    }
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
    	
    	if(worldListener.onBlockFromTo(BukkitUtils.toPoint(event.getBlock().getLocation()),
    			new ItemBukkit(event.getBlock().getType()))) {
    		event.setCancelled(true);
    	}
    	
    }

    /**************************************************************************
	 * Factoid events
	 *************************************************************************/

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerLandChange(PlayerLandChangeEvent event) {
    	
		if(landListener.onPlayerLandChange(event.getFPlayer(), event.getLastLand(), event.getLand(),
				event.getToLoc())) {
			event.setCancelled(true);
		}
    }
	
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerContainerLandBan(PlayerContainerLandBanEvent event) {

        landListener.checkForBannedPlayers(event.getLand(), 
        		event.getPlayerContainer(), "ACTION.BANNED");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerContainerAddNoEnter(PlayerContainerAddNoEnterEvent event) {

    	landListener.checkForBannedPlayers(event.getLand(), 
    			event.getPlayerContainer(), "ACTION.NOENTRY");
    }

	/**************************************************************************
	 * Private methods
	 *************************************************************************/
	
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

    /**
     * Explode blocks.
     *
     * @param event The cancellable event
     * @param blocks the blocks
     * @param ft the flag type
     * @param loc the location
     * @param yield the yield
     * @param power the power
     * @param setFire the set fire
     * @param doExplosion the do explosion
     */
    private void ExplodeBlocks(Cancellable event, List<Block> blocks, FlagType ft, Location loc,
            float yield, float power, boolean setFire, boolean doExplosion) {

        FlagValue value;
        boolean cancelEvent = false;
        Iterator<Block> itBlock = blocks.iterator();
        Block block;

        Factoid.getFactoidLog().write("Explosion : " + ", Yield: " + yield + ", power: " + power);

        // Check if 1 block or more is in a protected place
        while(itBlock.hasNext() && !cancelEvent) {
        	block = itBlock.next();
        	value = Factoid.getLands().getLandOrOutsideArea(
        			BukkitUtils.toPoint(block.getLocation())).getFlagAndInherit(ft);
            if (value.getValueBoolean() == false) {
                cancelEvent = true;
            }
        }
        
        if(cancelEvent) {
        	// Cancel Event and do a false explosion
        	event.setCancelled(true);
            loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(),
                    power, setFire, false);
        }
        
        // If not the event will be executed has is
    }
}
