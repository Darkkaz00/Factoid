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

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.config.players.PlayerConfEntry;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


/**
 * Contains general information for commandExecutor
 */
public class CommandEntities {
    
    public final MainCommand mainCommand;
	
	/** The command. */
    public final InfoCommand infoCommand;
    
    /** The sender. */
    public final CommandSender sender;
    
    /** The arg list. */
    public final ArgList argList;
    
    /** The player. */
    public final Player player;
    
    /** The player name. */
    public final String playerName;
    
    /** The player conf. */
    public final PlayerConfEntry playerConf;
    
    public final OnCommand onCommand;

    /**
     * Instantiates a new command entities.
     *
     * @param mainCommand the main command
     * @param commandValue the command value
     * @param sender the sender
     * @param argList the arg list
     */
    public CommandEntities(MainCommand mainCommand, InfoCommand infoCommand, 
    		CommandSender sender, ArgList argList, OnCommand onCommand) {
        
        this.mainCommand = mainCommand;
    	this.infoCommand = infoCommand;
        this.sender = sender;
        this.argList = argList;
        this.onCommand = onCommand;

        if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            player = null;
        }
        
        playerName = sender.getName();
        playerConf = Factoid.getThisPlugin().iPlayerConf().get(sender);
    }
    
    

}
