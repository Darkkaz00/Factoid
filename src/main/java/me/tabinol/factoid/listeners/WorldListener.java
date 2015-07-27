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
package me.tabinol.factoid.listeners;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.lands.DummyLand;
import me.tabinol.factoid.lands.areas.Point;
import me.tabinol.factoid.minecraft.Item;
import me.tabinol.factoid.parameters.FlagList;


/**
 * World listener
 */
public class WorldListener extends CommonListener {

    /**
     * Instantiates a new world listener.
     */
    public WorldListener() {

        super();
    }

    public boolean onExplosionPrime(Point loc, String entityType) {

        DummyLand land = Factoid.getLands().getLandOrOutsideArea(loc);

        // Check for Explosion cancel 
        if ((entityType.equals("CREEPER")
                && land.getFlagAndInherit(FlagList.CREEPER_EXPLOSION.getFlagType()).getValueBoolean() == false)
                || (entityType.contains("TNT")
                && land.getFlagAndInherit(FlagList.TNT_EXPLOSION.getFlagType()).getValueBoolean() == false)
                || land.getFlagAndInherit(FlagList.EXPLOSION.getFlagType()).getValueBoolean() == false) {
            return true;

        }
    
        return false;
    }

    public boolean onHangingBreakExplosion(Point loc) {

        if (Factoid.getConf().isOverrideExplosions()) {
            // Check for painting
            Factoid.getFactoidLog().write("Cancel HangingBreak in : " + loc.toString());
            return true;
        }
        
        return false;
    }

    public boolean onEntityChangeBlock(Point loc, String entityType, Item fromType, Item toType) {

        DummyLand land = Factoid.getLands().getLandOrOutsideArea(loc);

        // Enderman removeblock
        if ((entityType.equals("ENDERMAN")
                && land.getFlagAndInherit(FlagList.ENDERMAN_DAMAGE.getFlagType()).getValueBoolean() == false)
                || (entityType.equals("WITHER")
                && land.getFlagAndInherit(FlagList.WITHER_DAMAGE.getFlagType()).getValueBoolean() == false)) {
            return true;
        
        // Crop trample
        } else if ((fromType.strEquals("SOIL") /* BUKKIT */ || fromType.equals("FARMLAND")) /* SPONGE */ 
				&& toType.strEquals("DIRT")
                && land.getFlagAndInherit(FlagList.CROP_TRAMPLE.getFlagType()).getValueBoolean() == false) {
        	return true;
        }
        
        return false;
    }

    public boolean onBlockIgniteNatural(Point loc) {

        DummyLand land = Factoid.getLands().getLandOrOutsideArea(loc);

        if (land.getFlagAndInherit(FlagList.FIRESPREAD.getFlagType()).getValueBoolean() == false
                || land.getFlagAndInherit(FlagList.FIRE.getFlagType()).getValueBoolean() == false) {
            return true;
        }
        
        return false;
    }

    public boolean onBlockBurn(Point loc) {

        DummyLand land = Factoid.getLands().getLandOrOutsideArea(loc);

        if ((land.getFlagAndInherit(FlagList.FIRESPREAD.getFlagType()).getValueBoolean() == false)
                || (land.getFlagAndInherit(FlagList.FIRE.getFlagType()).getValueBoolean() == false)) {
        return true;
        }
        
        return false;
    }

    public boolean onCreatureSpawn(Point loc, boolean isAnimal, boolean isMob) {

        DummyLand land = Factoid.getLands().getLandOrOutsideArea(loc);

        if ((isAnimal
                && land.getFlagAndInherit(FlagList.ANIMAL_SPAWN.getFlagType()).getValueBoolean() == false)
                || (isMob
                && land.getFlagAndInherit(FlagList.MOB_SPAWN.getFlagType()).getValueBoolean() == false)) {
            return true;
        }
        
        return false;
    }

    public boolean onLeavesDecay(Point loc) {

        DummyLand land = Factoid.getLands().getLandOrOutsideArea(loc);

        if (land.getFlagAndInherit(FlagList.LEAF_DECAY.getFlagType()).getValueBoolean() == false) {
            return true;
        }
        
        return false;
    }

    public boolean onBlockFromTo(Point loc, Item blockType) {

        DummyLand land = Factoid.getLands().getLandOrOutsideArea(loc);

        // Liquid flow
        if ((blockType.contains("LAVA")
        		&& land.getFlagAndInherit(FlagList.LAVA_FLOW.getFlagType()).getValueBoolean() == false)
        		|| (blockType.contains("WATER")
                		&& land.getFlagAndInherit(FlagList.WATER_FLOW.getFlagType()).getValueBoolean() == false)) {
            return true;
        }
        
        return false;
    }

    public boolean onEntityDamageHangingNonPlayer(Point loc) {

        if (Factoid.getConf().isOverrideExplosions()) {
            // Check for ItemFrame
            Factoid.getFactoidLog().write("Cancel HangingBreak : " + loc.toString());
            return true;
        }
        
        return false;
    }
}
