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
package me.tabinol.factoid.event;

import me.tabinol.factoid.lands.DummyLand;
import me.tabinol.factoid.lands.Land;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;


/**
 * The Class PlayerLandChangeEvent.
 * @deprecated Use FactoidAPI
 */
@Deprecated
public class PlayerLandChangeEvent extends LandEvent implements Cancellable {
    
    /** The Constant handlers. */
    private static final HandlerList handlers = new HandlerList();
    
    /** The cancelled. */
    protected boolean cancelled = false;
    
    /** The player. */
    Player player;
    
    /** The from loc. */
    Location fromLoc;
    
    /** The to loc. */
    Location toLoc;
    
    /** The last land. */
    Land lastLand;
    
    /** The last dummy land. */
    DummyLand lastDummyLand;
    
    /** The is tp. */
    boolean isTp;

    /**
     * Instantiates a new player land change event.
     *
     * @param lastDummyLand the last dummy land
     * @param dummyLand the dummy land
     * @param player the player
     * @param fromLoc the from loc
     * @param toLoc the to loc
     * @param isTp the is tp
     */
    public PlayerLandChangeEvent(final DummyLand lastDummyLand, final DummyLand dummyLand, final Player player, 
            final Location fromLoc, final Location toLoc, final boolean isTp) {

        super(dummyLand);
        this.lastDummyLand = lastDummyLand;
        
        if(lastDummyLand instanceof Land) {
            lastLand = (Land) lastDummyLand;
        } else {
            lastLand = null;
        }
        
        this.player = player;
        this.fromLoc = fromLoc;
        this.toLoc = toLoc;
        this.isTp = isTp;
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.event.LandEvent#getHandlers()
     */
    @Override
    public HandlerList getHandlers() {

        return handlers;
    }

    /**
     * Gets the handler list.
     *
     * @return the handler list
     */
    public static HandlerList getHandlerList() {

        return handlers;
    }

    /* (non-Javadoc)
     * @see org.bukkit.event.Cancellable#isCancelled()
     */
    @Override
    public boolean isCancelled() {
        
        return cancelled;
    }

    /* (non-Javadoc)
     * @see org.bukkit.event.Cancellable#setCancelled(boolean)
     */
    @Override
    public void setCancelled(boolean bln) {
        
        cancelled = bln;
    }
    
    /**
     * Gets the player.
     *
     * @return the player
     */
    public Player getPlayer() {
        
        return player;
    }
    
    /**
     * Gets the last land.
     *
     * @return the last land
     */
    public Land getLastLand() {
        
        return lastLand;
    }
    
    /**
     * Gets the last land or outside.
     *
     * @return the last land or outside
     */
    public DummyLand getLastLandOrOutside() {
        
        return lastDummyLand;
    }
    
    /**
     * Gets the from loc.
     *
     * @return the from loc
     */
    public Location getFromLoc() {
        
        return fromLoc;
    }

    /**
     * Gets the to loc.
     *
     * @return the to loc
     */
    public Location getToLoc() {
        
        return toLoc;
    }
    
    /**
     * Checks if is tp.
     *
     * @return true, if is tp
     */
    public boolean isTp() {
        
        return isTp;
    }
}
