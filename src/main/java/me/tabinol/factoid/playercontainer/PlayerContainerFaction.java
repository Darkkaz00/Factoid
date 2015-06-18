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
package me.tabinol.factoid.playercontainer;

import me.tabinol.factoid.factions.Faction;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.minecraft.FPlayer;
import me.tabinol.factoid.utilities.ChatStyle;

/**
 * The Class PlayerContainerFaction.
 */
public class PlayerContainerFaction extends PlayerContainer {

    /** The faction. */
    private Faction faction;

    /**
     * Instantiates a new player container faction.
     *
     * @param faction the faction
     */
    public PlayerContainerFaction(Faction faction) {

        super(faction.getName(), PlayerContainerType.FACTION, true);
        this.faction = faction;
    }

    /**
     * Gets the faction.
     *
     * @return the faction
     */
    public Faction getFaction() {

        return faction;
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.playercontainer.PlayerContainerInterface#equals(me.tabinol.factoid.playercontainer.PlayerContainer)
     */
    @Override
    public boolean equals(PlayerContainer container2) {

        return container2 instanceof PlayerContainerFaction
                && name.equalsIgnoreCase(container2.getName());
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.playercontainer.PlayerContainerInterface#copyOf()
     */
    @Override
    public PlayerContainer copyOf() {

        return new PlayerContainerFaction(faction);
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.playercontainer.PlayerContainerInterface#hasAccess(org.bukkit.entity.Player)
     */
    @Override
    public boolean hasAccess(FPlayer player) {

        return faction.isPlayerInList(player.getPlayerContainer());
    }

    @Override
    public boolean hasAccess(FPlayer player, Land land) {
        
        return hasAccess(player);
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.playercontainer.PlayerContainer#getPrint()
     */
    @Override
    public String getPrint() {

        return ChatStyle.GOLD + "F:" + ChatStyle.WHITE + name;
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.playercontainer.PlayerContainerInterface#setLand(me.tabinol.factoid.lands.Land)
     */
    @Override
    public void setLand(Land land) {

    }
}
