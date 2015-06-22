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
package me.tabinol.factoid.selection.region;

import static java.lang.Math.abs;

import java.util.HashMap;
import java.util.Map;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.lands.DummyLand;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.areas.CuboidArea;
import me.tabinol.factoid.lands.areas.Point;
import me.tabinol.factoid.minecraft.FPlayer;
import me.tabinol.factoid.parameters.PermissionList;
import me.tabinol.factoid.selection.PlayerSelection.SelectionType;


/**
 * The Class AreaSelection.
 */
public class AreaSelection extends RegionSelection {

    /** The area. */
    CuboidArea area;
    
    /** The is collision. */
    boolean isCollision = false;
    
    /** The by. */
    private final byte by = 0;
    
    /** The block list. */
    private final Map<Point, String> blockList = new HashMap<Point, String>();
    
    /** The is from land. */
    private boolean isFromLand = false;
    
    /** Parent detected */
    private DummyLand parentDetected = null;

    /**
     * Instantiates a new area selection.
     *
     * @param player the player
     * @param area the area
     */
    public AreaSelection(FPlayer player, CuboidArea area) {

        super(SelectionType.AREA, player);
        this.area = area;
        
        makeVisualSelection();
    }

    // Called from Land Selection list
    /**
     * Instantiates a new area selection.
     *
     * @param player the player
     * @param area the area
     * @param isFromLand the is from land
     */
    public AreaSelection(FPlayer player, CuboidArea area, boolean isFromLand) {

        super(SelectionType.AREA, player);
        this.area = area;
        this.isFromLand = isFromLand;
        
        makeVisualSelection();
    }

    // Called from ActiveAreaSelection
    /**
     * Instantiates a new area selection.
     *
     * @param player the player
     */
    AreaSelection(FPlayer player) {

        super(SelectionType.AREA, player);
    }

    /**
     * Make visual selection.
     */
	final void makeVisualSelection() {

        // Get the size (x and z) no abs (already ajusted)
        int diffX = area.getX2() - area.getX1();
        int diffZ = area.getZ2() - area.getZ1();

        // Do not show a too big select to avoid crash or severe lag
        int maxSize = Factoid.getConf().getMaxVisualSelect();
        int maxDisPlayer = Factoid.getConf().getMaxVisualSelectFromPlayer();
        Point playerLoc = player.getLocation();
        if (diffX > maxSize || diffZ > maxSize
                || abs(area.getX1() - playerLoc.getBlockX()) > maxDisPlayer
                || abs(area.getX2() - playerLoc.getBlockX()) > maxDisPlayer
                || abs(area.getZ1() - playerLoc.getBlockZ()) > maxDisPlayer
                || abs(area.getZ2() - playerLoc.getBlockZ()) > maxDisPlayer) {
            Factoid.getFactoidLog().write("Selection disabled!");
            return;
        }
        
        // Detect the curent land from the 8 points
        DummyLand Land1 = Factoid.getLands().getLandOrOutsideArea(new Point(
        		area.getWorldName(), area.getX1(), area.getY1(), area.getZ1()));
        DummyLand Land2 = Factoid.getLands().getLandOrOutsideArea(new Point(
        		area.getWorldName(), area.getX1(), area.getY1(), area.getZ2()));
        DummyLand Land3 = Factoid.getLands().getLandOrOutsideArea(new Point(
        		area.getWorldName(), area.getX2(), area.getY1(), area.getZ1()));
        DummyLand Land4 = Factoid.getLands().getLandOrOutsideArea(new Point(
        		area.getWorldName(), area.getX2(), area.getY1(), area.getZ2()));
        DummyLand Land5 = Factoid.getLands().getLandOrOutsideArea(new Point(
        		area.getWorldName(), area.getX1(), area.getY2(), area.getZ1()));
        DummyLand Land6 = Factoid.getLands().getLandOrOutsideArea(new Point(
        		area.getWorldName(), area.getX1(), area.getY2(), area.getZ2()));
        DummyLand Land7 = Factoid.getLands().getLandOrOutsideArea(new Point(
        		area.getWorldName(), area.getX2(), area.getY2(), area.getZ1()));
        DummyLand Land8 = Factoid.getLands().getLandOrOutsideArea(new Point(
        		area.getWorldName(), area.getX2(), area.getY2(), area.getZ2()));
        
        if(Land1 == Land2 && Land1 == Land3 && Land1 == Land4 && Land1 == Land5 && Land1 == Land6
        		&& Land1 == Land7 && Land1 == Land8) {
        	parentDetected = Land1;
        } else {
        	parentDetected = Factoid.getLands().getOutsideArea(Land1.getWorldName());
        }
        
        boolean canCreate = parentDetected.checkPermissionAndInherit(player, PermissionList.LAND_CREATE.getPermissionType());

        //MakeSquare
        for (int posX = area.getX1(); posX <= area.getX2(); posX++) {
            for (int posZ = area.getZ1(); posZ <= area.getZ2(); posZ++) {
                if (posX == area.getX1() || posX == area.getX2()
                        || posZ == area.getZ1() || posZ == area.getZ2()) {

                    Point newloc = new Point(area.getWorldName(), posX, this.getYNearPlayer(posX, posZ) - 1, posZ);
                    blockList.put(newloc, Factoid.getServer().getBlockTypeName(newloc));

                    if (!isFromLand) {

                        // Active Selection
                        DummyLand testCuboidarea = Factoid.getLands().getLandOrOutsideArea(newloc);
                        if (parentDetected == testCuboidarea 
                        		&& (canCreate == true || player.isAdminMod())) {
                            this.player.sendBlockChange(newloc, "SPONGE", this.by);
                        } else {
                            this.player.sendBlockChange(newloc, "REDSTONE_BLOCK", this.by);
                            isCollision = true;
                        }
                    } else {

                        // Passive Selection (created area)
                        if ((posX == area.getX1() && posZ == area.getZ1() + 1)
                                || (posX == area.getX1() && posZ == area.getZ2() - 1)
                                || (posX == area.getX2() && posZ == area.getZ1() + 1)
                                || (posX == area.getX2() && posZ == area.getZ2() - 1)
                                || (posX == area.getX1() + 1 && posZ == area.getZ1())
                                || (posX == area.getX2() - 1 && posZ == area.getZ1())
                                || (posX == area.getX1() + 1 && posZ == area.getZ2())
                                || (posX == area.getX2() - 1 && posZ == area.getZ2())) {

                            // Subcorner
                            this.player.sendBlockChange(newloc, "IRON_BLOCK", this.by);

                        } else if ((posX == area.getX1() && posZ == area.getZ1())
                                || (posX == area.getX2() && posZ == area.getZ1())
                                || (posX == area.getX1() && posZ == area.getZ2())
                                || (posX == area.getX2() && posZ == area.getZ2())) {

                            // Exact corner
                            this.player.sendBlockChange(newloc, "BEACON", this.by);
                        }
                    }

                } else {
                    // Square center, skip!
                    posZ = area.getZ2() - 1;
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.selection.region.RegionSelection#removeSelection()
     */
	@Override
    public void removeSelection() {

        for (Map.Entry<Point, String> EntrySet : this.blockList.entrySet()) {
            this.player.sendBlockChange(EntrySet.getKey(), EntrySet.getValue(), this.by);
        }

        blockList.clear();
    }

    /**
     * Gets the cuboid area.
     *
     * @return the cuboid area
     */
    public CuboidArea getCuboidArea() {
        
        return area;
    }
    
    /**
     * Gets the collision.
     *
     * @return the collision
     */
    public boolean getCollision() {
        
        return isCollision;
    }
    
    public Land getParentDetected() {
    	
    	if(parentDetected instanceof Land) {
    		return (Land) parentDetected;
    	} else {
    		return null;
    	}
    }
    
    /**
      * Gets the y near player before air.
      *
      * @param x the x
      * @param z the z
      * @return the y near player
      */
     private int getYNearPlayer(int x, int z) {

        Point loc = new Point(player.getLocation().getWorldName(), x, player.getLocation().getY() - 1, z);

        if (Factoid.getServer().getBlockTypeName(loc).equals("AIR")) {
            while (Factoid.getServer().getBlockTypeName(loc.add(0, -1, 0)).equals("AIR")
                    && loc.getBlockY() > 0);
        } else {
            while (!Factoid.getServer().getBlockTypeName(loc).equals("AIR") 
            		&& loc.getBlockY() < loc.getWorld().getMaxHeight()) {
                loc.add(0, 1, 0);
            }
        }
        return loc.getBlockY();
    }
}
