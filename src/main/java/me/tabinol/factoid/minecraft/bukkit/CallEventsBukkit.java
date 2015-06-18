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

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.tabinol.factoid.event.LandModifyReason;
import me.tabinol.factoid.event.bukkit.LandDeleteEvent;
import me.tabinol.factoid.event.bukkit.LandEvent;
import me.tabinol.factoid.event.bukkit.LandModifyEvent;
import me.tabinol.factoid.event.bukkit.PlayerContainerAddNoEnterEvent;
import me.tabinol.factoid.event.bukkit.PlayerContainerLandBanEvent;
import me.tabinol.factoid.event.bukkit.PlayerLandChangeEvent;
import me.tabinol.factoid.lands.DummyLand;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.areas.Point;
import me.tabinol.factoid.minecraft.CallEvents;
import me.tabinol.factoid.minecraft.FPlayer;
import me.tabinol.factoid.playercontainer.PlayerContainer;

public class CallEventsBukkit implements CallEvents {

	private final PluginManager pluginManager;
	
	public CallEventsBukkit(JavaPlugin plugin) {

		pluginManager = plugin.getServer().getPluginManager();
    }

	@Override
    public void callLandEvent(DummyLand dummyLand) {
	    
		pluginManager.callEvent(new LandEvent(dummyLand));
    }

	@Override
    public boolean callLandDeleteEvent(Land deletedLand) {

		LandDeleteEvent event = new LandDeleteEvent(deletedLand);
		pluginManager.callEvent(event);
		
		return event.isCancelled();
    }

	@Override
    public void callLandModifyEvent(Land land, LandModifyReason modifyReason,
            Object newObject) {
		
		pluginManager.callEvent(new LandModifyEvent(land, modifyReason, newObject));
	}

	@Override
    public void callPlayerContainerAddNoEnterEvent(Land land,
            PlayerContainer playerContainer) {

		pluginManager.callEvent(new PlayerContainerAddNoEnterEvent(land, playerContainer));
    }

	@Override
    public void callPlayerContainerLandBanEvent(Land land,
            PlayerContainer playerContainer) {
		
		pluginManager.callEvent(new PlayerContainerLandBanEvent(land, playerContainer));
    }

	@Override
    public boolean callPlayerLandChangeEvent(DummyLand lastDummyLand,
            DummyLand dummyLand, FPlayer player, Point fromLoc,
            Point toLoc, boolean isTp) {
		
		PlayerLandChangeEvent event = new PlayerLandChangeEvent(lastDummyLand, dummyLand, player, 
				fromLoc, toLoc, isTp);
		pluginManager.callEvent(event);
		
		return event.isCancelled();
    }
}
