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
import me.tabinol.factoid.exceptions.FactoidCommandException;
import me.tabinol.factoidapi.lands.ILand;
import me.tabinol.factoid.parameters.PermissionList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


/**
 * The Class CommandKick.
 */
public class CommandKick extends CommandExec {

    /** The arg list. */
    private final ArgList argList;
    
    /** The player. */
    private final Player player;

    /**
     * Instantiates a new command kick.
     *
     * @param entity the entity
     * @throws FactoidCommandException the factoid command exception
     */
    public CommandKick(CommandEntities entity) throws FactoidCommandException {

        super(entity, false, true);
        argList = entity.argList;
        player = entity.player;

    }
    
    // From other command
    /**
     * Instantiates a new command kick.
     *
     * @param player the player
     * @param argList the arg list
     * @param land the land
     * @throws FactoidCommandException the factoid command exception
     */
    public CommandKick(Player player, ArgList argList, ILand land) throws FactoidCommandException {
        
        super(null, false, false);
        this.argList = argList;
        this.player = player;
        this.land = land;
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.commands.executor.CommandInterface#commandExecute()
     */
    @Override
    public void commandExecute() throws FactoidCommandException {

        String playerKickName = argList.getNext();

        getLandFromCommandIfNoLandSelected();

        // Only if it is from Kick command
        if (entity != null) {
            checkSelections(true, null);
            checkPermission(true, true, PermissionList.LAND_KICK.getPermissionType(), null);
        }

        // No player name?
        if (playerKickName == null) {
            throw new FactoidCommandException("Kicked", player, "COMMAND.KICK.PLAYERNULL");
        }

        @SuppressWarnings("deprecation")
		Player playerKick = Factoid.getThisPlugin().getServer().getPlayer(playerKickName);

        // Player not in land?
        if (playerKick == null || !land.isPlayerinLandNoVanish(playerKick, player)
                || Factoid.getThisPlugin().iPlayerConf().get(playerKick).isAdminMod()) {
            throw new FactoidCommandException("Kicked", player, "COMMAND.KICK.NOTINLAND");
        }
        
        //Kick the player
        playerKick.teleport(player.getLocation().getWorld().getSpawnLocation());
        player.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.KICK.DONE", playerKickName, land.getName()));
        playerKick.sendMessage(ChatColor.YELLOW + "[Factoid] " + Factoid.getThisPlugin().iLanguage().getMessage("COMMAND.KICK.KICKED", land.getName()));
        Factoid.getThisPlugin().iLog().write("Player " + playerKick + " kicked from " + land.getName() + ".");
    }
}
