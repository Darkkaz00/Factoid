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

import me.tabinol.factoid.cache.ServerCache;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.economy.EcoScheduler;
import me.tabinol.factoid.factions.Factions;
import me.tabinol.factoid.lands.Lands;
import me.tabinol.factoid.lands.approve.ApproveNotif;
import me.tabinol.factoid.lands.types.Types;
import me.tabinol.factoid.minecraft.DependPlugin;
import me.tabinol.factoid.minecraft.Economy;
import me.tabinol.factoid.minecraft.Server;
import me.tabinol.factoid.parameters.Parameters;
import me.tabinol.factoid.playerscache.PlayersCache;
import me.tabinol.factoid.storage.StorageThread;
import me.tabinol.factoid.utilities.Lang;
import me.tabinol.factoid.utilities.Log;
import me.tabinol.factoid.utilities.MavenAppProperties;

/**
 * Main class for both (Bukkit and Sponge).
 */
public class Factoid {

	/**  The Economy schedule interval. */
	public static final long ECO_SCHEDULE_INTERVAL = 20 * 60 * 5;
	
	public enum ServerType {
		BUKKIT,
		SPONGE;
	}
	
	private static ServerType serverType;
	
    /** The maven app properties. */
    private static MavenAppProperties mavenAppProperties;

    /** The Minecraft logger */
    private static Server minecraftServer; 

    /** The factions. */
    protected static Factions factions;
	
	/** The types */
    protected static Types types;
    
    /** The lands. */
	protected static Lands lands;
    
    /** The parameters. */
    protected static Parameters parameters;
    
    /** The player money. */
    private static Economy playerMoney;
    
    /** The language. */
    private static Lang language;

    /** The log. */
    private static Log log;
    
    /** The storage thread. */
    private static StorageThread storageThread = null;
    
    /** The players cache. */
    private static PlayersCache playersCache;
    
    /** The server cache. */
    private static ServerCache serverCache;
    
    /** The approve notif. */
    private static ApproveNotif approveNotif;
    
    /** The depend plugin. */
    private static DependPlugin dependPlugin;

    /**  The economy scheduler. */
    private EcoScheduler ecoScheduler;
    
    /** The conf. */
    private static Config conf;
    
    /**************************************************************************
     * Static methods (gets)
     *************************************************************************/
    
    /**
     * Gets the maven app properties.
     *
     * @return the maven app properties
     */
    public static MavenAppProperties getMavenAppProperties() {

        return mavenAppProperties;
    }

    public static ServerType getServerType() {
    	
    	return serverType;
    }
    
    public static Server getServer() {
    	
    	return minecraftServer;
    }
    
    public static Config getConf() {
    	
    	return conf;
    }
    
    public static Lang getLanguage() {

        return language;
    }
    
    public static Economy getPlayerMoney() {

        return playerMoney;
    }
    
    public static Log getFactoidLog() {

        return log;
    }

    public static StorageThread getStorageThread() {

        return storageThread;
    }

    public static PlayersCache getPlayersCache() {
    	
    	return playersCache;
    }
    
    public static Lands getLands() {
    	
    	return lands;
    }
    
    public static Factions getFactions() {
    	
    	return factions;
    }
    
    public static Types getTypes() {
    	
    	return types;
    }
    
    public static Parameters getParameters() {
    	
    	return parameters;
    }
    
    public static ServerCache getServerCache() {
    	
    	return serverCache;
    }
    
    public static ApproveNotif getApproveNotif() {
    	
    	return approveNotif;
    }
    
    public static DependPlugin getDependPlugin() {
    	
    	return dependPlugin;
    }
    
    /**************************************************************************
     * Server init, start and stop
     *************************************************************************/

    /**
     * Main declaration (start) for both Bukkit and Sponge
     * @param serverType BUKKIT or SPONGE
     */
    public Factoid(ServerType serverType) {

    	// Init Server dependencies
    	Factoid.serverType = serverType;
        mavenAppProperties = new MavenAppProperties();
        mavenAppProperties.loadProperties();
        
        // Init Server access (Minecraft/Sponge)
        minecraftServer.initServer();
        
        // Init API
        // TODO FactoidAPI Re-Enable FactoidAPI.initFactoidPluginAccess();
        
        // Init Factoid
        serverCache = new ServerCache();
        parameters = new Parameters();
        types = new Types();
        conf = minecraftServer.newConfig();
        log = new Log();
        dependPlugin = minecraftServer.newDependPlugin();
        if (conf.useEconomy() == true && dependPlugin.getEconomy() != null) {
            playerMoney = new Economy();
        } else {
            playerMoney = null;
        }
        language = new Lang();
        storageThread = new StorageThread();
        factions = new Factions();
        lands = new Lands();
        storageThread.loadAllAndStart();
        approveNotif = new ApproveNotif();
        approveNotif.runApproveNotifLater();
        ecoScheduler = new EcoScheduler();
        minecraftServer.createTask(ecoScheduler, ECO_SCHEDULE_INTERVAL, true);
        playersCache = new PlayersCache();
        playersCache.start();
        log.write(getLanguage().getMessage("ENABLE"));
    }
    
    /**
     * Reload.
     */
    public void reload() {

        types = new Types();
        // No reload of Parameters to avoid Deregistering external parameters
        conf.reloadConfig();
        if (conf.useEconomy() == true && dependPlugin.getEconomy() != null) {
            playerMoney = new Economy();
        } else {
            playerMoney = null;
        }
        log.setDebug(conf.isDebug());
        language.reloadConfig();
        factions = new Factions();
        lands = new Lands();
        storageThread.stopNextRun();
        storageThread= new StorageThread();
        storageThread.loadAllAndStart();
        approveNotif.stopNextRun();
        approveNotif.runApproveNotifLater();
    }

    public void serverStop() {

        log.write(getLanguage().getMessage("DISABLE"));
        playersCache.stopNextRun();
        approveNotif.stopNextRun();
        storageThread.stopNextRun();
    }
}
