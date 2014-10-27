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
import me.tabinol.factoid.parameters.PermissionList;
import me.tabinol.factoidapi.playercontainer.IPlayerContainer;
import me.tabinol.factoid.playerscache.PlayerCacheEntry;
import me.tabinol.factoidapi.playercontainer.EPlayerContainerType;

import org.bukkit.ChatColor;


/**
 * The Class CommandBan.
 */
public class CommandBan extends CommandThreadExec {

	private String fonction;

	/**
     * Instantiates a new command ban.
     *
     * @param entity the entity
     * @throws FactoidCommandException the factoid command exception
     */
    public CommandBan(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, true);
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws FactoidCommandException {

        checkSelections(true, null);
        checkPermission(true, true, PermissionList.LAND_BAN.getPermissionType(), null);

        fonction = entity.argList.getNext();

        if (fonction.equalsIgnoreCase("add")) {

            pc = entity.argList.getPlayerContainerFromArg(land,
                    new EPlayerContainerType[]{EPlayerContainerType.EVERYBODY,
                        EPlayerContainerType.OWNER, EPlayerContainerType.VISITOR,
                        EPlayerContainerType.RESIDENT});
            Factoid.getThisPlugin().iPlayersCache().getUUIDWithNames(this, pc);

        } else if (fonction.equalsIgnoreCase("remove")) {

            pc = entity.argList.getPlayerContainerFromArg(land, null);
            Factoid.getThisPlugin().iPlayersCache().getUUIDWithNames(this, pc);

        } else if (fonction.equalsIgnoreCase("list")) {

            StringBuilder stList = new StringBuilder();
            if (!land.getBanneds().isEmpty()) {
                for (IPlayerContainer pc : land.getBanneds()) {
                    if (stList.length() != 0) {
                        stList.append(" ");
                    }
                    stList.append(ChatColor.WHITE).append(pc.getPrint());
                }
                stList.append(Config.NEWLINE);
            } else {
                entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.BANNED.LISTROWNULL"));
            }
            new ChatPage("COMMAND.BANNED.LISTSTART", stList.toString(), entity.player, land.getName()).getPage(1);
        
        } else {
            throw new FactoidCommandException("Missing information command", entity.player, "GENERAL.MISSINGINFO");
        }
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.commands.executor.CommandThreadExec#commandThreadExecute(me.tabinol.factoid.playerscache.PlayerCacheEntry[])
     */
    @Override
    public void commandThreadExecute(PlayerCacheEntry[] playerCacheEntry)
    		throws FactoidCommandException {
	
    	convertPcIfNeeded(playerCacheEntry);

    	if (fonction.equalsIgnoreCase("add")) {

    		if (land.isLocationInside(land.getWorld().getSpawnLocation())) {
    			throw new FactoidCommandException("Banned", entity.player, "COMMAND.BANNED.NOTINSPAWN");
    		}
    		land.addBanned(pc);

    		entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.BANNED.ISDONE", 
    				pc.getPrint() + ChatColor.YELLOW, land.getName()));
    		Factoid.getThisPlugin().iLog().write("Ban added: " + pc.toString());

    	} else if (fonction.equalsIgnoreCase("remove")) {

    		if (!land.removeBanned(pc)) {
    			throw new FactoidCommandException("Banned", entity.player, "COMMAND.BANNED.REMOVENOTEXIST");
    		}
    		entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.BANNED.REMOVEISDONE", 
    				pc.getPrint() + ChatColor.YELLOW, land.getName()));
    		Factoid.getThisPlugin().iLog().write("Ban removed: " + pc.toString());
    	}
    }
}