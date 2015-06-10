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

import org.bukkit.ChatStyle;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.CommandEntities;
import me.tabinol.factoid.commands.CommandExec;
import me.tabinol.factoid.commands.InfoCommand;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.collisions.Collisions.LandAction;

@InfoCommand(name="parent", forceParameter=true)
public class CommandParent extends CommandExec {
	
	public CommandParent(CommandEntities entity) throws FactoidCommandException {
		
		super(entity);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void commandExecute() throws FactoidCommandException {

        checkSelections(true, null);
        checkPermission(true, true, null, null);
    	
        String curArg = entity.argList.getNext();
        Land parent = null;
        
        if(!curArg.equalsIgnoreCase("unset")) {
        	parent = Factoid.getLands().getLand(curArg);
        	
            // Check if the parent exist
        	if (parent == null) {
        		throw new FactoidCommandException("CommandParent", entity.player, "COMMAND.PARENT.INVALID");
        	}
        	
        	// Check if the land is a children
        	if(land.isDescendants(parent)) {
        		throw new FactoidCommandException("CommandParent", entity.player, "COMMAND.PARENT.NOTCHILD");
        	}
        }
        
        // Check for collision
        if (checkCollision(land.getName(), land, null, LandAction.LAND_PARENT, 0, null, parent, 
        		land.getOwner(), 0, true)) {
            return;
        }
        
        // Set parent
        land.setParent(parent);
        if(parent == null) {
        	entity.player.sendMessage(ChatStyle.GREEN + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.PARENT.REMOVEDONE"));
        	Factoid.getThisPlugin().iLog().write(entity.playerName + " has set land " + land.getName() + " to no parent ");
        } else {
        	entity.player.sendMessage(ChatStyle.GREEN + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.PARENT.DONE", parent.getName()));
        	Factoid.getThisPlugin().iLog().write(entity.playerName + " has set land " + land.getName() + " to parent " + parent.getName());
        }
    }
}
