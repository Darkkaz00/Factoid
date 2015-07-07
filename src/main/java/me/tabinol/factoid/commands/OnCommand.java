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
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.tabinol.factoid.commands.executor.CommandHelp;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.minecraft.FSender;

import org.reflections.Reflections;



/**
 * The Class OnCommand.
 */
public class OnCommand {

	private final Map<MainCommand, Map<String, Class<?>>> commands = new EnumMap<MainCommand, Map<String, Class<?>>>(MainCommand.class);
	
	/**
     * Instantiates a new on command.
     */
    public OnCommand() {
    	
    	// Create Command list
    	for(MainCommand mainCommandList : MainCommand.values()) {
    		commands.put(mainCommandList, new TreeMap<String, Class<?>>());
    	}
    	
    	
    	// Gets all annotations
    	Reflections reflections = new Reflections("me.tabinol.factoid.commands.executor");
    	Set<Class<?>> classCommands = 
    		    reflections.getTypesAnnotatedWith(InfoCommand.class);
    	
		for(Class<?> presentClass : classCommands) {

			// Store command information
        	InfoCommand infoCommand = presentClass.getAnnotation(InfoCommand.class);
        	for(MainCommand command : infoCommand.mainCommand()) {
            	Map<String, Class<?>> subCommand = commands.get(command);
            	subCommand.put(infoCommand.name().toLowerCase(), presentClass);
            	for(String alias : infoCommand.aliases()) {
            		subCommand.put(alias.toLowerCase(), presentClass);
           		}
        	}
        }
    }

    public boolean onCommand(FSender sender, String cmd, String[] arg) {

        // Others commands then /factoid, /claim and /fd will not be send.
        
    	ArgList argList = new ArgList(arg, sender);
            try {
                // Check the command to send
                getCommand(sender, cmd, argList);
                return true;
                // If error on command, send the message to the player
            } catch (FactoidCommandException ex) {
                return true;
            }
    }

    // Get command from args
    private void getCommand(FSender sender, String cmd, ArgList argList) throws FactoidCommandException {

        try {
            MainCommand mainCommand = MainCommand.valueOf(cmd.toUpperCase());
            
            // Show help if there is no arguments
            if (argList.isLast()) {
                new CommandHelp(this, (FSender) sender, mainCommand, "GENERAL").commandExecute();
                return;
            }

            String command = argList.getNext().toLowerCase();

            // take the name
            Class<?> cv = commands.get(mainCommand).get(command.toLowerCase());
            
            // The command does not exist
            if(cv == null) {
            	throw new FactoidCommandException("Command not existing", sender, "COMMAND.NOTEXIST", mainCommand.name());
            }
            
            // Remove page from memory if needed
            if(cv != commands.get(MainCommand.FACTOID).get("page")) {
                sender.setChatPage(null);
            }

            // Do the command
            InfoCommand ci = cv.getAnnotation(InfoCommand.class);
            CommandExec ce = (CommandExec) cv.getConstructor(CommandEntities.class)
                    .newInstance(new CommandEntities(mainCommand, ci, sender, argList, this));
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
    
    public InfoCommand getInfoCommand(MainCommand mainCommand, String command) {
    	
    	Class<?> infoClass = commands.get(mainCommand).get(command.toLowerCase());
    	
    	if(infoClass == null) {
    		return null;
    	}
    	
    	return infoClass.getAnnotation(InfoCommand.class);
    }
}
