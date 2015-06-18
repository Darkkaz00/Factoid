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

import java.util.UUID;

import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandSource;

import me.tabinol.factoid.minecraft.FSender;

public class FSenderSponge extends FSender {
	
	private final CommandSource sender;
	
	/**
	 * Constructor for Console
	 * @param sender
	 */
	public FSenderSponge(CommandSource sender) {
		
		super();
		this.sender = sender;
	}

	/**
	 * Constructor for Player
	 * @param sender
	 * @param uuid
	 */
	public FSenderSponge(CommandSource sender, UUID uuid) {
		
		super(uuid);
		this.sender = sender;
	}

	@Override
    public String getName() {

		return sender.getName();
    }
	
	@Override
    public void sendMessage(String msg) {
	    
		sender.sendMessage(Texts.builder(msg).build());
    }

	@Override
    public void sendMessage(String[] msg) {
	    
		TextBuilder text = Texts.builder();
		for(String line : msg) {
			text.append(Texts.builder(line).build());
		}
		sender.sendMessage(text.build());
    }

	@Override
    public boolean hasPermission(String perm) {

	    return sender.hasPermission(perm);
    }
	
	/**************************************************************************
	 * Sponge only methods
	 *************************************************************************/
	
	public CommandSource getSpongeSender() {
		
		return sender;
	}
}
