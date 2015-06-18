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
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.UUID;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.Factoid.ServerType;
import me.tabinol.factoid.minecraft.CallEvents;
import me.tabinol.factoid.minecraft.ChatPaginator;
import me.tabinol.factoid.minecraft.FPlayer;
import me.tabinol.factoid.minecraft.Server;
import me.tabinol.factoid.minecraft.Task;
import me.tabinol.factoid.utilities.FactoidRunnable;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.ServerStartedEvent;
import org.spongepowered.api.event.state.ServerStoppingEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.config.ConfigDir;
import org.spongepowered.api.world.World;

import com.google.common.base.Optional;
import com.google.inject.Inject;

/**
 * Main class for Sponge
 * CAUTION : You must edit ServerSponge.java.template. ServerSponge.java is overwrite at compile time!
 * @author Tabinol
 *
 */
@Plugin(id = "Factoid", name = "Factoid", version = "1.2.1-SNAPSHOT")
public class ServerSponge implements Server {
	
	@Inject
	private Logger logger;
	
	@Inject 
	private Game game;
	
	@Inject
	private PluginContainer plugin;
	
	@Inject 
	@ConfigDir(sharedRoot = false)
	private File configDir;

	private Factoid factoid;

	private CallEvents callEvents;

    @Subscribe
    public void onServerStart(ServerStartedEvent event) {
    	
    	factoid = new Factoid(ServerType.SPONGE);

        // Start Listener
        new ListenerSponge();
    }
    
    public void initServer() {
		
		callEvents = new CallEventsSponge(game);

        // Add loaded worlds
        for(World world : game.getServer().getWorlds()) {
        	Factoid.getServerCache().addWorld(new FWorldSponge(world));
        }

        // Add players (in case of reload)
        for(Player player : game.getServer().getOnlinePlayers()) {
        	Factoid.getServerCache().addPlayer(new FPlayerSponge(player));
        }
	}

    @Subscribe
    public void onServerStop(ServerStoppingEvent event) {
    	
    	factoid.serverStop();
    }

	@Override
    public Factoid getFactoid() {
    	
    	return factoid;
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
	public void callTaskNow(Runnable runnable) {
		
		game.getSyncScheduler().runTask(plugin, runnable);
	}

	@Override
    public File getDataFolder() {

		return configDir;
    }
	
	@Override
	public InputStream getResource(String path) {
		
		return this.getClass().getResourceAsStream(path);
	}

	@Override
    public String getVersion() {
	    
		return plugin.getVersion();
    }

	@Override
    public CallEvents CallEvents() {

		return callEvents;
    }

	@Override
    public String getOfflinePlayerName(UUID uuid) {
	    
		Optional<Player> playerOp = game.getServer().getPlayer(uuid);
		
		if(playerOp.isPresent()) {
			return playerOp.get().getName();
		} else {
			return null;
		}
    }

	@Override
    public FPlayer getOfflinePlayer(UUID uuid) {

		Optional<Player> playerOp = game.getServer().getPlayer(uuid);
		
		if(playerOp.isPresent()) {
			return new FPlayerSponge(playerOp.get());
		} else {
			return null;
		}
    }

	@Override
    public ChatPaginator getChatPaginator(String text, int pageNumber) {
	    
		return new ChatPaginatorSponge(text, pageNumber, game);
    }

	@Override
    public String[] getMaterials() {
	    
		Field[] materials = ItemTypes.class.getDeclaredFields();
	    String[] names = new String[materials.length];
	    
	    for (int i = 0; i < materials.length; i++) {
	    	names[i] = materials[i].getName();
		}
	    return names;
	}
}
