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

import me.tabinol.factoid.minecraft.DependPlugin;
import me.tabinol.factoid.minecraft.EditWorld;
import me.tabinol.factoid.minecraft.Vanish;

public class DependPluginSponge extends DependPlugin {
	
    @SuppressWarnings("unused")
    private final Game game;

	public DependPluginSponge(Game game) {
        
    	this.game = game;
    	
    	// TODO Sponge plugins compatibility
   		chat = new me.tabinol.factoid.minecraft.Chat();
   		editWorld = new EditWorld();
       	vanish = new Vanish();
   		permission = new me.tabinol.factoid.minecraft.Permission();
   		economy = new me.tabinol.factoid.minecraft.Economy();
    }
}
