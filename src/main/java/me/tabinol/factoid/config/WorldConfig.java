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
package me.tabinol.factoid.config;

import static me.tabinol.factoid.config.Config.GLOBAL;

import java.io.File;
import java.util.Set;
import java.util.TreeMap;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.lands.DummyLand;
import me.tabinol.factoid.parameters.FlagType;
import me.tabinol.factoid.parameters.FlagValue;
import me.tabinol.factoid.parameters.LandFlag;
import me.tabinol.factoid.parameters.Permission;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import me.tabinol.factoidapi.FactoidAPI;
import me.tabinol.factoidapi.lands.types.IType;
import me.tabinol.factoidapi.playercontainer.EPlayerContainerType;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


// Started by Lands.Class
// Load world config and lands default
/**
 * The Class WorldConfig.
 */
public class WorldConfig {

    /** The this plugin. */
    private final Factoid thisPlugin;
    
    /** The land default. */
    private final FileConfiguration landDefault;
    
    /** The world config. */
    private final FileConfiguration worldConfig;

    /** Default config (No Type or global) */
    private DummyLand defaultConfNoType;

    /**
     * Instantiates a new world config.
     */
    public WorldConfig() {

        thisPlugin = Factoid.getThisPlugin();

        // Create files (if not exist) and load
        if (!new File(thisPlugin.getDataFolder(), "landdefault.yml").exists()) {
            thisPlugin.saveResource("landdefault.yml", false);
        }
        if (!new File(thisPlugin.getDataFolder(), "worldconfig.yml").exists()) {
            thisPlugin.saveResource("worldconfig.yml", false);
        }
        landDefault = YamlConfiguration.loadConfiguration(new File(thisPlugin.getDataFolder(), "landdefault.yml"));
        worldConfig = YamlConfiguration.loadConfiguration(new File(thisPlugin.getDataFolder(), "worldconfig.yml"));
        
        // Create default (whitout type)
        defaultConfNoType = getLandDefaultConf();
    }
    
    /**
     * Gets the land outside area.
     *
     * @return the land outside area
     */
    public TreeMap<String, DummyLand> getLandOutsideArea() {

        TreeMap<String, DummyLand> landList = new TreeMap<String, DummyLand>();
        Set<String> keys = worldConfig.getConfigurationSection("").getKeys(false);
        
        // We have to take _global_ first then others
        for (String worldName : keys) {
        	if(worldName.equalsIgnoreCase(GLOBAL)) {
            	createConfForWorld(worldName, landList, false);
        	}
        }
        
        // The none-global
        for (String worldName : keys) {
        	if(!worldName.equalsIgnoreCase(GLOBAL)) {
            	createConfForWorld(worldName, landList, true);
        	}
        }

        return landList;
    }
    
    private void createConfForWorld(String worldName, TreeMap<String, DummyLand> landList, boolean copyFromGlobal) {
    	
        String worldNameLower = worldName.toLowerCase();
        Factoid.getThisPlugin().iLog().write("Create conf for World: " + worldNameLower);
        DummyLand dl = new DummyLand(worldName);
        if(copyFromGlobal) {
        	landList.get(GLOBAL).copyPermsFlagsTo(dl);
        }
        landList.put(worldNameLower, landModify(dl, worldConfig, 
        		worldName + ".ContainerPermissions", worldName + ".ContainerFlags"));
    }
    
    /**
     * Gets the land default conf.
     *
     * @return the land default conf
     */
    private DummyLand getLandDefaultConf() {

        Factoid.getThisPlugin().iLog().write("Create default conf for lands");
        return landModify(new DummyLand(GLOBAL), landDefault, "ContainerPermissions", "ContainerFlags");
    }

    /**
     * Get the default configuration of a land without a Type.
     * @return The land configuration (DummyLand)
     */
    public DummyLand getDefaultconfNoType() {
    	
    	return defaultConfNoType;
    }

    /**
     * Gets the default conf for each type
     * @return a TreeMap of default configuration
     */
    public TreeMap<IType, DummyLand> getTypeDefaultConf() {
    	
        Factoid.getThisPlugin().iLog().write("Create default conf for lands");
    	TreeMap<IType, DummyLand> defaultConf = new TreeMap<IType, DummyLand>();

    	for(IType type : FactoidAPI.iTypes().getTypes()) {
    		ConfigurationSection typeConf = landDefault.getConfigurationSection(type.getName());
    		DummyLand dl = new DummyLand(type.getName());
            defaultConfNoType.copyPermsFlagsTo(dl);
    		defaultConf.put(type, landModify(dl, typeConf, 
    				"ContainerPermissions", "ContainerFlags"));
    	}
    	
    	return defaultConf;
    }

    private DummyLand landModify(DummyLand dl, ConfigurationSection fc, String perms, String flags) {

        ConfigurationSection csPerm = null;
        ConfigurationSection csFlags = null;
        
        if(fc != null) {
            csPerm = fc.getConfigurationSection(perms);
            csFlags = fc.getConfigurationSection(flags);
        }

        // Add permissions
        if (csPerm != null) {
            for (String container : csPerm.getKeys(false)) {
                
                EPlayerContainerType pcType = EPlayerContainerType.getFromString(container);
                
                if (pcType.hasParameter()) {
                    for (String containerName : fc.getConfigurationSection(perms + "." + container).getKeys(false)) {
                        for (String perm : fc.getConfigurationSection(perms + "." + container + "." + containerName).getKeys(false)) {
                            Factoid.getThisPlugin().iLog().write("Container: " + container + ":" + containerName + ", " + perm);
                            
                            // Remove _ if it is a Bukkit Permission
                            String containerNameLower;
                            if(pcType == EPlayerContainerType.PERMISSION) {
                                containerNameLower = containerName.toLowerCase().replaceAll("_", ".");
                            } else {
                                containerNameLower = containerName.toLowerCase();
                            }
                            
                            dl.addPermission(
                                    PlayerContainer.create(null, pcType, containerNameLower),
                                    new Permission(Factoid.getThisPlugin().iParameters().getPermissionTypeNoValid(perm.toUpperCase()),
                                            fc.getBoolean(perms + "." + container + "." + containerName + "." + perm + ".Value"),
                                            fc.getBoolean(perms + "." + container + "." + containerName + "." + perm + ".Heritable")));
                        }
                    }
                } else {
                    for (String perm : fc.getConfigurationSection(perms + "." + container).getKeys(false)) {
                        Factoid.getThisPlugin().iLog().write("Container: " + container + ", " + perm);
                        dl.addPermission(
                                PlayerContainer.create(null, pcType, null),
                                new Permission(Factoid.getThisPlugin().iParameters().getPermissionTypeNoValid(perm.toUpperCase()),
                                        fc.getBoolean(perms + "." + container + "." + perm + ".Value"),
                                        fc.getBoolean(perms + "." + container + "." + perm + ".Heritable")));
                    }
                }
            }
        }

        // add flags
        if (csFlags != null) {
            for (String flag : csFlags.getKeys(false)) {
                Factoid.getThisPlugin().iLog().write("Flag: " + flag);
                FlagType ft = Factoid.getThisPlugin().iParameters().getFlagTypeNoValid(flag.toUpperCase());
                dl.addFlag(new LandFlag(ft,
                        FlagValue.getFromString(fc.getString(flags + "." + flag + ".Value"), ft), 
                        fc.getBoolean(flags + "." + flag + ".Heritable")));
            }
        }
        
        return dl;
    }
}
