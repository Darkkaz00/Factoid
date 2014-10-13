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

package me.tabinol.factoid.listeners;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoidapi.lands.IDummyLand;
import me.tabinol.factoidapi.parameters.IPermissionType;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

/**
 * Common methods for Listeners
 */
public class CommonListener {
	
	/**
	 * Check permission.
	 * 
	 * @param land
	 *            the land
	 * @param player
	 *            the player
	 * @param pt
	 *            the pt
	 * @return true, if successful
	 */
	protected boolean checkPermission(IDummyLand land, Player player,
			IPermissionType pt) {

		return land.checkPermissionAndInherit(player, pt) == pt
				.getDefaultValue();
	}

	/**
	 * Message permission.
	 * 
	 * @param player
	 *            the player
	 */
	protected void messagePermission(Player player) {

		player.sendMessage(ChatColor.GRAY + "[Factoid] "
				+ Factoid.getThisPlugin().iLanguage().getMessage("GENERAL.MISSINGPERMISSION"));
	}
	
	/**
	 * Gets the source player from entity or projectile
	 *
	 * @param entity the entity
	 * @return the source player
	 */
	protected Player getSourcePlayer(Entity entity) {
		
		Projectile damagerProjectile;

		// Check if the damager is a player
		if (entity instanceof Player) {
			return (Player) entity;
		} else if (entity instanceof Projectile
				&& entity.getType() != EntityType.EGG
				&& entity.getType() != EntityType.SNOWBALL) {
			damagerProjectile = (Projectile) entity;
			if (damagerProjectile.getShooter() instanceof Player) {
				return (Player) damagerProjectile.getShooter();
			}
		}
		
		return null;
	}
}
