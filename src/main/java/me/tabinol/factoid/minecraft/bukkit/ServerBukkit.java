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

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.Factoid.ServerType;
import me.tabinol.factoid.commands.OnCommand;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.config.bukkit.ConfigBukkit;
import me.tabinol.factoid.exceptions.SignException;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.areas.Point;
import me.tabinol.factoid.minecraft.CallEvents;
import me.tabinol.factoid.minecraft.ChatPaginator;
import me.tabinol.factoid.minecraft.DependPlugin;
import me.tabinol.factoid.minecraft.FPlayer;
import me.tabinol.factoid.minecraft.FSign;
import me.tabinol.factoid.minecraft.Server;
import me.tabinol.factoid.minecraft.Task;
import me.tabinol.factoid.utilities.FactoidRunnable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.mcstats.MetricsLite;

public class ServerBukkit extends JavaPlugin implements Server {
	
	private Logger logger;
	private JavaPlugin plugin;
	private CallEvents callEvents;
	private Factoid factoid;
	
    @Override
    public void onEnable() {
    	
    	factoid = new Factoid(ServerType.BUKKIT, this);

    	// Start Plugin Metrics
        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (IOException e) {
            // Failed to submit the stats :-(
        }
        
        // Start Listener
        new ListenerBukkit(this);
    }

    public void initServer() {
		
		logger = Bukkit.getLogger();
		plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("Factoid");
		callEvents = new CallEventsBukkit(plugin);
		
        // Register commands
		OnCommand onCommand = new OnCommand();
		CommandsBukkit commands = new CommandsBukkit(onCommand);
		plugin.getCommand("factoid").setExecutor(commands);
        plugin.getCommand("faction").setExecutor(commands);
        
        // Add loaded worlds
        for(World world : Bukkit.getWorlds()) {
        	Factoid.getServerCache().addWorld(new FWorldBukkit(world));
        }
        
        // Add players (in case of reload)
        for(Player player : Bukkit.getOnlinePlayers()) {
        	Factoid.getServerCache().addPlayer(new FPlayerBukkit(player));
        }
	}
    
    @Override
    public Config newConfig() {
    	
    	return new ConfigBukkit(plugin);
    }
    
    @Override
	public DependPlugin newDependPlugin() {
    	
    	return new DependPluginBukkit();
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
	    logger.info(msg);
    }

	@Override
    public void warn(String msg) {
	    logger.warning(msg);
    }

	@Override
    public void error(String msg) {
	    logger.severe(msg);
    }

	@Override
    public Task createTask(FactoidRunnable runnable, Long tick, boolean multiple) {

		BukkitTask task;
		
		runnable.stopNextRun();

        if (multiple) {
            task = Bukkit.getServer().getScheduler().runTaskTimer(plugin, (Runnable) runnable, tick, tick);
        } else {
            task = Bukkit.getServer().getScheduler().runTaskLater(plugin, (Runnable) runnable, tick);
        }
    
	return new TaskBukkit(task);
	}

	@Override
	public void callTaskNow(Runnable runnable) {
		
		plugin.getServer().getScheduler().runTask(this, runnable);
	}

	@Override
    public String getVersion() {

		return plugin.getDescription().getVersion();
    }

	@Override
    public CallEvents CallEvents() {

		return callEvents;
    }
    
    @Override
    public void onDisable() {
    	
    	factoid.serverStop();
    }

	@Override
    public String getOfflinePlayerName(UUID uuid) {
	    
	    OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
	    
	    if(player != null) {
	    	return player.getName();
	    } else {
	    	return null;
	    }
    }

	@Override
    public FPlayer getOfflinePlayer(UUID uuid) {
	    
		OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
		
		if(player != null) {
			return new FPlayerBukkit(player);
		} else {
			return null;
		}
	}

	@Override
    public ChatPaginator getChatPaginator(String text, int pageNumber) {
	    
		return new ChatPaginatorBukkit(text, pageNumber);
    }

	@Override
    public String[] getMaterials() {
	    
		Material[] materials = Material.values();
	    String[] names = new String[materials.length];

	    for (int i = 0; i < materials.length; i++) {
	    	names[i] = materials[i].name();
		}
	    return names;
	}

	@Override
    public String getBlockTypeName(Point point) {
	    
		World world = ((FWorldBukkit) point.getWorld()).getWorld();
		
		return world.getBlockAt(BukkitUtils.toLocation(world, point)).getType().name();
    }

	
	public void removeBlockAndDropSign(Point point) {
		
		World world = ((FWorldBukkit) point.getWorld()).getWorld();
		Location location = BukkitUtils.toLocation(world, point);
		Block block = world.getBlockAt(location);
		
		block.setType(Material.AIR);
		world.dropItem(location, new ItemStack(Material.SIGN, 1));
	}

	@Override
    public void loadChunk(Point point) {

		World world = ((FWorldBukkit) point.getWorld()).getWorld();

		BukkitUtils.toLocation(world, point).getChunk().load();
	}

	@Override
    public FSign getSign(Point point) throws SignException {
	    
		World world = ((FWorldBukkit) point.getWorld()).getWorld();
		Location loc = BukkitUtils.toLocation(world, point);
		Block block = loc.getBlock();
		
		if(block.getType() != Material.WALL_SIGN && block.getType() != Material.SIGN_POST) {
			throw new SignException();
		}

		return new FSignBukkit(block);
    }

	@Override
    public FSign createSign(Point point, float yaw, String[] lines, Land land,
            boolean isWallSign) throws SignException {

		return new FSignBukkit(point, yaw, lines, land, isWallSign);
    }
}
