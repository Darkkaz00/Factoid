package me.tabinol.factoid.listeners;

import java.util.Set;
import java.util.TreeSet;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.config.Config;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.minecraft.FPlayer;
import me.tabinol.factoid.utilities.ChatStyle;
import me.tabinol.factoid.utilities.ColoredConsole;

/**
 * 
 * Chat listener
 *
 */
public class ChatListener extends CommonListener {

    /** The conf. */
    private final Config conf;

	/**
     * Instantiates a new chat listener.
     */
    public ChatListener() {

        super();
        conf = Factoid.getConf();
    }
    
    public boolean onAsyncPlayerChat(FPlayer player, String message) {
    	
    	if(!conf.isLandChat()) {
    		return false;
    	}
    	
    	String firstChar = message.substring(0, 1);
    	
    	// Chat in a land
    	if(firstChar.equals("=") || firstChar.equals(">") || firstChar.equals("<")) {
			
			Land land = Factoid.getLands().getLand(player.getLocation());
    		
    		// The player is not in a land
    		if(land == null) {
				player.sendMessage(ChatStyle.RED + "[Factoid] "
						+ Factoid.getLanguage().getMessage("CHAT.OUTSIDE"));
				return true;
    		}
    		
    		// Return if the player is muted
    		if(Factoid.getDependPlugin().getChat().isMuted(player)) {
    			return true;
    		}
    		
    		// Get users list
    		Set<FPlayer> playersToMsg;
    		
    		if(firstChar.equals("=")) {
    			playersToMsg = copyWithSpy(land.getPlayersInLand());
    		} else if(firstChar.equals("<")) {
    			playersToMsg = copyWithSpy(land.getPlayersInLandAndChildren());
    		} else { // ">"
    			playersToMsg = copyWithSpy(land.getAncestor(land.getGenealogy()).getPlayersInLandAndChildren());
    		}
    		
    		String messageSend = message.substring(1);
    		
    		// send messages
 			ColoredConsole.info( ChatStyle.WHITE + "[" + player.getDisplayName()
					+ ChatStyle.WHITE + " " + firstChar + " " + "'" 
					+ ChatStyle.GREEN + land.getName() + ChatStyle.WHITE + "'] "
					+ ChatStyle.GRAY + messageSend);
    		for(FPlayer playerToMsg : playersToMsg) {
    			playerToMsg.sendMessage(ChatStyle.WHITE + "[" + player.getDisplayName()
    					+ ChatStyle.WHITE + " " + firstChar + " " + "'" 
    					+ ChatStyle.GREEN + land.getName() + ChatStyle.WHITE + "'] "
    					+ ChatStyle.GRAY + messageSend);
    		}
    		return true;
    	}
    	return false;
    }
    
    private Set<FPlayer> copyWithSpy(Set<FPlayer> a) {
    	
    	Set<FPlayer> listSet = new TreeSet<FPlayer>();
    	
    	for(FPlayer player : a) {
    		listSet.add(player);
    	}
    	for(FPlayer player : Factoid.getServerCache().getPlayers()) {
    		if(Factoid.getDependPlugin().getChat().isSpy(player)) {
    			listSet.add(player);
    		}
    	}
    	
    	return listSet;
    }
}
