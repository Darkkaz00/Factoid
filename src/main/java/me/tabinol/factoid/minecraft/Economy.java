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



/**
 * Money from players. This class return zero, but has inheritence for real plugins.
 *
 * @author Tabinol
 */
public class Economy {

    /**
     * Gets the player balance.
     *
     * @param offlinePlayer the offline player
     * @param worldName the world name
     * @return the player balance
     */
    public Double getPlayerBalance(FPlayer offlinePlayer, String worldName) {
        
        return 0d;
    }
    
    /**
     * Give to player.
     *
     * @param offlinePlayer the offline player
     * @param worldName the world name
     * @param amount the amount
     * @return true, if successful
     */
    public boolean giveToPlayer(FPlayer offlinePlayer, String worldName, Double amount) {
        
        return false;
    }

    /**
     * Gets the from player.
     *
     * @param offlinePlayer the offline player
     * @param worldName the world name
     * @param amount the amount
     * @return the from player
     */
    public boolean getFromPlayer(FPlayer offlinePlayer, String worldName, Double amount) {
        
        return false;
    }
    
    /**
     * To format.
     *
     * @param amount the amount
     * @return the string
     */
    public String toFormat(Double amount) {
        
        return amount + "";
    }
}
