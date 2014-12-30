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
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoidapi.FactoidAPI;
import me.tabinol.factoidapi.lands.types.IType;

import org.bukkit.ChatColor;

public class CommandType extends CommandExec {
	
	public CommandType(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, true);
    }

    @Override
    public void commandExecute() throws FactoidCommandException {

        checkSelections(true, null);
        checkPermission(true, false, null, null);
        
        String curArg = entity.argList.getNext();

        if (curArg.equalsIgnoreCase("list")) {
            
            StringBuilder stList = new StringBuilder();
            for (IType type : FactoidAPI.iTypes().getTypes()) {
            	if (stList.length() != 0) {
            		stList.append(" ");
                }
                stList.append(ChatColor.WHITE).append(type.getName());
            stList.append(Config.NEWLINE);
            }
            new ChatPage("COMMAND.TYPES.LISTSTART", stList.toString(), entity.player, null).getPage(1);
        
        } else if(curArg.equals("remove")) {
        	
        	land.setType(null);
            entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.TYPES.REMOVEISDONE", land.getName()));
            Factoid.getThisPlugin().iLog().write("Land type removed: " + land.getName());
        
        } else { // Type change 
        	
        	IType type = FactoidAPI.iTypes().getType(curArg);
        	
        	if(type == null) {
        		throw new FactoidCommandException("Land Types", entity.player, "COMMAND.TYPES.INVALID");
        	}
        	
        	land.setType(type);
            entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.TYPES.ISDONE", type.getName(), land.getName()));
            Factoid.getThisPlugin().iLog().write("Land type: " + type.getName() + " for land: " + land.getName());
        }
    }
}
