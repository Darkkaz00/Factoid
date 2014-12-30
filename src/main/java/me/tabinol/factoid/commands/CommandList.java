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


/**
 * The Enum CommandList.
 */
public enum CommandList {

    /** The reload. */
    RELOAD,
    
    /** The select. */
    SELECT,
    
    /** The expand. */
    EXPAND,
    
    /** The create. */
    CREATE,
    
    /** The area. */
    AREA,
    
    /** The owner. */
    OWNER,
    
    /** The flag. */
    FLAG,
    
    /** The permission. */
    PERMISSION,
    
    /** The resident. */
    RESIDENT,
    
    /** The ban. */
    BAN,
    
    /** The remove. */
    REMOVE,
    
    /** The confirm. */
    CONFIRM,
    
    /** The cancel. */
    CANCEL,
    
    /** The info. */
    INFO,
    
    /** The adminmod. */
    ADMINMOD,
    
    /** The page. */
    PAGE,
    
    /** The default. */
    DEFAULT,
    
    /** The priority. */
    PRIORITY,
    
    /** The approve. */
    APPROVE,
    
    /** The rename. */
    RENAME,
    
    /** The help. */
    HELP,
    
    /** The kick. */
    KICK,
    
    /** The who. */
    WHO,
    
    /** The notify. */
    NOTIFY,
    
    /** The money. */
    MONEY,
    
    /** The list. */
    LIST,
    
    /** Set the land spawn point */
    SETSPAWN,
    
    /** Teleport to a land */
    TP,
    
    /** Make the land for sale */
    SALE,
    
    /** Make the land for rent */
    RENT,
    
    /** Set/Unset the parent */
    PARENT,
    
    /** Type **/
    TYPE;

    // If a command has a second name
    /**
     * The Enum SecondName.
     */
    public enum SecondName {

        /** The current. */
        CURRENT(INFO),
        
        /** The here. */
        HERE(INFO);

        /** The main command. */
        public final CommandList mainCommand;

        /**
         * Instantiates a new second name.
         *
         * @param mainCommand the main command
         */
        SecondName(CommandList mainCommand) {

            this.mainCommand = mainCommand;
        }
    }

}
