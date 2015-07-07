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
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.areas.CuboidArea;
import me.tabinol.factoid.lands.areas.Point;
import me.tabinol.factoid.minecraft.FPlayer;
import me.tabinol.factoid.parameters.PermissionList;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import me.tabinol.factoid.selection.PlayerSelection.SelectionType;
import me.tabinol.factoid.selection.region.ActiveAreaSelection;
import me.tabinol.factoid.selection.region.AreaSelection;
import me.tabinol.factoid.selection.region.LandSelection;
import me.tabinol.factoid.utilities.ChatStyle;


/**
 * The Class CommandSelect.
 */
@InfoCommand(name="select")
public class CommandSelect extends CommandExec {

    /** The player.getFSender(). */
    private final FPlayer player;
    
    /** The location. */
    private final Point location;
    
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
    public CommandSelect(FPlayer player, ArgList argList, Point location) throws FactoidCommandException {

        super(null);
        this.player = player;
        this.location = location;
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

        if (player.getFSender().getSelection().getCuboidArea() == null) {
            Factoid.getFactoidLog().write(player.getFSender().getName() + " join select mode");

            if (!argList.isLast()) {

                curArg = argList.getNext();
                if (curArg.equalsIgnoreCase("worldedit")) {
                    if (!Factoid.getDependPlugin().getEditWorld().isPluginLoaded()) {
                        throw new FactoidCommandException("CommandSelect", player.getFSender(), "COMMAND.SELECT.WORLDEDIT.NOTLOAD");
                    }
                    Factoid.getDependPlugin().getEditWorld().makeSelect(entity.player);

                } else {

                    Land landtest;
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
                        throw new FactoidCommandException("CommandSelect", player.getFSender(), "COMMAND.SELECT.NOLAND");

                    }
                    PlayerContainer owner = landtest.getOwner();

                    if (!owner.hasAccess(player) && !player.getFSender().isAdminMod()
                            && !(landtest.checkPermissionAndInherit(player, PermissionList.RESIDENT_MANAGER.getPermissionType())
                            		&& (landtest.isResident(player) || landtest.isOwner(player)))) {
                        throw new FactoidCommandException("CommandSelect", player.getFSender(), "GENERAL.MISSINGPERMISSION");
                    }
                    if (player.getFSender().getSelection().getLand() == null) {

                        player.getFSender().getSelection().addSelection(new LandSelection(player, landtest));

                        player.getFSender().sendMessage(ChatStyle.GREEN + "[Factoid] " + ChatStyle.DARK_GRAY + Factoid.getLanguage().getMessage("COMMAND.SELECT.SELECTEDLAND", landtest.getName()));
                        player.getFSender().setAutoCancelSelect(true);
                    } else {

                        player.getFSender().sendMessage(ChatStyle.RED + "[Factoid] " + ChatStyle.DARK_GRAY + Factoid.getLanguage().getMessage("COMMAND.SELECT.ALREADY"));
                    }
                }
            } else {

                player.getFSender().sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.JOINMODE"));
                player.getFSender().sendMessage(ChatStyle.DARK_GRAY + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.HINT", ChatStyle.ITALIC.toString(), ChatStyle.RESET.toString(), ChatStyle.DARK_GRAY.toString()));
                ActiveAreaSelection select = new ActiveAreaSelection(player);
                player.getFSender().getSelection().addSelection(select);
                player.getFSender().setAutoCancelSelect(true);
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
            throw new FactoidCommandException("CommandSelect", player.getFSender(), "COMMAND.SELECT.ALREADY");
        }
    }

    /**
     * Do select area done.
     *
     * @throws FactoidCommandException the factoid command exception
     */
    private void doSelectAreaDone() throws FactoidCommandException {

        checkSelections(null, true);

        AreaSelection select = (AreaSelection) player.getFSender().getSelection().getSelection(SelectionType.AREA);
        player.getFSender().getSelection().addSelection(new AreaSelection(player, select.getCuboidArea()));
        player.getFSender().setAutoCancelSelect(true);

        if (!select.getCollision()) {

            player.getFSender().sendMessage(ChatStyle.GREEN + "[Factoid] " + ChatStyle.DARK_GRAY
                    + Factoid.getLanguage().getMessage("COMMAND.SELECT.LAND.NOCOLLISION"));
        } else {
            player.getFSender().sendMessage(ChatStyle.GREEN + "[Factoid] " + ChatStyle.RED
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

        AreaSelection select = (AreaSelection) player.getFSender().getSelection().getSelection(SelectionType.AREA);
        CuboidArea area = select.getCuboidArea();

        player.getFSender().sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.INFO.INFO1",
                area.getPrint()));
        player.getFSender().sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.INFO.INFO2",
                area.getTotalBlock() + ""));

        // Price (economy)
        price = player.getFSender().getSelection().getLandCreatePrice();
        if (price != 0L) {
            player.getFSender().sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.INFO.INFO3",
                    Factoid.getPlayerMoney().toFormat(price)));
        }
        price = player.getFSender().getSelection().getAreaAddPrice();
        if (price != 0L) {
            player.getFSender().sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COMMAND.SELECT.INFO.INFO4",
                    Factoid.getPlayerMoney().toFormat(price)));
        }
    }
}
