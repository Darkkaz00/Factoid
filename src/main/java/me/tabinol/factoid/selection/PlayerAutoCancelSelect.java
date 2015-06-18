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
package me.tabinol.factoid.selection;

import java.util.logging.Level;
import java.util.logging.Logger;

import me.tabinol.factoid.commands.executor.CommandCancel;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.minecraft.FSender;
import me.tabinol.factoid.utilities.FactoidRunnable;


// Auto cancel selection
/**
 * The Class PlayerAutoCancelSelect.
 */
public class PlayerAutoCancelSelect extends FactoidRunnable {

    /** The entry. */
    private final FSender entry;

    /**
     * Instantiates a new player auto cancel select.
     *
     * @param entry the entry
     */
    public PlayerAutoCancelSelect(FSender entry) {

        super();
        this.entry = entry;
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        
        setOneTimeDone();
        try {            
            new CommandCancel(entry, true).commandExecute();
        } catch (FactoidCommandException ex) {
            Logger.getLogger(PlayerAutoCancelSelect.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
