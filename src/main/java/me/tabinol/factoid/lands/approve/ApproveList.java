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

import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.Factoid.ServerType;
import me.tabinol.factoid.lands.approve.bukkit.ApproveListBukkit;
import me.tabinol.factoid.lands.approve.sponge.ApproveListSponge;


/**
 * The Class ApproveList.
 */
public abstract class ApproveList {

    /** The land names. */
    final protected TreeSet<String> landNames;

    public ApproveList() {
    	
        landNames = new TreeSet<String>();
    }
    
    /**
     * Adds the approve.
     *
     * @param approve the approve
     */
    public abstract void addApprove(Approve approve);
    
    /**
     * Gets the approve list.
     *
     * @return the approve list
     */
    public TreeMap<String,Approve> getApproveList() {

    	TreeMap<String,Approve> approves = new TreeMap<String,Approve>();
    	TreeMap<String,Approve> approvesToRemove = new TreeMap<String,Approve>();
    	
    	// Check if land names are ok
    	for(String landName : landNames) {
    		
        	Approve app = getApprove(landName);
        	
        	if(app != null) {
        		
        		// Approve ok, put in list
        		approves.put(landName, app);
        	} else {
        		
        		// Approve not ok, add it to list
        		approvesToRemove.put(landName, app);
        	}
        }
    	
    	// Remove wrong approves
    	for(Map.Entry<String,Approve> appEntry : approvesToRemove.entrySet()) {
    		
    		removeApprove(appEntry.getKey());
    	}
    	
    	return approves;
    }

    /**
     * Checks if is in approve.
     *
     * @param landName the land name
     * @return true, if is in approve
     */
    public boolean isInApprove(String landName) {

        return landNames.contains(landName.toLowerCase());
    }
    
    /**
     * Gets the approve.
     *
     * @param landName the land name
     * @return the approve
     */
    public abstract Approve getApprove(String landName);
    
    /**
     * Removes the approve.
     *
     * @param approve the approve
     */
    public abstract void removeApprove(Approve approve);
    
    /**
     * Removes the approve.
     *
     * @param landName the land name
     */
    public abstract void removeApprove(String landName);
    
    /**
     * Removes the all.
     */
    public abstract void removeAll();

	
    /**
     * Create the specific Approve List
     * @return approve list
     */
    public static ApproveList newApproveList() {
    	
		if(Factoid.getServerType() == ServerType.BUKKIT) {
			return new ApproveListBukkit();
		} else {
			return new ApproveListSponge();
		}
	}
}
