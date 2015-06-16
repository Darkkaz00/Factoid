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
package me.tabinol.factoid.minecraft;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import me.tabinol.factoid.utilities.FactoidRunnable;

/**
 * Represent Minecraft Server
 * @author Tabinol
 *
 */
public abstract class Server {
	
	protected final Map<UUID, FWorld> worldUList;
	protected final Map<String, FWorld> worldNList;
	protected final Map<UUID, FPlayer> playerUList;
	protected final Map<String, FPlayer> playerNList;
	
	protected Server() {
		
		worldUList = new TreeMap<UUID, FWorld>();
		worldNList = new TreeMap<String, FWorld>();
		playerUList = new TreeMap<UUID, FPlayer>();
		playerNList = new TreeMap<String, FPlayer>();
	}
	
	// Messages
	public abstract void info(String msg);
	public abstract void debug(String msg);
	public abstract void warn(String msg);
	public abstract void error(String msg);
	
	// Worlds
	public void addWorld(FWorld world) {
		
		worldUList.put(world.getUUID(), world);
		worldNList.put(world.getName(), world);
	}
	
	public void removeWorld(FWorld world) {
		
		worldUList.remove(world.getUUID());
		worldNList.remove(world.getName());
	}
	
	public FWorld getWorld(String worldName) {
		
		return worldNList.get(worldName);
	}
	
	public FWorld getWorld(UUID uuid) {
		
		return worldUList.get(uuid);
	}
	
	public Collection<FWorld> getWorlds() {
		
		return worldNList.values();
	}

	// Players
	public void addPlayer(FPlayer player) {
		
		playerUList.put(player.getUUID(), player);
		playerNList.put(player.getName(), player);
	}
	
	public void removePlayer(FPlayer player) {
		
		playerUList.remove(player.getUUID());
		playerNList.remove(player.getName());
	}
	
	public FPlayer getPlayer(String playerName) {
		
		return playerNList.get(playerName);
	}
	
	public FPlayer getPlayer(UUID uuid) {
		
		return playerUList.get(uuid);
	}
	
	public Collection<FPlayer> getPlayers() {
		
		return playerNList.values();
	}
	
	// Task
	public abstract Task createTask(FactoidRunnable runnable, Long tick, boolean multiple);
	
	public abstract File getDataFolder();
	
	public abstract String getVersion();
}
