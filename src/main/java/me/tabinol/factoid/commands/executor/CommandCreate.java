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
import me.tabinol.factoid.commands.CommandEntities;
import me.tabinol.factoid.commands.CommandExec;
import me.tabinol.factoid.commands.InfoCommand;
import me.tabinol.factoid.config.BannedWords;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.exceptions.FactoidLandException;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.areas.CuboidArea;
import me.tabinol.factoid.lands.collisions.Collisions.LandAction;
import me.tabinol.factoid.lands.types.Type;
import me.tabinol.factoid.parameters.PermissionList;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import me.tabinol.factoid.playercontainer.PlayerContainerNobody;
import me.tabinol.factoid.selection.PlayerSelection.SelectionType;
import me.tabinol.factoid.selection.region.AreaSelection;
import me.tabinol.factoid.utilities.ChatStyle;

/**
 * The Class CommandCreate.
 */
@InfoCommand(name="create", forceParameter=true)
public class CommandCreate extends CommandExec {

    /**
     * Instantiates a new command create.
     *
     * @param entity the entity
     * @throws FactoidCommandException the factoid command exception
     */
    public CommandCreate(CommandEntities entity) throws FactoidCommandException {

        super(entity);
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws FactoidCommandException {

        checkSelections(null, true);
        // checkPermission(false, false, null, null);

        AreaSelection select = (AreaSelection) entity.sender.getSelection().getSelection(SelectionType.AREA);

        CuboidArea area = select.getCuboidArea();
        Double price = entity.sender.getSelection().getLandCreatePrice();
        Land parent;

        // Quit select mod
        // entity.sender.setAreaSelection(null);
        // entity.sender.setLandSelected(null);
        // select.resetSelection();

        String curArg = entity.argList.getNext();

        // Check if is is a banned word
        if (BannedWords.isBannedWord(curArg.toUpperCase())) {
            throw new FactoidCommandException("CommandCreate", entity.sender, "COMMAND.CREATE.HINTUSE");
        }

        // Check for parent
        if (!entity.argList.isLast()) {

            String curString = entity.argList.getNext();
            
            if(curString.equalsIgnoreCase("noparent")) {
            	
            	parent = null;
            }
        	
            else {
        	
            	parent = Factoid.getLands().getLand(curString);

            	if (parent == null) {
            		throw new FactoidCommandException("CommandCreate", entity.sender, "COMMAND.CREATE.PARENTNOTEXIST");
            	}
            }
        } else {

        	// Autodetect parent
            parent = select.getParentDetected();
        }

        // Not complicated! The player must be AdminMod, or access to create (in world) 
        // or access to create in parent if it is a subland.
        if (!entity.sender.isAdminMod()
                && ((parent == null && !Factoid.getLands().getOutsideArea(area.getWorldName()).checkPermissionAndInherit(entity.player, PermissionList.LAND_CREATE.getPermissionType()))
                || (parent != null && !parent.checkPermissionAndInherit(entity.player, PermissionList.LAND_CREATE.getPermissionType())))) {
            throw new FactoidCommandException("CommandCreate", entity.sender, "GENERAL.MISSINGPERMISSION");
        }

        // If the player is adminmod, the owner is nobody, and set type
        PlayerContainer owner;
        Type type;
        if(entity.sender.isAdminMod()) {
            owner = new PlayerContainerNobody();
            type = Factoid.getConf().getTypeAdminMod();
        } else {
            owner = entity.sender.getPlayerContainer();
            type = Factoid.getConf().getTypeNoneAdminMod();
        }

        // Check for collision
        if (checkCollision(curArg, null, type, LandAction.LAND_ADD, 0, area, parent, owner, price, true)) {
            new CommandCancel(entity.player, true).commandExecute();
            return;
        }

        // Create Land
        Land land = null;
        
        try {
            land = Factoid.getLands().createLand(curArg, owner, area, parent, price, type);
        } catch (FactoidLandException ex) {
            Logger.getLogger(CommandCreate.class.getName()).log(Level.SEVERE, "On land create", ex);
        }

        entity.sender.sendMessage(ChatStyle.GREEN + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CREATE.DONE"));
        Factoid.getFactoidLog().write(entity.playerName + " have create a land named " + land.getName() + " at position " + land.getAreas().toString());
        
        // Cancel and select the land
        new CommandCancel(entity.player, true).commandExecute();
        new CommandSelect(entity.player, new ArgList(new String[] {land.getName()}, 
                entity.sender), null).commandExecute();
    }
}
