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

package me.tabinol.factoid.minecraft.sponge;

import org.bukkit.entity.Player;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.OnCommand;
import me.tabinol.factoid.minecraft.Commands;
import me.tabinol.factoid.minecraft.FSenderInterface;
import me.tabinol.factoid.minecraft.bukkit.FSenderBukkit;

public class CommandsSpongeFaction implements Commands, CommandExecutor {

	private final OnCommand onCommand;
	
	public CommandsSpongeFaction(OnCommand onCommand) {
	    
		this.onCommand = onCommand;
    }

	@Override
    public CommandResult execute(CommandSource src, CommandContext args)
            throws CommandException {

		FSenderInterface fSender;
		
		if(src instanceof Player) {
			fSender = Factoid.getServerCache().getPlayer(((Player) src).getUniqueId());
		} else {
			fSender = new FSenderBukkit(null);
		}

		onCommand.onCommand(fSender, "faction", args.<String>getAll("string").toArray(new String[0]));
		
		return CommandResult.success();
    }
}
