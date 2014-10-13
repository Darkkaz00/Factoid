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
import me.tabinol.factoidapi.lands.IDummyLand;
import me.tabinol.factoidapi.lands.areas.ICuboidArea;
import me.tabinol.factoid.parameters.PermissionList;
import me.tabinol.factoid.selection.PlayerSelection.SelectionType;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;


/**
 * The Class AreaSelection.
 */
public class AreaSelection extends RegionSelection implements Listener {

    /** The area. */
    ICuboidArea area;
    
    /** The is collision. */
    boolean isCollision = false;
    
    /** The by. */
    private final byte by = 0;
    
    /** The block list. */
    private final Map<Location, Material> blockList = new HashMap<Location, Material>();
    
    /** The is from land. */
    private boolean isFromLand = false;

    /**
     * Instantiates a new area selection.
     *
     * @param player the player
     * @param area the area
     */
    public AreaSelection(Player player, ICuboidArea area) {

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
    public AreaSelection(Player player, ICuboidArea area, boolean isFromLand) {

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
    AreaSelection(Player player) {

        super(SelectionType.AREA, player);
    }

    /**
     * Make visual selection.
     */
    @SuppressWarnings("deprecation")
	final void makeVisualSelection() {

        // Get the size (x and z) no abs (already ajusted)
        int diffX = area.getX2() - area.getX1();
        int diffZ = area.getZ2() - area.getZ1();

        // Do not show a too big select to avoid crash or severe lag
        int maxSize = Factoid.getThisPlugin().iConf().getMaxVisualSelect();
        int maxDisPlayer = Factoid.getThisPlugin().iConf().getMaxVisualSelectFromPlayer();
        Location playerLoc = player.getLocation();
        if (diffX > maxSize || diffZ > maxSize
                || abs(area.getX1() - playerLoc.getBlockX()) > maxDisPlayer
                || abs(area.getX2() - playerLoc.getBlockX()) > maxDisPlayer
                || abs(area.getZ1() - playerLoc.getBlockZ()) > maxDisPlayer
                || abs(area.getZ2() - playerLoc.getBlockZ()) > maxDisPlayer) {
            Factoid.getThisPlugin().iLog().write("Selection disabled!");
            return;
        }
        
        // Detect the curent land from the first postion
        IDummyLand actualLand = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(new Location(
        		area.getWord(), area.getX1(), area.getY1(), area.getZ1()));
        boolean canCreate = actualLand.checkPermissionAndInherit(player, PermissionList.LAND_CREATE.getPermissionType()); 

        //MakeSquare
        for (int posX = area.getX1(); posX <= area.getX2(); posX++) {
            for (int posZ = area.getZ1(); posZ <= area.getZ2(); posZ++) {
                if (posX == area.getX1() || posX == area.getX2()
                        || posZ == area.getZ1() || posZ == area.getZ2()) {

                    Location newloc = new Location(area.getWord(), posX, this.getYNearPlayer(posX, posZ) - 1, posZ);
                    blockList.put(newloc, newloc.getBlock().getType());

                    if (!isFromLand) {

                        // Active Selection
                        IDummyLand testCuboidarea = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(newloc);
                        if (actualLand == testCuboidarea 
                        		&& (canCreate == true || Factoid.getThisPlugin().iPlayerConf().get(player).isAdminMod())) {
                            this.player.sendBlockChange(newloc, Material.SPONGE, this.by);
                        } else {
                            this.player.sendBlockChange(newloc, Material.REDSTONE_BLOCK, this.by);
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
                            this.player.sendBlockChange(newloc, Material.IRON_BLOCK, this.by);

                        } else if ((posX == area.getX1() && posZ == area.getZ1())
                                || (posX == area.getX2() && posZ == area.getZ1())
                                || (posX == area.getX1() && posZ == area.getZ2())
                                || (posX == area.getX2() && posZ == area.getZ2())) {

                            // Exact corner
                            this.player.sendBlockChange(newloc, Material.BEACON, this.by);
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
    @SuppressWarnings("deprecation")
	@Override
    public void removeSelection() {

        for (Map.Entry<Location, Material> EntrySet : this.blockList.entrySet()) {
            this.player.sendBlockChange(EntrySet.getKey(), EntrySet.getValue(), this.by);
        }

        blockList.clear();
    }

    /**
     * Gets the cuboid area.
     *
     * @return the cuboid area
     */
    public ICuboidArea getCuboidArea() {
        
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
    
     // Get the nearest block from player before air
    /**
      * Gets the y near player.
      *
      * @param x the x
      * @param z the z
      * @return the y near player
      */
     private int getYNearPlayer(int x, int z) {

        Location loc = new Location(player.getWorld(), x, player.getLocation().getY() - 1, z);

        if (loc.getBlock().getType() == Material.AIR) {
            while (loc.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR
                    && loc.getBlockY() > 1) {
                loc.subtract(0, 1, 0);
            }
        } else {
            while (loc.getBlock().getType() != Material.AIR && loc.getBlockY() < player.getWorld().getMaxHeight()) {
                loc.add(0, 1, 0);
            }
        }
        return loc.getBlockY();
    }
}
