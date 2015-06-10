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
package me.tabinol.factoid.listeners;

import java.util.logging.Level;
import java.util.logging.Logger;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ArgList;
import me.tabinol.factoid.commands.executor.CommandCancel;
import me.tabinol.factoid.commands.executor.CommandEcosign;
import me.tabinol.factoid.commands.executor.CommandEcosign.SignType;
import me.tabinol.factoid.commands.executor.CommandInfo;
import me.tabinol.factoid.commands.executor.CommandSelect;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.config.players.PlayerStaticConfig;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.DummyLand;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.areas.CuboidArea;
import me.tabinol.factoid.lands.areas.Point;
import me.tabinol.factoid.minecraft.FPlayer;
import me.tabinol.factoid.parameters.FlagList;
import me.tabinol.factoid.parameters.PermissionList;
import me.tabinol.factoid.selection.region.PlayerMoveListen;
import me.tabinol.factoid.selection.region.RegionSelection;
import me.tabinol.factoid.utilities.ChatStyle;
import me.tabinol.factoidapi.FactoidAPI;
import me.tabinol.factoidapi.config.players.IPlayerConfEntry;
import me.tabinol.factoidapi.event.PlayerLandChangeEvent;
import me.tabinol.factoidapi.lands.IDummyLand;
import me.tabinol.factoidapi.lands.ILand;
import me.tabinol.factoidapi.parameters.IParameters.SpecialPermPrefix;
import me.tabinol.factoidapi.utilities.StringChanges;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
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
import org.bukkit.plugin.PluginManager;


/**
 * Players listener
 */
public class PlayerListener extends CommonListener {

	/** The conf. */
	private Config conf;

	/** The Constant DEFAULT_TIME_LAPS. */
	public static final int DEFAULT_TIME_LAPS = 500; // in milliseconds

	/** The time check. */
	private int timeCheck;

	/** The pm. */
	private PluginManager pm;
	
	/**
	 * Instantiates a new player listener.
	 */
	public PlayerListener() {

		super();
		conf = Factoid.getConf();
		timeCheck = DEFAULT_TIME_LAPS;
		pm = Factoid.getThisPlugin().getServer().getPluginManager();
	}

	public void onPlayerJoinMonitor(FPlayer player) {

		// Update players cache and create config
		Factoid.getPlayersCache().updatePlayer(player.getUUID(), player.getName());
		
		updatePosInfo(false, player, player.getLocation(), true);

		// Check if AdminMod is auto
		if (player.hasPermission("factoid.adminmod.auto")) {
			player.setAdminMod(true);
		}
	}

	// Must be running after LandListener
	public void onPlayerQuitMonitor(FPlayer player) {

		// Remove player from the land
		DummyLand land = player.getLastLand();
		if (land instanceof ILand) {
			land.removePlayerInLand(player);
		}
	}

	public boolean onPlayerTeleport(FPlayer player, Point to, boolean isEnderPearl) {

		IDummyLand land;

		if (!player.hasTpCancel()) {
			updatePosInfo(true, player, to, false);
		} else {
			player.setTpCancel(false);
		}

		land = Factoid.getLands().getLandOrOutsideArea(Point);

		// TP With ender pearl
		if (!player.isAdminMod()
				&& isEnderPearl
				&& !checkPermission(land, player,
						PermissionList.ENDERPEARL_TP.getPermissionType())) {
			messagePermission(player);
			return true;
		}
		
		return false;
	}

	public void onPlayerMoveMonitor(FPlayer player, Point from, Point to) {

		// Check if the player must move
		long last = player.getLastMoveUpdate();
		long now = System.currentTimeMillis();
		if (now - last < timeCheck) {
			return;
		}

		player.setLastMoveUpdate(now);
		if (from.getWorld() == to.getWorld()) {
			if (from.distance(to) == 0) {
				return;
			}
		}
		updatePosInfo(false, player, to, false);
	}

	public boolean onPlayerInteract(FPlayer player, Click click, String itemInHand, String clickedItem, Point loc) {

		IDummyLand land;

		Factoid.getFactoidLog().write(
				"PlayerInteract player name: " + player.getName()
						+ ", Action: " + click.toString()
						+ ", Material: " + clickedItem);

		// For infoItem
		if (itemInHand != null && click == Click.LEFT
				&& itemInHand == conf.getInfoItem()) {
			try {
				new CommandInfo(player, 
						(CuboidArea) Factoid.getThisPlugin().iLands().getCuboidArea(loc))
						.commandExecute();
			} catch (FactoidCommandException ex) {
				Logger.getLogger(PlayerListener.class.getName()).log(
						Level.SEVERE, "Error when trying to get area", ex);
			}
			return true;

			// For Select
		} else if (itemInHand != null
				&& click == Click.LEFT
				&& itemInHand == conf.getSelectItem()) {

			try {
				new CommandSelect(player, new ArgList(new String[] { "here" },
						player), loc)
						.commandExecute();
			} catch (FactoidCommandException ex) {
				// Empty, message is sent by the catch
			}

			return true;

			// For Select Cancel
		} else if (itemInHand != null
				&& click == Click.RIGHT
				&& itemInHand == conf.getSelectItem()
				&& player.getSelection().hasSelection()) {

			try {
				new CommandCancel(player, false).commandExecute();
			} catch (FactoidCommandException ex) {
				// Empty, message is sent by the catch
			}

			return true;

			// For economy (buy or rent/unrent)
		} else if ((click == Click.RIGHT || click == Click.LEFT)
				&& clickedItem.contains("SIGN")) {

			ILand trueLand = Factoid.getLands().getLand(loc);

			
			if (trueLand != null) {

			    Factoid.getFactoidLog().write("EcoSignClick: ClickLoc: " + loc + ", SignLoc" + trueLand.getSaleSignLoc());
			    
				try {
					if (trueLand.getSaleSignLoc() != null
							&& trueLand.getSaleSignLoc().equals(loc)) {
						new CommandEcosign(playerConf.get(player), (Land) trueLand,
								action, SignType.SALE).commandExecute();
						return true;
						
					} else if (trueLand.getRentSignLoc() != null
							&& trueLand.getRentSignLoc().equals(loc)) {
						event.setCancelled(true);
						new CommandEcosign(playerConf.get(player), (Land)trueLand,
								action, SignType.RENT).commandExecute();
						return true;
					}
				} catch (FactoidCommandException ex) {
					// Empty, message is sent by the catch
				}
			}

			// Citizen bug, check if entry exist before
		} else if (!player.isAdminMod()) {
			land = Factoid.getLands().getLandOrOutsideArea(loc);
			if ((land instanceof Land && ((Land) land).isBanned(player))
					|| (((click == Click.RIGHT // BEGIN of USE
					&& (clickedItem.contains("DOOR")
							|| clickedItem.contains("BUTTON")
							|| clickedItem.equals("LEVER")
							|| clickedItem.equals("TRAPPED_CHEST")
							|| clickedItem.matches("^ENCHANT.*TABLE$") 
							|| clickedItem.equals("ANVIL")
							|| clickedItem.equals("MOB_SPAWNER")
							|| clickedItem.contains("DAYLIGHT_DETECTOR")
							|| (click == Click.NONE && (clickedItem.contains("PLATE")
							|| clickedItem.equals("STRING")))) && !checkPermission(
								land, player,
								PermissionList.USE.getPermissionType())) // End
																		// of
																		// "USE"
					|| (click == Click.RIGHT
							&& clickedItem.contains("DOOR") && !checkPermission(
								land, player,
								PermissionList.USE_DOOR.getPermissionType()))
					|| (click == Click.RIGHT
							&& clickedItem.contains("BUTTON") && !checkPermission(
								land, player,
								PermissionList.USE_BUTTON.getPermissionType()))
					|| (click == Click.RIGHT
							&& clickedItem.equals("LEVER") && !checkPermission(land,
								player,
								PermissionList.USE_LEVER.getPermissionType()))
					|| (click == Click.NONE
							&& clickedItem.contains("PLATE") && !checkPermission(
								land, player, PermissionList.USE_PRESSUREPLATE
										.getPermissionType()))
					|| (click == Click.RIGHT
							&& clickedItem.equals("TRAPPED_CHEST") && !checkPermission(
								land, player,
								PermissionList.USE_TRAPPEDCHEST
										.getPermissionType()))
					|| (click == Click.NONE && clickedItem.equals("STRING") && !checkPermission(
							land, player,
							PermissionList.USE_STRING.getPermissionType()))
					|| (click == Click.RIGHT && clickedItem.equals("MOB_SPAWNER")
					        && !checkPermission(land, player, PermissionList.USE_MOBSPAWNER.getPermissionType()))
					|| (click == Click.RIGHT && clickedItem.contains("DAYLIGHT_DETECTOR")
					        && !checkPermission(land, player, PermissionList.USE_LIGHTDETECTOR.getPermissionType()))
					|| (click == Click.RIGHT && clickedItem.matches("^ENCHANT.*TABLE$")
							&& !checkPermission(land, player, PermissionList.USE_ENCHANTTABLE.getPermissionType()))
					|| (click == Click.RIGHT && clickedItem.equals("ANVIL")
						&& !checkPermission(land, player, PermissionList.USE_ANVIL.getPermissionType()))) {

				if (click != Click.NONE) {
					messagePermission(player);
				}
				return true;

			} else if (click == Click.RIGHT
					&& (((clickedItem.equals("CHEST")
							|| clickedItem.equals("ENDER_CHEST") // Begin of OPEN
							|| clickedItem.equals("WORKBENCH") // Bukkit 
							|| clickedItem.equals("CRAFTING_TABLE") // Sponge
							|| clickedItem.equals("BREWING_STAND")
							|| clickedItem.contains("FURNACE")
							|| clickedItem.equals("BEACON")
							|| clickedItem.equals("DROPPER") || clickedItem.equals("HOPPER")
							|| clickedItem.equals("DISPENSER") || clickedItem.equals("JUKEBOX")) 
							&& !checkPermission(land, player,
								PermissionList.OPEN.getPermissionType())) // End
																			// of
																			// OPEN
							|| (clickedItem.equals("CHEST") && !checkPermission(land,
									player,
									PermissionList.OPEN_CHEST
											.getPermissionType()))
							|| (clickedItem.equals("ENDER_CHEST") && !checkPermission(
									land, player,
									PermissionList.OPEN_ENDERCHEST
											.getPermissionType()))
							|| ((clickedItem.equals("WORKBENCH") || clickedItem.equals("CRAFTING_TABLE"))
									&& !checkPermission(land, player, PermissionList.OPEN_CRAFT
											.getPermissionType()))
							|| (clickedItem.equals("BREWING_STAND") && !checkPermission(
									land, player,
									PermissionList.OPEN_BREW.getPermissionType()))
							|| (clickedItem.contains("FURNACE") && !checkPermission(
									land, player,
									PermissionList.OPEN_FURNACE
											.getPermissionType()))
							|| (clickedItem.equals("BEACON") && !checkPermission(land,
									player,
									PermissionList.OPEN_BEACON
											.getPermissionType()))
							|| (clickedItem.equals("DISPENSER") && !checkPermission(land,
									player,
									PermissionList.OPEN_DISPENSER
											.getPermissionType()))
							|| (clickedItem.equals("DROPPER") && !checkPermission(
									land, player,
									PermissionList.OPEN_DROPPER
											.getPermissionType())) || (clickedItem.equals("HOPPER") 
													&& !checkPermission(land, player,
							PermissionList.OPEN_HOPPER.getPermissionType()))
							|| (clickedItem.equals("JUKEBOX") && !checkPermission(
									land, player,
									PermissionList.OPEN_JUKEBOX.getPermissionType())))
					// For dragon egg fix
					|| (clickedItem.equals("DRAGON_EGG") && (!checkPermission(land,
							player,
							PermissionList.BUILD.getPermissionType()) || !checkPermission(
							land, player,
							PermissionList.BUILD_DESTROY.getPermissionType())))) {
				messagePermission(player);
				return true;
				
				// For tile entities
			} else if(itemInHand != null
					&& click == Click.RIGHT
					&& (itemInHand.equals("ARMOR_STAND") || clickedItem.contains("SKULL")
							|| itemInHand.equals("PAINTING") || clickedItem.equals("ITEM_FRAME"))
					&& ((land instanceof ILand && ((ILand) land).isBanned(player))
						|| !checkPermission(land, player,
								PermissionList.BUILD.getPermissionType())
						|| !checkPermission(land, player,
								PermissionList.BUILD_PLACE.getPermissionType()))) {
				messagePermission(player);
				return true;
			}
		}
		return false;
	}

	public boolean onBlockPlace(FPlayer player, String blockType, Point loc) {

		// Check for fire init
		if(blockType.contains("FIRE")) {
			if(checkForPutFire(event, player)) {
				return true;
			}
			
		} else if (!player.isAdminMod()) {

			IDummyLand land = Factoid.getLands().getLandOrOutsideArea(loc);

			if (land instanceof ILand && ((ILand) land).isBanned(player)) {
				// Player banned!!
				messagePermission(player);
				return true;
			
			} else if(!checkPermission(land, player, PermissionList.BUILD.getPermissionType())
					|| !checkPermission(land, player, PermissionList.BUILD_PLACE.getPermissionType())) {
				if(checkPermission(land, player, 
						Factoid.getParameters().getSpecialPermission(SpecialPermPrefix.PLACE, blockType))) {
					messagePermission(player);
					return true;
				}
			} else if(!checkPermission(land, player, 
					Factoid.getParameters().getSpecialPermission(SpecialPermPrefix.NOPLACE, blockType))) {
				messagePermission(player);
				return true;
			}
		}
		return false;
	}

	public boolean onPlayerInteractEntity(FPlayer player, String entityType, Point loc) {
		
		if (!player.isAdminMod()
				&& (entityType.equals("ITEM_FRAME") || entityType.equals("PAINTING"))) {

			IDummyLand land = Factoid.getLands().getLandOrOutsideArea(
					event.getRightClicked().getLocation());

			if ((land instanceof ILand && ((ILand) land).isBanned(player))
					|| !checkPermission(land, player,
							PermissionList.BUILD.getPermissionType())
					|| !checkPermission(land, player,
							PermissionList.BUILD_PLACE.getPermissionType())) {
				messagePermission(player);
				return true;
			}
		}
		return false;
	}

	public boolean onBlockBreak(FPlayer player, String blockType, Point loc) {

		if (!player.isAdminMod()) {

			IDummyLand land = Factoid.getLands().getLandOrOutsideArea(loc);

			if (land instanceof ILand && (((ILand) land).isBanned(player)
					|| hasEcoSign((Land) land, event.getBlock()))) {
				// Player banned (or ecosign)
				messagePermission(player);
				return true;
			} else if (!checkPermission(land, player,
							PermissionList.BUILD.getPermissionType())
					|| !checkPermission(land, player,
							PermissionList.BUILD_DESTROY.getPermissionType())) {
				if(checkPermission(land, player,
						Factoid.getParameters().getSpecialPermission(SpecialPermPrefix.DESTROY, mat))) {
					messagePermission(player);
					return true;
				}
			} else if(!checkPermission(land, player,
						Factoid.getParameters().getSpecialPermission(SpecialPermPrefix.NODESTROY, mat))) {
				messagePermission(player);
				return true;
			}
		}
		return false;
	}

	public boolean onPlayerDropItem(FPlayer player, String itemType, Point loc) {

		if (!player.isAdminMod()) {
			IDummyLand land = Factoid.getLands().getLandOrOutsideArea(loc);

			if (!checkPermission(land, player, PermissionList.DROP.getPermissionType())) {
				messagePermission(player);
				return true;
			}
		}
		return false;
	}

	public boolean onPlayerPickupItem(FPlayer player, String itemType, Point loc) {

		if (!player.isAdminMod()) {
			IDummyLand land = Factoid.getLands().getLandOrOutsideArea(loc);

			if (!checkPermission(land, player,
					PermissionList.PICKETUP.getPermissionType())) {
				messagePermission(player);
				return true;
			}
		}
		return false;
	}

	public boolean onPlayerBedEnter(FPlayer player, Point loc) {

		if (!player.isAdminMod()) {
			IDummyLand land = Factoid.getLands().getLandOrOutsideArea(loc);

			if ((land instanceof ILand && ((ILand) land).isBanned(player))
					|| (!checkPermission(land, player, PermissionList.SLEEP.getPermissionType()))) {
				messagePermission(player);
				return true;
			}
		}
		return false;
	}

	public boolean onEntityDamageByEntity(FPlayer player, String entityType, Point loc,
			boolean isAnimal, boolean isMonster, boolean isTamedAndNotOwner) {

		IPlayerConfEntry entry;

		// Check for non-player kill
		if (player != null) {
			IDummyLand land = Factoid.getLands().getLandOrOutsideArea(loc);

			// kill an entity (none player)
			if (!player.isAdminMod()
					&& ((land instanceof ILand && ((ILand) land).isBanned(player))
							|| ((entityType.equals("ARMOR_STAND") || entityType.equals("ITEM_FRAME") 
									|| entityType.equals("PAINTING"))
									&& (!checkPermission(land, player,
											PermissionList.BUILD.getPermissionType())
									|| !checkPermission(land, player,
											PermissionList.BUILD_DESTROY.getPermissionType())))
							|| (isAnimal && !checkPermission(
									land, player,
									PermissionList.ANIMAL_KILL
											.getPermissionType()))
							|| (isMonster && !checkPermission(
									land, player,
									PermissionList.MOB_KILL
											.getPermissionType()))
							|| (entityType.equals("VILLAGER") && !checkPermission(land, player,
									PermissionList.VILLAGER_KILL
											.getPermissionType()))
							|| (entityType.equals("IRON_GOLEM") && !checkPermission(
									land, player,
									PermissionList.VILLAGER_GOLEM_KILL
											.getPermissionType()))
							|| (entityType.equals("HORSE") && !checkPermission(
									land, player,
									PermissionList.HORSE_KILL.getPermissionType())) || 
									(isTamedAndNotOwner && !checkPermission(land, player,
								PermissionList.TAMED_KILL.getPermissionType())))) {
				messagePermission(player);
				return true;
			} 
		}
		return false;
	}

	public boolean onPlayerBucketFill(FPlayer player, String blockType, Point loc) {

		IDummyLand land = Factoid.getLands().getLandOrOutsideArea(loc);

		if ((land instanceof ILand && ((ILand) land).isBanned(player))
				|| (blockType.equals("LAVA_BUCKET") && !checkPermission(land, player,
						PermissionList.BUCKET_LAVA.getPermissionType()))
				|| (blockType.equals("WATER_BUCKET") && !checkPermission(land, player,
						PermissionList.BUCKET_WATER.getPermissionType()))) {
			messagePermission(player);
			return true;
		}
		return false;
	}

	public boolean onPlayerBucketEmpty(FPlayer player, String itemType, Point loc) {

		IDummyLand land = Factoid.getLands().getLandOrOutsideArea(loc);

		if ((land instanceof ILand && ((ILand) land).isBanned(event
				.getPlayer()))
				|| (itemType.equals("LAVA_BUCKET") && !checkPermission(land, player,
						PermissionList.BUCKET_LAVA.getPermissionType()))
				|| (itemType.equals("WATER_BUCKET") && !checkPermission(land, player,
						PermissionList.BUCKET_WATER.getPermissionType()))) {
			messagePermission(player);
			return true;
		}
		return false;
	}
	
    public boolean onPlayerChangeBlock(FPlayer player, String fromType, String toType, Point loc) {

        // Crop trample
		IDummyLand land = Factoid.getLands().getLandOrOutsideArea(loc);
		
		if(((land instanceof Land && ((Land) land).isBanned(player))
				|| (toType.equals("DIRT")
				&& !checkPermission(land, player,
						PermissionList.CROP_TRAMPLE.getPermissionType())))) {
			return true;
		}
		return false;
    }

	// Must be after Essentials
	public Point onPlayerRespawn(FPlayer player, Point loc) {

		DummyLand land = Factoid.getLands().getLandOrOutsideArea(
				player.getLocation());
		String strLoc;

		// For repsawn after death
		if (land.checkPermissionAndInherit(player,
						PermissionList.TP_DEATH.getPermissionType())
				&& !(strLoc = land.getFlagAndInherit(
						FlagList.SPAWN.getFlagType()).getValueString()).isEmpty()
				&& (loc = StringChanges.stringToLocation(strLoc)) != null) {
			return loc;
		}
		
		return null;
	}

	// For land listener
	public void onPlayerRespawnMonitor(FPlayer player, Point loc) {

		updatePosInfo(false, player, loc, false);
	}

	public boolean onBlockIgnite(FPlayer player, Point loc) {

		if(checkForPutFire(loc, player)) {
			return true;
		}
		return false;
	}

	public boolean onPotionSplash(FPlayer player, Point loc) {

		IDummyLand land = Factoid.getLands().getLandOrOutsideArea(loc);

		if (!checkPermission(land, player,
				PermissionList.POTION_SPLASH.getPermissionType())) {
			if (player.isOnline()) {
				messagePermission(player);
			}
			return true;
		}
		return false;
	}
	
	public boolean onEntityRegainHealth(FPlayer player, Point loc) {
		
		IPlayerConfEntry entry;
		
		if(!player.isAdminMod()) {
		
			IDummyLand land = Factoid.getLands().getLandOrOutsideArea(player.getLocation());
			
			if (!checkPermission(land, player, PermissionList.FOOD_HEAL.getPermissionType())) {
				return true;
			}
		}
		return false;
	}

	public boolean onPlayerItemConsume(FPlayer player, Point loc) {
		
		IPlayerConfEntry entry;
		
		if(!player.isAdminMod()) {
		
			IDummyLand land = Factoid.getLands().getLandOrOutsideArea(player.getLocation());
			
			if (!checkPermission(land, player, PermissionList.EAT.getPermissionType())) {
				messagePermission(player);
				return true;
			}
		}
		return false;
	}

	public boolean onPlayerCommandPreprocess(FPlayer player, Point loc, String commandTyped) {

		if (!player.isAdminMod()) {

			IDummyLand land = Factoid.getLands().getLandOrOutsideArea(loc);
			String[] excludedCommands = land
					.getFlagAndInherit(FlagList.EXCLUDE_COMMANDS.getFlagType()).getValueStringList();

			if (excludedCommands.length > 0) {

				for (String commandTest : excludedCommands) {

					if (commandTest.equalsIgnoreCase(commandTyped)) {
						player.sendMessage(ChatStyle.RED
								+ "[Factoid] "
								+ Factoid.getLanguage().getMessage(
										"GENERAL.MISSINGPERMISSIONHERE"));
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public boolean onPlayerDamage(FPlayer player, Point loc) {

		IDummyLand land = Factoid.getLands().getLandOrOutsideArea(player);
		
		if (!checkPermission(land, player, PermissionList.GOD.getPermissionType())) {
			return true;
		}
		return false;
	}

	public boolean onPlayerInteractAtEntity(FPlayer player, String entityType, String itemInHand, 
			Point loc) {

		IDummyLand land;

		Factoid.getFactoidLog().write(
				"PlayerInteractAtEntity player name: " + player.getName()
						+ ", Entity: " + entityType);

		if (!player.isAdminMod()) {
			land = Factoid.getLands().getLandOrOutsideArea(loc);
			
			// Remove and add an item from an armor stand
			if(entityType.equals("ARMOR_STAND")) {
				if (((!checkPermission(land, player, PermissionList.BUILD.getPermissionType())
						|| !checkPermission(land, player, PermissionList.BUILD_DESTROY.getPermissionType()))
						&& itemInHand == null)
						|| ((!checkPermission(land, player, PermissionList.BUILD.getPermissionType())
								|| !checkPermission(land, player, PermissionList.BUILD_PLACE.getPermissionType()))
								&& itemInHand != null)) {
					messagePermission(player);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Check when a player deposits fire
	 *
	 * @param loc the location of fire
	 * @param player the player
	 * @return if the event must be cancelled
	 */
	private boolean checkForPutFire(Point loc, FPlayer player) {
		
		if (!player.isAdminMod()) {

			IDummyLand land = Factoid.getLands().getLandOrOutsideArea(loc);

			if ((land instanceof ILand && ((ILand) land).isBanned(player))
					|| (!checkPermission(land, player,
							PermissionList.FIRE.getPermissionType()))) {
				messagePermission(player);
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Update the player position
	 * @param isTp is the player is teleported?
	 * @param player
	 * @param loc
	 * @param newPlayer is a new player online?
	 * @return
	 */
	private boolean updatePosInfo(boolean isTp, FPlayer player,
			Point loc, boolean newPlayer) {

		DummyLand land;
		DummyLand landOld;
		PlayerLandChangeEvent landEvent;
		land = Factoid.getLands().getLandOrOutsideArea(loc);

		if (newPlayer) {
			player.setLastLand((DummyLand) (landOld = land));
		} else {
			landOld = player.getLastLand();
		}
		if (newPlayer || land != landOld) {
			// First parameter : If it is a new player, it is null, if not new
			// player, it is "landOld"
			landEvent = new PlayerLandChangeEvent(newPlayer ? null : (DummyLand) landOld,
					(DummyLand) land, player, player.getLastLoc(), loc, isTp);
			pm.callEvent(landEvent);
			
			// Deprecated old land change event
			if(!landEvent.isCancelled()) {
				oldLandEvent = new me.tabinol.factoid.event.PlayerLandChangeEvent(
						newPlayer ? null : (DummyLand) landOld,
						(DummyLand) land, player, entry.getLastLoc(), loc, isTp);
				pm.callEvent(oldLandEvent);
			}
			
			if (landEvent.isCancelled() || oldLandEvent.isCancelled()) {
				if (isTp) {
					((PlayerTeleportEvent) event).setCancelled(true);
					return;
				}
				if (land == landOld || newPlayer) {
					player.teleport(player.getWorld().getSpawnLocation());
				} else {
					Location retLoc = entry.getLastLoc();
					player.teleport(new Location(retLoc.getWorld(), retLoc
							.getX(), retLoc.getBlockY(), retLoc.getZ(), loc
							.getYaw(), loc.getPitch()));
				}
				entry.setTpCancel(true);
				return;
			}
			entry.setLastLand((me.tabinol.factoid.lands.DummyLand) land);

			// Update player in the lands
			if (landOld instanceof Land && landOld != land) {
				landOld.removePlayerInLand(player);
			}
			if (land instanceof Land) {
				land.addPlayerInLand(player);
			}
		}
		entry.setLastLoc(loc);

		// Update visual selection
		if (entry.getSelection().hasSelection()) {
			for (RegionSelection sel : entry.getSelection().getSelections()) {
				if (sel instanceof PlayerMoveListen) {
					((PlayerMoveListen) sel).playerMove();
				}
			}
		}
	}
}
