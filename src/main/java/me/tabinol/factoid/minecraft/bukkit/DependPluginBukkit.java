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

import static org.bukkit.Bukkit.getServer;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.minecraft.DependPlugin;
import me.tabinol.factoid.minecraft.EditWorld;
import me.tabinol.factoid.minecraft.Vanish;
import me.tabinol.factoid.minecraft.bukkit.plugins.ChatEssentials;
import me.tabinol.factoid.minecraft.bukkit.plugins.EditWorldEdit;
import me.tabinol.factoid.minecraft.bukkit.plugins.VanishEssentials;
import me.tabinol.factoid.minecraft.bukkit.plugins.VanishNoPacket;
import me.tabinol.factoid.minecraft.bukkit.plugins.VaultEconomy;
import me.tabinol.factoid.minecraft.bukkit.plugins.VaultPermission;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;


/**
 * The Class DependPlugin.
 */
public class DependPluginBukkit extends DependPlugin {

    /**
     * Instantiates a new depend plugin.
     */
    public DependPluginBukkit() {

    	Plugin plugin;
        
        // Essential (first)
    	Plugin essentials = getPlugin("Essentials");
    	if(essentials != null) {
    		chat = new ChatEssentials(essentials);
    	} else {
    		chat = new me.tabinol.factoid.minecraft.Chat();
    	}
        
    	// World Edit
    	plugin = getPlugin("WorldEdit");
    	if(plugin != null) {
    		editWorld = new EditWorldEdit(plugin);
    	} else {
    		editWorld = new EditWorld();
    	}
    	
        // Vanish
        plugin = getPlugin("VanishNoPacket");
        if(plugin != null) {
        	vanish = new VanishNoPacket();
        } else if(essentials != null) {
        	vanish = new VanishEssentials(essentials);
        } else {
        	vanish = new Vanish();
        }
        
        // Vault
        getPlugin("Vault");
       	Permission permissionP = setupPermissions();
       	if(permissionP != null) {
       		permission = new VaultPermission(permissionP); 
       	} else {
       		permission = new me.tabinol.factoid.minecraft.Permission();
       	}
       	Economy economyP = setupEconomy();
       	if(economyP != null) {
       		economy = new VaultEconomy(economyP);
       	} else {
       		economy = new me.tabinol.factoid.minecraft.Economy();
       	}
    }

    /**
     * Gets the plugin.
     *
     * @param pluginName the plugin name
     * @return the plugin
     */
    private Plugin getPlugin(String pluginName) {

        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);

        if (plugin != null) {
            Bukkit.getServer().getPluginManager().enablePlugin(plugin);
            Factoid.getFactoidLog().write(pluginName + " detected!");
            Factoid.getServer().info(pluginName + " detected!");
        }

        return plugin;
    }

    /**
     * Setup permissions.
     *
     * @return permissions
     */
    private Permission setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            return permissionProvider.getProvider();
        }
        return null;
    }

    /**
     * Setup economy.
     *
     * @return economy
     */
    private Economy setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            return economyProvider.getProvider();
        }

        return null;
    }
}
