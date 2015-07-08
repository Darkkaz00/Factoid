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

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;

import com.google.common.base.Optional;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.OnCommand;
import me.tabinol.factoid.minecraft.Commands;
import me.tabinol.factoid.minecraft.FSender;

public class CommandsSponge implements Commands, CommandCallable {
	
    private final String name;
    private final String perm;
	private final Optional<Text> desc;
    private final Optional<Text> help;
    private final Text usage;
    private final String suggestion;

	private final OnCommand onCommand;

	public CommandsSponge(OnCommand onCommand, 
			String name,
			String perm,
			Optional<Text> desc,
			Optional<Text> help,
			Text usage,
			String suggestion) {
	    
		this.onCommand = onCommand;
		this.name = name;
		this.perm = perm;
		this.desc = desc;
		this.help = help;
		this.usage = usage;
		this.suggestion = suggestion;
    }

	@Override
	public Optional<? extends Text> getHelp(CommandSource source) {

		return help;
	}

	@Override
	public Optional<? extends Text> getShortDescription(CommandSource source) {

		return desc;
	}

	@Override
	public List<String> getSuggestions(CommandSource source, String arguments)
	        throws CommandException {
		
		List<String> stl = new ArrayList<String>();
		stl.add(suggestion);
		return stl;
	}

	@Override
	public Text getUsage(CommandSource source) {
		
		return usage;
	}

	@Override
	public Optional<CommandResult> process(CommandSource source,
	        String arguments) throws CommandException {
		
		FSender fSender;
		
		if(source instanceof Player) {
			fSender = Factoid.getServerCache().getPlayer(((Player) source).getUniqueId()).getFSender();
		} else {
			fSender = new FSenderSponge(source);
		}

		onCommand.onCommand(fSender, name, arguments.split(":"));
		
		return Optional.of(CommandResult.success());
	}

	@Override
	public boolean testPermission(CommandSource source) {
		
		return source.hasPermission(perm);
	}
}
