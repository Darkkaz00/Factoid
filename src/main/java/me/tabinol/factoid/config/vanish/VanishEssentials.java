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
package me.tabinol.factoid.config.vanish;

import com.earth2me.essentials.Essentials;

import me.tabinol.factoid.BKVersion;
import me.tabinol.factoid.Factoid;

import org.bukkit.entity.Player;


/**
 * Essentials Functions.
 *
 * @author Tabinol
 */
public class VanishEssentials implements Vanish {
    
    /** The essentials. */
    private final Essentials essentials;
    
    /**
     * Instantiates a new vanish essentials.
     */
    public VanishEssentials() {
        
        essentials = (Essentials)Factoid.getDependPlugin().getEssentials();
    }
    
    /* (non-Javadoc)
     * @see me.tabinol.factoid.config.vanish.Vanish#isVanished(org.bukkit.entity.Player)
     */
    @Override
    public boolean isVanished(Player player) {
        
        return (Factoid.getConf().isSpectatorIsVanish() 
        		&& BKVersion.isSpectatorMode(player))
        		|| essentials.getUser(player).isVanished();
    }
}
