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

package me.tabinol.factoid.config.bukkit;

import java.util.TreeSet;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.config.WorldConfig;
import me.tabinol.factoid.parameters.FlagType;
import me.tabinol.factoid.parameters.PermissionType;

public class ConfigBukkit extends Config {
	
    private final JavaPlugin plugin;
    
    /** The config. */
    private FileConfiguration config;
	
	/**
     * Instantiates a new config.
     */
    public ConfigBukkit(JavaPlugin plugin) {

        this.plugin = plugin;
        plugin.saveDefaultConfig();

        // Get Bukkit Config for this plugin, not this class!!!
        config = plugin.getConfig();

        reloadConfig();
    }

    /**
     * Reload config.
     */
    public void reloadConfig() {

        plugin.reloadConfig();
        config = plugin.getConfig();
        getConfig();
    }

    /**
     * Gets the config.
     *
     * @return the config
     */
    private void getConfig() {

        debug = config.getBoolean("general.debug", false);
        lang = config.getString("general.lang", "english");
        useEconomy = config.getBoolean("general.UseEconomy", false);
        infoItem = config.getString("general.InfoItem", "BONE");
        selectItem = config.getString("general.SelectItem", "ROTTEN_FLESH");
        // Remove error if the parameter is not here (AllowCollision)
        try {
            allowCollision = AllowCollisionType.valueOf(config.getString("land.AllowCollision", "approve").toUpperCase());
        } catch (NullPointerException ex) {
            allowCollision = AllowCollisionType.APPROVE;
        }
        isLandChat = config.getBoolean("land.LandChat", true);
        isSpectatorIsVanish = config.getBoolean("land.SpectatorIsVanish", true);
        approveNotifyTime = config.getLong("land.ApproveNotifyTime", 24002);
        selectAutoCancel = config.getLong("land.SelectAutoCancel", 12000);
        maxVisualSelect = config.getInt("land.MaxVisualSelect", 256);
        maxVisualSelectFromPlayer = config.getInt("land.MaxVisualSelectFromPlayer", 128);
        defaultXSize = config.getInt("land.defaultXSize", 10);
        defaultZSize = config.getInt("land.defaultZSize", 10);
        defaultBottom = config.getInt("land.defaultBottom", 0);
        defaultTop = config.getInt("land.defaultTop", 255);
        maxAreaPerLand = config.getInt("land.area.MaxAreaPerLand", 3);
        maxLandPerPlayer = config.getInt("land.MaxLandPerPlayer", 5);
        beaconLight = config.getBoolean("land.BeaconLight", false);
        overrideExplosions = config.getBoolean("general.OverrideExplosions", true);

        config.addDefault("land.OwnerCanSet.Flags", new String[] {"MESSAGE_JOIN", "MESSAGE_QUIT"});
        ownerConfigFlag = new TreeSet<FlagType>();
        for (String value : config.getStringList("land.OwnerCanSet.Flags")) {
            ownerConfigFlag.add(Factoid.getParameters().getFlagTypeNoValid(value.toUpperCase()));
        }
        config.addDefault("land.OwnerCanSet.Permissions", new String[] {"BUILD", "OPEN", "USE"});
        ownerConfigPerm = new TreeSet<PermissionType>();
        for (String value : config.getStringList("land.OwnerCanSet.Permissions")) {
            ownerConfigPerm.add(Factoid.getParameters().getPermissionTypeNoValid(value.toUpperCase()));
        }
        
        // Add types
        for(String typeName : config.getStringList("land.Types.List")) {
        	Factoid.getTypes().addOrGetType(typeName);
        }
        typeAdminMod = Factoid.getTypes().addOrGetType(getStringOrNull("land.Types.OnCreate.AdminMod", "admin"));
        typeNoneAdminMod = Factoid.getTypes().addOrGetType(getStringOrNull("land.Types.OnCreate.NoneAdminMod", "player"));
    }
    
    private String getStringOrNull(String path, String defaultSt) {
    	
    	String result = config.getString(path, defaultSt);
    	if(result.equalsIgnoreCase("-null-")) {
    		result = null;
    	}
    	
    	return result;
    }

	@Override
    public WorldConfig newWorldConfig() {

		return new WorldConfigBukkit(plugin);
    }
}
