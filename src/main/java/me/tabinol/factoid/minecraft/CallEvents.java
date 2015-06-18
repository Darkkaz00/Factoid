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

import me.tabinol.factoid.event.LandModifyReason;
import me.tabinol.factoid.lands.DummyLand;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.areas.Point;
import me.tabinol.factoid.playercontainer.PlayerContainer;

/**
 * Send events to Mincraft
 * @author Tabinol
 *
 */
public interface CallEvents {
	
	public void callLandEvent(DummyLand dummyLand);
	public boolean callLandDeleteEvent(final Land deletedLand);
	public void callLandModifyEvent(final Land land, final LandModifyReason modifyReason, 
			final Object newObject);
	public void callPlayerContainerAddNoEnterEvent(final Land land, final PlayerContainer playerContainer);
	public void callPlayerContainerLandBanEvent(final Land land, final PlayerContainer playerContainer);
	public boolean callPlayerLandChangeEvent(final DummyLand lastDummyLand, final DummyLand dummyLand, final FPlayer player, 
            final Point fromLoc, final Point toLoc, final boolean isTp);
}
