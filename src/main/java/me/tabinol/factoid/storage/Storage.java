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

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.factions.Faction;
import me.tabinol.factoid.lands.Land;


/**
 * The Class Storage.
 */
public abstract class Storage implements StorageInt {

    /** The Constant LAND_VERSION. */
    public static final int LAND_VERSION = Factoid.getMavenAppProperties().getPropertyInt("landVersion");
    
    /** The Constant FACTION_VERSION. */
    public static final int FACTION_VERSION = Factoid.getMavenAppProperties().getPropertyInt("factionVersion");

    /** The to resave. */
    private boolean toResave = false; // If a new version of .conf file, we need to save again

    /**
     * Instantiates a new storage.
     */
    public Storage() {
    }
    
    /* (non-Javadoc)
     * @see me.tabinol.factoid.storage.StorageInt#loadAll()
     */
    @Override
    public void loadAll() {

        loadFactions();
        loadLands();

        // New version, we have to save all
        if (toResave) {
            saveAll();
        }
    }

    /**
     * Save all.
     */
    private void saveAll() {

        for (Land land : Factoid.getLands().getLands()) {

            land.forceSave();
        }

        for (Faction faction : Factoid.getFactions().getFactions()) {

            faction.forceSave();
        }
    }
}
