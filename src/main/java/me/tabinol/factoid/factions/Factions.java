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
import java.util.TreeMap;
import java.util.UUID;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoidapi.factions.IFaction;
import me.tabinol.factoidapi.factions.IFactions;
import me.tabinol.factoidapi.playercontainer.IPlayerContainerPlayer;


/**
 * The Class Factions.
 */
public class Factions {

    /** The faction list. */
    private final TreeMap<String, IFaction> factionList;
    
    /** The faction uuid list. */
    private final TreeMap<UUID, IFaction> factionUUIDList;

    /**
     * Instantiates a new factions.
     */
    public Factions() {

        factionList = new TreeMap<String, IFaction>();
        factionUUIDList = new TreeMap<UUID, IFaction>();
    }

    /**
     * Creates the faction.
     *
     * @param factionName the faction name
     * @return the faction
     */
    public Faction createFaction(String factionName) {

        return createFaction(factionName, null);

    }

    /**
     * Creates the faction.
     *
     * @param factionName the faction name
     * @param uuid the uuid
     * @return the faction
     */
    public Faction createFaction(String factionName, UUID uuid) {

        Faction faction;

        if (factionList.containsKey(factionName)) {
            return null;
        }

        if (uuid == null) {
            uuid = UUID.randomUUID();
        }

        faction = new Faction(factionName, uuid);

        factionList.put(factionName, faction);
        factionUUIDList.put(uuid, faction);
        Factoid.getFactoidLog().write("add faction: " + faction.getName());

        return faction;
    }

    /**
     * Removes the faction.
     *
     * @param faction the faction
     * @return true, if successful
     */
    public boolean removeFaction(IFaction faction) {

        if (!factionList.containsKey(faction.getName())) {
            return false;
        }
        Factoid.getStorageThread().removeFaction((Faction) faction);
        factionList.remove(faction.getName());
        Factoid.getFactoidLog().write("remove faction: " + faction.getName());
        return true;
    }

    /**
     * Removes the faction.
     *
     * @param factionName the faction name
     * @return true, if successful
     */
    public boolean removeFaction(String factionName) {

        String factionLower;

        if (factionName == null || !factionList.containsKey(factionLower = factionName.toLowerCase())) {
            return false;
        }
        return removeFaction(factionList.get(factionLower));
    }

    /**
     * Gets the faction.
     *
     * @param factionName the faction name
     * @return the faction
     */
    public Faction getFaction(String factionName) {

        return (Faction) factionList.get(factionName.toLowerCase());
    }

    /**
     * Gets the player faction.
     *
     * @param player the player
     * @return the player faction
     */
    public Faction getPlayerFaction(IPlayerContainerPlayer player) {

        for (IFaction faction : factionList.values()) {
            if (faction.isPlayerInList(player)) {
                return (Faction) faction;
            }
        }

        return null;
    }

    /**
     * Gets the factions.
     *
     * @return the factions
     */
    public Collection<IFaction> getFactions() {

        return factionList.values();
    }
}
