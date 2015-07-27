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

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.lands.DummyLand;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.areas.Point;
import me.tabinol.factoid.minecraft.FPlayer;
import me.tabinol.factoid.parameters.PermissionType;
import me.tabinol.factoid.utilities.ChatStyle;

/**
 * Common methods for Listeners
 */
public class CommonListener {
	
	public enum Click {
		RIGHT,
		LEFT,
		NONE;
	}
	
	/**
	 * Check permission.
	 * 
	 * @param land
	 *            the land
	 * @param player
	 *            the player
	 * @param pt
	 *            the pt
	 * @return true, if successful
	 */
	protected boolean checkPermission(DummyLand land, FPlayer player,
			PermissionType pt) {

		return land.checkPermissionAndInherit(player, pt) == pt
				.getDefaultValue();
	}

	/**
	 * Message permission.
	 * 
	 * @param player
	 *            the player
	 */
	protected void messagePermission(FPlayer player) {

		player.getFSender().sendMessage(ChatStyle.GRAY + "[Factoid] "
				+ Factoid.getLanguage().getMessage("GENERAL.MISSINGPERMISSION"));
	}
	
	/**
	 * Check is the block to destroy is attached to an eco sign
	 * @param land the land
	 * @param point the location
	 * @return true if the sign is attached
	 */
	protected boolean hasEcoSign(Land land, Point point) {
		
		return (land.getSaleSignLoc() != null && hasEcoSign(point, land.getSaleSignLoc()))
				|| (land.getRentSignLoc() != null && hasEcoSign(point, land.getRentSignLoc())); 
	}

	/**
	 * Check is the block to destroy is attached to an eco sign
	 * @param block the block
	 * @param ecoSignLoc the eco sign location
	 * @return true if the sign is attached
	 */
	private boolean hasEcoSign(Point point, Point ecoSignLoc) {
		
		Point up = point.getNearPoint(0, 1, 0);
		
		if((up.equals(ecoSignLoc) && Factoid.getServer().getBlockItem(up).strEquals("SIGN"))
				|| isEcoSignAttached(point.getNearPoint(0, 0, -1), ecoSignLoc)
				|| isEcoSignAttached(point.getNearPoint(0, 0, 1), ecoSignLoc)
				|| isEcoSignAttached(point.getNearPoint(-1, 0, 0), ecoSignLoc)
				|| isEcoSignAttached(point.getNearPoint(0, 0, 1), ecoSignLoc)) {
			return true;
		}
		
		return false;
	}
	
	private boolean isEcoSignAttached(Point checkPoint, Point ecoSignLoc) {
		
		if(checkPoint.equals(ecoSignLoc) && Factoid.getServer().getBlockItem(checkPoint).strEquals("WALL_SIGN")) {
			return true;
		}
		
		return false;
	}
}
