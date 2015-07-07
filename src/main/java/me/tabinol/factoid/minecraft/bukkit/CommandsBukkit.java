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

package me.tabinol.factoid.minecraft.bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.OnCommand;
import me.tabinol.factoid.minecraft.Commands;
import me.tabinol.factoid.minecraft.FSender;

public class CommandsBukkit implements Commands, CommandExecutor  {
	
	private final OnCommand onCommand;
	
	public CommandsBukkit(OnCommand onCommand) {
	    
		this.onCommand = onCommand;
    }

	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label,
            String[] arg) {

		FSender fSender;
		
		if(sender instanceof Player) {
			fSender = Factoid.getServerCache().getPlayer(((Player) sender).getUniqueId()).getFSender();
		} else {
			fSender = new FSenderBukkit(sender);
		}
		
		return onCommand.onCommand(fSender, cmd.getName(), arg);
    }

}
