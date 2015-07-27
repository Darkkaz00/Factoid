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
import me.tabinol.factoid.minecraft.FSender;
import me.tabinol.factoid.minecraft.Item;

import org.spongepowered.api.data.manipulator.entity.FoodData;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.world.Location;

public class FPlayerSponge implements FPlayer, Comparable<FPlayer> {
	
	private final FSender sender;
	private final Player player;
	
	protected FPlayerSponge(Player player) {
		
		this.player = player;

		if(player.isOnline()) {
			sender = new FSenderSponge(this, player);
		} else {
			sender = null;
		}
	}

	@Override
	public int compareTo(FPlayer fplayer) {
		
		return getName().compareTo(fplayer.getName());
	}

	@Override
    public FSender getFSender() {
		
		return sender;
	}

	@Override
	public String getName() {
		
		return player.getName();
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
	public boolean hasPermission(String perm) {
		
		return player.hasPermission(perm);
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
    public void sendBlockChange(Point loc, Item blockType, byte by) {
	    
		// TODO Visual selection in Sponge
    }

	@Override
	public void sendBlockChange(Point loc, String blockShortName, byte by) {
		
		// TODO Visual selection in Sponge
	}
	@Override
    public Point getTargetBlockLocation() {
		
		// TODO Get target item
		return SpongeUtils.toPoint(player.getLocation());
	}

	@Override
    public Item getItemInHand() {

		if(player.getItemInHand().isPresent()) {
			return new ItemSponge(player.getItemInHand().get().getItem());
		} else {
			return null;
		}
    }

	@Override
    public int getFoodLevel() {
	    
		return (int) player.getData(FoodData.class).get().getFoodLevel();
    }

	@Override
    public void setFoodLevel(int level) {

		player.getData(FoodData.class).get().setFoodLevel(level);
    }

	@Override
    public double getMaxHealth() {

		return player.getHealthData().getMaxHealth();
    }

	@Override
    public double getHealth() {

		return player.getHealthData().getHealth();
    }

	@Override
    public void setHealth(double health) {

		player.getHealthData().setHealth(health);
    }

	@Override
    public boolean isDead() {
	    
	    return player.getHealthData().getHealth() <= 0;
    }

	/**************************************************************************
	 * Sponge only methods
	 * ***********************************************************************/

	public Player getPlayer() {
		
		return player;
	}
}
