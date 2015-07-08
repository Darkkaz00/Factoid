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

package me.tabinol.factoid.lands.approve.sponge;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.approve.Approve;
import me.tabinol.factoid.lands.approve.ApproveList;
import me.tabinol.factoid.lands.areas.CuboidArea;
import me.tabinol.factoid.lands.collisions.Collisions.LandAction;
import me.tabinol.factoid.lands.types.Type;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import me.tabinol.factoid.playercontainer.PlayerContainerType;
import me.tabinol.factoid.utilities.StringChanges;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class ApproveListSponge extends ApproveList {

    /** The approve file. */
    final private File approveFile;
    
    ConfigurationLoader<CommentedConfigurationNode> loader;
    
    /** The approve config. */
    private ConfigurationNode approveConfig;
    
    /**
     * Instantiates a new approve list.
     */
    public ApproveListSponge() {

        super();
    	approveFile = new File(Factoid.getServer().getDataFolder() + "/approvelist.conf");
        loadFile();
    }

    /**
     * Adds the approve.
     *
     * @param approve the approve
     */
    @Override
    public void addApprove(Approve approve) {

        landNames.add(approve.getLandName());
        ConfigurationNode section = approveConfig.getNode(approve.getLandName());
        if(approve.getType() != null) {
        	section.getNode("Type").setValue(approve.getType().getName());
        }
        section.getNode("Action").setValue(approve.getAction().toString());
        section.getNode("RemovedAreaId").setValue(approve.getRemovedAreaId());
        if (approve.getNewArea() != null) {
        	section.getNode("NewArea").setValue(approve.getNewArea().toString());
        }
        section.getNode("Owner").setValue(approve.getOwner().toString());
        if (approve.getParent() != null) {
            section.getNode("Parent").setValue(approve.getParent().getName());
        }
        section.getNode("Price").setValue(approve.getPrice());
        section.getNode("DateTime").setValue(approve.getDateTime().getTimeInMillis());
        saveFile();
        Factoid.getApproveNotif().notifyForApprove(approve.getLandName(), approve.getOwner().getPrint());
    }

    /**
     * Gets the approve.
     *
     * @param landName the land name
     * @return the approve
     */
    @Override
    public Approve getApprove(String landName) {

        Factoid.getFactoidLog().write("Get approve for: " + landName);
        ConfigurationNode section = approveConfig.getNode(landName);

        if (!section.hasListChildren()) {
            Factoid.getFactoidLog().write("Error Section null");
            return null;
        }
        
        String typeName = section.getNode("Type").getString();
        Type type = null;
        if(typeName != null) {
        	type = Factoid.getTypes().addOrGetType(typeName);
        }

        String[] ownerS = StringChanges.splitAddVoid(section.getNode("Owner").getString(), ":");
        PlayerContainer pc = PlayerContainer.create(null, PlayerContainerType.getFromString(ownerS[0]), ownerS[1]);
        Land parent = null;
        CuboidArea newArea = null;
        
        if (section.getNode("Parent").hasMapChildren()) {
            parent = Factoid.getLands().getLand(section.getNode("Parent").getString());
            
            // If the parent does not exist
            if (parent == null) {
                Factoid.getFactoidLog().write("Error, parent not found");
                return null;
            }
        }
        
        if(section.getNode("NewArea").hasMapChildren()) {
        	newArea = CuboidArea.getFromString(section.getNode("NewArea").getString());
        }
        
        LandAction action = LandAction.valueOf(section.getNode("Action").getString());
        
        // If the land was deleted
        if(action != LandAction.LAND_ADD && Factoid.getLands().getLand(landName) == null) {
        	return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(section.getNode("DateTime").getLong());

        return new Approve(landName, type, action,
                section.getNode("RemovedAreaId").getInt(), newArea, pc,
                parent, section.getNode("Price").getDouble(), cal);
    }

    /**
     * Removes the approve.
     *
     * @param approve the approve
     */
    @Override
    public void removeApprove(Approve approve) {
    
    	removeApprove(approve.getLandName());
    }

    /**
     * Removes the approve.
     *
     * @param landName the land name
     */
    @Override
    public void removeApprove(String landName) {
        
    	Factoid.getFactoidLog().write("Remove Approve from list: " + landName);

        approveConfig.removeChild(landName);
        landNames.remove(landName);
        saveFile();
    }

    /**
     * Removes the all.
     */
    @Override
    public void removeAll() {

        Factoid.getFactoidLog().write("Remove all Approves from list.");

        // Delete file
        if (approveFile.exists()) {
            approveFile.delete();
        }
        
        // Delete list
        landNames.clear();
        
        // Reload file
        loadFile();
    }

    /**
     * Load file.
     */
    private void loadFile() {

        Factoid.getFactoidLog().write("Loading Approve list file");

        if (!approveFile.exists()) {
            try {
                approveFile.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(ApproveList.class.getName()).log(Level.SEVERE, "Error on approve file creation", ex);
            }
        }
        try {
        	loader = HoconConfigurationLoader
    				.builder().setFile(approveFile).build();
        	approveConfig = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(ApproveList.class.getName()).log(Level.SEVERE, "Error on approve file load", ex);
        }

        // add land names to list
        for (Entry<Object, ? extends ConfigurationNode> landNamess : approveConfig.getChildrenMap().entrySet()) {
            landNames.add(landNamess.getKey().toString());
        }
    }

    /**
     * Save file.
     */
    private void saveFile() {

        Factoid.getFactoidLog().write("Saving Approve list file");

        try {
            loader.save(approveConfig);
        } catch (IOException ex) {
            Logger.getLogger(ApproveList.class.getName()).log(Level.SEVERE, "Error on approve file save", ex);
        }
    }
}
