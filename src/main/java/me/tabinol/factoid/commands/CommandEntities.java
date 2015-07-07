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
package me.tabinol.factoid.commands;

import me.tabinol.factoid.minecraft.FPlayer;
import me.tabinol.factoid.minecraft.FSender;

/**
 * Contains general information for commandExecutor.
 */
public class CommandEntities {
    
    /** The main command. */
    public final MainCommand mainCommand;
	
	/** The command. */
    public final InfoCommand infoCommand;
    
    /** The sender. */
    public final FSender sender;
    
    /** The arg list. */
    public final ArgList argList;
    
    /** The player.getFSender(). */
    public final FPlayer player;
    
    /** The player name. */
    public final String playerName;
    
    /** The on command. */
    public final OnCommand onCommand;

    /**
     * Instantiates a new command entities.
     *
     * @param mainCommand the main command
     * @param infoCommand the info command
     * @param sender the sender
     * @param argList the arg list
     * @param onCommand the on command
     */
    public CommandEntities(MainCommand mainCommand, InfoCommand infoCommand, 
    		FSender sender, ArgList argList, OnCommand onCommand) {
        
        this.mainCommand = mainCommand;
    	this.infoCommand = infoCommand;
        this.sender = (FSender) sender;
        this.argList = argList;
        this.onCommand = onCommand;

        player = sender.getFPlayer();
        
        playerName = sender.getName();
    }
}
