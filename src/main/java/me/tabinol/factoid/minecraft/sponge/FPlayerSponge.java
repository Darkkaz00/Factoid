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

import java.util.UUID;

import me.tabinol.factoid.lands.areas.Point;
import me.tabinol.factoid.minecraft.FPlayer;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.world.Location;

public class FPlayerSponge extends FSenderSponge implements FPlayer {
	
	private final Player player;
	
	protected FPlayerSponge(Player player) {
		
		super(player);
		this.player = player;
	}

	@Override
    public Point getLocation() {

		Location loc = player.getLocation();
		
		return new Point(player.getWorld().getName(), loc.getBlockX(), loc.getBlockY(),loc.getBlockZ());
    }

	@Override
	public UUID getUUID() {
		
		return player.getUniqueId();
	}

	@Override
    public String getDisplayName() {
	    
		return player.getDisplayNameData().getDisplayName().toString();
    }

	@Override
    public boolean isOnline() {

		return player.isOnline();
    }

	@Override
    public String getGameMode() {
	    
		return player.getGameModeData().getGameMode().getName();
    }

	@Override
    public void removeOneItemFromHand() {

		if(player.getItemInHand().get().getQuantity() == 1) {
			player.setItemInHand(null);
		} else {
			player.getItemInHand().get().setQuantity(player.getItemInHand().get().getQuantity() - 1);
		}
    }

	@Override
    public void teleport(Point newLocation) {
	    
	    player.transferToWorld(newLocation.getWorldName(), SpongeUtils.toLocationVector(newLocation));
    }

	@Override
    public void sendBlockChange(Point loc, String blockType, byte by) {
	    
		// TODO Visual selection in Sponge
    }

	@Override
    public Point getTargetBlockLocation() {
		
		// TODO Get target item
		return SpongeUtils.toPoint(player.getLocation());
	}

	@Override
    public String getItemInHand() {

		if(player.getItemInHand().isPresent()) {
			return player.getItemInHand().get().getItem().getName();
		} else {
			return null;
		}
    }
}
