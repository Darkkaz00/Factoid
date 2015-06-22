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
package me.tabinol.factoid.economy;

import java.sql.Timestamp;
import java.util.Calendar;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.exceptions.SignException;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.playercontainer.PlayerContainerPlayer;
import me.tabinol.factoid.utilities.FactoidRunnable;

public class EcoScheduler extends FactoidRunnable {

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
    	
    	Calendar now = Calendar.getInstance();
    	
    	// Check for rent renew
    	for(Land land : Factoid.getLands().getForRent()) {
    		
    		long nextPaymentTime = land.getLastPaymentTime().getTime() + (86400000 * land.getRentRenew());
    		
    		if(land.isRented() && nextPaymentTime < now.getTimeInMillis()) {
    			
    			//Check if the tenant has enough money or time limit whit no auto renew 
    			if(Factoid.getPlayerMoney().getPlayerBalance(land.getTenant().getOfflinePlayer(), land.getWorldName()) < land.getRentPrice()
    					|| !land.getRentAutoRenew()) {
    				
					// Unrent
					land.unSetRented();
					try {
						new EcoSign(land, land.getRentSignLoc()).createSignForRent(
								land.getRentPrice(), land.getRentRenew(),
								land.getRentAutoRenew(), null);
					} catch (SignException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    			} else {
    			
    				// renew rent
    				Factoid.getPlayerMoney().getFromPlayer(land.getTenant().getOfflinePlayer(), 
    					land.getWorldName(), land.getRentPrice());
    				if(land.getOwner() instanceof PlayerContainerPlayer) {
        				Factoid.getPlayerMoney().giveToPlayer(((PlayerContainerPlayer)land.getOwner()).getOfflinePlayer(), 
        					land.getWorldName(), land.getRentPrice());
    				}
    				land.setLastPaymentTime(new Timestamp(now.getTime().getTime()));
    			}
    		}
    	}
    }
}
