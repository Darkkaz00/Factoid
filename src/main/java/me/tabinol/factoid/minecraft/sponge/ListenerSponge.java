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

package me.tabinol.factoid.minecraft.sponge;

import java.util.Iterator;
import java.util.List;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.event.sponge.PlayerContainerAddNoEnterEvent;
import me.tabinol.factoid.event.sponge.PlayerContainerLandBanEvent;
import me.tabinol.factoid.event.sponge.PlayerLandChangeEvent;
import me.tabinol.factoid.lands.areas.Point;
import me.tabinol.factoid.listeners.ChatListener;
import me.tabinol.factoid.listeners.CommonListener.Click;
import me.tabinol.factoid.listeners.LandListener;
import me.tabinol.factoid.listeners.PlayerListener;
import me.tabinol.factoid.listeners.PvpListener;
import me.tabinol.factoid.listeners.WorldListener;
import me.tabinol.factoid.minecraft.FPlayer;
import me.tabinol.factoid.minecraft.Listener;
import me.tabinol.factoid.parameters.FlagList;
import me.tabinol.factoid.parameters.FlagType;
import me.tabinol.factoid.parameters.FlagValue;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityInteractionTypes;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.animal.Animal;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.block.BlockBurnEvent;
import org.spongepowered.api.event.block.BlockChangeEvent;
import org.spongepowered.api.event.block.BlockIgniteEvent;
import org.spongepowered.api.event.block.LeafDecayEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.EntityChangeBlockEvent;
import org.spongepowered.api.event.entity.EntityExplosionEvent;
import org.spongepowered.api.event.entity.EntitySpawnEvent;
import org.spongepowered.api.event.entity.EntityTeleportEvent;
import org.spongepowered.api.event.entity.ExplosionPrimeEvent;
import org.spongepowered.api.event.entity.living.LivingChangeHealthEvent;
import org.spongepowered.api.event.entity.player.PlayerBreakBlockEvent;
import org.spongepowered.api.event.entity.player.PlayerChangeBlockEvent;
import org.spongepowered.api.event.entity.player.PlayerChangeHealthEvent;
import org.spongepowered.api.event.entity.player.PlayerChatEvent;
import org.spongepowered.api.event.entity.player.PlayerDropItemEvent;
import org.spongepowered.api.event.entity.player.PlayerInteractBlockEvent;
import org.spongepowered.api.event.entity.player.PlayerInteractEntityEvent;
import org.spongepowered.api.event.entity.player.PlayerItemConsumeEvent;
import org.spongepowered.api.event.entity.player.PlayerJoinEvent;
import org.spongepowered.api.event.entity.player.PlayerMoveEvent;
import org.spongepowered.api.event.entity.player.PlayerPickUpItemEvent;
import org.spongepowered.api.event.entity.player.PlayerPlaceBlockEvent;
import org.spongepowered.api.event.entity.player.PlayerQuitEvent;
import org.spongepowered.api.event.entity.player.PlayerRespawnEvent;
import org.spongepowered.api.event.entity.player.PlayerSleepEvent;
import org.spongepowered.api.event.message.CommandEvent;
import org.spongepowered.api.event.world.WorldLoadEvent;
import org.spongepowered.api.event.world.WorldUnloadEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;

import com.google.common.base.Optional;

public class ListenerSponge implements Listener {

    private final ChatListener chatListener;
    private final LandListener landListener;
    private final PlayerListener playerListener;
    private final PvpListener pvpListener;
    private final WorldListener worldListener;

    public ListenerSponge() {
    	
    	chatListener = new ChatListener();
    	landListener = new LandListener();
    	playerListener = new PlayerListener();
    	pvpListener = new PvpListener();
    	worldListener = new WorldListener();
    }
    
	/**************************************************************************
	 * Sponge events
	 *************************************************************************/

    @Subscribe(order = Order.BEFORE_POST)
	public void onWorldLoadMonitor(WorldLoadEvent event) {
		
		// Add world to list
		Factoid.getServerCache().addWorld(new FWorldSponge(event.getWorld()));
	}

	@Subscribe(order = Order.BEFORE_POST)
	public void onWorldUnloadMonitor(WorldUnloadEvent event) {
		
		// Remove world to list
		Factoid.getServerCache().removeWorld(new FWorldSponge(event.getWorld()));
	}
	
	@Subscribe(order = Order.BEFORE_POST)
	public void onPlayerJoinMonitor(PlayerJoinEvent event) {
		
		FPlayer player = new FPlayerSponge(event.getEntity());
		
		Factoid.getServerCache().addPlayer(player); // Add player in the list
		playerListener.onPlayerJoinMonitor(player);
	}

	@Subscribe(order = Order.BEFORE_POST)
	public void onPlayerQuitMonitor(PlayerQuitEvent event) {

		FPlayer player = Factoid.getServerCache().getPlayer(event.getEntity().getUniqueId());

		landListener.onPlayerQuitMonitor(player);
		Factoid.getServerCache().removePlayer(player); // Remove player from the list
		playerListener.onPlayerQuitMonitor(player);
	}

	@Subscribe
	public void onEntityTeleportMonitor(EntityTeleportEvent event) {
		
		// Must be a player
    	if(!(event.getEntity() instanceof Player)) {
			return;
		}

    	FPlayer player = Factoid.getServerCache().getPlayer(event.getEntity().getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

		if(playerListener.onPlayerTeleport(player, SpongeUtils.toPoint(event.getNewLocation()),
				false /* TODO Support Ender pearl TP */ )) {
			event.setCancelled(true);
		}
	}

	@Subscribe(order = Order.BEFORE_POST)
	public void onPlayerMoveMonitor(PlayerMoveEvent event) {

		FPlayer player = Factoid.getServerCache().getPlayer(event.getEntity().getUniqueId());

		// Real player?
		if(player == null) {
			return;
		}

		playerListener.onPlayerMoveMonitor(player, 
				SpongeUtils.toPoint(event.getOldLocation()), SpongeUtils.toPoint(event.getNewLocation()));
	}

	@Subscribe
    public void onPlayerChat(PlayerChatEvent event) {
    	
		// Must be a player
    	if(!(event.getSource() instanceof Player)) {
			return;
		}
    	
    	FPlayer player = Factoid.getServerCache().getPlayer(((Player) event.getSource()).getUniqueId());
    	
    	if(chatListener.onAsyncPlayerChat(player, event.getMessage().toString())) {
    		event.setCancelled(true);
		}
    }
	
	@Subscribe
	public void onPlayerInteractBlock(PlayerInteractBlockEvent event) {
		
		// get click
		Click click;
		if(event.getInteractionType() == EntityInteractionTypes.ATTACK) {
			click = Click.LEFT;
		} else if(event.getInteractionType() == EntityInteractionTypes.USE) {
			click = Click.RIGHT;
		}else  if(event.getInteractionType() == EntityInteractionTypes.PICK_BLOCK) {
			return;
		} else {
			click = Click.NONE;
		}
		
		// Check item in hand
		String itemInHand;
		if(event.getEntity().getItemInHand().isPresent()) {
			itemInHand = event.getEntity().getItemInHand().get().getItem().getName();
		} else {
			itemInHand = null;
		}
		
    	FPlayer player = Factoid.getServerCache().getPlayer(event.getEntity().getUniqueId());
		if(playerListener.onPlayerInteract(player, click, itemInHand, event.getBlock().getBlockType().getName(),
				SpongeUtils.toPoint(event.getBlock()))) {
			event.setCancelled(true);
		}
	}
	
	@Subscribe
	public void onPlayerPlaceBlock(PlayerPlaceBlockEvent event) {
		
    	FPlayer player = Factoid.getServerCache().getPlayer(event.getEntity().getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

    	if(playerListener.onBlockPlace(player, event.getBlock().getBlockType().getName(),
    			SpongeUtils.toPoint(event.getBlock()))) {
    		event.setCancelled(true);
    	}
	}
	
	@Subscribe(order = Order.BEFORE_POST)
	public void onPlayerPlaceBlockMonitor(PlayerPlaceBlockEvent event) {
		
    	FPlayer player = Factoid.getServerCache().getPlayer(event.getEntity().getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

		pvpListener.onBlockPlaceMonitor(player, event.getBlock().getBlockType().getName(),
				SpongeUtils.toPoint(event.getBlock()));
	}

	@Subscribe
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		
		FPlayer player = Factoid.getServerCache().getPlayer(event.getEntity().getUniqueId());
		
		if(event.getInteractionType() == EntityInteractionTypes.USE) {
			// Right click
			// Check if item in hand is present
			String itemInHandType = null;
			if(event.getEntity().getItemInHand().isPresent()) {
				itemInHandType = event.getEntity().getItemInHand().get().getItem().getName();
			}
			
			if(playerListener.onPlayerInteractAtEntity(player, event.getTargetEntity().getType().getName(),
					itemInHandType, SpongeUtils.toPoint(event.getTargetEntity().getLocation()))) {
				event.setCancelled(true);
			}
		
		} else {
			// others
			if(playerListener.onPlayerInteractEntity(player, event.getTargetEntity().getType().getName(),
					SpongeUtils.toPoint(event.getTargetEntity().getLocation()))) {
				event.setCancelled(true);
			}
		}
	}

	@Subscribe
	public void onPlayerBreakBlock(PlayerBreakBlockEvent event) {
		
    	FPlayer player = Factoid.getServerCache().getPlayer(event.getEntity().getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

    	if(playerListener.onBlockPlace(player, event.getBlock().getBlockType().getName(),
    			SpongeUtils.toPoint(event.getBlock()))) {
    		event.setCancelled(true);
    	}
	}
	
	@Subscribe
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		
		FPlayer player = Factoid.getServerCache().getPlayer(event.getEntity().getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

		for(ItemStack item : event.getDroppedItems()) {
			if(playerListener.onPlayerDropItem(player, item.getItem().getName(),
					SpongeUtils.toPoint(event.getEntity().getLocation()))) {
				event.setCancelled(true);
			}
		}
	}
	
	@Subscribe
	public void onPlayerPickUpItem(PlayerPickUpItemEvent event) {
		
		FPlayer player = Factoid.getServerCache().getPlayer(event.getEntity().getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

		for(Item item : event.getItems()) {
			if(playerListener.onPlayerPickupItem(player, item.getType().getName(),
					SpongeUtils.toPoint(item.getLocation()))) {
				event.setCancelled(true);
			}
		}
	}
	
	@Subscribe
	public void onPlayerSleep(PlayerSleepEvent event) {
		
		FPlayer player = Factoid.getServerCache().getPlayer(event.getEntity().getUniqueId());
		
		if(playerListener.onPlayerBedEnter(player, SpongeUtils.toPoint(event.getLocation()))) {
			event.setCancelled(true);
		}
	}
	
	@Subscribe
	// TODO Hanging is not take in charge for now
	public void onLivingChangeHealth(LivingChangeHealthEvent event) {
		
		// Get the source player
		Player mplayer = getSourcePlayer(event.getCause());
		
		// No player found?
		if(mplayer == null) {
			return;
		}
		
		Living entity = event.getEntity();
		boolean isPlayer = entity instanceof Player;
		boolean isAnimal = entity instanceof Animal;
		boolean isMonster = entity instanceof Monster;
		boolean isTamedAndNotOwner = false; // TODO Check if animal is tamed
		
		FPlayer player = Factoid.getServerCache().getPlayer(mplayer.getUniqueId());
		Point locPlayer = SpongeUtils.toPoint(mplayer.getLocation());
		Point locVictime = SpongeUtils.toPoint(entity.getLocation());

		// Real player?
		if(player == null) {
			return;
		}

		if(playerListener.onEntityDamageByEntity(player, entity.getType().getName(),
				SpongeUtils.toPoint(entity.getLocation()), isAnimal, isMonster, isTamedAndNotOwner)) {
			event.setCancelled(true);

		} else if(isPlayer) { 
			
			// PVP
			FPlayer victime = Factoid.getServerCache().getPlayer(entity.getUniqueId());
			
			if(victime != null && pvpListener.onPlayerDamageByPlayer(player, victime, locPlayer, locVictime)) {
				event.setCancelled(true);
			}
		}
	}
	
	// TODO Bucket fill event

	// TODO Bucket empty event
	
	@Subscribe
	public void onPlayerChangeBlock(PlayerChangeBlockEvent event) {
		
    	FPlayer player = Factoid.getServerCache().getPlayer(event.getEntity().getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

    	if(playerListener.onPlayerChangeBlock(player, event.getBlock().getBlockType().getName(),
    			event.getReplacementBlock().getState().getType().getName(), SpongeUtils.toPoint(event.getBlock()))) {
    		event.setCancelled(true);
    	}
	}
	
	@Subscribe(order = Order.LAST)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		
    	FPlayer player = Factoid.getServerCache().getPlayer(event.getEntity().getUniqueId());
    	
		// Real player?
		if(player == null) {
			return;
		}

    	Point newLoc = playerListener.onPlayerRespawn(player, SpongeUtils.toPoint(event.getRespawnLocation()));
		
    	if(newLoc != null) {
    		// TODO Yaw and Pitch
    		event.setNewRespawnLocation(SpongeUtils.toLocation(event.getGame().getServer().getWorld(newLoc.getWorldName()).get(), 
    				newLoc));
    	}
	}
	
	@Subscribe(order = Order.BEFORE_POST)
	public void onPlayerRespawnMonitor(PlayerRespawnEvent event) {
		
    	FPlayer player = Factoid.getServerCache().getPlayer(event.getEntity().getUniqueId());
    	
		// Real player?
		if(player == null) {
			return;
		}

    	playerListener.onPlayerRespawnMonitor(player, SpongeUtils.toPoint(event.getRespawnLocation()));
	}
	
	@Subscribe
	public void onBlockIgnite(BlockIgniteEvent event) {
		
		Point loc = SpongeUtils.toPoint(event.getBlock());
		
		// Natural cause
		if(event.getCause().isPresent() && event.getCause().get() instanceof BlockState
				&& worldListener.onBlockIgniteNatural(loc)) {
			// event.setCancelled(true); TODO Block Ignite not cancellable?
			// return;
		}
		
		
		
		// Player only
		if(event.getCause().isPresent() && !(event.getCause().get() instanceof Player)) {
			return;
		}
		
		FPlayer player = Factoid.getServerCache().getPlayer(((Player) event.getCause().get()).getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

		if(playerListener.onBlockIgnite(player, loc)) {
			// event.setCancelled(true); TODO Block Ignite not cancellable?
		}
	}
	
	@Subscribe(order = Order.BEFORE_POST)
	public void onBlockIgniteMonitor(BlockIgniteEvent event) {
		
		if(!event.getCause().isPresent() || !(event.getCause().get() instanceof Player)) {
			return;
		}
		
		FPlayer player = Factoid.getServerCache().getPlayer(((Player) event.getCause().get()).getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

		pvpListener.onBlockIgniteMonitor(player, SpongeUtils.toPoint(event.getBlock()));
	}

	// TODO Fire Spread
	
	// TODO PotionSplash
	
	@Subscribe
	public void onPlayerChangeHealth(PlayerChangeHealthEvent event) {
		
		FPlayer player = Factoid.getServerCache().getPlayer(event.getEntity().getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

		// Health loss
		if(event.getOldData().getHealth() > event.getNewData().getHealth()) {
			if(playerListener.onPlayerDamage(player, SpongeUtils.toPoint(event.getEntity().getLocation()))) {
				event.setCancelled(true);
			}
		}
		
		// TODO Get the reason of gain change heal
		
		// TODO Get when the player burn
	}
	
	@Subscribe
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		
		FPlayer player = Factoid.getServerCache().getPlayer(event.getEntity().getUniqueId());
		
		// Real player?
		if(player == null) {
			return;
		}

		if(playerListener.onPlayerItemConsume(player, SpongeUtils.toPoint(event.getEntity().getLocation()))) {
			event.setCancelled(true);
		}
		
	}
	
	@Subscribe
	public void onCommand(CommandEvent event) {
		
		// Not a player
		if(!(event.getSource() instanceof Player)) {
			return;
		}
		
		FPlayer player = Factoid.getServerCache().getPlayer(((Player) event.getSource()).getUniqueId());
		String commandTyped = event.getCommand();

		if(playerListener.onPlayerCommandPreprocess(player, SpongeUtils.toPoint(((Player) event.getSource()).getLocation()),
				commandTyped)) {
			event.setCancelled(true);
		}
	}
	
	/**************************************************************************
	 * World events
	 *************************************************************************/

	@Subscribe
	public void onExplosionPrime(ExplosionPrimeEvent event) {
    	
		Entity entity = event.getEntity();
		
		if(worldListener.onExplosionPrime(SpongeUtils.toPoint(entity.getLocation()), 
				entity.getType().getName())) {
			event.setCancelled(true);
            if (entity.getType() == EntityTypes.CREEPER) {
                event.getEntity().remove();
            }
		}
    }

	@Subscribe
	public void EntityExplosion(EntityExplosionEvent event) {
    	
        if (event.getEntity() == null) {
            return;
        }

        if (Factoid.getConf().isOverrideExplosions()) {

            EntityType entityType = event.getEntity().getType();
            Location loc = event.getEntity().getLocation();

            // Creeper Explosion
            if (entityType == EntityTypes.CREEPER) {
                ExplodeBlocks(event, event.getBlocks(), FlagList.CREEPER_DAMAGE.getFlagType(), loc);

                //  Wither
            } else if (entityType == EntityTypes.WITHER_SKULL) {
                ExplodeBlocks(event, event.getBlocks(), FlagList.WITHER_DAMAGE.getFlagType(), loc);
            } else if (entityType == EntityTypes.WITHER) {
                ExplodeBlocks(event, event.getBlocks(), FlagList.WITHER_DAMAGE.getFlagType(), loc);

                // Ghast
            } else if (entityType == EntityTypes.FIREBALL) {
                ExplodeBlocks(event, event.getBlocks(), FlagList.GHAST_DAMAGE.getFlagType(), loc);

                // TNT
            } else if (entityType == EntityTypes.TNT_MINECART
                    || entityType == EntityTypes.PRIMED_TNT) {
                ExplodeBlocks(event, event.getBlocks(), FlagList.TNT_DAMAGE.getFlagType(), loc);
            } else if (entityType == EntityTypes.ENDER_DRAGON) {
                ExplodeBlocks(event, event.getBlocks(), FlagList.ENDERDRAGON_DAMAGE.getFlagType(), loc);
            }
        }
    }
	
	// TODO HangingBreakEvent

	@Subscribe
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		
		
		// All entities section
		if(worldListener.onEntityChangeBlock(SpongeUtils.toPoint(event.getBlock()), 
				event.getEntity().getType().getName(),
				event.getBlock().getBlockType().getName(), 
				event.getReplacementBlock().getState().getType().getName())) {
			event.setCancelled(true);
			return;
		}
	}
	
	@Subscribe
	public void onBlockBurn(BlockBurnEvent event) {
		
		if(worldListener.onBlockBurn(SpongeUtils.toPoint(event.getBlock()))) {
			event.setCancelled(true);
		}
	}
	
	@Subscribe
    public void onEntitySpawn(EntitySpawnEvent event) {
    	
    	Entity entity = event.getEntity();
    	boolean isAnimal = false;
    	boolean isMob = false;
    	
    	if(entity instanceof Animal) {
    		isAnimal = true;
    	}
    	
    	if(event.getEntity() instanceof Monster) {
    		isMob = true;
    	}
    	
    	if(worldListener.onCreatureSpawn(SpongeUtils.toPoint(event.getLocation()), 
    			isAnimal, isMob)) {
    		event.setCancelled(true);
    	}
    }

	@Subscribe
    public void onLeafDecay(LeafDecayEvent event) {
    	
    	if(worldListener.onLeavesDecay(SpongeUtils.toPoint(event.getBlock()))) {
    		event.setCancelled(true);
    	}
    }
	
	@Subscribe
	public void onBlockChange(BlockChangeEvent event) {
		
		if(worldListener.onBlockFromTo(SpongeUtils.toPoint(event.getBlock()), 
				event.getBlock().getBlockType().getName())) {
			event.setCancelled(true);
		}
	}

	// TODO Hanging Damage
	
    /**************************************************************************
	 * Factoid events
	 *************************************************************************/

	@Subscribe(order = Order.LAST)
	public void onPlayerLandChange(PlayerLandChangeEvent event) {
    	
		if(landListener.onPlayerLandChange(event.getFPlayer(), event.getLastLand(), event.getLand(),
				event.getToLoc())) {
			event.setCancelled(true);
		}
    }

	@Subscribe(order = Order.EARLY)
    public void onPlayerContainerLandBan(PlayerContainerLandBanEvent event) {

        landListener.checkForBannedPlayers(event.getLand(), 
        		event.getPlayerContainer(), "ACTION.BANNED");
    }

	@Subscribe(order = Order.EARLY)
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
	private Player getSourcePlayer(Optional<Cause> cause) {
		
		// Check if cause exist
		if(!cause.isPresent()) {
			return null;
		}
		
		Object object = cause.get().getCause();

		// Check if the damager is a player
		if (object instanceof Player) {
			return (Player) object;
		} else if (object instanceof Projectile) {
			Projectile projectile = (Projectile) object;
			
			if(projectile.getType() != EntityTypes.EGG
				&& projectile.getType() != EntityTypes.SNOWBALL
				&& projectile.getShooter() instanceof Player) {
					
				return (Player) projectile.getShooter();
			}
		}
		
		return null;
	}

    /**
     * Explode blocks.
     *
     * @param event The  event
     * @param blocks the blocks
     * @param ft the flag type
     * @param loc the location
     */
    private void ExplodeBlocks(EntityExplosionEvent event, List<Location> blocks, FlagType ft, Location loc) {

        FlagValue value;
        boolean cancelEvent = false;
        Iterator<Location> itBlock = blocks.iterator();
        Location block;

        Factoid.getFactoidLog().write("Explosion!");

        // Check if 1 block or more is in a protected place
        while(itBlock.hasNext() && !cancelEvent) {
        	block = itBlock.next();
        	value = Factoid.getLands().getLandOrOutsideArea(
        			SpongeUtils.toPoint(block)).getFlagAndInherit(ft);
            if (value.getValueBoolean() == false) {
                cancelEvent = true;
            }
        }
        
        if(cancelEvent) {
        	// Cancel Event and do a false explosion
        	event.setYield(0);
        }
        
        // If not the event will be executed has is
    }
}
