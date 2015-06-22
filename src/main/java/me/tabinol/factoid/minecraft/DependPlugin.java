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

package me.tabinol.factoid.minecraft;

public abstract class DependPlugin {

    protected Permission permission;
	protected Chat chat;
    protected Economy economy;
    protected Vanish vanish;
    protected EditWorld editWorld;
	
    public Permission getPermission() {
    	
    	return permission;
    }
    
    public Chat getChat() {
    	
    	return chat;
    }
    public Economy getEconomy() {
    	
    	return economy;
    }
    
    public Vanish getVanish() {
    	
    	return vanish;
    }
    
    public EditWorld getEditWorld() {
    	
    	return editWorld;
    }
}
