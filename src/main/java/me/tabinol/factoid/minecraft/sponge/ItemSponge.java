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

import org.spongepowered.api.CatalogType;

import me.tabinol.factoid.minecraft.Item;

public class ItemSponge implements Item {

	private final CatalogType catalogType;
	
	public ItemSponge(CatalogType catalogType) {
		
		this.catalogType = catalogType;
	}

	@Override
	public String getShortName() {
		
		String[] divStr = catalogType.getName().split(":");
		
		if(divStr.length >= 2) {
			// Short name without "minecraft:"
			return divStr[1].toUpperCase();
		} else {
			// Bad name
			return catalogType.getName();
		}
	}

	@Override
	public String getLongName() {
		
		return catalogType.getName();
	}

	@Override
	public boolean strEquals(String str) {
		
		return getShortName().equals(str);
	}

	@Override
	public boolean strLongEquals(String str) {
		
		return getLongName().equals(str);
	}

	@Override
	public boolean contains(String str) {
		
		return getShortName().contains(str);
	}

	@Override
	public boolean matches(String regex) {
		
		return getShortName().matches(regex);
	}
}
