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
import me.tabinol.factoid.exceptions.FactoidCommandException;
import org.bukkit.ChatColor;


/**
 * The Class CommandAdminmod.
 */
public class CommandAdminmod extends CommandExec {

    /**
     * Instantiates a new command adminmod.
     *
     * @param entity the entity
     * @throws FactoidCommandException the factoid command exception
     */
    public CommandAdminmod(CommandEntities entity) throws FactoidCommandException {

        super(entity, true, false);
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws FactoidCommandException {

        checkPermission(false, false, null, "factoid.adminmod");
        
        if (entity.playerConf.isAdminMod()) {
            entity.playerConf.setAdminMod(false);
            entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.ADMINMOD.QUIT"));
        } else {
            entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.ADMINMOD.JOIN"));
            entity.playerConf.setAdminMod(true);
        }
    }
}
