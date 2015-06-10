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

@InfoCommand(name="sale", forceParameter=true)
public class CommandSale extends CommandExec {

    public CommandSale(CommandEntities entity) throws FactoidCommandException {

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
        	checkPermission(false, false, PermissionList.ECO_LAND_FOR_SALE.getPermissionType(), null);
        }
        
        String curArg = entity.argList.getNext();
        double salePrice = 0;
        EcoSign ecoSign = null;
        
        // Check for sign in hand
        if(entity.player.getGameMode() != GameMode.CREATIVE && entity.player.getItemInHand().getType() != Material.SIGN) {
        	throw new FactoidCommandException("Must have a sign in hand", entity.player, "COMMAND.ECONOMY.MUSTHAVEISIGN");
        }
        
        // If 'recreate'
        if(curArg.equalsIgnoreCase("recreate")) {
        	if(!land.isForSale()) {
        		throw new FactoidCommandException("The land is not for sale", entity.player, "COMMAND.ECONOMY.ERRORCREATESIGN");
        	}
        	try {
        		ecoSign = new EcoSign(land, entity.player);
				ecoSign.createSignForSale(land.getSalePrice());
				removeSignFromHand();
				if(!ecoSign.getLocation().getBlock().equals(land.getSaleSignLoc().getBlock())) {
					ecoSign.removeSign(land.getSaleSignLoc());
					((Land) land).setSaleSignLoc(ecoSign.getLocation());
				}
			} catch (SignException e) {
				throw new FactoidCommandException("Error in the command", entity.player, "COMMAND.ECONOMY.ERRORCREATESIGN");
			}
        	
            entity.player.sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.ECONOMY.RECREATE"));
            Factoid.getThisPlugin().iLog().write("Sign recreated for land " + land.getName() + " by: " + entity.playerName);
            
            return;
        }
        
        // get price
        try {
            salePrice = Double.parseDouble(curArg);
        } catch (NumberFormatException ex) {
        	throw new FactoidCommandException("Error in the command", entity.player, "GENERAL.MISSINGINFO");
        }
        
        // Land already for sale?
        if(land.isForSale()) {
        	throw new FactoidCommandException("Land already for sale", entity.player, "COMMAND.ECONOMY.ALREADYSALE");
        }

        // Create Sign
        try {
			ecoSign = new EcoSign(land, entity.player);
			ecoSign.createSignForSale(salePrice);
			removeSignFromHand();
		} catch (SignException e) {
			throw new FactoidCommandException("Error in the command", entity.player, "COMMAND.ECONOMY.ERRORCREATESIGN");
		}
        ((Land) land).setForSale(true, salePrice, ecoSign.getLocation());
        entity.player.sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.ECONOMY.SIGNDONE"));
        Factoid.getThisPlugin().iLog().write("The land " + land.getName() + " is set to for sale by: " + entity.playerName);
    }


}
