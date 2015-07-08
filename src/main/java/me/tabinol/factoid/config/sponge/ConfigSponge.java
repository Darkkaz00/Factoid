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

package me.tabinol.factoid.config.sponge;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.TreeSet;

import com.google.common.base.Function;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.config.WorldConfig;
import me.tabinol.factoid.parameters.FlagType;
import me.tabinol.factoid.parameters.PermissionType;
import me.tabinol.factoid.utilities.FileCopy;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class ConfigSponge extends Config {
	
	public static final String FILE_CONFIG_SRC = "/config.conf";
	
	private File defaultConfig;
    private File configDir;

	ConfigurationNode config;

	/**
     * Instantiates a new config.
     */
    public ConfigSponge(File defaultConfig, File configDir) {
    	
    	this.defaultConfig = defaultConfig;
    	this.configDir = configDir;
    	
    	try {
    		if (!defaultConfig.exists()) {
    			// Copy file
    			InputStream source = Factoid.getServer().getResource(FILE_CONFIG_SRC);
    			FileCopy.copyTextFromJav(source, defaultConfig);
    		}
   		} catch (IOException e) {
    		e.printStackTrace();
   		}

   		reloadConfig();
    }

    /**
     * Reload config.
     */
    public void reloadConfig() {

    	try {
    		ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader
    				.builder().setFile(defaultConfig).build();
	        config = loader.load();
	        getConfig();
        } catch (IOException e) {
	        e.printStackTrace();
        }
    }

    /**
     * Gets the config.
     *
     * @return the config
     */
    private void getConfig() {

    	// Create the String transformer for String list (source : Sponge Forum)
    	Function<Object,String> stringTransformer = new Function<Object,String>() {
    	    @Override
    	    public String apply(Object input) {
    	        if (input instanceof String) {
    	            return (String) input;
    	        } else {
    	            return null;
    	        }
    	    }
    	};
    	
    	debug = config.getNode("general", "debug").getBoolean(false);
        lang = config.getNode("general", "lang").getString("english");
        useEconomy = config.getNode("general", "UseEconomy").getBoolean(false);
        infoItem = config.getNode("general", "InfoItem").getString("BONE");
        selectItem = config.getNode("general", "SelectItem").getString("ROTTEN_FLESH");
        // Remove error if the parameter is not here (AllowCollision)
        try {
            allowCollision = AllowCollisionType.valueOf(config.getNode("land","AllowCollision").getString("approve").toUpperCase());
        } catch (NullPointerException ex) {
            allowCollision = AllowCollisionType.APPROVE;
        }
        isLandChat = config.getNode("land", "LandChat").getBoolean(true);
        isSpectatorIsVanish = config.getNode("land", "SpectatorIsVanish").getBoolean(true);
        approveNotifyTime = config.getNode("land", "ApproveNotifyTime").getLong(24002);
        selectAutoCancel = config.getNode("land", "SelectAutoCancel").getLong(12000);
        maxVisualSelect = config.getNode("land.MaxVisualSelect").getInt(256);
        maxVisualSelectFromPlayer = config.getNode("land", "MaxVisualSelectFromPlayer").getInt(128);
        defaultXSize = config.getNode("land", "defaultXSize").getInt(10);
        defaultZSize = config.getNode("land", "defaultZSize").getInt(10);
        defaultBottom = config.getNode("land", "defaultBottom").getInt(0);
        defaultTop = config.getNode("land", "defaultTop").getInt(255);
        maxAreaPerLand = config.getNode("land.area", "MaxAreaPerLand").getInt(3);
        maxLandPerPlayer = config.getNode("land", "MaxLandPerPlayer").getInt(5);
        beaconLight = config.getNode("land", "BeaconLight").getBoolean(false);
        overrideExplosions = config.getNode("general", "OverrideExplosions").getBoolean(true);

        ownerConfigFlag = new TreeSet<FlagType>();
        for (String value : config.getNode("land", "OwnerCanSet", "Flags").getList(stringTransformer, 
        		Arrays.asList(new String[] {"MESSAGE_JOIN", "MESSAGE_QUIT"}))) {
            ownerConfigFlag.add(Factoid.getParameters().getFlagTypeNoValid(value.toUpperCase()));
        }

        ownerConfigPerm = new TreeSet<PermissionType>();
        for (String value : config.getNode("land", "OwnerCanSet", "Permissions").getList(stringTransformer, 
        		Arrays.asList(new String[] {"BUILD", "OPEN", "USE"}))) {
            ownerConfigPerm.add(Factoid.getParameters().getPermissionTypeNoValid(value.toUpperCase()));
        }
        
        // Add types
        for(String typeName : config.getNode("land", "Types", "List").getList(stringTransformer)) {
        	Factoid.getTypes().addOrGetType(typeName);
        }
        typeAdminMod = Factoid.getTypes().addOrGetType(getStringOrNull("admin", 
        		"land", "Types", "OnCreate", "AdminMod"));
        typeNoneAdminMod = Factoid.getTypes().addOrGetType(getStringOrNull("player", 
        		"land", "Types", "OnCreate", "NoneAdminMod"));
    }
    
    private String getStringOrNull(String defaultSt, String... path) {
    	
    	String result = config.getNode((Object[]) path).getString(defaultSt);
    	if(result.equalsIgnoreCase("-null-")) {
    		result = null;
    	}
    	
    	return result;
    }

	@Override
    public WorldConfig newWorldConfig() {

		return new WorldConfigSponge(configDir);
    }
}
