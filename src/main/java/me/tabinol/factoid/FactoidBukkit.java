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

package me.tabinol.factoid;

import java.io.IOException;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.Factoid.ServerType;
import me.tabinol.factoid.minecraft.bukkit.ListenerBukkit;

import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

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


/**
 * Main class for Bukkit/Spigot
 * @author Tabinol
 *
 */
public class FactoidBukkit extends JavaPlugin {
	
	
	private Factoid factoid;
	
    @Override
    public void onEnable() {
    	
    	factoid = new Factoid(ServerType.BUKKIT);

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

    public void onDisable() {
    	
    	factoid.serverStop();
    }
}
