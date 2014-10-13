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
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoidapi.lands.ILand;
import me.tabinol.factoid.lands.approve.Approve;
import me.tabinol.factoid.lands.approve.ApproveList;
import me.tabinol.factoidapi.lands.areas.ICuboidArea;
import me.tabinol.factoid.lands.collisions.Collisions;
import me.tabinol.factoidapi.playercontainer.IPlayerContainer;

import org.bukkit.ChatColor;


/**
 * The Class CommandApprove.
 */
public class CommandApprove extends CommandExec {

    /**
     * Instantiates a new command approve.
     *
     * @param entity the entity
     * @throws FactoidCommandException the factoid command exception
     */
    public CommandApprove(CommandEntities entity) throws FactoidCommandException {

        super(entity, true, true);
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws FactoidCommandException {

        String curArg = entity.argList.getNext();
        ApproveList approveList = Factoid.getThisPlugin().iLands().getApproveList();
        boolean isApprover = entity.sender.hasPermission("factoid.collisionapprove");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        if (curArg.equalsIgnoreCase("clear")) {
            
            if(!isApprover) {
            	throw new FactoidCommandException("Approve", entity.sender, "GENERAL.MISSINGPERMISSION");
            }
            approveList.removeAll();
            entity.sender.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COLLISION.GENERAL.CLEAR"));
            
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
                    stList.append(ChatColor.WHITE + Factoid.getThisPlugin().iLanguage().getMessage("COLLISION.SHOW.LIST",
                            ChatColor.BLUE + df.format(app.getDateTime().getTime()) + ChatColor.WHITE,
                            ChatColor.BLUE + app.getLandName() + ChatColor.WHITE,
                            app.getOwner().getPrint() + ChatColor.WHITE,
                            ChatColor.BLUE + app.getAction().toString() + ChatColor.WHITE));
                    stList.append(Config.NEWLINE);
                    t++;
                }
            }
            if (t == 0) {

                // List empty
                entity.sender.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COLLISION.SHOW.LISTROWNULL"));
            } else {

                // List not empty
                new ChatPage("COLLISION.SHOW.LISTSTART", stList.toString(), entity.sender, null).getPage(1);
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

            ILand land = Factoid.getThisPlugin().iLands().getLand(param);
            Collisions.LandAction action = approve.getAction();
            int removeId = approve.getRemovedAreaId();
            ICuboidArea newArea = approve.getNewArea();
            ILand parent = approve.getParent();
            Double price = approve.getPrice();
            boolean mustPay = approve.isMustPay();
            IPlayerContainer owner = approve.getOwner();

            if (curArg.equalsIgnoreCase("info") || curArg.equalsIgnoreCase("confirm")) {

                // Print area
                if(newArea != null) {
                    entity.sender.sendMessage(newArea.getPrint());
                }
                
                // Info on the specified land (Collision)
                checkCollision(param, land, action, removeId, newArea, parent, owner, price, mustPay, false);

                if (curArg.equalsIgnoreCase("confirm")) {

                    // Create the action (if it is possible)
                    approveList.removeApprove(approve);
                    approve.createAction();
                    entity.sender.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COLLISION.GENERAL.DONE"));
                }
            } else if (curArg.equalsIgnoreCase("cancel")) {

                // Remove in approve list
                approveList.removeApprove(approve);
                entity.sender.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COLLISION.GENERAL.REMOVE"));
            } else {
                throw new FactoidCommandException("Approve", entity.sender, "GENERAL.MISSINGPERMISSION");
            }
        } else {
            throw new FactoidCommandException("Missing information command", entity.sender, "GENERAL.MISSINGINFO");
        }
    }
}
