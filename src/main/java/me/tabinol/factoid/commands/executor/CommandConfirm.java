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
import me.tabinol.factoid.commands.CommandEntities;
import me.tabinol.factoid.commands.CommandExec;
import me.tabinol.factoid.commands.ConfirmEntry;
import me.tabinol.factoid.commands.InfoCommand;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.exceptions.FactoidLandException;
import me.tabinol.factoid.utilities.ChatStyle;


/**
 * The Class CommandConfirm.
 */
@InfoCommand(name="confirm")
public class CommandConfirm extends CommandExec {

    /**
     * Instantiates a new command confirm.
     *
     * @param entity the entity
     * @throws FactoidCommandException the factoid command exception
     */
    public CommandConfirm(CommandEntities entity) throws FactoidCommandException {

        super(entity);
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws FactoidCommandException {

        ConfirmEntry confirmEntry;

        if ((confirmEntry = entity.sender.getConfirm()) != null) {
            
            if (confirmEntry.confirmType == ConfirmEntry.ConfirmType.REMOVE_LAND) {

                // Remove land
                int i = confirmEntry.land.getAreas().size();
                try {
                    Factoid.getLands().removeLand(confirmEntry.land);
                } catch (FactoidLandException ex) {
                    Logger.getLogger(CommandConfirm.class.getName()).log(Level.SEVERE, "On land remove", ex);
                    throw new FactoidCommandException("On land remove", entity.sender, "GENERAL.ERROR");
                }
                entity.sender.sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.REMOVE.DONE.LAND", confirmEntry.land.getName(), i + ""));
                Factoid.getFactoidLog().write(entity.playerName + " confirm for removing " + confirmEntry.land.getName());

            } else if (confirmEntry.confirmType == ConfirmEntry.ConfirmType.REMOVE_AREA) {

                // Remove area
                if (!confirmEntry.land.removeArea(confirmEntry.areaNb)) {
                    throw new FactoidCommandException("Area", entity.sender, "COMMAND.REMOVE.AREA.INVALID");
                }
                entity.sender.getSelection().refreshLand();
                entity.sender.sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.REMOVE.DONE.AREA", confirmEntry.land.getName()));
                Factoid.getFactoidLog().write("area " + confirmEntry.areaNb + " for land " + confirmEntry.land.getName() + " is removed by " + entity.playerName);

            } else if (confirmEntry.confirmType == ConfirmEntry.ConfirmType.LAND_DEFAULT) {

                // Set to default
                confirmEntry.land.setDefault();
                entity.sender.sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SETDEFAULT.ISDONE", confirmEntry.land.getName()));
                Factoid.getFactoidLog().write("The land " + confirmEntry.land.getName() + "is set to default configuration by " + entity.playerName);
            }
            
            // Remove confirm
            entity.sender.setConfirm(null);
            
        } else {
            
            throw new FactoidCommandException("Nothing to confirm", entity.sender, "COMMAND.NOCONFIRM");
        }
    }
}
