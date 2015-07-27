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

package me.tabinol.factoid.minecraft;

import java.util.UUID;

import me.tabinol.factoid.lands.areas.Point;

/**
 * This interface represent a Player for Factoid and the setup.
 * @author Tabinol
 *
 */
public interface FPlayer {
	
    public FSender getFSender();
	public Point getLocation();
	public UUID getUUID();
	public String getName();
	public String getDisplayName();
	public boolean isOnline();
	public boolean hasPermission(String perm);
	
	/**
	 * Get the game mode in STRING format
	 * @return the game mode
	 */
	public String getGameMode();
	
	public void removeOneItemFromHand();
	public void teleport(Point newLocation);
	public void sendBlockChange(Point loc, Item blockType, byte by);
	public void sendBlockChange(Point loc, String blockShortName, byte by);
	public Point getTargetBlockLocation();
	public Item getItemInHand();
	
	// Heal
	public int getFoodLevel();
	public void setFoodLevel(int level);
	public double getMaxHealth();
	public double getHealth();
	public void setHealth(double health);
	public boolean isDead();
}
