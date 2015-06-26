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

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.tabinol.factoid.lands.areas.Point;
import me.tabinol.factoid.minecraft.FPlayer;

public class FPlayerBukkit extends FSenderBukkit implements FPlayer {
	
	private final Player player;
	private final OfflinePlayer offlinePlayer;
	
	protected FPlayerBukkit(Player player) {
		
		super(player);
		this.player = player;
		this.offlinePlayer = player;
	}

	protected FPlayerBukkit(OfflinePlayer player) {
		
		super(null);
		this.player = null;
		this.offlinePlayer = player;
	}

	@Override
    public Point getLocation() {
	    
		Location loc = player.getLocation();
		
		return new Point(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

	@Override
	public UUID getUUID() {
		
		return offlinePlayer.getUniqueId();
	}

	@Override
    public String getDisplayName() {

		return player.getDisplayName();
    }

	@Override
    public boolean isOnline() {

		return offlinePlayer.isOnline();
    }
	
	@Override
    public String getGameMode() {
	    
		return player.getGameMode().name();
    }

	@Override
    public void removeOneItemFromHand() {

		if(player.getItemInHand().getAmount() == 1) {
			player.setItemInHand(new ItemStack(Material.AIR));
		} else {
			player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
		}
    }

	@Override
    public void teleport(Point newLocation) {
	    
		player.teleport(BukkitUtils.toLocation(Bukkit.getWorld(newLocation.getWorldName()), newLocation));
    }
	
	@SuppressWarnings("deprecation")
    @Override
    public void sendBlockChange(Point loc, String blockType, byte by) {
	    
	    player.sendBlockChange(BukkitUtils.toLocation(((FWorldBukkit) loc.getWorld()).getWorld(), loc), 
	    		Material.getMaterial(blockType), by);
    }

	@Override
    public Point getTargetBlockLocation() {
	    
		return BukkitUtils.toPoint(player.getTargetBlock((HashSet<Material>) null, 10).getLocation());
    }

	@Override
    public String getItemInHand() {

		return player.getItemInHand().getType().name();
    }

	@Override
    public int getFoodLevel() {

		return player.getFoodLevel();
    }

	@Override
    public void setFoodLevel(int level) {

		player.setFoodLevel(level);
    }

	@Override
    public double getMaxHealth() {

		return player.getMaxHealth();
    }

	@Override
    public double getHealth() {

		return player.getHealth();
    }

	@Override
    public void setHealth(double health) {

		player.setHealth(health);
    }

	@Override
    public boolean isDead() {
	    
	    return player.isDead();
    }

	/**************************************************************************
	 * Bukkit only methods
	 * ***********************************************************************/
	
	public OfflinePlayer getOfflinePlayer() {
		
		return offlinePlayer;
	}
	
	public Player getPlayer() {
		
		return player;
	}
}
