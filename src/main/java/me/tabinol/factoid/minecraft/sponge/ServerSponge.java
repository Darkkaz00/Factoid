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

import java.io.File;

import me.tabinol.factoid.minecraft.Server;
import me.tabinol.factoid.minecraft.Task;
import me.tabinol.factoid.utilities.FactoidRunnable;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.config.ConfigDir;
import org.spongepowered.api.world.World;

import com.google.inject.Inject;

public class ServerSponge extends Server {
	
	@Inject
	private Logger logger;
	
	@Inject 
	private Game game;
	
	@Inject
	PluginContainer plugin;
	
	@Inject 
	@ConfigDir(sharedRoot = false)
	File configDir;

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

	@Override
    public Task createTask(FactoidRunnable runnable, Long tick, boolean multiple) {
		
		org.spongepowered.api.service.scheduler.Task task;
		
		runnable.stopNextRun();
		
		if(multiple) {
			task = game.getSyncScheduler().runRepeatingTask(plugin, runnable, tick).get();
		} else {
			task = game.getSyncScheduler().runTaskAfter(plugin, runnable, tick).get();
		}
		
		return new TaskSponge(task);
    }

	@Override
    public File getDataFolder() {

		return configDir;
    }

	@Override
    public String getVersion() {
	    
		return plugin.getVersion();
    }
}
