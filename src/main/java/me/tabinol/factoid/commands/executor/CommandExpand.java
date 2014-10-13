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
import me.tabinol.factoidapi.lands.ILand;
import me.tabinol.factoidapi.lands.areas.ICuboidArea;
import me.tabinol.factoid.selection.PlayerSelection.SelectionType;
import me.tabinol.factoid.selection.region.AreaSelection;
import me.tabinol.factoid.selection.region.ExpandAreaSelection;
import org.bukkit.ChatColor;


/**
 * The Class CommandExpand.
 */
public class CommandExpand extends CommandExec {

    /**
     * Instantiates a new command expand.
     *
     * @param entity the entity
     * @throws FactoidCommandException the factoid command exception
     */
    public CommandExpand(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, false);
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws FactoidCommandException {

        checkSelections(null, null);
        // checkPermission(false, false, null, null);

        ILand land = entity.playerConf.getSelection().getLand();
        String curArg = entity.argList.getNext();

        if (curArg == null) {

            if (entity.playerConf.getSelection().getSelection(SelectionType.AREA) instanceof ExpandAreaSelection) {
                throw new FactoidCommandException("Player Expand", entity.player, "COMMAND.EXPAND.ALREADY");
            }

            entity.player.sendMessage(ChatColor.GRAY + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.EXPAND.JOINMODE"));
            entity.player.sendMessage(ChatColor.DARK_GRAY + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.EXPAND.HINT", ChatColor.ITALIC.toString(), ChatColor.RESET.toString(), ChatColor.DARK_GRAY.toString()));
            Factoid.getThisPlugin().iLog().write(entity.player.getName() + " have join ExpandMode.");

            // Check the selection before (if exist)
            ICuboidArea area = entity.playerConf.getSelection().getCuboidArea();

            if (area == null && land != null && (area = land.getArea(1)) != null) {

                // Expand an existing area?
                entity.playerConf.getSelection().setAreaToReplace(area);
            }

            if (area == null) {
                entity.playerConf.getSelection().addSelection(new ExpandAreaSelection(entity.player));
            } else {
                entity.playerConf.getSelection().addSelection(new ExpandAreaSelection(entity.player, area.copyOf()));
            }

        } else if (curArg.equalsIgnoreCase("done")) {

            // Expand done
            entity.player.sendMessage(ChatColor.GREEN + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.EXPAND.COMPLETE"));
            entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.EXPAND.QUITMODE"));
            Factoid.getThisPlugin().iLog().write(entity.playerName + " have quit ExpandMode.");

            ICuboidArea area = entity.playerConf.getSelection().getCuboidArea();
            if (area != null) {

                entity.playerConf.getSelection().addSelection(new AreaSelection(entity.player, area));

                if (!((AreaSelection) entity.playerConf.getSelection().getSelection(SelectionType.AREA)).getCollision()) {
                    entity.player.sendMessage(ChatColor.GREEN + "[Factoid] " + ChatColor.DARK_GRAY
                            + Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.SELECT.LAND.NOCOLLISION"));
                } else {
                    entity.player.sendMessage(ChatColor.GREEN + "[Factoid] " + ChatColor.RED
                            + Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.SELECT.LAND.COLLISION"));
                }
            }

        } else {
            throw new FactoidCommandException("Missing information command", entity.player, "GENERAL.MISSINGINFO");
        }
    }
}
