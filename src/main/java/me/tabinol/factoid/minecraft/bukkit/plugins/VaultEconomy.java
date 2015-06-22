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

package me.tabinol.factoid.minecraft.bukkit.plugins;

import me.tabinol.factoid.minecraft.Economy;
import me.tabinol.factoid.minecraft.FPlayer;
import me.tabinol.factoid.minecraft.bukkit.FPlayerBukkit;

public class VaultEconomy extends Economy {
	
    /** The economy. */
    private final net.milkbowl.vault.economy.Economy economy;

    public VaultEconomy(net.milkbowl.vault.economy.Economy economy) {
    	
    	this.economy = economy;
    }

	@Override
    public Double getPlayerBalance(FPlayer offlinePlayer, String worldName) {
        
        return economy.getBalance(((FPlayerBukkit) offlinePlayer).getOfflinePlayer(), worldName);
    }
    
    @Override
    public boolean giveToPlayer(FPlayer offlinePlayer, String worldName, Double amount) {
        
        return economy.depositPlayer(((FPlayerBukkit) offlinePlayer).getOfflinePlayer(), worldName, amount).transactionSuccess();
    }

    @Override
    public boolean getFromPlayer(FPlayer offlinePlayer, String worldName, Double amount) {
        
        return economy.withdrawPlayer(((FPlayerBukkit) offlinePlayer).getOfflinePlayer(), worldName, amount).transactionSuccess();
    }
    
    @Override
    public String toFormat(Double amount) {
        
        return economy.format(amount);
    }
}
