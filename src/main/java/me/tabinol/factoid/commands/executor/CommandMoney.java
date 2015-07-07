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

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.CommandEntities;
import me.tabinol.factoid.commands.CommandExec;
import me.tabinol.factoid.commands.InfoCommand;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.minecraft.Economy;
import me.tabinol.factoid.parameters.PermissionList;
import me.tabinol.factoid.utilities.ChatStyle;


/**
 * The Class CommandMoney.
 */
@InfoCommand(name="money", forceParameter=true)
public class CommandMoney extends CommandExec {

    /** The player money. */
    private final Economy playerMoney;

    /**
     * Instantiates a new command money.
     *
     * @param entity the entity
     * @throws FactoidCommandException the factoid command exception
     */
    public CommandMoney(CommandEntities entity) throws FactoidCommandException {

        super(entity);
        playerMoney = Factoid.getPlayerMoney();
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.commands.executor.CommandInterface#commandExecute()
     */
    public void commandExecute() throws FactoidCommandException {

        if (playerMoney == null) {

            throw new FactoidCommandException("Economy not avalaible", entity.sender, "COMMAND.ECONOMY.NOTAVAILABLE");
        }

        checkSelections(true, null);

        String curArg = entity.argList.getNext();

        if (curArg.equalsIgnoreCase("balance")) {
            balance();
        } else if (curArg.equalsIgnoreCase("deposit")) {
            deposit();
        } else if (curArg.equalsIgnoreCase("withdraw")) {
            withdraw();
        } else {
            throw new FactoidCommandException("Missing information command", entity.sender, "GENERAL.MISSINGINFO");
        }
    }

    /**
     * Balance.
     *
     * @throws FactoidCommandException the factoid command exception
     */
    private void balance() throws FactoidCommandException {

        checkPermission(true, false, PermissionList.MONEY_BALANCE.getPermissionType(), null);
        entity.sender.sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.ECONOMY.LANDBALANCE",
                land.getName(), playerMoney.toFormat(land.getMoney())));
    }

    /**
     * Deposit.
     *
     * @throws FactoidCommandException the factoid command exception
     */
    private void deposit() throws FactoidCommandException {

        checkPermission(true, false, PermissionList.MONEY_DEPOSIT.getPermissionType(), null);

        double amount = getAmountFromCommandLine();

        // Amount is valid?
        if (amount > playerMoney.getPlayerBalance(entity.player, land.getWorldName())) {
            throw new FactoidCommandException("Invalid amount", entity.sender, "COMMAND.ECONOMY.INVALIDAMOUNT");
        }

        // Land Deposit
        playerMoney.getFromPlayer(entity.player, land.getWorldName(), amount);
        land.addMoney(amount);
        entity.sender.sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.ECONOMY.LANDDEPOSIT",
                playerMoney.toFormat(land.getMoney()), land.getName()));
    }

    /**
     * Withdraw.
     *
     * @throws FactoidCommandException the factoid command exception
     */
    private void withdraw() throws FactoidCommandException {

        checkPermission(true, false, PermissionList.MONEY_WITHDRAW.getPermissionType(), null);

        double amount = getAmountFromCommandLine();

        // Amount is valid?
        if (amount > land.getMoney()) {
            throw new FactoidCommandException("Invalid amount", entity.sender, "COMMAND.ECONOMY.INVALIDAMOUNT");
        }

        // Land Deposit
        land.substractMoney(amount);
        playerMoney.giveToPlayer(entity.player, land.getWorldName(), amount);
        entity.sender.sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.ECONOMY.LANDWITHDRAW",
                playerMoney.toFormat(land.getMoney()), land.getName()));
    }

    /**
     * Gets the amount from command line.
     *
     * @return the amount from command line
     * @throws FactoidCommandException the factoid command exception
     */
    private double getAmountFromCommandLine() throws FactoidCommandException {

        double ret = 0;
        boolean err = false;

        try {
            ret = Double.parseDouble(entity.argList.getNext());
            if (ret <= 0) {
                // Amount is 0 or less
                err = true;
            }
        } catch (NullPointerException ex) {
            // Amount is null
            err = true;
        } catch (NumberFormatException ex) {
            // Amount is unreadable
            err = true;
        }

        if (err) {
            throw new FactoidCommandException("Invalid amount", entity.sender, "COMMAND.ECONOMY.INVALIDAMOUNT");
        }

        return ret;
    }
}
