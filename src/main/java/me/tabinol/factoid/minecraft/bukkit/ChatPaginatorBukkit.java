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

import me.tabinol.factoid.minecraft.ChatPaginator;
import me.tabinol.factoid.minecraft.FSender;

public class ChatPaginatorBukkit implements ChatPaginator {
	
    public static final int PAGE_HEIGHT = org.bukkit.util.ChatPaginator.CLOSED_CHAT_PAGE_HEIGHT - 2;
    public static final int PAGE_WIDTH = org.bukkit.util.ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH;

    private final org.bukkit.util.ChatPaginator.ChatPage page;
    
    public ChatPaginatorBukkit(String text, int pageNumber) {
    	
    	page = org.bukkit.util.ChatPaginator.paginate(text, pageNumber, PAGE_WIDTH, PAGE_HEIGHT);
    }
    
    /**
     * No chat format here
     * sender The sender
     * title The title
     */
    @Override
    public void send(FSender sender, String title) {
    	
    	sender.sendMessage(page.getLines());
    }

	@Override
    public int getTotalPages() {

		return page.getTotalPages();
    }

	@Override
    public String[] getLines() {

		return page.getLines();
    }

}
