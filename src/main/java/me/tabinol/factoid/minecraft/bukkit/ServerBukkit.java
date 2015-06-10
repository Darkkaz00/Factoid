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

import java.util.logging.Logger;

import me.tabinol.factoid.minecraft.Server;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerBukkit extends Server {
	
	private final Logger logger;
	private final JavaPlugin plugin;
	
	public ServerBukkit() {
		
		super();
		
		logger = Bukkit.getLogger();
		plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("Factoid");
		
        // Register commands
		CommandsBukkit commands = new CommandsBukkit();
		plugin.getCommand("factoid").setExecutor(commands);
        plugin.getCommand("faction").setExecutor(commands);
        
        // Add loaded worlds
        for(World world : Bukkit.getWorlds()) {
        	addWorld(new FWorldBukkit(world));
        }
        
        // Add players (in case of reload)
        for(Player player : Bukkit.getOnlinePlayers()) {
        	addPlayer(new FPlayerBukkit(player));
        }
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
}
