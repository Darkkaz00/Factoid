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
package me.tabinol.factoid.playercontainer;

import me.tabinol.factoidapi.lands.ILand;
import me.tabinol.factoidapi.playercontainer.EPlayerContainerType;
import me.tabinol.factoidapi.playercontainer.IPlayerContainer;
import me.tabinol.factoidapi.playercontainer.IPlayerContainerPermission;

import org.bukkit.ChatStyle;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;


/**
 * The Class PlayerContainerPermission.
 */
public class PlayerContainerPermission extends PlayerContainer {

    /** The perm. */
    private Permission perm;
    
    /**
     * Instantiates a new player container permission.
     *
     * @param bukkitPermission the bukkit permission
     */
    public PlayerContainerPermission(String bukkitPermission) {

        super(bukkitPermission, EPlayerContainerType.PERMISSION, true);
        perm = new Permission(bukkitPermission);
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.playercontainer.PlayerContainerInterface#equals(me.tabinol.factoid.playercontainer.PlayerContainer)
     */
    @Override
    public boolean equals(IPlayerContainer container2) {

        return container2 instanceof PlayerContainerPermission
                && name.equalsIgnoreCase(container2.getName());
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.playercontainer.PlayerContainerInterface#copyOf()
     */
    @Override
    public PlayerContainer copyOf() {

        return new PlayerContainerPermission(name);
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.playercontainer.PlayerContainerInterface#hasAccess(org.bukkit.entity.Player)
     */
    @Override
    public boolean hasAccess(Player player) {

        return player.hasPermission(perm);
    }

    @Override
    public boolean hasAccess(Player player, ILand land) {
        
        return hasAccess(player);
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.playercontainer.PlayerContainer#getPrint()
     */
    @Override
    public String getPrint() {

        return ChatStyle.GRAY + "B:" + ChatStyle.WHITE + name;
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.playercontainer.PlayerContainerInterface#setLand(me.tabinol.factoid.lands.Land)
     */
    @Override
    public void setLand(ILand land) {

    }
}
