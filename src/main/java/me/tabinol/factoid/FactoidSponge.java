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

package me.tabinol.factoid;

import me.tabinol.factoid.Factoid.ServerType;
import me.tabinol.factoid.minecraft.sponge.ListenerSponge;

import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.ServerStartedEvent;
import org.spongepowered.api.event.state.ServerStoppingEvent;
import org.spongepowered.api.plugin.Plugin;

/**
 * Main class for Sponge
 * CAUTION : You must edit FactoidSponge.java.template. FactoidSponge.java is overwrited at compile time!
 * @author Tabinol
 *
 */
@Plugin(id = "Factoid", name = "Factoid", version = "1.2.1-SNAPSHOT")
public class FactoidSponge {
	
	private Factoid factoid;

    @Subscribe
    public void onServerStart(ServerStartedEvent event) {
    	
    	factoid = new Factoid(ServerType.SPONGE);

        // Start Listener
        new ListenerSponge();
    }
    
    @Subscribe
    public void onServerStop(ServerStoppingEvent event) {
    	
    	factoid.serverStop();
    }
}
