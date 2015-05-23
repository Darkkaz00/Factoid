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

import me.tabinol.factoid.Factoid;
import me.tabinol.factoidapi.lands.ILand;
import me.tabinol.factoidapi.playercontainer.EPlayerContainerType;
import me.tabinol.factoidapi.playercontainer.IPlayerContainer;
import me.tabinol.factoidapi.playercontainer.IPlayerContainerFactionTerritory;

import org.bukkit.entity.Player;


/**
 * The Class PlayerContainerFactionTerritory.
 */
public class PlayerContainerFactionTerritory extends PlayerContainer 
	implements IPlayerContainerFactionTerritory {
    
    /** The land. */
    private ILand land;
    
    /**
     * Instantiates a new player container faction territory.
     *
     * @param land the land
     */
    public PlayerContainerFactionTerritory(ILand land) {
        
        super("", EPlayerContainerType.FACTION_TERRITORY, false);
        this.land = land;
    }
    
    /* (non-Javadoc)
     * @see me.tabinol.factoid.playercontainer.PlayerContainerInterface#equals(me.tabinol.factoid.playercontainer.PlayerContainer)
     */
    @Override
    public boolean equals(IPlayerContainer container2) {
        
        return container2 instanceof PlayerContainerFactionTerritory &&
                land == ((PlayerContainerFactionTerritory)container2).land;
    }
    
    /* (non-Javadoc)
     * @see me.tabinol.factoid.playercontainer.PlayerContainerInterface#copyOf()
     */
    @Override
    public PlayerContainer copyOf() {
        
        return new PlayerContainerFactionTerritory(land);
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.playercontainer.PlayerContainerInterface#hasAccess(org.bukkit.entity.Player)
     */
    @Override
    public boolean hasAccess(Player player) {
        
        if(land.getFactionTerritory() == null) {
            return false;
        }
        
        return land.getFactionTerritory().isPlayerInList(Factoid.getThisPlugin().iPlayerConf().get(player).getPlayerContainer());
    }

    @Override
    public boolean hasAccess(Player player, ILand land) {
        
        return hasAccess(player);
    }

    /**
     * Gets the land.
     *
     * @return the land
     */
    public ILand getLand() {
        
        return land;
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.playercontainer.PlayerContainerInterface#setLand(me.tabinol.factoid.lands.Land)
     */
    @Override
    public void setLand(ILand land) {
     
        this.land = land;
    }
}
