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

package me.tabinol.factoid.minecraft.sponge;

import java.util.UUID;

import me.tabinol.factoid.lands.areas.Point;
import me.tabinol.factoid.minecraft.FWorld;

import org.spongepowered.api.world.World;

public class FWorldSponge implements FWorld {
	
	private final World world;
	
	protected FWorldSponge(World world) {
		
		this.world = world;
	}

	@Override
    public UUID getUUID() {

		return world.getUniqueId();
    }
	
	@Override
    public String getName() {

		return world.getName();
    }

	@Override
    public int getMaxHeight() {
	    
		return world.getBuildHeight();
    }

	@Override
    public Point getSpawnLocation() {
	    
		return SpongeUtils.toPoint(world.getSpawnLocation());
    }
}
