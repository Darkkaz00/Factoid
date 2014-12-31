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
package me.tabinol.factoid.commands.executor;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ArgList;
import me.tabinol.factoid.commands.ListCommands;
import me.tabinol.factoid.config.players.PlayerConfEntry;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


// Contains general information for commandExecutor
/**
 * The Class CommandEntities.
 */
public class CommandEntities {
    
    /** The command. */
    protected final ListCommands command;
    
    /** The sender. */
    protected final CommandSender sender;
    
    /** The arg list. */
    protected final ArgList argList;
    
    /** The player. */
    protected final Player player;
    
    /** The player name. */
    protected final String playerName;
    
    /** The player conf. */
    protected final PlayerConfEntry playerConf;

    /**
     * Instantiates a new command entities.
     *
     * @param command the command
     * @param sender the sender
     * @param argList the arg list
     */
    public CommandEntities(ListCommands command, CommandSender sender, ArgList argList) {
        
        this.command = command;
        this.sender = sender;
        this.argList = argList;

        if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            player = null;
        }
        
        playerName = sender.getName();
        playerConf = Factoid.getThisPlugin().iPlayerConf().get(sender);
    }

}
