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

import java.util.LinkedList;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ChatPage;
import me.tabinol.factoid.commands.CommandEntities;
import me.tabinol.factoid.commands.CommandExec;
import me.tabinol.factoid.commands.InfoCommand;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.DummyLand;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.parameters.FlagType;
import me.tabinol.factoid.parameters.LandFlag;
import me.tabinol.factoid.utilities.ChatStyle;


/**
 * The Class CommandFlag.
 */
@InfoCommand(name="flag", forceParameter=true)
public class CommandFlag extends CommandExec {
	
	private LinkedList<DummyLand> precDL; // Listed Precedent lands (no duplicates)
	private StringBuilder stList;

    /**
     * Instantiates a new command flag.
     *
     * @param entity the entity
     * @throws FactoidCommandException the factoid command exception
     */
    public CommandFlag(CommandEntities entity) throws FactoidCommandException {

        super(entity);
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws FactoidCommandException {

        checkSelections(true, null);
                String curArg = entity.argList.getNext();

        /*
        if (entity.argList.length() < 2) {

            entity.sender.sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.JOINMODE"));
            Factoid.getFactoidLog().write("PlayerSetFlagUI for " + entity.playerName);
            entity.sender.sendMessage(ChatStyle.DARK_GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.HINT"));
            CuboidArea area = Factoid.getLands().getCuboidArea(entity.sender.getLocation());
            LandSetFlag setting = new LandSetFlag(entity.player, area);
            entity.sender.setSetFlagUI(setting);
            
                    
        } else 
        */ 
        
        if (curArg.equalsIgnoreCase("set")) {

            // Permission check is on getFlagFromArg
            
            LandFlag landFlag = entity.argList.getFlagFromArg(entity.sender.isAdminMod(), land.isOwner(entity.player));
            
            if(!landFlag.getFlagType().isRegistered()) {
            	throw new FactoidCommandException("Flag not registered", entity.sender, "COMMAND.FLAGS.FLAGNULL");
            }
            
            ((Land)land).addFlag(landFlag);
            entity.sender.sendMessage(ChatStyle.YELLOW + "[Factoid] " + 
            Factoid.getLanguage().getMessage("COMMAND.FLAGS.ISDONE", landFlag.getFlagType().toString(), 
                    landFlag.getValue().getValuePrint() + ChatStyle.YELLOW));
            Factoid.getFactoidLog().write("Flag set: " + landFlag.getFlagType().toString() + ", value: " + 
                    landFlag.getValue().getValue().toString());

        } else if (curArg.equalsIgnoreCase("unset")) {
        
            FlagType flagType = entity.argList.getFlagTypeFromArg(entity.sender.isAdminMod(), land.isOwner(entity.player));
            if (!land.removeFlag(flagType)) {
                throw new FactoidCommandException("Flags", entity.sender, "COMMAND.FLAGS.REMOVENOTEXIST");
            }
            entity.sender.sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.FLAGS.REMOVEISDONE", flagType.toString()));
            Factoid.getFactoidLog().write("Flag unset: " + flagType.toString());
        
        } else if (curArg.equalsIgnoreCase("list")) {

        	precDL = new LinkedList<DummyLand>();
        	stList = new StringBuilder();
        	
        	// For the actual land
        	importDisplayFlagsFrom(land, false);
        	
        	// For default Type
        	if(land.getType() != null) {
            	stList.append(ChatStyle.DARK_GRAY + Factoid.getLanguage().getMessage("GENERAL.FROMDEFAULTTYPE",
        				land.getType().getName())).append(Config.NEWLINE);
            	importDisplayFlagsFrom(Factoid.getLands().getDefaultConf(land.getType()), false);
        	}
        	
        	// For parent (if exist)
        	Land parLand = land;
        	while((parLand = parLand.getParent()) != null) {
        		stList.append(ChatStyle.DARK_GRAY + Factoid.getLanguage().getMessage("GENERAL.FROMPARENT",
        				ChatStyle.GREEN + parLand.getName() + ChatStyle.DARK_GRAY)).append(Config.NEWLINE);
        		importDisplayFlagsFrom(parLand, true);
        	}
        	
        	// For world
        	stList.append(ChatStyle.DARK_GRAY + Factoid.getLanguage().getMessage("GENERAL.FROMWORLD",
    				land.getWorldName())).append(Config.NEWLINE);
        	importDisplayFlagsFrom(Factoid.getLands().getOutsideArea(land.getWorldName()), true);
                
            new ChatPage("COMMAND.FLAGS.LISTSTART", stList.toString(), entity.sender, land.getName()).getPage(1);

        } else {
            throw new FactoidCommandException("Missing information command", entity.sender, "GENERAL.MISSINGINFO");
        }
    }
    
    private void importDisplayFlagsFrom(DummyLand land, boolean onlyInherit) {
    	
    	StringBuilder stSubList = new StringBuilder();
    	for (LandFlag flag : land.getFlags()) {
            if (stSubList.length() != 0 && !stSubList.toString().endsWith(" ")) {
                stSubList.append(" ");
            }
            if((!onlyInherit || flag.isHeritable()) && !flagInList(flag)) {
                stSubList.append(flag.getFlagType().getPrint()).append(":").append(flag.getValue().getValuePrint());
            }
        }
        
    	if(stSubList.length() > 0) {
        	stList.append(stSubList).append(Config.NEWLINE);
        	precDL.add(land);
    	}
    }
    
    private boolean flagInList(LandFlag flag) {
    	
    	for(DummyLand listLand : precDL) {
    		for(LandFlag listFlag : listLand.getFlags()) {
    			if(flag.getFlagType() == listFlag.getFlagType()) {
    				return true;
    			}
    		}
    	}
    	
    	return false;
    }
}
