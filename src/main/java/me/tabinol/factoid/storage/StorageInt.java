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
package me.tabinol.factoid.storage;

import me.tabinol.factoid.factions.Faction;
import me.tabinol.factoid.lands.Land;


/**
 * The Interface StorageInt.
 */
public interface StorageInt {
    
    /**
     * Load all.
     */
    public void loadAll();
    
    /**
     * Save land.
     *
     * @param land the land
     */
    public void saveLand(Land land);
    
    /**
     * Removes the land.
     *
     * @param land the land
     */
    public void removeLand(Land land);

    /**
     * Load factions.
     */
    public void loadFactions();
    
    /**
     * Load lands.
     */
    public void loadLands();    
    
    /**
     * Save faction.
     *
     * @param faction the faction
     */
    public void saveFaction(Faction faction);
    
    /**
     * Removes the faction.
     *
     * @param faction the faction
     */
    public void removeFaction(Faction faction);
}
