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

package me.tabinol.factoid.minecraft.bukkit;

import me.tabinol.factoid.lands.areas.Point;

import org.bukkit.Location;
import org.bukkit.World;

public class BukkitUtils {

	public static Point toPoint(Location loc) {
		
		return new Point(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ());
	}
	
	public static Location toLocation(World world, Point loc) {
		
		return new Location(world, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
	}

}
