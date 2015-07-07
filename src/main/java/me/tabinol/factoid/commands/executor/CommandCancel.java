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
import me.tabinol.factoid.minecraft.FPlayer;
import me.tabinol.factoid.selection.PlayerSelection.SelectionType;
import me.tabinol.factoid.utilities.ChatStyle;

/**
 * The Class CommandCancel.
 */
@InfoCommand(name="cancel")
public class CommandCancel extends CommandExec {

    /** The player.getFSender(). */
    private final FPlayer player;
    
    /** The from auto cancel. */
    private final boolean fromAutoCancel; // true: launched from autoCancel

    /**
     * Instantiates a new command cancel.
     *
     * @param entity the entity
     * @throws FactoidCommandException the factoid command exception
     */
    public CommandCancel(CommandEntities entity) throws FactoidCommandException {

        super(entity);
        player = entity.player;
        fromAutoCancel = false;
    }

    // Called from PlayerListener
    /**
     * Instantiates a new command cancel.
     *
     * @param player the player
     * @param fromAutoCancel the from auto cancel
     * @throws FactoidCommandException the factoid command exception
     */
    public CommandCancel(FPlayer player, boolean fromAutoCancel) throws FactoidCommandException {

        super(null);
        this.player = player;
        this.fromAutoCancel = fromAutoCancel;
    }
    
    /* (non-Javadoc)
     * @see me.tabinol.factoid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws FactoidCommandException {

        if (player.getFSender().getConfirm() != null) {
            player.getFSender().setConfirm(null);
            player.getFSender().sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CANCEL.ACTION"));
            Factoid.getFactoidLog().write(player.getFSender().getName() + " cancel for action");
            
            if(!fromAutoCancel) {
                return;
            }
        }
        
        if (player.getFSender().getSelection().getSelection(SelectionType.AREA) != null) {

            player.getFSender().getSelection().removeSelection(SelectionType.AREA);
            player.getFSender().sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.CANCEL"));
            Factoid.getFactoidLog().write(player.getFSender().getName() + ": Select cancel");

            if(!fromAutoCancel) {
                return;
            }
        }

/*
        if (playerConf.getSetFlagUI() != null) {

            playerConf.setSetFlagUI(null);
            player.getFSender().sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CANCEL.FLAGS"));

            if(!fromAutoCancel) {
                return;
            }
        }
  
*/
        if (player.getFSender().getSelection().getSelection(SelectionType.LAND) != null) {

            player.getFSender().getSelection().removeSelection(SelectionType.LAND);
            player.getFSender().sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.CANCEL.SELECT"));

            // Cancel selection (it is the last think selected)
            player.getFSender().setAutoCancelSelect(false);
            
            if(!fromAutoCancel) {
                return;
            }
        }
        
        // No cancel done
        if(!fromAutoCancel) {
            throw new FactoidCommandException("Nothing to confirm", player.getFSender(), "COMMAND.CANCEL.NOCANCEL");
        }
    }
}
