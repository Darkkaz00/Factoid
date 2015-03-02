package me.tabinol.factoid.listeners;

import me.tabinol.factoid.BKVersion;
import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.config.players.PlayerConfEntry;
import me.tabinol.factoid.config.players.PlayerStaticConfig;
import me.tabinol.factoid.parameters.PermissionList;
import me.tabinol.factoidapi.lands.IDummyLand;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

/**
 * Players listener (for 1.8+)
 */
public class PlayerListener18 extends CommonListener implements Listener {

	/** The player conf. */
	private PlayerStaticConfig playerConf;

	/**
	 * Instantiates a new player listener.
	 */
	public PlayerListener18() {

		super();
		playerConf = Factoid.getThisPlugin().iPlayerConf();
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {

		IDummyLand land;
		EntityType et = event.getRightClicked().getType();
		Player player = event.getPlayer();
		Material mat = player.getItemInHand().getType();
		PlayerConfEntry entry;
		Location loc = event.getRightClicked().getLocation();

		Factoid.getThisPlugin().iLog().write(
				"PlayerInteractAtEntity player name: " + event.getPlayer().getName()
						+ ", Entity: " + et.name());

		// Citizen bug, check if entry exist before
		if ((entry = playerConf.get(player)) != null
				&& !entry.isAdminMod()) {
			land = Factoid.getThisPlugin().iLands().getLandOrOutsideArea(loc);
			
			// Remove and add an item from an armor stand
			if(BKVersion.isArmorStand(et)) {
				if (((!checkPermission(land, event.getPlayer(), PermissionList.BUILD.getPermissionType())
						|| !checkPermission(land, event.getPlayer(), PermissionList.BUILD_DESTROY.getPermissionType()))
						&& mat == Material.AIR)
						|| ((!checkPermission(land, event.getPlayer(), PermissionList.BUILD.getPermissionType())
								|| !checkPermission(land, event.getPlayer(), PermissionList.BUILD_PLACE.getPermissionType()))
								&& mat != Material.AIR)) {
					messagePermission(player);
					event.setCancelled(true);
				}
			}
		}
	}
}
