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

package me.tabinol.factoid.minecraft.bukkit.plugins;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.areas.CuboidArea;
import me.tabinol.factoid.minecraft.EditWorld;
import me.tabinol.factoid.minecraft.FPlayer;
import me.tabinol.factoid.selection.region.AreaSelection;
import me.tabinol.factoid.utilities.ChatStyle;

public class EditWorldEdit extends EditWorld {
	
	private WorldEditPlugin worldEdit;

	public EditWorldEdit(Plugin plugin) {
	    
		this.worldEdit = (WorldEditPlugin) plugin;
    }

    @Override
    public boolean isPluginLoaded() {
    	
    	return true;
    }

    @Override
	public void makeSelect(FPlayer player) throws FactoidCommandException {
        
        LocalSession session = worldEdit.getSession(Bukkit.getPlayer(player.getUUID()));
        
        try {
            Region sel;
            if (session.getSelectionWorld() == null
                    || !((sel = session.getSelection(session.getSelectionWorld())) != null && sel instanceof CuboidRegion)) {
                throw new FactoidCommandException("CommandSelectWorldEdit", player, "COMMAND.SELECT.WORLDEDIT.NOSELECTIONNED");
            }

            player.sendMessage(ChatStyle.GREEN + "[Factoid] " + ChatStyle.DARK_GRAY + Factoid.getLanguage().getMessage("COMMAND.SELECT.WORLDEDIT.SELECTIONNED"));
            Factoid.getFactoidLog().write(Factoid.getLanguage().getMessage("COMMAND.SELECT.WORLDEDIT.SELECTIONNED"));
            
            AreaSelection select = new AreaSelection(player, new CuboidArea(sel.getWorld().getName(), 
                    sel.getMinimumPoint().getBlockX(), sel.getMinimumPoint().getBlockY(),
                    sel.getMinimumPoint().getBlockZ(), sel.getMaximumPoint().getBlockX(), 
                    sel.getMaximumPoint().getBlockY(), sel.getMaximumPoint().getBlockZ()));
            
            player.getSelection().addSelection(select);
            player.setAutoCancelSelect(true);

        } catch (IncompleteRegionException ex) {
            throw new FactoidCommandException("CommandSelectWorldEdit", player, "COMMAND.SELECT.WORLDEDIT.SELECTIONINCOMPLET");
        }
    }
}
