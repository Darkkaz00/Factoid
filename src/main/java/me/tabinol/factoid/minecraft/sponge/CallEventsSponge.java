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

import org.spongepowered.api.Game;
import org.spongepowered.api.service.event.EventManager;

import me.tabinol.factoid.event.LandModifyReason;
import me.tabinol.factoid.event.sponge.LandDeleteEvent;
import me.tabinol.factoid.event.sponge.LandEvent;
import me.tabinol.factoid.event.sponge.LandModifyEvent;
import me.tabinol.factoid.event.sponge.PlayerContainerAddNoEnterEvent;
import me.tabinol.factoid.event.sponge.PlayerContainerLandBanEvent;
import me.tabinol.factoid.event.sponge.PlayerLandChangeEvent;
import me.tabinol.factoid.lands.DummyLand;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.areas.Point;
import me.tabinol.factoid.minecraft.CallEvents;
import me.tabinol.factoid.minecraft.FPlayer;
import me.tabinol.factoid.playercontainer.PlayerContainer;

public class CallEventsSponge implements CallEvents {
	
	private final EventManager eventManager;

	public CallEventsSponge(Game game) {
	    
		eventManager = game.getEventManager();
    }

	@Override
    public void callLandEvent(DummyLand dummyLand) {
	    
		eventManager.post(new LandEvent(dummyLand));
    }

	@Override
    public boolean callLandDeleteEvent(Land deletedLand) {

		LandDeleteEvent event = new LandDeleteEvent(deletedLand);
		eventManager.post(event);
		
		return event.isCancelled();
    }

	@Override
    public void callLandModifyEvent(Land land, LandModifyReason modifyReason,
            Object newObject) {
		
		eventManager.post(new LandModifyEvent(land, modifyReason, newObject));
	}

	@Override
    public void callPlayerContainerAddNoEnterEvent(Land land,
            PlayerContainer playerContainer) {

		eventManager.post(new PlayerContainerAddNoEnterEvent(land, playerContainer));
    }

	@Override
    public void callPlayerContainerLandBanEvent(Land land,
            PlayerContainer playerContainer) {
		
		eventManager.post(new PlayerContainerLandBanEvent(land, playerContainer));
    }

	@Override
    public boolean callPlayerLandChangeEvent(DummyLand lastDummyLand,
            DummyLand dummyLand, FPlayer player, Point fromLoc,
            Point toLoc, boolean isTp) {
		
		PlayerLandChangeEvent event = new PlayerLandChangeEvent(lastDummyLand, dummyLand, player, 
				fromLoc, toLoc, isTp);
		eventManager.post(event);
		
		return event.isCancelled();
    }
}
