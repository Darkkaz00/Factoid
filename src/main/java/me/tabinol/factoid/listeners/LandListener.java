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
package me.tabinol.factoid.listeners;

import java.util.ArrayList;
import java.util.HashSet;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.parameters.FlagList;
import me.tabinol.factoid.parameters.PermissionList;
import me.tabinol.factoid.parameters.PermissionType;
import me.tabinol.factoidapi.config.players.IPlayerStaticConfig;
import me.tabinol.factoidapi.event.PlayerContainerAddNoEnterEvent;
import me.tabinol.factoidapi.event.PlayerContainerLandBanEvent;
import me.tabinol.factoidapi.event.PlayerLandChangeEvent;
import me.tabinol.factoidapi.lands.IDummyLand;
import me.tabinol.factoidapi.lands.ILand;
import me.tabinol.factoidapi.playercontainer.IPlayerContainer;
import me.tabinol.factoidapi.playercontainer.IPlayerContainerPlayer;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;


/**
 * Land listener
 */
public class LandListener extends CommonListener implements Listener {

    /** The player heal. */
    private final ArrayList<Player> playerHeal;
    
    /** The land heal. */
    private final LandHeal landHeal;
    
    /** The player conf. */
    private final IPlayerStaticConfig playerConf;

    /**
     * The Class LandHeal.
     */
    private class LandHeal extends BukkitRunnable {

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {

            int foodLevel;
            double health;
            double maxHealth;

            for (Player player : playerHeal) {
                if (!player.isDead()) {
                    Factoid.getThisPlugin().iLog().write("Healing: " + player.getName());
                    foodLevel = player.getFoodLevel();
                    if (foodLevel < 20) {
                        foodLevel += 5;
                        if (foodLevel > 20) {
                            foodLevel = 20;
                        }
                        player.setFoodLevel(foodLevel);
                    }
                    health = player.getHealth();
                    maxHealth = player.getMaxHealth();
                    if (health < maxHealth) {
                        health += maxHealth / 10;
                        if (health > maxHealth) {
                            health = maxHealth;
                        }
                        player.setHealth(health);
                    }
                }
            }
        }
    }

    /**
     * Instantiates a new land listener.
     */
    public LandListener() {

        super();
        playerConf = Factoid.getThisPlugin().iPlayerConf();
        playerHeal = new ArrayList<Player>();
        landHeal = new LandHeal();
        landHeal.runTaskTimer(Factoid.getThisPlugin(), 20, 20);

    }

    // Must be running before PlayerListener
    /**
     * On player quit.
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        IDummyLand land = playerConf.get(player).getLastLand();

        // Notify for quit
        while (land instanceof ILand) {
            notifyPlayers((ILand)land, "ACTION.PLAYEREXIT", player);
            land = ((ILand)land).getParent();
        }

        if (playerHeal.contains(player)) {
            playerHeal.remove(player);
        }
    }

    /**
     * On player land change.
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerLandChange(PlayerLandChangeEvent event) {
        Player player = event.getPlayer();
        ILand lastLand = event.getLastLand();
        ILand land = event.getLand();
        IDummyLand dummyLand;
        String value;

        if (lastLand != null) {

            if (!(land != null && lastLand.isDescendants(land))) {

                //Notify players for exit
                notifyPlayers(lastLand, "ACTION.PLAYEREXIT", player);

                // Message quit
                value = lastLand.getFlagNoInherit(FlagList.MESSAGE_QUIT.getFlagType()).getValueString();
                if (!value.isEmpty()) {
                    player.sendMessage(ChatColor.GRAY + "[Factoid] (" + ChatColor.GREEN + lastLand.getName() + ChatColor.GRAY + "): " + ChatColor.WHITE + value);
                }
            }

            /*for(String playername : lastLand.getPlayersInLand()){
             Factoid.getThisPlugin().iScoreboard().sendScoreboard(lastLand.getPlayersInLand(), Factoid.getThisPlugin().getServer().getPlayer(playername), lastLand.getName());
             }
             Factoid.getThisPlugin().iScoreboard().sendScoreboard(lastLand.getPlayersInLand(), player, lastLand.getName());*/
        }
        if (land != null) {
            dummyLand = land;

            if (!playerConf.get(player).isAdminMod()) {
                // is banned or can enter
                PermissionType permissionType = PermissionList.LAND_ENTER.getPermissionType();
                if ((land.isBanned(player)
                        || land.checkPermissionAndInherit(player, permissionType) != permissionType.getDefaultValue())
                        && !land.isOwner(player) && !player.hasPermission("factoid.bypassban")) {
                    String message;
                    if (land.isBanned(player)) {
                        message = "ACTION.BANNED";
                    } else {
                        message = "ACTION.NOENTRY";
                    }
                    if (land == lastLand || lastLand == null) {
                        tpSpawn(player, land, message);
                        return;
                    } else {
                        player.sendMessage(ChatColor.GRAY + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage(message, land.getName()));
                        event.setCancelled(true);
                        return;
                    }
                }
            }

            if (!(lastLand != null && land.isDescendants(lastLand))) {

                //Notify players for Enter
                ILand landTest = land;
                while (landTest != null && landTest != lastLand) {
                    notifyPlayers(landTest, "ACTION.PLAYERENTER", player);
                    landTest = landTest.getParent();
                }
                // Message join
                value = land.getFlagNoInherit(FlagList.MESSAGE_JOIN.getFlagType()).getValueString();
                if (!value.isEmpty()) {
                    player.sendMessage(ChatColor.GRAY + "[Factoid] (" + ChatColor.GREEN + land.getName() + ChatColor.GRAY + "): " + ChatColor.WHITE + value);
                }
            }


            /*for(String playername:land.getPlayersInLand()){
             Factoid.getThisPlugin().iScoreboard().sendScoreboard(land.getPlayersInLand(), Factoid.getThisPlugin().getServer().getPlayer(playername), land.getName());
             }
             Factoid.getThisPlugin().iScoreboard().sendScoreboard(land.getPlayersInLand(), player, land.getName());*/
        } else {
            dummyLand = Factoid.getThisPlugin().iLands().getOutsideArea(event.getToLoc());
            Factoid.getThisPlugin().iScoreboard().resetScoreboard(player);
        }

        //Check for Healing
        PermissionType permissionType = PermissionList.AUTO_HEAL.getPermissionType();
        
        if (dummyLand.checkPermissionAndInherit(player, permissionType) != permissionType.getDefaultValue()) {
            if (!playerHeal.contains(player)) {
                playerHeal.add(player);
            }
        } else {
            if (playerHeal.contains(player)) {
                playerHeal.remove(player);
            }
        }
        
        //Death land
        permissionType = PermissionList.LAND_DEATH.getPermissionType();
        
        if (!playerConf.get(player).isAdminMod() 
        		&& dummyLand.checkPermissionAndInherit(player, permissionType) != permissionType.getDefaultValue()) {
        	player.setHealth(0);
        }
    }

    /**
     * On player container land ban.
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerContainerLandBan(PlayerContainerLandBanEvent event) {

        checkForBannedPlayers(event.getLand(), event.getPlayerContainer(), "ACTION.BANNED");
    }

    /**
     * On player container add no enter.
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerContainerAddNoEnter(PlayerContainerAddNoEnterEvent event) {

        checkForBannedPlayers(event.getLand(), event.getPlayerContainer(), "ACTION.NOENTRY");
    }

    /**
     * Check for banned players.
     *
     * @param land the land
     * @param pc the pc
     * @param message the message
     */
    private void checkForBannedPlayers(ILand land, IPlayerContainer pc, String message) {
    	
    	checkForBannedPlayers(land, pc, message, new ArrayList<Player>());
    }

    /**
     * Check for banned players.
     *
     * @param land the land
     * @param pc the pc
     * @param message the message
     * @param kickPlayers the kicked players list
     */
    private void checkForBannedPlayers(ILand land, IPlayerContainer pc, String message, ArrayList<Player> kickPlayers) {

    	Player[] playersArray = land.getPlayersInLand().toArray(new Player[0]); // Fix ConcurrentModificationException
    	
    	for (Player players : playersArray) {
            if (pc.hasAccess(players)
                    && !land.isOwner(players)
                    && !playerConf.get(players).isAdminMod()
                    && !players.hasPermission("factoid.bypassban")
                    && (land.checkPermissionAndInherit(players, PermissionList.LAND_ENTER.getPermissionType()) == false
                    || land.isBanned(players))
                    && !kickPlayers.contains(players)) {
                tpSpawn(players, land, message);
                kickPlayers.add(players);
            }
        }
    	
    	// check for children
    	for (ILand children : land.getChildren()) {
    		checkForBannedPlayers(children, pc, message);
    	}
    }
    
    // Notify players for land Enter/Exit
    /**
     * Notify players.
     *
     * @param land the land
     * @param message the message
     * @param playerIn the player in
     */
    private void notifyPlayers(ILand land, String message, Player playerIn) {

        Player player;
        
        for (IPlayerContainerPlayer playerC : land.getPlayersNotify()) {
            
            player = playerC.getPlayer();
            
            if (player != null && player != playerIn
                    // Only adminmod can see vanish
                    && (!playerConf.isVanished(playerIn) || playerConf.get(player).isAdminMod())) {
                player.sendMessage(ChatColor.GRAY + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage(
                        message, playerIn.getDisplayName(), land.getName() + ChatColor.GRAY));
            }
        }
    }

    /**
     * Tp spawn.
     *
     * @param player the player
     * @param land the land
     * @param message the message
     */
    private void tpSpawn(Player player, ILand land, String message) {

        player.teleport(player.getWorld().getSpawnLocation());
        player.sendMessage(ChatColor.GRAY + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage(message, land.getName()));
    }
}
