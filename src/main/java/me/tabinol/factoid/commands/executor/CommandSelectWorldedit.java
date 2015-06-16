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

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.config.players.PlayerConfEntry;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.areas.CuboidArea;
import me.tabinol.factoid.selection.region.AreaSelection;
import org.bukkit.ChatStyle;
import org.bukkit.entity.Player;


// WorldEdit is in a separate class from CommandSelect because if WorldEdit
// is not installed, we don't want to makes error.

/**
 * The Class CommandSelectWorldedit.
 */
public class CommandSelectWorldedit {
    
    /** The player. */
    Player player;
    
    /** The entry. */
    PlayerConfEntry entry;
    
    /**
     * Instantiates a new command select worldedit.
     *
     * @param player the player
     * @param entry the entry
     * @throws FactoidCommandException the factoid command exception
     */
    public CommandSelectWorldedit(Player player, PlayerConfEntry entry) throws FactoidCommandException{
        
        this.player = player;
        this.entry = entry;
    }
    
    /**
     * Make select.
     *
     * @throws FactoidCommandException the factoid command exception
     */
    protected void MakeSelect() throws FactoidCommandException {
        
        if (Factoid.getDependPlugin().getWorldEdit() == null) {
            throw new FactoidCommandException("CommandSelectWorldEdit", player, "COMMAND.SELECT.WORLDEDIT.NOTLOAD");
        }
        LocalSession session = ((WorldEditPlugin) Factoid.getDependPlugin().getWorldEdit()).getSession(player);
        
        try {
            Region sel;
            if (session.getSelectionWorld() == null
                    || !((sel = session.getSelection(session.getSelectionWorld())) != null && sel instanceof CuboidRegion)) {
                throw new FactoidCommandException("CommandSelectWorldEdit", player, "COMMAND.SELECT.WORLDEDIT.NOSELECTIONNED");
            }

            player.sendMessage(ChatStyle.GREEN + "[Factoid] " + ChatStyle.DARK_GRAY + Factoid.getLanguage().getMessage("COMMAND.SELECT.WORLDEDIT.SELECTIONNED"));
            Factoid.getLog().write(Factoid.getLanguage().getMessage("COMMAND.SELECT.WORLDEDIT.SELECTIONNED"));
            
            AreaSelection select = new AreaSelection(player, new CuboidArea(player.getWorld().getName(), 
                    sel.getMinimumPoint().getBlockX(), sel.getMinimumPoint().getBlockY(),
                    sel.getMinimumPoint().getBlockZ(), sel.getMaximumPoint().getBlockX(), 
                    sel.getMaximumPoint().getBlockY(), sel.getMaximumPoint().getBlockZ()));
            
            entry.getSelection().addSelection(select);
            entry.setAutoCancelSelect(true);

        } catch (IncompleteRegionException ex) {
            throw new FactoidCommandException("CommandSelectWorldEdit", player, "COMMAND.SELECT.WORLDEDIT.SELECTIONINCOMPLET");
        }
    }
}
