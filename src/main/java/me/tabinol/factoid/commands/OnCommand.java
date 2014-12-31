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

import java.lang.reflect.InvocationTargetException;

import me.tabinol.factoid.commands.executor.CommandEntities;
import me.tabinol.factoid.commands.executor.CommandExec;
import me.tabinol.factoid.commands.executor.CommandHelp;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoidapi.utilities.StringChanges;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

import java.util.logging.Level;
import java.util.logging.Logger;

import me.tabinol.factoid.Factoid;
import static me.tabinol.factoid.commands.ListCommands.valueOf;


/**
 * The Class OnCommand.
 */
public class OnCommand extends Thread implements CommandExecutor {

    /**
     * Instantiates a new on command.
     */
    public OnCommand() {

    }

    /* (non-Javadoc)
     * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {

        // Others commands then /factoid, /claim and /fd will not be send.
        
        ArgList argList = new ArgList(arg, sender);
            try {
                // Check the command to send
                getCommand(sender, argList);
                return true;
                // If error on command, send the message to the player
            } catch (FactoidCommandException ex) {
                return true;
            }
    }

    // Get command from args
    /**
     * Gets the command.
     *
     * @param sender the sender
     * @param argList the arg list
     * @throws FactoidCommandException the factoid command exception
     */
    public void getCommand(CommandSender sender, ArgList argList) throws FactoidCommandException {

        try {
            ListCommands cl;

            String command = argList.getNext();

            // Show help if there is no arguments
            if (command == null) {
                new CommandHelp(sender, "GENERAL").commandExecute();
                return;
            }

            // take the name
            cl = getCommandValue(command, sender);
            
            // Remove page from memory if needed
            if(cl != ListCommands.PAGE) {
                Factoid.getThisPlugin().iPlayerConf().get(sender).setChatPage(null);
            }

            // Do the command (get the class name from the CommandName)
            Class<?> commandClass = Class.forName("me.tabinol.factoid.commands.executor.Command"
                    + StringChanges.FirstUpperThenLower(cl.name()));
            CommandExec ce = (CommandExec) commandClass.getConstructor(CommandEntities.class)
                    .newInstance(new CommandEntities(cl, sender, argList));
            if (ce.isExecutable()) {
                ce.commandExecute();
            }

            // a huge number of Exception to catch!
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(OnCommand.class.getName()).log(Level.SEVERE, "General Error on Command class find", ex);
            throw new FactoidCommandException("General Error on Command class find", sender, "GENERAL.ERROR");
        } catch (SecurityException ex) {
            Logger.getLogger(OnCommand.class.getName()).log(Level.SEVERE, "General Error on Command class find", ex);
            throw new FactoidCommandException("General Error on Command class find", sender, "GENERAL.ERROR");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OnCommand.class.getName()).log(Level.SEVERE, "General Error on Command class find", ex);
            throw new FactoidCommandException("General Error on Command class find", sender, "GENERAL.ERROR");
        } catch (InstantiationException ex) {
            Logger.getLogger(OnCommand.class.getName()).log(Level.SEVERE, "General Error on Command class find", ex);
            throw new FactoidCommandException("General Error on Command class find", sender, "GENERAL.ERROR");
        } catch (IllegalAccessException ex) {
            Logger.getLogger(OnCommand.class.getName()).log(Level.SEVERE, "General Error on Command class find", ex);
            throw new FactoidCommandException("General Error on Command class find", sender, "GENERAL.ERROR");
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(OnCommand.class.getName()).log(Level.SEVERE, "General Error on Command class find", ex);
            throw new FactoidCommandException("General Error on Command class find", sender, "GENERAL.ERROR");
        } catch (InvocationTargetException ex) {
            Logger.getLogger(OnCommand.class.getName()).log(Level.SEVERE, "General Error on Command class find", ex);
            throw new FactoidCommandException("General Error on Command class find", sender, "GENERAL.ERROR");
        }
    }

    // Get the command value from command list
    /**
     * Gets the command value.
     *
     * @param command the command
     * @param sender the sender
     * @return the command value
     * @throws FactoidCommandException the factoid command exception
     */
    private ListCommands getCommandValue(String command, CommandSender sender) throws FactoidCommandException {

        ListCommands cl;

        try {
            cl = valueOf(command.toUpperCase());
        } catch (IllegalArgumentException e) {

            // Check if the second name works
            ListCommands.SecondName sn = null;
            try {
                sn = ListCommands.SecondName.valueOf(command.toUpperCase());
            } catch (IllegalArgumentException e2) {

                // The command does not exist
                throw new FactoidCommandException("Command not existing", sender, "COMMAND.NOTEXIST");
            }
            cl = sn.mainCommand;
        }

        return cl;

    }
}
