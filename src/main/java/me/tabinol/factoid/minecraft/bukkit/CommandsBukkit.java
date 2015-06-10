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

import me.tabinol.factoid.minecraft.Commands;

public class CommandsBukkit implements Commands, CommandExecutor  {
	
	private final OnCommand onCommand;
	
	public CommandsBukkit() {
		
		
	}
	
	@Override
    public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
            String[] arg3) {
	    // TODO Auto-generated method stub
	    return false;
    }

}
