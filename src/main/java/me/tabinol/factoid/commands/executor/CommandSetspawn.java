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

import org.bukkit.ChatColor;
import org.bukkit.Location;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.parameters.FlagList;
import me.tabinol.factoid.parameters.LandFlag;
import me.tabinol.factoid.utilities.StringChanges;

public class CommandSetspawn extends CommandExec {

    /**
     * Instantiates a new command set spawn.
     *
     * @param entity the entity
     * @throws FactoidCommandException the factoid command exception
     */
    public CommandSetspawn(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, false);
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.commands.executor.CommandInterface#commandExecute()
     */
	@Override
	public void commandExecute() throws FactoidCommandException {

        checkSelections(true, null);
        checkPermission(true, true, null, null);
        
        Location loc = entity.player.getLocation();
        
        // If the player is not inside the land
        if(!land.isLocationInside(loc)) {
        	throw new FactoidCommandException("On land tp create", entity.player, "COMMAND.TP.OUTSIDE");
        }
        
        // put player position to String
        String posStr = StringChanges.locationToString(loc);
        
        // Set flag
        LandFlag flag = new LandFlag(FlagList.SPAWN.getFlagType(), posStr, true);
        ((Land) land).addFlag(flag);
        
        entity.player.sendMessage(ChatColor.GREEN + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.TP.CREATED"));
	}

}
