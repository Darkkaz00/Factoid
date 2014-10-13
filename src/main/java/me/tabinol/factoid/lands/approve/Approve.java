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
package me.tabinol.factoid.lands.approve;

import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.exceptions.FactoidLandException;
import me.tabinol.factoidapi.lands.ILand;
import me.tabinol.factoidapi.lands.areas.ICuboidArea;
import me.tabinol.factoid.lands.collisions.Collisions.LandAction;
import me.tabinol.factoidapi.playercontainer.IPlayerContainer;


/**
 * The Class Approve.
 */
public class Approve {
    
    /** The action. */
    private final LandAction action;
    
    /** The land name. */
    private final String landName;
    
    /** The removed area id. */
    private final int removedAreaId;
    
    /** The new area. */
    private final ICuboidArea newArea;
    
    /** The owner. */
    private final IPlayerContainer owner;
    
    /** The parent. */
    private final ILand parent;
    
    /** The price. */
    private final double price;
    
    /** If the owner has to pay */
    private final boolean mustPay;
    
    /** The date time. */
    private final Calendar dateTime;
    
    /**
     * Instantiates a new approve.
     *
     * @param landName the land name
     * @param action the action
     * @param removedAreaId the removed area id
     * @param newArea the new area
     * @param owner the owner
     * @param parent the parent
     * @param price the price
     * @param mustPay If the owner has to pay
     * @param dateTime the date time
     */
    public Approve(String landName, LandAction action, int removedAreaId, 
            ICuboidArea newArea, IPlayerContainer owner, ILand parent, double price,
            boolean mustPay, Calendar dateTime) {
        
        this.action = action;
        this.landName = landName.toLowerCase();
        this.removedAreaId = removedAreaId;
        this.newArea = newArea;
        this.owner = owner;
        this.parent = parent;
        this.price = price;
        this.dateTime = dateTime;
        this.mustPay = mustPay;
        
    }

    /**
     * Gets the action.
     *
     * @return the action
     */
    public LandAction getAction() {
        
        return action;
    }
    
    /**
     * Gets the land name.
     *
     * @return the land name
     */
    public String getLandName() {
        
        return landName;
    }
    
    /**
     * Gets the removed area id.
     *
     * @return the removed area id
     */
    public int getRemovedAreaId() {
        
        return removedAreaId;
    }
    
    /**
     * Gets the new area.
     *
     * @return the new area
     */
    public ICuboidArea getNewArea() {
        
        return newArea;
    }
    
    /**
     * Gets the owner.
     *
     * @return the owner
     */
    public IPlayerContainer getOwner() {
        
        return owner;
    }
    
    /**
     * Gets the parent.
     *
     * @return the parent
     */
    public ILand getParent() {
        
        return parent;
    }
    
    /**
     * Gets the price.
     *
     * @return the price
     */
    public double getPrice() {
        
        return price;
    }
    
    /**
     * Checks if is must pay.
     *
     * @return true, if is must pay
     */
    public boolean isMustPay() {
    	
    	return mustPay;
    }
    
    /**
     * Gets the date time.
     *
     * @return the date time
     */
    public Calendar getDateTime() {
        
        return dateTime;
    }
    
    /**
     * Creates the action.
     */
    public void createAction() {
        
        if(action == LandAction.AREA_ADD) {
            Factoid.getThisPlugin().iLands().getLand(landName).addArea(newArea, price, mustPay);
        } else if(action == LandAction.AREA_REMOVE) {
            Factoid.getThisPlugin().iLands().getLand(landName).removeArea(removedAreaId);
        } else if(action == LandAction.AREA_MODIFY) {
            Factoid.getThisPlugin().iLands().getLand(landName).replaceArea(removedAreaId, newArea, price, mustPay);
        } else if(action == LandAction.LAND_ADD) {
            try {
                Factoid.getThisPlugin().iLands().createLand(landName, owner, newArea, parent, price);
            } catch (FactoidLandException ex) {
                Logger.getLogger(Approve.class.getName()).log(Level.SEVERE, "On land create", ex);
            }
        } else if(action == LandAction.LAND_REMOVE) {
            try {
                Factoid.getThisPlugin().iLands().removeLand(landName);
            } catch (FactoidLandException ex) {
                Logger.getLogger(Approve.class.getName()).log(Level.SEVERE, "On land remove", ex);
            }
        }
    }
}
