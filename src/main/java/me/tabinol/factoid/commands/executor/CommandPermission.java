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
import me.tabinol.factoid.commands.CommandThreadExec;
import me.tabinol.factoid.commands.InfoCommand;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.Lands;
import me.tabinol.factoid.parameters.PermissionList;
import me.tabinol.factoid.playerscache.PlayerCacheEntry;
import me.tabinol.factoidapi.FactoidAPI;
import me.tabinol.factoidapi.lands.IDummyLand;
import me.tabinol.factoidapi.lands.ILand;
import me.tabinol.factoidapi.parameters.IPermission;
import me.tabinol.factoidapi.parameters.IPermissionType;
import me.tabinol.factoidapi.playercontainer.IPlayerContainer;

import org.bukkit.ChatStyle;


/**
 * The Class CommandPermission.
 */
@InfoCommand(name="permission", forceParameter=true)
public class CommandPermission extends CommandThreadExec {

	private LinkedList<IDummyLand> precDL; // Listed Precedent lands (no duplicates)
	private StringBuilder stList;

	private String fonction;

	/**
     * Instantiates a new command permission.
     *
     * @param entity the entity
     * @throws FactoidCommandException the factoid command exception
     */
    public CommandPermission(CommandEntities entity) throws FactoidCommandException {

        super(entity);
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws FactoidCommandException {

        checkSelections(true, null);

        fonction = entity.argList.getNext();
        
        if (fonction.equalsIgnoreCase("set")) {

        	pc = entity.argList.getPlayerContainerFromArg(land, null);
        	
            Factoid.getThisPlugin().iPlayersCache().getUUIDWithNames(this, pc);

        } else if (fonction.equalsIgnoreCase("unset")) {

            pc = entity.argList.getPlayerContainerFromArg(land, null);
            Factoid.getThisPlugin().iPlayersCache().getUUIDWithNames(this, pc);

        } else if (fonction.equalsIgnoreCase("list")) {

        	precDL = new LinkedList<IDummyLand>();
        	stList = new StringBuilder();

        	// For the actual land
        	importDisplayPermsFrom(land, false);
        	
        	// For default Type
        	if(land.getType() != null) {
            	stList.append(ChatStyle.DARK_GRAY + Factoid.getThisPlugin().iLanguage().getMessage("GENERAL.FROMDEFAULTTYPE",
        				land.getType().getName())).append(Config.NEWLINE);
            	importDisplayPermsFrom(((Lands) FactoidAPI.iLands()).getDefaultConf(land.getType()), false);
        	}
        	
        	// For parent (if exist)
        	ILand parLand = land;
        	while((parLand = parLand.getParent()) != null) {
        		stList.append(ChatStyle.DARK_GRAY + Factoid.getThisPlugin().iLanguage().getMessage("GENERAL.FROMPARENT",
        				ChatStyle.GREEN + parLand.getName() + ChatStyle.DARK_GRAY)).append(Config.NEWLINE);
        		importDisplayPermsFrom(parLand, true);
        	}
        	
        	// For world
        	stList.append(ChatStyle.DARK_GRAY + Factoid.getThisPlugin().iLanguage().getMessage("GENERAL.FROMWORLD",
    				land.getWorldName())).append(Config.NEWLINE);
        	importDisplayPermsFrom(((Lands) FactoidAPI.iLands()).getOutsideArea(land.getWorldName()), true);
        	
            new ChatPage("COMMAND.PERMISSION.LISTSTART", stList.toString(), entity.player, land.getName()).getPage(1);

        } else {
            throw new FactoidCommandException("Missing information command", entity.player, "GENERAL.MISSINGINFO");
        }
    }

    private void importDisplayPermsFrom(IDummyLand land, boolean onlyInherit) {
    	
        boolean addToList = false;
    	
    	for (IPlayerContainer pc : land.getSetPCHavePermission()) {
        	StringBuilder stSubList = new StringBuilder();
        	
        	for (IPermission perm : land.getPermissionsForPC(pc)) {
                if((!onlyInherit || perm.isHeritable()) && !permInList(pc, perm)) {
                	addToList = true;
                    stSubList.append(" ").append(perm.getPermType().getPrint()).append(":").append(perm.getValuePrint());
                }
            }
        	
        	// Append to list
        	if(stSubList.length() > 0) {
                stList.append(ChatStyle.WHITE).append(pc.getPrint()).append(":");
            	stList.append(stSubList).append(Config.NEWLINE);
        	}
        	
        }
        
    	if(addToList) {
        	precDL.add(land);
    	}
    }
    
    private boolean permInList(IPlayerContainer pc, IPermission perm) {
    	
    	for(IDummyLand listLand : precDL) {
			
    		if(listLand.getSetPCHavePermission().contains(pc)) {
        		for(IPermission listPerm : listLand.getPermissionsForPC(pc)) {
        			if(perm.getPermType() == listPerm.getPermType()) {
        				return true;
        			}
        		}
    		}
    	}
    	
    	return false;
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.commands.executor.CommandThreadExec#commandThreadExecute(me.tabinol.factoid.playerscache.PlayerCacheEntry[])
     */
    @Override
    public void commandThreadExecute(PlayerCacheEntry[] playerCacheEntry)
    		throws FactoidCommandException {
        
    	convertPcIfNeeded(playerCacheEntry);

    	if (fonction.equalsIgnoreCase("set")) {

            IPermission perm = entity.argList.getPermissionFromArg(entity.playerConf.isAdminMod(), land.isOwner(entity.player));

            if(!perm.getPermType().isRegistered()) {
            	throw new FactoidCommandException("Permission not registered", entity.player, "COMMAND.PERMISSIONTYPE.TYPENULL");
            }
            
            if (perm.getPermType() == PermissionList.LAND_ENTER.getPermissionType()
                    && perm.getValue() != perm.getPermType().getDefaultValue()
                    && land.isLocationInside(land.getWorld().getSpawnLocation())) {
                throw new FactoidCommandException("Permission", entity.player, "COMMAND.PERMISSION.NOENTERNOTINSPAWN");
            }
            ((Land) land).addPermission(pc, perm);
            entity.player.sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.PERMISSION.ISDONE", perm.getPermType().getPrint(),
                    pc.getPrint() + ChatStyle.YELLOW, land.getName()));
            Factoid.getThisPlugin().iLog().write("Permission set: " + perm.getPermType().toString() + ", value: " + perm.getValue());

        } else if (fonction.equalsIgnoreCase("unset")) {

            IPermissionType pt = entity.argList.getPermissionTypeFromArg(entity.playerConf.isAdminMod(), land.isOwner(entity.player));
            if (!land.removePermission(pc, pt)) {
                throw new FactoidCommandException("Permission", entity.player, "COMMAND.PERMISSION.REMOVENOTEXIST");
            }
            entity.player.sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.PERMISSION.REMOVEISDONE", pt.toString()));
            Factoid.getThisPlugin().iLog().write("Permission unset: " + pt.toString());
        }
    }
}
