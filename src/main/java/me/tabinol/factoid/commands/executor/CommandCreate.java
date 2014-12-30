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

import java.util.logging.Level;
import java.util.logging.Logger;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ArgList;
import me.tabinol.factoid.config.BannedWords;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.exceptions.FactoidLandException;
import me.tabinol.factoidapi.lands.ILand;
import me.tabinol.factoidapi.lands.areas.ICuboidArea;
import me.tabinol.factoidapi.lands.types.IType;
import me.tabinol.factoid.lands.collisions.Collisions.LandAction;
import me.tabinol.factoid.parameters.PermissionList;
import me.tabinol.factoid.playercontainer.PlayerContainerNobody;
import me.tabinol.factoidapi.playercontainer.IPlayerContainer;
import me.tabinol.factoid.selection.PlayerSelection.SelectionType;
import me.tabinol.factoid.selection.region.AreaSelection;

import org.bukkit.ChatColor;


/**
 * The Class CommandCreate.
 */
public class CommandCreate extends CommandExec {

    /**
     * Instantiates a new command create.
     *
     * @param entity the entity
     * @throws FactoidCommandException the factoid command exception
     */
    public CommandCreate(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, true);
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws FactoidCommandException {

        checkSelections(null, true);
        // checkPermission(false, false, null, null);

        AreaSelection select = (AreaSelection) entity.playerConf.getSelection().getSelection(SelectionType.AREA);

        ICuboidArea area = select.getCuboidArea();
        Double price = entity.playerConf.getSelection().getLandCreatePrice();
        ILand parent;

        // Quit select mod
        // entity.playerConf.setAreaSelection(null);
        // entity.playerConf.setLandSelected(null);
        // select.resetSelection();

        String curArg = entity.argList.getNext();

        // Check if is is a banned word
        if (BannedWords.isBannedWord(curArg.toUpperCase())) {
            throw new FactoidCommandException("CommandCreate", entity.player, "COMMAND.CREATE.HINTUSE");
        }

        // Check for parent
        if (!entity.argList.isLast()) {

            String curString = entity.argList.getNext();
            
            if(curString.equalsIgnoreCase("noparent")) {
            	
            	parent = null;
            }
        	
            else {
        	
            	parent = Factoid.getThisPlugin().iLands().getLand(curString);

            	if (parent == null) {
            		throw new FactoidCommandException("CommandCreate", entity.player, "COMMAND.CREATE.PARENTNOTEXIST");
            	}
            }
        } else {

        	// Autodetect parent
            parent = select.getParentDetected();
        }

        // Not complicated! The player must be AdminMod, or access to create (in world) 
        // or access to create in parent if it is a subland.
        if (!entity.playerConf.isAdminMod()
                && ((parent == null && !Factoid.getThisPlugin().iLands().getOutsideArea(area.getWorldName()).checkPermissionAndInherit(entity.player, PermissionList.LAND_CREATE.getPermissionType()))
                || (parent != null && !parent.checkPermissionAndInherit(entity.player, PermissionList.LAND_CREATE.getPermissionType())))) {
            throw new FactoidCommandException("CommandCreate", entity.player, "GENERAL.MISSINGPERMISSION");
        }

        // If the player is adminmod, the owner is nobody, and set type
        IPlayerContainer owner;
        IType type;
        if(entity.playerConf.isAdminMod()) {
            owner = new PlayerContainerNobody();
            type = Factoid.getThisPlugin().iConf().getTypeAdminMod();
        } else {
            owner = entity.playerConf.getPlayerContainer();
            type = Factoid.getThisPlugin().iConf().getTypeNoneAdminMod();
        }

        // Check for collision
        if (checkCollision(curArg, null, type, LandAction.LAND_ADD, 0, area, parent, owner, price, true)) {
            new CommandCancel(entity.playerConf, true).commandExecute();
            return;
        }

        // Create Land
        ILand land = null;
        
        try {
            land = Factoid.getThisPlugin().iLands().createLand(curArg, owner, area, parent, price, type);
        } catch (FactoidLandException ex) {
            Logger.getLogger(CommandCreate.class.getName()).log(Level.SEVERE, "On land create", ex);
        }

        entity.player.sendMessage(ChatColor.GREEN + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.CREATE.DONE"));
        Factoid.getThisPlugin().iLog().write(entity.playerName + " have create a land named " + land.getName() + " at position " + land.getAreas().toString());
        
        // Cancel and select the land
        new CommandCancel(entity.playerConf, true).commandExecute();
        new CommandSelect(entity.player, new ArgList(new String[] {land.getName()}, 
                entity.player), null).commandExecute();
    }
}
