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
package me.tabinol.factoid.economy;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.exceptions.SignException;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.areas.Point;
import me.tabinol.factoid.minecraft.FPlayer;
import me.tabinol.factoid.minecraft.FSign;
import me.tabinol.factoid.utilities.ChatStyle;


/**
 * Represent the economy sign.
 *
 * @author Tabinol
 */
public class EcoSign {

	/** The land. */
	private final Land land;

	/** The location. */
	private final Point location;
	
	/** The facing. */
	private final float yaw;
	
	/** The is wall sign. */
	private final boolean isWallSign;

	// Create from player position
	/**
	 * Instantiates a new eco sign.
	 *
	 * @param land            the land
	 * @param player            the player
	 * @throws SignException the sign exception
	 */
	public EcoSign(Land land, FPlayer player) throws SignException {

		Point targetBlock = player.getTargetBlockLocation();
		Point testBlock;
		this.land = land;

		if(targetBlock == null) {
			throw new SignException();
		}
		
		testBlock = targetBlock.getNearPoint(0, 1, 0);
		if (Factoid.getServer().getBlockTypeName(testBlock).equals("AIR") && land.isLocationInside(testBlock)) {

			// If the block as air upside, put the block on top of it
			location = testBlock;
			yaw = player.getLocation().getYaw();
			isWallSign = false;
		
		} else {
			
			// A Wall Sign
			yaw = player.getLocation().getYaw();
			testBlock = targetBlock.getNearPoint(wallXDiff(yaw), 0, wallZDiff(yaw));
			if(!Factoid.getServer().getBlockTypeName(testBlock).equals("AIR")) {
				// Error no place to put the wall sign
				throw new SignException();
			}
			location = testBlock;
			isWallSign = true;
		}
		
		// Target is outside the land
		if(!land.isLocationInside(this.location)) {
			throw new SignException();
		}
		
		Factoid.getFactoidLog().write("SignToCreate: PlayerYaw: " + player.getLocation().getYaw() +
				", Location: " + location.toString() + ", Facing (yaw): " + yaw +
				", isWallSign: " + isWallSign);
	}
	
	/**
	 * Instantiates a new eco sign (If the sign is already existing only).
	 *
	 * @param land the land
	 * @param location the location
	 * @throws SignException the sign exception
	 */
	public EcoSign(Land land, Point location) throws SignException {
		
		this.land = land;
		this.location = location;
		
		// Load chunk
		Factoid.getServer().loadChunk(location);
		
		// Get Sign parameter
		FSign sign = Factoid.getServer().getSign(location);
		
		isWallSign = sign.isWallSign();
		
		yaw = sign.getYaw();
	}

	/**
	 * Gets the location.
	 *
	 * @return the location
	 */
	public Point getLocation() {

		return location;
	}

	/**
	 * Creates the sign for sale.
	 *
	 * @param price            the price
	 * @throws SignException the sign exception
	 */
	public void createSignForSale(double price) throws SignException {

		String[] lines = new String[4];
		lines[0] = ChatStyle.GREEN
				+ Factoid.getLanguage().getMessage("SIGN.SALE.FORSALE");
		lines[1] = ChatStyle.GREEN + land.getName();
		lines[2] = "";
		lines[3] = ChatStyle.BLUE + Factoid.getPlayerMoney().toFormat(price);

		Factoid.getServer().createSign(location, yaw, lines, land, isWallSign);
	}

	/**
	 * Creates the sign for rent.
	 *
	 * @param price            the price
	 * @param renew            the renew
	 * @param autoRenew            the auto renew
	 * @param tenantName            the tenant name
	 * @throws SignException the sign exception
	 */
	public void createSignForRent(double price, int renew,
			boolean autoRenew, String tenantName) throws SignException {

		String[] lines = new String[4];

		if (tenantName != null) {
			lines[0] = ChatStyle.RED
					+ Factoid.getLanguage().getMessage("SIGN.RENT.RENTED");
			lines[1] = ChatStyle.RED + tenantName;
		} else {
			lines[0] = ChatStyle.GREEN
					+ Factoid.getLanguage().getMessage("SIGN.RENT.FORRENT");
			lines[1] = ChatStyle.GREEN + land.getName();
		}

		if (autoRenew) {
			lines[2] = ChatStyle.BLUE
					+ Factoid.getLanguage().getMessage("SIGN.RENT.AUTORENEW");
		} else {
			lines[2] = "";
		}

		lines[3] = ChatStyle.BLUE + Factoid.getPlayerMoney().toFormat(price)
				+ "/" + renew;

		Factoid.getServer().createSign(location, yaw, lines, land, isWallSign);
	}


	/**
	 * Removes the sign.
	 */
	public void removeSign() {
		
		removeSign(location);
	}
	
	/**
	 * Removes the old sign.
	 *
	 * @param oldSignLocation the old sign location
	 */
	public void removeSign(Point oldSignLocation) {

		Factoid.getServer().loadChunk(oldSignLocation);

		// Remove only if it is a sign;
		if (Factoid.getServer().getBlockTypeName(oldSignLocation).contains("SIGN")) {
			Factoid.getServer().removeBlockAndDropSign(oldSignLocation);
		}
	}

	
	private int wallXDiff(float yaw) {
		
		if(yaw < 0) {
			yaw += 360;
		}

		if(yaw > 315 || yaw <= 45) {
			return 0;
		} else if(yaw <= 135) {
			return 1;
		} else if(yaw <= 225) {
			return 0;
		} else {
			return -1;
		}
	}

	private int wallZDiff(float yaw) {
		
		if(yaw < 0) {
			yaw += 360;
		}

		if(yaw > 315 || yaw <= 45) {
			return -1;
		} else if(yaw <= 135) {
			return 0;
		} else if(yaw <= 225) {
			return 1;
		} else {
			return 0;
		}
	}
}
