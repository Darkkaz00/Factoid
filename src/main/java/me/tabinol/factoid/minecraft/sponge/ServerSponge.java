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

import me.tabinol.factoid.minecraft.Server;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.world.World;

import com.google.inject.Inject;

public class ServerSponge extends Server {
	
	public ServerSponge() {
		
		super();

        // Add loaded worlds
        for(World world : game.getServer().getWorlds()) {
        	addWorld(new FWorldSponge(world));
        }

        // Add players (in case of reload)
        for(Player player : game.getServer().getOnlinePlayers()) {
        	addPlayer(new FPlayerSponge(player));
        }
	}

	@Inject
	private Logger logger;
	
	@Inject 
	private Game game;

	@Override
    public void info(String msg) {
	    logger.info(msg);
    }

	@Override
    public void debug(String msg) {
	    logger.debug(msg);
    }

	@Override
    public void warn(String msg) {
	    logger.warn(msg);
    }

	@Override
    public void error(String msg) {
	    logger.error(msg);
    }
}
