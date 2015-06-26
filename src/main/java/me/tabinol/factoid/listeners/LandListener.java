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

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.lands.DummyLand;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.areas.Point;
import me.tabinol.factoid.minecraft.FPlayer;
import me.tabinol.factoid.parameters.FlagList;
import me.tabinol.factoid.parameters.PermissionList;
import me.tabinol.factoid.parameters.PermissionType;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import me.tabinol.factoid.playercontainer.PlayerContainerPlayer;
import me.tabinol.factoid.utilities.ChatStyle;
import me.tabinol.factoid.utilities.FactoidRunnable;

/**
 * Land listener
 */
public class LandListener extends CommonListener {

    /** The player heal. */
    private final ArrayList<FPlayer> playerHeal;
    
    /** The land heal. */
    private final LandHeal landHeal;
    
    /**
     * The Class LandHeal.
     */
    private class LandHeal extends FactoidRunnable {

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {

            int foodLevel;
            double health;
            double maxHealth;

            for (FPlayer player : playerHeal) {
                if (!player.isDead()) {
                    Factoid.getFactoidLog().write("Healing: " + player.getName());
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
        playerHeal = new ArrayList<FPlayer>();
        landHeal = new LandHeal();
        Factoid.getServer().createTask(landHeal, 20l, true);
    }

    // Must be running before PlayerListener
    public void onPlayerQuitMonitor(FPlayer player) {

        DummyLand land = player.getLastLand();

        // Notify for quit
        while (land instanceof Land) {
            notifyPlayers((Land)land, "ACTION.PLAYEREXIT", player);
            land = ((Land)land).getParent();
        }

        if (playerHeal.contains(player)) {
            playerHeal.remove(player);
        }
    }

    public boolean onPlayerLandChange(FPlayer player, Land lastLand, Land land, Point toLoc) {
        
    	DummyLand dummyLand;
        String value;

        if (lastLand != null) {

            if (!(land != null && lastLand.isDescendants(land))) {

                //Notify players for exit
                notifyPlayers(lastLand, "ACTION.PLAYEREXIT", player);

                // Message quit
                value = lastLand.getFlagNoInherit(FlagList.MESSAGE_QUIT.getFlagType()).getValueString();
                if (!value.isEmpty()) {
                    player.sendMessage(ChatStyle.GRAY + "[Factoid] (" + ChatStyle.GREEN + lastLand.getName() + ChatStyle.GRAY + "): " + ChatStyle.WHITE + value);
                }
            }

            /*for(String playername : lastLand.getPlayersInLand()){
             Factoid.getScoreboard().sendScoreboard(lastLand.getPlayersInLand(), Factoid.getThisPlugin().getServerCache().getPlayer(playername), lastLand.getName());
             }
             Factoid.getScoreboard().sendScoreboard(lastLand.getPlayersInLand(), player, lastLand.getName());*/
        }
        if (land != null) {
            dummyLand = land;

            if (!player.isAdminMod()) {
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
                        return false;
                    } else {
                        player.sendMessage(ChatStyle.GRAY + "[Factoid] " + Factoid.getLanguage().getMessage(message, land.getName()));
                        return true;
                    }
                }
            }

            if (!(lastLand != null && land.isDescendants(lastLand))) {

                //Notify players for Enter
                Land landTest = land;
                while (landTest != null && landTest != lastLand) {
                    notifyPlayers(landTest, "ACTION.PLAYERENTER", player);
                    landTest = landTest.getParent();
                }
                // Message join
                value = land.getFlagNoInherit(FlagList.MESSAGE_JOIN.getFlagType()).getValueString();
                if (!value.isEmpty()) {
                    player.sendMessage(ChatStyle.GRAY + "[Factoid] (" + ChatStyle.GREEN + land.getName() + ChatStyle.GRAY + "): " + ChatStyle.WHITE + value);
                }
            }


            /*for(String playername:land.getPlayersInLand()){
             Factoid.getScoreboard().sendScoreboard(land.getPlayersInLand(), Factoid.getThisPlugin().getServerCache().getPlayer(playername), land.getName());
             }
             Factoid.getScoreboard().sendScoreboard(land.getPlayersInLand(), player, land.getName());*/
        } else {
            dummyLand = Factoid.getLands().getOutsideArea(toLoc);
            // Factoid.getScoreboard().resetScoreboard(player);
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
        
        if (player.isAdminMod() 
        		&& dummyLand.checkPermissionAndInherit(player, permissionType) != permissionType.getDefaultValue()) {
        	player.setHealth(0);
        }
        
        return false;
    }

    /**
     * Check for banned players.
     *
     * @param land the land
     * @param pc the pc
     * @param message the message
     */
    public void checkForBannedPlayers(Land land, PlayerContainer pc, String message) {
    	
    	checkForBannedPlayers(land, pc, message, new ArrayList<FPlayer>());
    }

    /**
     * Check for banned players.
     *
     * @param land the land
     * @param pc the pc
     * @param message the message
     * @param kickPlayers the kicked players list
     */
    private void checkForBannedPlayers(Land land, PlayerContainer pc, String message, ArrayList<FPlayer> kickPlayers) {

    	FPlayer[] playersArray = land.getPlayersInLand().toArray(new FPlayer[0]); // Fix ConcurrentModificationException
    	
    	for (FPlayer players : playersArray) {
            if (pc.hasAccess(players)
                    && !land.isOwner(players)
                    && !players.isAdminMod()
                    && !players.hasPermission("factoid.bypassban")
                    && (land.checkPermissionAndInherit(players, PermissionList.LAND_ENTER.getPermissionType()) == false
                    || land.isBanned(players))
                    && !kickPlayers.contains(players)) {
                tpSpawn(players, land, message);
                kickPlayers.add(players);
            }
        }
    	
    	// check for children
    	for (Land children : land.getChildren()) {
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
    private void notifyPlayers(Land land, String message, FPlayer playerIn) {

        FPlayer player;
        
        for (PlayerContainerPlayer playerC : land.getPlayersNotify()) {
            
            player = playerC.getPlayer();
            
            if (player != null && player != playerIn
                    // Only adminmod can see vanish
                    && (!Factoid.getDependPlugin().getVanish().isVanished(playerIn) || player.isAdminMod())) {
                player.sendMessage(ChatStyle.GRAY + "[Factoid] " + Factoid.getLanguage().getMessage(
                        message, playerIn.getDisplayName(), land.getName() + ChatStyle.GRAY));
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
    private void tpSpawn(FPlayer player, Land land, String message) {

        player.teleport(player.getLocation().getWorld().getSpawnLocation());
        player.sendMessage(ChatStyle.GRAY + "[Factoid] " + Factoid.getLanguage().getMessage(message, land.getName()));
    }
}
