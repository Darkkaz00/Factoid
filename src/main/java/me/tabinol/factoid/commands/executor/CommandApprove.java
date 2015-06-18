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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.commands.ChatPage;
import me.tabinol.factoid.commands.CommandEntities;
import me.tabinol.factoid.commands.CommandExec;
import me.tabinol.factoid.commands.InfoCommand;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.approve.Approve;
import me.tabinol.factoid.lands.approve.ApproveList;
import me.tabinol.factoid.lands.areas.CuboidArea;
import me.tabinol.factoid.lands.collisions.Collisions;
import me.tabinol.factoid.playercontainer.PlayerContainer;
import me.tabinol.factoid.utilities.ChatStyle;

/**
 * The Class CommandApprove.
 */
@InfoCommand(name="approve", allowConsole=true, forceParameter=true)
public class CommandApprove extends CommandExec {

    /**
     * Instantiates a new command approve.
     *
     * @param entity the entity
     * @throws FactoidCommandException the factoid command exception
     */
    public CommandApprove(CommandEntities entity) throws FactoidCommandException {

        super(entity);
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws FactoidCommandException {

        String curArg = entity.argList.getNext();
        ApproveList approveList = Factoid.getLands().getApproveList();
        boolean isApprover = entity.sender.hasPermission("factoid.collisionapprove");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        if (curArg.equalsIgnoreCase("clear")) {
            
            if(!isApprover) {
            	throw new FactoidCommandException("Approve", entity.sender, "GENERAL.MISSINGPERMISSION");
            }
            approveList.removeAll();
            entity.sender.sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COLLISION.GENERAL.CLEAR"));
            
        } else if (curArg.equalsIgnoreCase("list")) {

            // List of Approve
            StringBuilder stList = new StringBuilder();
            int t = 0;
            TreeMap<Date,Approve> approveTree = new TreeMap<Date,Approve>();
            
            //create list (short by date/time)
            for (Approve app : approveList.getApproveList().values()) {
                approveTree.put(app.getDateTime().getTime(), app);
            }
            
            // show Approve List
            for(Map.Entry<Date,Approve> approveEntry : approveTree.descendingMap().entrySet()) {
                Approve app = approveEntry.getValue();
                if (app != null && (isApprover || app.getOwner().hasAccess(entity.player))) {
                    stList.append(ChatStyle.WHITE + Factoid.getLanguage().getMessage("COLLISION.SHOW.LIST",
                            ChatStyle.BLUE + df.format(app.getDateTime().getTime()) + ChatStyle.WHITE,
                            ChatStyle.BLUE + app.getLandName() + ChatStyle.WHITE,
                            app.getOwner().getPrint() + ChatStyle.WHITE,
                            ChatStyle.BLUE + app.getAction().toString() + ChatStyle.WHITE));
                    stList.append(Config.NEWLINE);
                    t++;
                }
            }
            if (t == 0) {

                // List empty
                entity.sender.sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COLLISION.SHOW.LISTROWNULL"));
            } else {

                // List not empty
                new ChatPage("COLLISION.SHOW.LISTSTART", stList.toString(), entity.sender, null).getPage();
            }
        } else if (curArg.equalsIgnoreCase("info") || curArg.equalsIgnoreCase("confirm") || curArg.equalsIgnoreCase("cancel")) {

            String param = entity.argList.getNext();

            if (param == null) {
                throw new FactoidCommandException("Approve", entity.sender, "COLLISION.SHOW.PARAMNULL");
            }

            Approve approve = approveList.getApprove(param);

            if (approve == null) {
                throw new FactoidCommandException("Approve", entity.sender, "COLLISION.SHOW.PARAMNULL");
            }

            // Check permission
            if ((curArg.equalsIgnoreCase("confirm") && !isApprover)
                    || ((curArg.equalsIgnoreCase("cancel") || curArg.equalsIgnoreCase("info"))
                    && !(isApprover || approve.getOwner().hasAccess(entity.player)))) {
                throw new FactoidCommandException("Approve", entity.sender, "GENERAL.MISSINGPERMISSION");
            }

            Land land = Factoid.getLands().getLand(param);
            Collisions.LandAction action = approve.getAction();
            int removeId = approve.getRemovedAreaId();
            CuboidArea newArea = approve.getNewArea();
            Land parent = approve.getParent();
            Double price = approve.getPrice();
            PlayerContainer owner = approve.getOwner();

            if (curArg.equalsIgnoreCase("info") || curArg.equalsIgnoreCase("confirm")) {

                // Print area
                if(newArea != null) {
                    entity.sender.sendMessage(newArea.getPrint());
                }
                
                // Info on the specified land (Collision)
                checkCollision(param, land, null, action, removeId, newArea, parent, owner, price, false);

                if (curArg.equalsIgnoreCase("confirm")) {

                    // Create the action (if it is possible)
                    approveList.removeApprove(approve);
                    approve.createAction();
                    entity.sender.sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COLLISION.GENERAL.DONE"));
                }
            } else if (curArg.equalsIgnoreCase("cancel")) {

                // Remove in approve list
                approveList.removeApprove(approve);
                entity.sender.sendMessage(ChatStyle.YELLOW + "[Factoid] " + Factoid.getLanguage().getMessage("COLLISION.GENERAL.REMOVE"));
            } else {
                throw new FactoidCommandException("Approve", entity.sender, "GENERAL.MISSINGPERMISSION");
            }
        } else {
            throw new FactoidCommandException("Missing information command", entity.sender, "GENERAL.MISSINGINFO");
        }
    }
}
