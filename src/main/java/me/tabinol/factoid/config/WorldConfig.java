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

import java.util.TreeMap;

import me.tabinol.factoid.lands.DummyLand;
import me.tabinol.factoid.lands.types.Type;


// Started by Lands.Class
// Load world config and lands default
/**
 * The Class WorldConfig.
 */
public abstract class WorldConfig {

    /** Default config (No Type or global) */
    protected DummyLand defaultConfNoType;

    public abstract TreeMap<String, DummyLand> getLandOutsideArea();
    
    /**
     * Get the default configuration of a land without a Type.
     * @return The land configuration (DummyLand)
     */
    public DummyLand getDefaultconfNoType() {
    	
    	return defaultConfNoType;
    }

    /**
     * Gets the default conf for each type
     * @return a TreeMap of default configuration
     */
    public abstract TreeMap<Type, DummyLand> getTypeDefaultConf();
}
