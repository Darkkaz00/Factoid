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
import me.tabinol.factoid.commands.ChatPage;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoidapi.parameters.IFlagType;
import me.tabinol.factoidapi.parameters.ILandFlag;

import org.bukkit.ChatColor;


/**
 * The Class CommandFlag.
 */
public class CommandFlag extends CommandExec {

    /**
     * Instantiates a new command flag.
     *
     * @param entity the entity
     * @throws FactoidCommandException the factoid command exception
     */
    public CommandFlag(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, true);
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws FactoidCommandException {

        checkSelections(true, null);
                String curArg = entity.argList.getNext();

        /*
        if (entity.argList.length() < 2) {

            entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.FLAGS.JOINMODE"));
            Factoid.getThisPlugin().iLog().write("PlayerSetFlagUI for " + entity.playerName);
            entity.player.sendMessage(ChatColor.DARK_GRAY + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.FLAGS.HINT"));
            CuboidArea area = Factoid.getThisPlugin().iLands().getCuboidArea(entity.player.getLocation());
            LandSetFlag setting = new LandSetFlag(entity.player, area);
            entity.playerConf.setSetFlagUI(setting);
            
                    
        } else 
        */ 
        
        if (curArg.equalsIgnoreCase("set")) {

            // Permission check is on getFlagFromArg
            
            ILandFlag landFlag = entity.argList.getFlagFromArg(entity.playerConf.isAdminMod(), land.isOwner(entity.player));
            
            if(!landFlag.getFlagType().isRegistered()) {
            	throw new FactoidCommandException("Flag not registered", entity.player, "COMMAND.FLAGS.FLAGNULL");
            }
            
            ((Land)land).addFlag(landFlag);
            entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + 
            Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.FLAGS.ISDONE", landFlag.getFlagType().toString(), 
                    landFlag.getValue().getValuePrint() + ChatColor.YELLOW));
            Factoid.getThisPlugin().iLog().write("Flag set: " + landFlag.getFlagType().toString() + ", value: " + 
                    landFlag.getValue().getValueString());

        } else if (curArg.equalsIgnoreCase("unset")) {
        
            IFlagType flagType = entity.argList.getFlagTypeFromArg(entity.playerConf.isAdminMod(), land.isOwner(entity.player));
            if (!land.removeFlag(flagType)) {
                throw new FactoidCommandException("Flags", entity.player, "COMMAND.FLAGS.REMOVENOTEXIST");
            }
            entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.FLAGS.REMOVEISDONE", flagType.toString()));
            Factoid.getThisPlugin().iLog().write("Flag unset: " + flagType.toString());
        
        } else if (curArg.equalsIgnoreCase("list")) {

            StringBuilder stList = new StringBuilder();
            if (!land.getFlags().isEmpty()) {
                for (ILandFlag flag : land.getFlags()) {
                    if (stList.length() != 0) {
                        stList.append(" ");
                    }
                    stList.append(flag.getFlagType().getPrint()).append(":").append(flag.getValue().getValuePrint());
                }
                stList.append(Config.NEWLINE);
            } else {
                entity.player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.FLAGS.LISTROWNULL"));
            }
            new ChatPage("COMMAND.FLAGS.LISTSTART", stList.toString(), entity.player, land.getName()).getPage(1);

        } else {
            throw new FactoidCommandException("Missing information command", entity.player, "GENERAL.MISSINGINFO");
        }
    }
}
