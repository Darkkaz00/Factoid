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

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import me.tabinol.factoid.minecraft.Item;

public class ItemBukkit implements Item {
	
	private final Material mat;
	private final EntityType entityType;
	private final String name;
	
	public ItemBukkit(Material mat) {
		
		this.mat = mat;
		this.entityType = null;
		name = mat.name();
	}

	public ItemBukkit(EntityType entityType) {
		
		this.mat = null;
		this.entityType = entityType;
		name = entityType.name();
	}

	@Override
	public String getShortName() {

		return name;
	}

	@Override
	public String getLongName() {

		return name;
	}

	@Override
	public boolean strEquals(String str) {

		return name.equals(str);
	}

	@Override
	public boolean strLongEquals(String str) {
		
		return name.equals(str);
	}
	
	@Override
	public boolean contains(String str) {

		return name.contains(str);
	}

	@Override
	public boolean matches(String regex) {

		return name.matches(regex);
	}
	
	/**************************************************************************
	 * Bukkit only
	 *************************************************************************/
	
	public Material getMaterial() {
		
		return mat;
	}

	public EntityType getEntityType() {
		
		return entityType;
	}
}
