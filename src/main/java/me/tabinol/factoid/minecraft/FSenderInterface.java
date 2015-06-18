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

import me.tabinol.factoid.commands.ChatPage;
import me.tabinol.factoid.commands.ConfirmEntry;
import me.tabinol.factoid.lands.DummyLand;
import me.tabinol.factoid.lands.areas.Point;
import me.tabinol.factoid.playercontainer.PlayerContainerPlayer;
import me.tabinol.factoid.selection.PlayerSelection;

/**
 * This interface is only to get easily methods from FPlayer
 * @author Tabinol
 *
 */
public interface FSenderInterface {

	public PlayerContainerPlayer getPlayerContainer();
    
    /**
     * Gets the selection.
     *
     * @return the selection
     */
    public PlayerSelection getSelection();

    public boolean isAdminMod();

    /**
     * Sets the admin mod.
     *
     * @param value the new admin mod
     */
    public void setAdminMod(boolean value);

    /**
     * Gets the confirm.
     *
     * @return the confirm
     */
    public ConfirmEntry getConfirm();

    /**
     * Sets the confirm.
     *
     * @param entry the new confirm
     */
    public void setConfirm(ConfirmEntry entry);

    /**
     * Gets the chat page.
     *
     * @return the chat page
     */
    public ChatPage getChatPage();

    /**
     * Sets the chat page.
     *
     * @param page the new chat page
     */
    public void setChatPage(ChatPage page);

	public long getLastMoveUpdate();

    /**
     * Sets the last move update.
     *
     * @param lastMove the new last move update
     */
    public void setLastMoveUpdate(Long lastMove);

	public DummyLand getLastLand();

    /**
     * Sets the last land.
     *
     * @param land the new last land
     */
    public void setLastLand(DummyLand land);

	public Point getLastLoc();

    /**
     * Sets the last loc.
     *
     * @param loc the new last loc
     */
    public void setLastLoc(Point loc);

    /**
     * Checks for tp cancel.
     *
     * @return true, if successful
     */
    public boolean hasTpCancel();

    /**
     * Sets the tp cancel.
     *
     * @param tpCancel the new tp cancel
     */
    public void setTpCancel(boolean tpCancel);

    /**
     * Sets the auto cancel select.
     *
     * @param value the new auto cancel select
     */
    public void setAutoCancelSelect(boolean value);

	public abstract void sendMessage(String msg);
	public abstract void sendMessage(String[] msg);
	public abstract boolean hasPermission(String perm);
	public abstract String getName();
}
