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
import me.tabinol.factoid.commands.CommandEntities;
import me.tabinol.factoid.commands.CommandExec;
import me.tabinol.factoid.commands.InfoCommand;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.types.Type;
import me.tabinol.factoid.utilities.ChatStyle;

@InfoCommand(name="type", forceParameter=true)
public class CommandType extends CommandExec {
	
	public CommandType(CommandEntities entity) throws FactoidCommandException {

        super(entity);
    }

    @Override
    public void commandExecute() throws FactoidCommandException {

        checkSelections(true, null);
        checkPermission(true, false, null, null);
        
        String curArg = entity.argList.getNext();

        if (curArg.equalsIgnoreCase("list")) {
            
            StringBuilder stList = new StringBuilder();
            for (Type type : Factoid.getTypes().getTypes()) {
            	if (stList.length() != 0) {
            		stList.append(" ");
                }
                stList.append(ChatStyle.WHITE).append(type.getName());
            stList.append(Config.NEWLINE);
            }
            new ChatPage("COMMAND.TYPES.LISTSTART", stList.toString(), entity.sender, null).getPage(1);
        
        } else if(curArg.equals("remove")) {
        	
        	land.setType(null);
            entity.sender.sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.TYPES.REMOVEISDONE", land.getName()));
            Factoid.getFactoidLog().write("Land type removed: " + land.getName());
        
        } else { // Type change 
        	
        	Type type = Factoid.getTypes().getType(curArg);
        	
        	if(type == null) {
        		throw new FactoidCommandException("Land Types", entity.sender, "COMMAND.TYPES.INVALID");
        	}
        	
        	land.setType(type);
            entity.sender.sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.TYPES.ISDONE", type.getName(), land.getName()));
            Factoid.getFactoidLog().write("Land type: " + type.getName() + " for land: " + land.getName());
        }
    }
}
