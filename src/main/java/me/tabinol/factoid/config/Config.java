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

import java.util.TreeSet;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.parameters.FlagType;
import me.tabinol.factoid.parameters.PermissionType;
import org.bukkit.configuration.file.FileConfiguration;


/**
 * The Class Config.
 */
public class Config {

    // Global
    /** The Constant NEWLINE. */
	public static final String NEWLINE = "\n";
    //public static final String NEWLINE = System.getProperty("line.separator");
    
    /** The Constant GLOBAL. */
    public static final String GLOBAL = "_global_";
    
    /** The this plugin. */
    private final Factoid thisPlugin;
    
    /** The config. */
    private FileConfiguration config;
    // Configuration
    /** The debug. */
    private boolean debug;
    
    /**
     * Checks if is debug.
     *
     * @return true, if is debug
     */
    public boolean isDebug() { return debug; }
    
    /** The lang. */
    private String lang;
    
    /**
     * Gets the lang.
     *
     * @return the lang
     */
    public String getLang() { return lang; }
    
    /** The use economy. */
    private boolean useEconomy;
    
    /**
     * Use economy.
     *
     * @return true, if successful
     */
    public boolean useEconomy() { return useEconomy; }
    
    /** The info item. */
    private int infoItem;
    
    /**
     * Gets the info item.
     *
     * @return the info item
     */
    public int getInfoItem() { return infoItem; }
    
    /** The select item. */
    private int selectItem;
    
    /**
     * Gets the select item.
     *
     * @return the select item
     */
    public int getSelectItem() { return selectItem; }
    
    /**
     * The Enum AllowCollisionType.
     */
    public enum AllowCollisionType {

        /** The true. */
        TRUE,
        
        /** The approve. */
        APPROVE,
        
        /** The false. */
        FALSE;
    }
    
    /** The allow collision. */
    private AllowCollisionType allowCollision;
    
    /**
     * Gets the allow collision.
     *
     * @return the allow collision
     */
    public AllowCollisionType getAllowCollision() { return allowCollision; }
    
    /** The land chat. */
    private boolean isLandChat;
    
    /**
     * Gets if land chat is activated.
     *
     * @return the land chat
     */
    public boolean isLandChat() { return isLandChat; }

    /** The approve notify time. */
    private long approveNotifyTime;
    
    /**
     * Gets the approve notify time.
     *
     * @return the approve notify time
     */
    public long getApproveNotifyTime() { return approveNotifyTime; }
    
    /** The select auto cancel. */
    private long selectAutoCancel;
    
    /**
     * Gets the select auto cancel.
     *
     * @return the select auto cancel
     */
    public long getSelectAutoCancel() { return selectAutoCancel; }
    
    /** The max visual select. */
    private int maxVisualSelect;
    
    /**
     * Gets the max visual select.
     *
     * @return the max visual select
     */
    public int getMaxVisualSelect() { return maxVisualSelect; }
    
    /** The max visual select from player. */
    private int maxVisualSelectFromPlayer;
    
    /**
     * Gets the max visual select from player.
     *
     * @return the max visual select from player
     */
    public int getMaxVisualSelectFromPlayer() { return maxVisualSelectFromPlayer; }

    /** The max land. */
    private int maxLand;
    
    /**
     * Gets the max land.
     *
     * @return the max land
     */
    public int getMaxLand() { return maxLand; } 
    
    /** The min land size. */
    private int minLandSize;
    
    /**
     * Gets the min land size.
     *
     * @return the min land size
     */
    public int getMinLandSize() { return minLandSize; }
    
    /** The max land size. */
    private int maxLandSize;
    
    /**
     * Gets the max land size.
     *
     * @return the max land size
     */
    public int getMaxLandSize() { return maxLandSize; }
    
    /** The max area per land. */
    private int maxAreaPerLand;
    
    /**
     * Gets the max area per land.
     *
     * @return the max area per land
     */
    public int getMaxAreaPerLand() { return maxAreaPerLand; }
    
    /** The max land per player. */
    private int maxLandPerPlayer;
    
    /**
     * Gets the max land per player.
     *
     * @return the max land per player
     */
    public int getMaxLandPerPlayer() { return maxLandPerPlayer; }
    
    /** The min area size. */
    private int minAreaSize;
    
    /**
     * Gets the min area size.
     *
     * @return the min area size
     */
    public int getMinAreaSize() { return minAreaSize; }
    
    /** The max area size. */
    private int maxAreaSize;
    
    /**
     * Gets the max area size.
     *
     * @return the max area size
     */
    public int getMaxAreaSize() { return maxAreaSize; }
    
    /** The min land high. */
    private int minLandHigh;
    
    /**
     * Gets the min land high.
     *
     * @return the min land high
     */
    public int getMinLandHigh() { return minLandHigh; }
    
    /** The max land high. */
    private int maxLandHigh;
    
    /**
     * Gets the max land high.
     *
     * @return the max land high
     */
    public int getMaxLandHigh() { return maxLandHigh; }
    
    /** The default x size. */
    private int defaultXSize;
    
    /**
     * Gets the default x size.
     *
     * @return the default x size
     */
    public int getDefaultXSize() { return defaultXSize; }
  
    /** The default z size. */
    private int defaultZSize;
    
    /**
     * Gets the default z size.
     *
     * @return the default z size
     */
    public int getDefaultZSize() { return defaultZSize; }
  
    /** The default bottom. */
    private int defaultBottom;
    
    /**
     * Gets the default bottom.
     *
     * @return the default bottom
     */
    public int getDefaultBottom() { return defaultBottom; }
  
    /** The default top. */
    private int defaultTop;
    
    /**
     * Gets the default top.
     *
     * @return the default top
     */
    public int getDefaultTop() { return defaultTop; }

    /** The price by cube. */
    private double priceByCube;
    
    /**
     * Gets the price by cube.
     *
     * @return the price by cube
     */
    public double getPriceByCube() { return priceByCube; }
    
    /** The min price location. */
    private double minPriceLocation;
    
    /**
     * Gets the min price location.
     *
     * @return the min price location
     */
    public double getMinPriceLocation() { return minPriceLocation; }
    
    /** The max price location. */
    private double maxPriceLocation;
    
    /**
     * Gets the max price location.
     *
     * @return the max price location
     */
    public double getMaxPriceLocation() { return maxPriceLocation; }
    
    /** The min price sell. */
    private double minPriceSell;
    
    /**
     * Gets the min price sell.
     *
     * @return the min price sell
     */
    public double getMinPriceSell() { return minPriceSell; }
    
    /** The max price sell. */
    private double maxPriceSell;
    
    /**
     * Max price sell.
     *
     * @return the double
     */
    public double maxPriceSell() { return maxPriceSell; }
    
    /** The beacon light. */
    private boolean beaconLight;
    
    /**
     * Checks if is beacon light.
     *
     * @return true, if is beacon light
     */
    public boolean isBeaconLight() { return beaconLight; }
    
    /** The override explosions. */
    private boolean overrideExplosions;
    
    /**
     * Checks if is override explosions.
     *
     * @return true, if is override explosions
     */
    public boolean isOverrideExplosions() { return overrideExplosions; }
    
    /** The owner config flag. */
    private TreeSet<FlagType> ownerConfigFlag; // Flags a owner can set
    
    /**
     * Gets the owner config flag.
     *
     * @return the owner config flag
     */
    public TreeSet<FlagType> getOwnerConfigFlag() { return ownerConfigFlag; }
    
    /** The owner config perm. */
    private TreeSet<PermissionType> ownerConfigPerm; // Permissions a owner can set
    
    /**
     * Gets the owner config perm.
     *
     * @return the owner config perm
     */
    public TreeSet<PermissionType> getOwnerConfigPerm() { return ownerConfigPerm; }

    /**
     * Instantiates a new config.
     */
    public Config() {

        thisPlugin = Factoid.getThisPlugin();
        thisPlugin.saveDefaultConfig();

        // Get Bukkit Config for this plugin, not this class!!!
        config = thisPlugin.getConfig();

        reloadConfig();
    }

    /**
     * Reload config.
     */
    public final void reloadConfig() {

        thisPlugin.reloadConfig();
        config = thisPlugin.getConfig();
        getConfig();
    }

    /**
     * Gets the config.
     *
     * @return the config
     */
    private void getConfig() {

        debug = config.getBoolean("general.debug", false);
        config.addDefault("general.worlds", new String[] {"world", "world_nether", "world_the_end"});
        lang = config.getString("general.lang", "english");
        useEconomy = config.getBoolean("general.UseEconomy", false);
        infoItem = config.getInt("general.InfoItem", 352);
        selectItem = config.getInt("general.SelectItem", 367);
        // Remove error if the parameter is not here (AllowCollision)
        try {
            allowCollision = AllowCollisionType.valueOf(config.getString("land.AllowCollision", "approve").toUpperCase());
        } catch (NullPointerException ex) {
            allowCollision = AllowCollisionType.APPROVE;
        }
        isLandChat = config.getBoolean("land.LandChat", true);
        approveNotifyTime = config.getLong("land.ApproveNotifyTime", 24002);
        selectAutoCancel = config.getLong("land.SelectAutoCancel", 12000);
        maxVisualSelect = config.getInt("land.MaxVisualSelect", 256);
        maxVisualSelectFromPlayer = config.getInt("land.MaxVisualSelectFromPlayer", 128);
        maxLand = config.getInt("land.MaxLand", 1);
        minLandSize = config.getInt("land.MinLandSize", 1);
        maxLandSize = config.getInt("land.MaxLandSize", 1);
        defaultXSize = config.getInt("land.defaultXSize", 10);
        defaultZSize = config.getInt("land.defaultZSize", 10);
        defaultBottom = config.getInt("land.defaultBottom", 0);
        defaultTop = config.getInt("land.defaultTop", 255);
        maxAreaPerLand = config.getInt("land.area.MaxAreaPerLand", 3);
        maxLandPerPlayer = config.getInt("land.MaxLandPerPlayer", 5);
        minAreaSize = config.getInt("land.area.MinAreaSize", 1);
        maxAreaSize = config.getInt("land.area.MaxAreaSize", 1);
        minLandHigh = config.getInt("land.area.MinLandHigh", 0);
        maxLandHigh = config.getInt("land.area.MaxLandHigh", 256);
        beaconLight = config.getBoolean("land.BeaconLight", false);
        overrideExplosions = config.getBoolean("general.OverrideExplosions", true);

        if (useEconomy) {
            priceByCube = config.getDouble("economy.PricebyCube", 0.01);
            minPriceSell = config.getDouble("economy.MinPriceSell", 1);
            maxPriceSell = config.getDouble("economy.MaxPriceSell", 1);
            minPriceLocation = config.getDouble("economy.MinPriceLocation", 1);
            maxPriceLocation = config.getDouble("economy.MaxPriceLocation", 1);
        }

        config.addDefault("land.OwnerCanSet.Flags", new String[] {"MESSAGE_JOIN", "MESSAGE_QUIT"});
        ownerConfigFlag = new TreeSet<FlagType>();
        for (String value : config.getStringList("land.OwnerCanSet.Flags")) {
            ownerConfigFlag.add(Factoid.getThisPlugin().iParameters().getFlagTypeNoValid(value.toUpperCase()));
        }
        config.addDefault("land.OwnerCanSet.Permissions", new String[] {"BUILD", "OPEN", "USE"});
        ownerConfigPerm = new TreeSet<PermissionType>();
        for (String value : config.getStringList("land.OwnerCanSet.Permissions")) {
            ownerConfigPerm.add(Factoid.getThisPlugin().iParameters().getPermissionTypeNoValid(value.toUpperCase()));
        }
    }
}
