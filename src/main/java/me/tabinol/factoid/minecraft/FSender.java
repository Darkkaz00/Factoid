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

import java.util.UUID;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ChatPage;
import me.tabinol.factoid.commands.ConfirmEntry;
import me.tabinol.factoid.lands.DummyLand;
import me.tabinol.factoid.lands.areas.Point;
import me.tabinol.factoid.playercontainer.PlayerContainerPlayer;
import me.tabinol.factoid.selection.PlayerAutoCancelSelect;
import me.tabinol.factoid.selection.PlayerSelection;

/**
 * This class represent a console sender.
 * This class replace also the old abandoned class PlayerConfEntry (with FPlayer)
 * @author Tabinol
 *
 */
public abstract class FSender implements FSenderInterface, Comparable<FSender> {
	
    /**************************************************************************
     * Settings for player and Sender
     *************************************************************************/
	
	/** The player selection. */
    private final PlayerSelection playerSelection; // Player Lands, areas and visual selections
    
    /** The admin mod. */
    private boolean adminMod = false; // If the player is in Admin Mod
    
    /** The confirm. */
    private ConfirmEntry confirm = null; // "/factoid confirm" command
    
    /** The chat page. */
    private ChatPage chatPage = null; // pages for "/factoid page" command
    
    /** The last move update. */
    private long lastMoveUpdate = 0; // Time of lastupdate for PlayerEvents
    
    /** The last land. */
    private DummyLand lastLand = null; // Last Land for player
    
    /** The last loc. */
    private Point lastLoc = null; // Present location
    
    /** The tp cancel. */
    private boolean tpCancel = false; // If the player has a teleportation cacelled
    
    /** The cancel select. */
    private PlayerAutoCancelSelect cancelSelect = null; // Auto cancel selection system
    
    /** The pcp. */
    private PlayerContainerPlayer pcp; // PlayerContainerPlayer for this player
	
    /**************************************************************************
     * Constructor
     *************************************************************************/
    
    /**
     * Constructor for a player
     * @param uuid
     */
    protected FSender(UUID uuid) {

        playerSelection = new PlayerSelection(this);
        pcp = new PlayerContainerPlayer(uuid);
    }
    
    /**
     * Constructor for the console
     */
    protected FSender() {
    	
        playerSelection = null;
        pcp = null;
    }
    
    /**************************************************************************
     * Methods
     *************************************************************************/
    
	@Override
    public int compareTo(FSender arg0) {
		
		return getName().compareTo(getName());
    }
	
	public PlayerContainerPlayer getPlayerContainer() {
        
        return pcp;
    }
    
    /**
     * Gets the selection.
     *
     * @return the selection
     */
    public PlayerSelection getSelection() {

        return playerSelection;
    }

	public boolean isAdminMod() {

        // Security for adminmod
        if (adminMod == true && !hasPermission("factoid.adminmod")) {
            adminMod = false;
            return false;
        }

        return adminMod;
    }

    /**
     * Sets the admin mod.
     *
     * @param value the new admin mod
     */
    public void setAdminMod(boolean value) {

        adminMod = value;
    }

    /**
     * Gets the confirm.
     *
     * @return the confirm
     */
    public ConfirmEntry getConfirm() {

        return confirm;
    }

    /**
     * Sets the confirm.
     *
     * @param entry the new confirm
     */
    public void setConfirm(ConfirmEntry entry) {

        confirm = entry;
    }

    /**
     * Gets the chat page.
     *
     * @return the chat page
     */
    public ChatPage getChatPage() {

        return chatPage;
    }

    /**
     * Sets the chat page.
     *
     * @param page the new chat page
     */
    public void setChatPage(ChatPage page) {

        chatPage = page;
    }

	public long getLastMoveUpdate() {

        return lastMoveUpdate;
    }

    /**
     * Sets the last move update.
     *
     * @param lastMove the new last move update
     */
    public void setLastMoveUpdate(Long lastMove) {

        lastMoveUpdate = lastMove;
    }

	public DummyLand getLastLand() {

        return lastLand;
    }

    /**
     * Sets the last land.
     *
     * @param land the new last land
     */
    public void setLastLand(DummyLand land) {

        lastLand = land;
    }

	public Point getLastLoc() {

        return lastLoc;
    }

    /**
     * Sets the last loc.
     *
     * @param loc the new last loc
     */
    public void setLastLoc(Point loc) {

        lastLoc = loc;
    }

    /**
     * Checks for tp cancel.
     *
     * @return true, if successful
     */
    public boolean hasTpCancel() {

        return tpCancel;
    }

    /**
     * Sets the tp cancel.
     *
     * @param tpCancel the new tp cancel
     */
    public void setTpCancel(boolean tpCancel) {

        this.tpCancel = tpCancel;
    }

    // Set auto cancel select
    /**
     * Sets the auto cancel select.
     *
     * @param value the new auto cancel select
     */
    public void setAutoCancelSelect(boolean value) {

        Long timeTick = Factoid.getConf().getSelectAutoCancel();

        if (timeTick == 0) {
            return;
        }

        if (cancelSelect == null && value == true) {
            cancelSelect = new PlayerAutoCancelSelect(this);
        }

        if (cancelSelect == null) {
            return;
        }

        if (value == true) {

            // Schedule task
            cancelSelect.runLater(timeTick, false);
        } else {

            // Stop!
            cancelSelect.stopNextRun();
        }
    }
}
