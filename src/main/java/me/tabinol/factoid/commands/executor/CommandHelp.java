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
import me.tabinol.factoid.commands.ChatPage;
import me.tabinol.factoid.commands.ListCommands;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import org.bukkit.command.CommandSender;


/**
 * The Class CommandHelp.
 */
public class CommandHelp extends CommandExec {

    /** The sender. */
    private final CommandSender sender;
    
    /** The command name. */
    private String commandName;

    /**
     * Instantiates a new command help.
     *
     * @param entity the entity
     * @throws FactoidCommandException the factoid command exception
     */
    public CommandHelp(CommandEntities entity) throws FactoidCommandException {

        super(entity, true, false);
        sender = entity.sender;
    }

    // Call directly the Help without verification CommandName is UPERCASE
    /**
     * Instantiates a new command help.
     *
     * @param sender the sender
     * @param commandName the command name
     * @throws FactoidCommandException the factoid command exception
     */
    public CommandHelp(CommandSender sender, String commandName) throws FactoidCommandException {

        super(null, true, false);
        this.sender = sender;
        this.commandName = commandName;
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws FactoidCommandException {

        if (commandName == null) {
            String arg = entity.argList.getNext();

            if (arg == null) {
                commandName = "GENERAL";
            } else {
                // Will throw an exception if the command name is invalid
                try {
                    ListCommands cl = ListCommands.valueOf(arg.toUpperCase());
                    commandName = cl.name();
                } catch (IllegalArgumentException ex) {
                    commandName = "GENERAL";
                }
            }
        }
        
        showHelp();
    }

    /**
     * Show help.
     *
     * @throws FactoidCommandException the factoid command exception
     */
    private void showHelp() throws FactoidCommandException {

        String help = Factoid.getThisPlugin().iLanguage().getHelp(commandName);
        
        // If there is no help for this command
        if(help == null) {
            throw new FactoidCommandException("Command with no help", entity.player, "HELP.NOHELP");
        }
        
        if (commandName.equals("GENERAL")) {
            new ChatPage("HELP.LISTSTART", help, sender, null).getPage(1);
        } else {
            sender.sendMessage(help);
        }
    }

}
