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
import me.tabinol.factoid.commands.ArgList;
import me.tabinol.factoid.commands.CommandEntities;
import me.tabinol.factoid.commands.CommandExec;
import me.tabinol.factoid.commands.InfoCommand;
import me.tabinol.factoid.config.players.PlayerConfEntry;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoidapi.lands.ILand;
import me.tabinol.factoidapi.lands.areas.ICuboidArea;
import me.tabinol.factoid.parameters.PermissionList;
import me.tabinol.factoidapi.playercontainer.IPlayerContainer;
import me.tabinol.factoid.selection.PlayerSelection.SelectionType;
import me.tabinol.factoid.selection.region.ActiveAreaSelection;
import me.tabinol.factoid.selection.region.AreaSelection;
import me.tabinol.factoid.selection.region.LandSelection;

import org.bukkit.ChatStyle;
import org.bukkit.Location;
import org.bukkit.entity.Player;


/**
 * The Class CommandSelect.
 */
@InfoCommand(name="select")
public class CommandSelect extends CommandExec {

    /** The player. */
    private final Player player;
    
    /** The location. */
    private final Location location;
    
    /** The player conf. */
    private final PlayerConfEntry playerConf;
    
    /** The arg list. */
    private final ArgList argList;

    /**
     * Instantiates a new command select.
     *
     * @param entity the entity
     * @throws FactoidCommandException the factoid command exception
     */
    public CommandSelect(CommandEntities entity) throws FactoidCommandException {

        super(entity);
        player = entity.player;
        location = null;
        playerConf = entity.playerConf;
        argList = entity.argList;
    }

    // Called from player action, not a command
    /**
     * Instantiates a new command select.
     *
     * @param player the player
     * @param argList the arg list
     * @param location the location
     * @throws FactoidCommandException the factoid command exception
     */
    public CommandSelect(Player player, ArgList argList, Location location) throws FactoidCommandException {

        super(null);
        this.player = player;
        this.location = location;
        playerConf = Factoid.getPlayerConf().get(player);
        this.argList = argList;
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws FactoidCommandException {

        // Done nothing but for future use
        checkSelections(null, null);

        String curArg;

        if (playerConf.getSelection().getCuboidArea() == null) {
            Factoid.getLog().write(player.getName() + " join select mode");

            if (!argList.isLast()) {

                curArg = argList.getNext();
                if (curArg.equalsIgnoreCase("worldedit")) {
                    if (Factoid.getDependPlugin().getWorldEdit() == null) {
                        throw new FactoidCommandException("CommandSelect", player, "COMMAND.SELECT.WORLDEDIT.NOTLOAD");
                    }
                    new CommandSelectWorldedit(player, playerConf).MakeSelect();

                } else {

                    ILand landtest;
                    if (curArg.equalsIgnoreCase("here")) {

                        // add select Here to select the the cuboid
                        if (location != null) {

                            // With an item
                            landtest = Factoid.getLands().getLand(location);
                        } else {

                            // Player location
                            landtest = Factoid.getLands().getLand(player.getLocation());
                        }

                    } else {

                        landtest = Factoid.getLands().getLand(curArg);
                    }

                    if (landtest == null) {
                        throw new FactoidCommandException("CommandSelect", player, "COMMAND.SELECT.NOLAND");

                    }
                    IPlayerContainer owner = landtest.getOwner();

                    if (!owner.hasAccess(player) && !playerConf.isAdminMod()
                            && !(landtest.checkPermissionAndInherit(player, PermissionList.RESIDENT_MANAGER.getPermissionType())
                            		&& (landtest.isResident(player) || landtest.isOwner(player)))) {
                        throw new FactoidCommandException("CommandSelect", player, "GENERAL.MISSINGPERMISSION");
                    }
                    if (playerConf.getSelection().getLand() == null) {

                        playerConf.getSelection().addSelection(new LandSelection(player, landtest));

                        player.sendMessage(ChatStyle.GREEN + "[Factoid] " + ChatStyle.DARK_GRAY + Factoid.getLanguage().getMessage("COMMAND.SELECT.SELECTEDLAND", landtest.getName()));
                        playerConf.setAutoCancelSelect(true);
                    } else {

                        player.sendMessage(ChatStyle.RED + "[Factoid] " + ChatStyle.DARK_GRAY + Factoid.getLanguage().getMessage("COMMAND.SELECT.ALREADY"));
                    }
                }
            } else {

                player.sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.JOINMODE"));
                player.sendMessage(ChatStyle.DARK_GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.HINT", ChatStyle.ITALIC.toString(), ChatStyle.RESET.toString(), ChatStyle.DARK_GRAY.toString()));
                ActiveAreaSelection select = new ActiveAreaSelection(player);
                playerConf.getSelection().addSelection(select);
                playerConf.setAutoCancelSelect(true);
            }
        } else if ((curArg = argList.getNext()) != null && curArg.equalsIgnoreCase("done")) {

            //if (playerConf.getSelection().getLand() != null) {
            //    throw new FactoidCommandException("CommandSelect", player, "COMMAND.SELECT.CANTDONE");
            //}

            //if (playerConf.getSelection().getCuboidArea() != null) {
                doSelectAreaDone();
            //}

        } else if (curArg != null && curArg.equalsIgnoreCase("info")) {

            doSelectAreaInfo();

        } else {
            throw new FactoidCommandException("CommandSelect", player, "COMMAND.SELECT.ALREADY");
        }
    }

    /**
     * Do select area done.
     *
     * @throws FactoidCommandException the factoid command exception
     */
    private void doSelectAreaDone() throws FactoidCommandException {

        checkSelections(null, true);

        AreaSelection select = (AreaSelection) playerConf.getSelection().getSelection(SelectionType.AREA);
        playerConf.getSelection().addSelection(new AreaSelection(player, select.getCuboidArea()));
        playerConf.setAutoCancelSelect(true);

        if (!select.getCollision()) {

            player.sendMessage(ChatStyle.GREEN + "[Factoid] " + ChatStyle.DARK_GRAY
                    + Factoid.getLanguage().getMessage("COMMAND.SELECT.LAND.NOCOLLISION"));
        } else {
            player.sendMessage(ChatStyle.GREEN + "[Factoid] " + ChatStyle.RED
                    + Factoid.getLanguage().getMessage("COMMAND.SELECT.LAND.COLLISION"));
        }
    }

    /**
     * Do select area info.
     *
     * @throws FactoidCommandException the factoid command exception
     */
    private void doSelectAreaInfo() throws FactoidCommandException {

        checkSelections(null, true);

        double price;

        AreaSelection select = (AreaSelection) playerConf.getSelection().getSelection(SelectionType.AREA);
        ICuboidArea area = select.getCuboidArea();

        player.sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.INFO.INFO1",
                area.getPrint()));
        player.sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.INFO.INFO2",
                area.getTotalBlock() + ""));

        // Price (economy)
        price = playerConf.getSelection().getLandCreatePrice();
        if (price != 0L) {
            player.sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.INFO.INFO3",
                    Factoid.getPlayerMoney().toFormat(price)));
        }
        price = playerConf.getSelection().getAreaAddPrice();
        if (price != 0L) {
            player.sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.INFO.INFO4",
                    Factoid.getPlayerMoney().toFormat(price)));
        }
    }
}
