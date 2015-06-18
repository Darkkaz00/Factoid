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
import me.tabinol.factoid.commands.CommandEntities;
import me.tabinol.factoid.commands.CommandExec;
import me.tabinol.factoid.commands.InfoCommand;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.areas.CuboidArea;
import me.tabinol.factoid.selection.PlayerSelection.SelectionType;
import me.tabinol.factoid.selection.region.AreaSelection;
import me.tabinol.factoid.selection.region.ExpandAreaSelection;
import me.tabinol.factoid.utilities.ChatStyle;

/**
 * The Class CommandExpand.
 */
@InfoCommand(name="expand")
public class CommandExpand extends CommandExec {

    /**
     * Instantiates a new command expand.
     *
     * @param entity the entity
     * @throws FactoidCommandException the factoid command exception
     */
    public CommandExpand(CommandEntities entity) throws FactoidCommandException {

        super(entity);
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws FactoidCommandException {

        checkSelections(null, null);
        // checkPermission(false, false, null, null);

        Land land = entity.player.getSelection().getLand();
        String curArg = entity.argList.getNext();

        if (curArg == null) {

            if (entity.player.getSelection().getSelection(SelectionType.AREA) instanceof ExpandAreaSelection) {
                throw new FactoidCommandException("Player Expand", entity.player, "COMMAND.EXPAND.ALREADY");
            }

            entity.player.sendMessage(ChatStyle.GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.EXPAND.JOINMODE"));
            entity.player.sendMessage(ChatStyle.DARK_GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.EXPAND.HINT", ChatStyle.ITALIC.toString(), ChatStyle.RESET.toString(), ChatStyle.DARK_GRAY.toString()));
            Factoid.getFactoidLog().write(entity.player.getName() + " have join ExpandMode.");

            // Check the selection before (if exist)
            CuboidArea area = entity.player.getSelection().getCuboidArea();

            if (area == null && land != null && (area = land.getArea(1)) != null) {

                // Expand an existing area?
                entity.player.getSelection().setAreaToReplace(area);
            }

            if (area == null) {
                entity.player.getSelection().addSelection(new ExpandAreaSelection(entity.player));
            } else {
                entity.player.getSelection().addSelection(new ExpandAreaSelection(entity.player, area.copyOf()));
            }

        } else if (curArg.equalsIgnoreCase("done")) {

            // Expand done
            entity.player.sendMessage(ChatStyle.GREEN + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.EXPAND.COMPLETE"));
            entity.player.sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.EXPAND.QUITMODE"));
            Factoid.getFactoidLog().write(entity.playerName + " have quit ExpandMode.");

            CuboidArea area = entity.player.getSelection().getCuboidArea();
            if (area != null) {

                entity.player.getSelection().addSelection(new AreaSelection(entity.player, area));

                if (!((AreaSelection) entity.player.getSelection().getSelection(SelectionType.AREA)).getCollision()) {
                    entity.player.sendMessage(ChatStyle.GREEN + "[Factoid] " + ChatStyle.DARK_GRAY
                            + Factoid.getLanguage().getMessage("COMMAND.SELECT.LAND.NOCOLLISION"));
                } else {
                    entity.player.sendMessage(ChatStyle.GREEN + "[Factoid] " + ChatStyle.RED
                            + Factoid.getLanguage().getMessage("COMMAND.SELECT.LAND.COLLISION"));
                }
            }

        } else {
            throw new FactoidCommandException("Missing information command", entity.player, "GENERAL.MISSINGINFO");
        }
    }
}
