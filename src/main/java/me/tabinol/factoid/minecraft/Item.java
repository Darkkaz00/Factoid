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

package me.tabinol.factoid.minecraft;

/**
 * Represent an Item/Block item for Factoid
 * @author Tabinol
 *
 */
public interface Item {

	/**
	 * Short name is like "DIRT"
	 * @return short name
	 */
	public String getShortName();
	
	/**
	 * Long name is same has short name for Bukkit
	 * For Sponge, it is the minecraft Name id : "minecraft:dirt" 
	 * @return long name
	 */
	public String getLongName();
	
	/**
	 * Compare with the short name
	 * @param str the string
	 * @return true if it is the same
	 */
	public boolean strEquals(String str);

	/**
	 * Compare with the long name
	 * @param str the string
	 * @return true if it is the same
	 */
	public boolean strLongEquals(String str);

	/**
	 * Contains in the short name
	 * @param str the string
	 * @return true or false
	 */
	public boolean contains(String str);

	/**
	 * Matches (regex) short name
	 * @param regex the regex
	 * @return true or false
	 */
	public boolean matches(String regex);
}
