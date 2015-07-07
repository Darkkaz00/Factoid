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

import me.tabinol.factoid.lands.types.Type;
import me.tabinol.factoid.parameters.FlagType;
import me.tabinol.factoid.parameters.PermissionType;

/**
 * The Class Config.
 */
public abstract class Config {

    // Global
    /** The Constant NEWLINE. */
	public static final String NEWLINE = "\n";
    //public static final String NEWLINE = System.getProperty("line.separator");
    
    /** The Constant GLOBAL. */
    public static final String GLOBAL = "_Global_";
    
    // Configuration
    /** The debug. */
    protected boolean debug;
    
    /**
     * Checks if is debug.
     *
     * @return true, if is debug
     */
    public boolean isDebug() { return debug; }
    
    /** The lang. */
    protected String lang;
    
    /**
     * Gets the lang.
     *
     * @return the lang
     */
    public String getLang() { return lang; }
    
    /** The use economy. */
    protected boolean useEconomy;
    
    /**
     * Use economy.
     *
     * @return true, if successful
     */
    public boolean useEconomy() { return useEconomy; }
    
    /** The info item. */
    protected String infoItem;
    
    /**
     * Gets the info item.
     *
     * @return the info item
     */
    public String getInfoItem() { return infoItem; }
    
    /** The select item. */
    protected String selectItem;
    
    /**
     * Gets the select item.
     *
     * @return the select item
     */
    public String getSelectItem() { return selectItem; }
    
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
    protected AllowCollisionType allowCollision;
    
    /**
     * Gets the allow collision.
     *
     * @return the allow collision
     */
    public AllowCollisionType getAllowCollision() { return allowCollision; }
    
    /** The land chat. */
    protected boolean isLandChat;
    
    /**
     * Gets if land chat is activated.
     *
     * @return the land chat
     */
    public boolean isLandChat() { return isLandChat; }

    /** The is spectator is vanish. */
    protected boolean isSpectatorIsVanish;
    
    /**
     * Checks if is spectator is vanish.
     *
     * @return true, if is spectator is vanish
     */
    public boolean isSpectatorIsVanish() { return isSpectatorIsVanish; }
    
    /** The approve notify time. */
    protected long approveNotifyTime;
    
    /**
     * Gets the approve notify time.
     *
     * @return the approve notify time
     */
    public long getApproveNotifyTime() { return approveNotifyTime; }
    
    /** The select auto cancel. */
    protected long selectAutoCancel;
    
    /**
     * Gets the select auto cancel.
     *
     * @return the select auto cancel
     */
    public long getSelectAutoCancel() { return selectAutoCancel; }
    
    /** The max visual select. */
    protected int maxVisualSelect;
    
    /**
     * Gets the max visual select.
     *
     * @return the max visual select
     */
    public int getMaxVisualSelect() { return maxVisualSelect; }
    
    /** The max visual select from player. */
    protected int maxVisualSelectFromPlayer;
    
    /**
     * Gets the max visual select from player.
     *
     * @return the max visual select from player
     */
    public int getMaxVisualSelectFromPlayer() { return maxVisualSelectFromPlayer; }

    /** The max area per land. */
    protected int maxAreaPerLand;
    
    /**
     * Gets the max area per land.
     *
     * @return the max area per land
     */
    public int getMaxAreaPerLand() { return maxAreaPerLand; }
    
    /** The max land per player. */
    protected int maxLandPerPlayer;
    
    /**
     * Gets the max land per player.
     *
     * @return the max land per player
     */
    public int getMaxLandPerPlayer() { return maxLandPerPlayer; }
    
    /** The default x size. */
    protected int defaultXSize;
    
    /**
     * Gets the default x size.
     *
     * @return the default x size
     */
    public int getDefaultXSize() { return defaultXSize; }
  
    /** The default z size. */
    protected int defaultZSize;
    
    /**
     * Gets the default z size.
     *
     * @return the default z size
     */
    public int getDefaultZSize() { return defaultZSize; }
  
    /** The default bottom. */
    protected int defaultBottom;
    
    /**
     * Gets the default bottom.
     *
     * @return the default bottom
     */
    public int getDefaultBottom() { return defaultBottom; }
  
    /** The default top. */
    protected int defaultTop;
    
    /**
     * Gets the default top.
     *
     * @return the default top
     */
    public int getDefaultTop() { return defaultTop; }

    
    /** The beacon light. */
    protected boolean beaconLight;
    
    /**
     * Checks if is beacon light.
     *
     * @return true, if is beacon light
     */
    public boolean isBeaconLight() { return beaconLight; }
    
    /** The override explosions. */
    protected boolean overrideExplosions;
    
    /**
     * Checks if is override explosions.
     *
     * @return true, if is override explosions
     */
    public boolean isOverrideExplosions() { return overrideExplosions; }
    
    /** The owner config flag. */
    protected TreeSet<FlagType> ownerConfigFlag; // Flags a owner can set
    
    /**
     * Gets the owner config flag.
     *
     * @return the owner config flag
     */
    public TreeSet<FlagType> getOwnerConfigFlag() { return ownerConfigFlag; }
    
    /** The owner config perm. */
    protected TreeSet<PermissionType> ownerConfigPerm; // Permissions a owner can set
    
    /**
     * Gets the owner config perm.
     *
     * @return the owner config perm
     */
    public TreeSet<PermissionType> getOwnerConfigPerm() { return ownerConfigPerm; }

    /** The type admin mod. */
    protected Type typeAdminMod;
    
    /**
     * Gets the type admin mod.
     *
     * @return the type admin mod
     */
    public Type getTypeAdminMod() { return typeAdminMod; } 
    
    /** The type none admin mod. */
    protected Type typeNoneAdminMod;
    
    /**
     * Gets the type none admin mod.
     *
     * @return the type none admin mod
     */
    public Type getTypeNoneAdminMod() { return typeNoneAdminMod; }
    
    /**
     * Load or reload the general configuration
     */
    public abstract void reloadConfig();
    
    /**
     * Create a new world config for Lands
     * @return WorldConfig
     */
    public abstract WorldConfig newWorldConfig();
}
