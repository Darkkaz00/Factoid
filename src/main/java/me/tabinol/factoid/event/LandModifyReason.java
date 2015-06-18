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

package me.tabinol.factoid.event;

/**
 * The Enum LandModifyReason.
 */
public enum LandModifyReason {
	
	/** The area add. */
	AREA_ADD,
	
	/** The area remove. */
	AREA_REMOVE,
	
	/** The area replace. */
	AREA_REPLACE,
	
	/** The resident add. */
	RESIDENT_ADD,
	
	/** The resident remove. */
	RESIDENT_REMOVE,
	
	/** The permission set. */
	PERMISSION_SET,
	
	/** The permission unset. */
	PERMISSION_UNSET,
	
	/** The flag set. */
	FLAG_SET,
	
	/** The flag unset. */
	FLAG_UNSET,
	
	/** The owner change. */
	OWNER_CHANGE,
	
	/** The land rename. */
	RENAME,
	
	/** Change the faction territory or siege */
	FACTION_TERRITORY_CHANGE;
}
