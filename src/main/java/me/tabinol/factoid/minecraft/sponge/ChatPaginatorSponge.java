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

import me.tabinol.factoid.minecraft.ChatPaginator;
import me.tabinol.factoid.minecraft.FSender;

import org.spongepowered.api.Game;
import org.spongepowered.api.service.pagination.PaginationBuilder;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Texts;

public class ChatPaginatorSponge implements ChatPaginator {

	final private PaginationBuilder builder;
	final private String text;
	
    public ChatPaginatorSponge(String text, int pageNumber, Game game) {
    	
    	PaginationService paginationService = game.getServiceManager().provide(PaginationService.class).get();
    	builder = paginationService.builder();
    	this.text = text;
    }

	@Override
	public void send(FSender sender, String title) {
		
		builder.title(Texts.of(title)).contents(Texts.of(text)).paddingString("-")
			.sendTo(((FSenderSponge) sender).getSpongeSender());
	}
    
    /**
	 * Not needed, using Sponge internal page system
	 * @return 0
	 */
	@Override
    public int getTotalPages() {

		return 0;
    }

	/**
	 * Not needed, using Sponge internal page system
	 * @return null
	 */
	@Override
    public String[] getLines() {

		return null;
    }
}
