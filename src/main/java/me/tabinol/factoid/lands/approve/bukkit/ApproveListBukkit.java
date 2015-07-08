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

package me.tabinol.factoid.lands.approve.bukkit;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

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

public class ApproveListBukkit extends ApproveList {

    /** The approve file. */
    final private File approveFile;
    
    /** The approve config. */
    private FileConfiguration approveConfig;
    
    /**
     * Instantiates a new approve list.
     */
    public ApproveListBukkit() {

        super();
    	approveFile = new File(Factoid.getServer().getDataFolder() + "/approvelist.yml");
        approveConfig = new YamlConfiguration();
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
        ConfigurationSection section = approveConfig.createSection(approve.getLandName());
        if(approve.getType() != null) {
        	section.set("Type", approve.getType().getName());
        }
        section.set("Action", approve.getAction().toString());
        section.set("RemovedAreaId", approve.getRemovedAreaId());
        if (approve.getNewArea() != null) {
        	section.set("NewArea", approve.getNewArea().toString());
        }
        section.set("Owner", approve.getOwner().toString());
        if (approve.getParent() != null) {
            section.set("Parent", approve.getParent().getName());
        }
        section.set("Price", approve.getPrice());
        section.set("DateTime", approve.getDateTime().getTimeInMillis());
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
        ConfigurationSection section = approveConfig.getConfigurationSection(landName);

        if (section == null) {
            Factoid.getFactoidLog().write("Error Section null");
            return null;
        }
        
        String typeName = section.getString("Type");
        Type type = null;
        if(typeName != null) {
        	type = Factoid.getTypes().addOrGetType(typeName);
        }

        String[] ownerS = StringChanges.splitAddVoid(section.getString("Owner"), ":");
        PlayerContainer pc = PlayerContainer.create(null, PlayerContainerType.getFromString(ownerS[0]), ownerS[1]);
        Land parent = null;
        CuboidArea newArea = null;
        
        if (section.contains("Parent")) {
            parent = Factoid.getLands().getLand(section.getString("Parent"));
            
            // If the parent does not exist
            if (parent == null) {
                Factoid.getFactoidLog().write("Error, parent not found");
                return null;
            }
        }
        
        if(section.contains("NewArea")) {
        	newArea = CuboidArea.getFromString(section.getString("NewArea"));
        }
        
        LandAction action = LandAction.valueOf(section.getString("Action"));
        
        // If the land was deleted
        if(action != LandAction.LAND_ADD && Factoid.getLands().getLand(landName) == null) {
        	return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(section.getLong("DateTime"));

        return new Approve(landName, type, action,
                section.getInt("RemovedAreaId"), newArea, pc,
                parent, section.getDouble("Price"), cal);
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

        approveConfig.set(landName, null);
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
        approveConfig = new YamlConfiguration();
        
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
            approveConfig.load(approveFile);
        } catch (IOException ex) {
            Logger.getLogger(ApproveList.class.getName()).log(Level.SEVERE, "Error on approve file load", ex);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(ApproveList.class.getName()).log(Level.SEVERE, "Error on approve file load", ex);
        }

        // add land names to list
        for (String landName : approveConfig.getKeys(false)) {
            landNames.add(landName);
        }
    }

    /**
     * Save file.
     */
    private void saveFile() {

        Factoid.getFactoidLog().write("Saving Approve list file");

        try {
            approveConfig.save(approveFile);
        } catch (IOException ex) {
            Logger.getLogger(ApproveList.class.getName()).log(Level.SEVERE, "Error on approve file save", ex);
        }
    }
}
