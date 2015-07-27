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

import java.util.Map;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.factions.Faction;
import me.tabinol.factoid.lands.DummyLand;
import me.tabinol.factoid.lands.areas.Point;
import me.tabinol.factoid.minecraft.FPlayer;
import me.tabinol.factoid.minecraft.Item;
import me.tabinol.factoid.parameters.FlagList;
import me.tabinol.factoid.playercontainer.PlayerContainerPlayer;
import me.tabinol.factoid.utilities.ExpirableHashMap;

/**
 * PVP Listener
 */
public class PvpListener extends CommonListener {

	/** The Constant FIRE_EXPIRE. */
	public final static long FIRE_EXPIRE = 20 * 30;

	/** The player fire location. */
	private ExpirableHashMap<Point, PlayerContainerPlayer> playerFireLocation;

	/**
	 * Instantiates a new pvp listener.
	 */
	public PvpListener() {

		super();
		playerFireLocation = new ExpirableHashMap<Point, PlayerContainerPlayer>(FIRE_EXPIRE);
	}

	public boolean onPlayerDamageByPlayer(FPlayer player, FPlayer victime, Point locPlayer, Point locVictime) {

		DummyLand land = Factoid.getLands().getLandOrOutsideArea(locVictime);
		DummyLand landSource = Factoid.getLands().getLandOrOutsideArea(locPlayer);

		// For PVP
		if (!isPvpValid(land, landSource, player.getFSender().getPlayerContainer(), 
				victime.getFSender().getPlayerContainer())) {
			return true;
		}
		
		return false;
	}

	public void onBlockPlaceMonitor(FPlayer player, Item blockType, Point loc) {
		
		if(blockType.strEquals("FIRE")) {
			
			checkForPvpFire(loc, player);
		}
	}

	public void onBlockIgniteMonitor(FPlayer player, Point point) {

		checkForPvpFire(point, player);
	}
	
	public void onBlockSpreadMonitor(Point source, Point location) {
		
		PlayerContainerPlayer pc = playerFireLocation.get(source);
		
		if(pc != null) {
			
			// Add fire for pvp listen
			playerFireLocation.put(location, pc);
		}
	}
	
	public boolean onPlayerDamage(FPlayer player, Point loc, String cause, Item blockType) {
		
		// Check for fire cancel
		if(cause.contains("FIRE")) {
			
			DummyLand land = Factoid.getLands().getLandOrOutsideArea(loc);
			
			// Check for fire near the player
			for(Map.Entry<Point, PlayerContainerPlayer> fireEntry : playerFireLocation.entrySet()) {
				
				if(loc.getWorld() == fireEntry.getKey().getWorld() 
						&& loc.distance(fireEntry.getKey()) < 5) {
					if((blockType.strEquals("FIRE") || blockType.strEquals("AIR")) 
							&& !isPvpValid(land, land, fireEntry.getValue(), player.getFSender().getPlayerContainer())) {
						
						// remove fire
						Factoid.getFactoidLog().write("Anti-pvp from " 
								+ player.getFSender().getPlayerContainer().getPlayer().getName()
								+ " to " + player.getName());
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Check when a player deposits fire and add it to list
	 *
	 * @param point the bloc location
	 * @param player the player
	 */
	private void checkForPvpFire(Point point, FPlayer player) {
		
		DummyLand land = Factoid.getLands().getLandOrOutsideArea(point);

		if (land.getFlagAndInherit(FlagList.FULL_PVP.getFlagType()).getValueBoolean() == false
				|| land.getFlagAndInherit(FlagList.FACTION_PVP.getFlagType()).getValueBoolean() == false) {
				
			// Add fire for pvp listen
			playerFireLocation.put(point, player.getFSender().getPlayerContainer());
		}
	}
	
	/**
	 * Checks if pvp is valid.
	 *
	 * @param land the land
	 * @param land the land of the attacker
	 * @param attacker the attacker
	 * @param victim the victim
	 * @return true, if is pvp valid
	 */
	private boolean isPvpValid(DummyLand land, DummyLand landSource, PlayerContainerPlayer attacker, 
			PlayerContainerPlayer victim) {
		
		Faction faction = Factoid.getFactions().getPlayerFaction(attacker);
		Faction factionVictim = Factoid.getFactions().getPlayerFaction(victim);

		boolean result = isPvpValid(land, faction, factionVictim);
		
		if(result) {
			
			// Check if the atacker is in the land
			if(land == landSource) {
				return true;
			} else {
				return isPvpValid(landSource, faction, factionVictim);
			}
		}
		
		return false;
	}
		
	/**
	 * Checks if pvp is valid.
	 *
	 * @param land the land
	 * @param faction the attacker faction
	 * @param factionVictim the victim faction
	 * @return true, if is pvp valid
	 */
	private boolean isPvpValid(DummyLand land, Faction faction, 
			Faction factionVictim) {


		if (faction != null && faction == factionVictim
				&& land.getFlagAndInherit(FlagList.FACTION_PVP.getFlagType()).getValueBoolean() == false) {
				
			return false;
		} else if (land.getFlagAndInherit(FlagList.FULL_PVP.getFlagType()).getValueBoolean() == false) {

			return false;
		}
		
		return true;	
	}
}
