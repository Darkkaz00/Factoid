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
import me.tabinol.factoid.minecraft.FPlayer;
import me.tabinol.factoid.parameters.PermissionList;
import me.tabinol.factoid.utilities.ChatStyle;


/**
 * The Class CommandWho.
 */
@InfoCommand(name="who")
public class CommandWho extends CommandExec {

    /**
     * Instantiates a new command who.
     *
     * @param entity the entity
     * @throws FactoidCommandException the factoid command exception
     */
    public CommandWho(CommandEntities entity) throws FactoidCommandException {

        super(entity);
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws FactoidCommandException {

        getLandFromCommandIfNoLandSelected();
        checkSelections(true, null);
        checkPermission(true, true, PermissionList.LAND_WHO.getPermissionType(), null);

        // Create list
        StringBuilder stList = new StringBuilder();
        for (FPlayer player : land.getPlayersInLandNoVanish(entity.player)) {
            stList.append(player.getDisplayName()).append(Config.NEWLINE);
        }

        if (stList.length() != 0) {
            new ChatPage("COMMAND.WHO.LISTSTART", stList.toString(), entity.player, land.getName()).getPage(1);
        } else {
            entity.player.sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.WHO.LISTNULL", land.getName()));
        }
    }
}
