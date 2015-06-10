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
package me.tabinol.factoid.factions;

import java.util.Collection;
import java.util.TreeSet;
import java.util.UUID;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoidapi.factions.IFaction;
import me.tabinol.factoidapi.playercontainer.IPlayerContainerPlayer;

/**
 * The Class Faction.
 */
public class Faction {

    /** The name. */
    private String name;
    
    /** The uuid. */
    private final UUID uuid;
    
    /** The players. */
    private TreeSet<IPlayerContainerPlayer> players;
    
    /** The auto save. */
    private boolean autoSave = true;

    /**
     * Instantiates a new faction.
     *
     * @param name the name
     * @param uuid the uuid
     */
    public Faction(String name, UUID uuid) {

        this.name = name.toLowerCase();
        this.uuid = uuid;
        this.players = new TreeSet<IPlayerContainerPlayer>();
        doSave();
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {

        return name;
    }
    
    /**
     * Gets the uuid.
     *
     * @return the uuid
     */
    public UUID getUUID() {
        
        return uuid;
    }

    /**
     * Adds the player.
     *
     * @param player the player
     */
    public void addPlayer(IPlayerContainerPlayer player) {

        players.add(player);
        doSave();
        Factoid.getFactoidLog().write(player.toString() + " is added in faction " + name);
    }

    /**
     * Removes the player.
     *
     * @param player the player
     * @return true, if successful
     */
    public boolean removePlayer(IPlayerContainerPlayer player) {

        if (players.remove(player)) {
            doSave();
            Factoid.getFactoidLog().write(player.toString() + " is removed in faction " + name);
            return true;
        }

        return false;
    }

    /**
     * Checks if is player in list.
     *
     * @param player the player
     * @return true, if is player in list
     */
    public boolean isPlayerInList(IPlayerContainerPlayer player) {

        return players.contains(player);
    }

    /**
     * Gets the players.
     *
     * @return the players
     */
    public Collection<IPlayerContainerPlayer> getPlayers() {

        return players;
    }

    /**
     * Sets the auto save.
     *
     * @param autoSave the new auto save
     */
    public void setAutoSave(boolean autoSave) {
        
        this.autoSave = autoSave;
    }
    
    /**
     * Force save.
     */
    public void forceSave() {
        
        Factoid.getStorageThread().saveFaction(this);
        Factoid.getFactoidLog().write("Faction " + name + " is saved.");
    }
    
    /**
     * Do save.
     */
    private void doSave() {
        
        if(autoSave) {
            forceSave();
        }
    }
}
