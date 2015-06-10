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

import java.util.Collection;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ChatPage;
import me.tabinol.factoid.commands.CommandEntities;
import me.tabinol.factoid.commands.CommandThreadExec;
import me.tabinol.factoid.commands.InfoCommand;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoidapi.FactoidAPI;
import me.tabinol.factoidapi.lands.ILand;
import me.tabinol.factoidapi.lands.types.IType;
import me.tabinol.factoid.playerscache.PlayerCacheEntry;

import org.bukkit.ChatStyle;


/**
 * The Class CommandList.
 *
 * @author Tabinol
 */
@InfoCommand(name="list")
public class CommandList extends CommandThreadExec {

    private String worldName = null;
    private IType type = null;

    /**
     * Instantiates a new command list.
     *
     * @param entity the entity
     * @throws FactoidCommandException the factoid command exception
     */
    public CommandList(CommandEntities entity) throws FactoidCommandException {

        super(entity);

    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws FactoidCommandException {

        String curArg = entity.argList.getNext();

        if (curArg != null) {
            if (curArg.equalsIgnoreCase("world")) {

                // Get worldName
                worldName = entity.argList.getNext();
                if (worldName == null) {
                    // No worldName has parameter
                    worldName = entity.player.getLocation().getWorld().getName().toLowerCase();
                }

            } else if (curArg.equalsIgnoreCase("type")) {
            	
            	// Get the category name
                String typeName = entity.argList.getNext();
                
                if(typeName != null) {
                	type = FactoidAPI.iTypes().getType(typeName);
                }
                
                if(type == null) {
                	throw new FactoidCommandException("CommandList", entity.sender, "COMMAND.LAND.TYPENOTEXIST");
                }
            	
            } else {

                // Get the player Container
                entity.argList.setPos(0);
                pc = entity.argList.getPlayerContainerFromArg(null, null);

            }
        }
        
        Factoid.getThisPlugin().iPlayersCache().getUUIDWithNames(this, pc);
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.commands.executor.CommandThreadExec#commandThreadExecute(me.tabinol.factoid.playerscache.PlayerCacheEntry[])
     */
    @Override
    public void commandThreadExecute(PlayerCacheEntry[] playerCacheEntry)
    		throws FactoidCommandException {
        
    	convertPcIfNeeded(playerCacheEntry);

    	// Check if the player is AdminMod or send only owned lands
        Collection<ILand> lands;

        if (entity.playerConf.isAdminMod()) {
            lands = Factoid.getThisPlugin().iLands().getLands();
        } else {
            lands = Factoid.getThisPlugin().iLands().getLands(entity.playerConf.getPlayerContainer());
        }

        // Get the list of the land
        StringBuilder stList = new StringBuilder();
        stList.append(ChatStyle.YELLOW);

        for (ILand land : lands) {
            if (((worldName != null && worldName.equals(land.getWorldName()))
            		|| (type !=null && type == land.getType())
            		|| (worldName == null && type == null))
                    && (pc == null || land.getOwner().equals(pc))) {
                stList.append(land.getName()).append(" ");
            }
        }

        new ChatPage("COMMAND.LAND.LISTSTART", stList.toString(), entity.player, null).getPage(1);
    }
}
