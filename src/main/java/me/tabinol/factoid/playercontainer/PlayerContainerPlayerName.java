package me.tabinol.factoid.playercontainer;

import me.tabinol.factoidapi.lands.ILand;
import me.tabinol.factoidapi.playercontainer.EPlayerContainerType;
import me.tabinol.factoidapi.playercontainer.IPlayerContainer;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


/**
 * The Class PlayerContainerPlayerName.
 */
public class PlayerContainerPlayerName extends PlayerContainer {

    /**
     * Instantiates a new player container player name.
     *
     * @param name the name
     */
    public PlayerContainerPlayerName(String name) {

        super(name, EPlayerContainerType.PLAYERNAME, false);
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.playercontainer.PlayerContainerInterface#equals(me.tabinol.factoid.playercontainer.PlayerContainer)
     */
    @Override
    public boolean equals(IPlayerContainer container2) {
        
        return container2 instanceof PlayerContainerPlayerName &&
                name.equals(((PlayerContainerPlayer) container2).name);
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.playercontainer.PlayerContainerInterface#copyOf()
     */
    @Override
    public PlayerContainer copyOf() {
        
        return new PlayerContainerPlayerName(name);
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.playercontainer.PlayerContainerInterface#hasAccess(org.bukkit.entity.Player)
     */
    @Override
    public boolean hasAccess(Player player) {
        
         return false;
    }

    @Override
    public boolean hasAccess(Player player, ILand land) {
        
        return hasAccess(player);
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.playercontainer.PlayerContainer#getPrint()
     */
    @Override
    public String getPrint() {

        StringBuilder sb = new StringBuilder();
        
        sb.append(ChatColor.DARK_RED).append("P:");
       	sb.append(ChatColor.GRAY).append(name);

       	return sb.toString();
    }

    /* (non-Javadoc)
     * @see me.tabinol.factoid.playercontainer.PlayerContainerInterface#setLand(me.tabinol.factoid.lands.Land)
     */
    @Override
    public void setLand(ILand land) {

    }
}
