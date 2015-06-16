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
package me.tabinol.factoid.commands.executor;

import org.bukkit.ChatStyle;
import org.bukkit.GameMode;
import org.bukkit.Material;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.CommandEntities;
import me.tabinol.factoid.commands.CommandExec;
import me.tabinol.factoid.commands.InfoCommand;
import me.tabinol.factoid.economy.EcoSign;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.exceptions.SignException;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.parameters.PermissionList;

@InfoCommand(name="rent", forceParameter=true)
public class CommandRent extends CommandExec {
	
    public CommandRent(CommandEntities entity) throws FactoidCommandException {

        super(entity);
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws FactoidCommandException {
    	
        checkSelections(true, null);
        checkPermission(true, true, null, null);
        if(!entity.playerConf.isAdminMod()) {
        	// If the player not adminmod, he must be owner && permission true
        	checkPermission(false, false, PermissionList.ECO_LAND_FOR_RENT.getPermissionType(), null);
        }
        
        String curArg = entity.argList.getNext();
        double rentPrice = 0;
        int rentRenew = 0;
        boolean rentAutoRenew = true;
        EcoSign ecoSign = null;
        
        // Check for sign in hand
        if(entity.player.getGameMode() != GameMode.CREATIVE && entity.player.getItemInHand().getType() != Material.SIGN) {
        	throw new FactoidCommandException("Must have a sign in hand", entity.player, "COMMAND.ECONOMY.MUSTHAVEISIGN");
        }
        
        // If 'recreate'
        if(curArg.equalsIgnoreCase("recreate")) {
        	if(!land.isForRent()) {
        		throw new FactoidCommandException("The land is not for rent", entity.player, "COMMAND.ECONOMY.ERRORCREATESIGN");
        	}
        	try {
        		ecoSign = new EcoSign(land, entity.player);
				ecoSign.createSignForRent(land.getRentPrice(), land.getRentRenew(), land.getRentAutoRenew(),
						land.isRented() ? land.getTenant().getPlayerName() : null); // Tenant name if the land is rented
				removeSignFromHand();
				if(!ecoSign.getLocation().getBlock().equals(land.getRentSignLoc().getBlock())) {
					ecoSign.removeSign(land.getRentSignLoc());
					((Land) land).setRentSignLoc(ecoSign.getLocation());
				}
			} catch (SignException e) {
				throw new FactoidCommandException("Error in the command", entity.player, "COMMAND.ECONOMY.ERRORCREATESIGN");
			}
        	
            entity.player.sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.ECONOMY.RECREATE"));
            Factoid.getLog().write("Sign recreated for land " + land.getName() + " by: " + entity.playerName);
            
            return;
        }
        
        // get price
        try {
            rentPrice = Double.parseDouble(curArg);
        } catch (NumberFormatException ex) {
        	throw new FactoidCommandException("Error in the command", entity.player, "GENERAL.MISSINGINFO");
        }
        
        // get renew
        curArg = entity.argList.getNext();
        try {
            rentRenew = Integer.parseInt(curArg);
        } catch (NumberFormatException ex) {
        	throw new FactoidCommandException("Error in the command", entity.player, "GENERAL.MISSINGINFO");
        }
        
        // get auto renew
        curArg = entity.argList.getNext();
        if(curArg != null) {
        	try {
        		rentAutoRenew = Boolean.parseBoolean(curArg);
        	} catch (NumberFormatException ex) {
        		// Default value
        		rentAutoRenew = true;
        	}
        }
        
        // Land already for rent?
        if(land.isForRent()) {
        	throw new FactoidCommandException("Land already for rent", entity.player, "COMMAND.ECONOMY.ALREADYRENT");
        }
        
        // Create Sign
        try {
			ecoSign = new EcoSign(land, entity.player);
			ecoSign.createSignForRent(rentPrice, rentRenew, rentAutoRenew, null);
			removeSignFromHand();
		} catch (SignException e) {
			throw new FactoidCommandException("Error in the command", entity.player, "COMMAND.ECONOMY.ERRORCREATESIGN");
		}
        ((Land) land).setForRent(rentPrice, rentRenew, rentAutoRenew, ecoSign.getLocation());
        entity.player.sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.ECONOMY.SIGNDONE"));
        Factoid.getLog().write("The land " + land.getName() + " is set to for rent by: " + entity.playerName);
    }
}
