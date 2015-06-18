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
import java.io.InputStream;
import java.util.UUID;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.utilities.FactoidRunnable;

/**
 * Represent Minecraft Server
 * @author Tabinol
 *
 */
public interface Server {
	
	// init
	public void initServer();
	
	public Factoid getFactoid();
	
	// Messages
	public void info(String msg);
	public void debug(String msg);
	public void warn(String msg);
	public void error(String msg);
	
	// Task
	public Task createTask(FactoidRunnable runnable, Long tick, boolean multiple);
	public void callTaskNow(Runnable runnable);
	
	// Files
	public File getDataFolder();
	public InputStream getResource(String path);
	
	public String getVersion();
	
	public CallEvents CallEvents();
	
	public FPlayer getOfflinePlayer(UUID uuid);
	public String getOfflinePlayerName(UUID uuid);
	
	public ChatPaginator getChatPaginator(String text, int pageNumber);
	
	/**
	 * Return complete list of material type
	 * @return list of material type
	 */
	public String[] getMaterials();
}
