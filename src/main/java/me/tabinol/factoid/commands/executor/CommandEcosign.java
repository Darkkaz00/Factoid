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
import me.tabinol.factoid.commands.CommandExec;
import me.tabinol.factoid.economy.EcoSign;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.exceptions.SignException;
import me.tabinol.factoid.parameters.PermissionList;
import me.tabinol.factoid.playercontainer.PlayerContainerPlayer;
import me.tabinol.factoid.utilities.ChatStyle;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.listeners.CommonListener.Click;
import me.tabinol.factoid.minecraft.FPlayer;

public class CommandEcosign extends CommandExec {

	public enum SignType {
		SALE, RENT;
	}

	/** The player. */
	private final FPlayer player;

	/** The player conf. */
	private final Click click;
	private final SignType signType;

	// Called from PlayerListener (right or leftclick)
	public CommandEcosign(FPlayer player, Land land, Click click,
			SignType signType) throws FactoidCommandException {

		super(null);
		this.player = player;
		this.land = land;
		this.click = click;
		this.signType = signType;
	}

	/* (non-Javadoc)
	 * @see me.tabinol.factoid.commands.executor.CommandInterface#commandExecute()
	 */
	public void commandExecute() throws FactoidCommandException {

		if (click == Click.RIGHT) {
			if (signType == SignType.SALE) {

				// Buy a land
				if (!land.checkPermissionAndInherit(player,
						PermissionList.ECO_LAND_BUY.getPermissionType())) {
					throw new FactoidCommandException("No permission to do this action", player, "GENERAL.MISSINGPERMISSION");
				}
				if (Factoid.getPlayerMoney().getPlayerBalance(player,
						land.getWorldName()) < land.getSalePrice()) {
					throw new FactoidCommandException("Not enough money to buy a land", player, "COMMAND.ECONOMY.NOTENOUGHMONEY");
				}
				Factoid.getPlayerMoney().getFromPlayer(player,
						land.getWorldName(), land.getSalePrice());
				if (land.getOwner() instanceof PlayerContainerPlayer) {
					Factoid.getPlayerMoney()
							.giveToPlayer(
									((PlayerContainerPlayer) land.getOwner())
											.getOfflinePlayer(),
									land.getWorldName(), land.getSalePrice());
				}
				try {
					new EcoSign(land, land.getSaleSignLoc()).removeSign();
				} catch (SignException e) {
					// Real Error
					e.printStackTrace();
				}
				((Land) land).setForSale(false, 0, null);
				land.setOwner(player.getPlayerContainer());
		        player.sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.ECONOMY.BUYLAND",
		        		land.getName()));
		        Factoid.getFactoidLog().write("The land " + land.getName() + " is purchased by : " + player.getName());
			} else {

				// Rent and unrent
				if (land.isRented()
						&& (land.getTenant().hasAccess(player) || land.getOwner().hasAccess(player)
								|| player.isAdminMod())) {

					// Unrent
					((Land) land).unSetRented();
					try {
						new EcoSign(land, land.getRentSignLoc()).createSignForRent(
								land.getRentPrice(), land.getRentRenew(),
								land.getRentAutoRenew(), null);
					} catch (SignException e) {
						// Real Error
						e.printStackTrace();
					}
			        player.sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.ECONOMY.UNRENTLAND",
			        		land.getName()));
			        Factoid.getFactoidLog().write("The land " + land.getName() + " is unrented by : " + player.getName());
				
				} else if (!land.isRented()) {

					// Rent
					if (!land.checkPermissionAndInherit(player,
							PermissionList.ECO_LAND_RENT.getPermissionType())) {
						throw new FactoidCommandException("No permission to do this action", player, "GENERAL.MISSINGPERMISSION");
					}
					if (Factoid.getPlayerMoney().getPlayerBalance(player,
							land.getWorldName()) < land.getRentPrice()) {
						throw new FactoidCommandException("Not enough money to rent a land", player, "COMMAND.ECONOMY.NOTENOUGHMONEY");
					}
					Factoid.getPlayerMoney().getFromPlayer(player,
							land.getWorldName(), land.getRentPrice());
					if (land.getOwner() instanceof PlayerContainerPlayer) {
						Factoid.getPlayerMoney()
								.giveToPlayer(
										((PlayerContainerPlayer) land
												.getOwner()).getOfflinePlayer(),
										land.getWorldName(),
										land.getRentPrice());
					}
					((Land) land).setRented(player.getPlayerContainer());
					try {
						new EcoSign(land, land.getRentSignLoc()).createSignForRent(
								land.getRentPrice(), land.getRentRenew(),
								land.getRentAutoRenew(), player.getName());
					} catch (SignException e) {
						// Real Error
						e.printStackTrace();
					}
			        player.sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.ECONOMY.RENTLAND",
			        		land.getName()));
			        Factoid.getFactoidLog().write("The land " + land.getName() + " is rented by : " + player.getName());
				}
			}
		} else {

			// Left Click, destroy the sign
			if (land.getOwner().hasAccess(player) || player.isAdminMod()) {
				
				if (signType == SignType.SALE) {

					// Destroy sale sign
					try {
						new EcoSign(land, land.getSaleSignLoc()).removeSign();
					} catch (SignException e) {
						// Real Error
						e.printStackTrace();
					}
					((Land) land).setForSale(false, 0, null);
			        player.sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.ECONOMY.UNFORSALE", 
			        		land.getName()));
			        Factoid.getFactoidLog().write("The land " + land.getName() + " is no longer for sale by : " + player.getName());
				} else {

					// Destroy rent sign
					try {
						new EcoSign(land, land.getRentSignLoc()).removeSign();
					} catch (SignException e) {
						// Real Error
						e.printStackTrace();
					}
					((Land) land).unSetRented();
					((Land) land).unSetForRent();
			        player.sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.ECONOMY.UNFORRENT",
			        		land.getName()));
			        Factoid.getFactoidLog().write("The land " + land.getName() + " is no longer for rent by : " + player.getName());
				}
			}
		}
	}
}
