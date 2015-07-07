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
package me.tabinol.factoid.commands;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.Factoid.ServerType;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.minecraft.ChatPaginator;
import me.tabinol.factoid.minecraft.FSender;
import me.tabinol.factoid.utilities.ChatStyle;

/**
 * The Class ChatPage.
 */
public class ChatPage {

    /** The header. */
    private final String header;
    
    /** The text. */
    private final String text;
    
    /** The sender. */
    private final FSender sender;
    
    /** The param. */
    private final String param;
    
    /** The total pages. */
    private int totalPages;

    /**
     * Instantiates a new chat page.
     *
     * @param header the header
     * @param text the text
     * @param sender the sender
     * @param param the param
     * @throws FactoidCommandException the factoid command exception
     */
    public ChatPage(String header, String text, FSender sender, String param) throws FactoidCommandException {

        this.header = header;
        this.text = text;
        this.sender = sender;
        this.param = param;
    }

    /**
     * Get the page
     */
    public void getPage() throws FactoidCommandException {
    	
    	if(Factoid.getServerType() == ServerType.BUKKIT) {
    		getPage(1);
    	} else {
    		// Using Sponge Internal System
    		Factoid.getServer().getChatPaginator(text, 1).send(sender, header);
    	}
    }
    
    /**
     * Gets the page.
     *
     * @param pageNumber the page number
     * @throws FactoidCommandException the factoid command exception
     */
    public void getPage(int pageNumber) throws FactoidCommandException {

        // Create page with Bukkit paginator
        ChatPaginator page = Factoid.getServer().getChatPaginator(text, pageNumber);
        totalPages = page.getTotalPages();

        // If the requested page is more than the last age
        if (pageNumber > totalPages) {
            throw new FactoidCommandException("Page error", sender, "COMMAND.PAGE.INVALID");
        }
        
        // Check if there is a parameter
        if (param != null) {
            sender.sendMessage(ChatStyle.GRAY + Factoid.getLanguage().getMessage(header,
                    ChatStyle.GREEN + param + ChatStyle.GRAY));
        } else {
            sender.sendMessage(ChatStyle.GRAY + Factoid.getLanguage().getMessage(header));
        }
        
        // Send lines to sender
        sender.sendMessage(page.getLines());
        
        // If there is one or multiple page, put the number of page at the bottom
        if (totalPages > 1) {
            sender.sendMessage(ChatStyle.GRAY + Factoid.getLanguage().getMessage("COMMAND.PAGE.MULTIPAGE",
                    "" + pageNumber, "" + totalPages));
            sender.setChatPage(this);
        } else {
            sender.sendMessage(ChatStyle.GRAY + Factoid.getLanguage().getMessage("COMMAND.PAGE.ONEPAGE"));
            sender.setChatPage(null);
        }

    }

    /**
     * Gets the total pages.
     *
     * @return the total pages
     */
    public final int getTotalPages() {

        return totalPages;
    }
}
