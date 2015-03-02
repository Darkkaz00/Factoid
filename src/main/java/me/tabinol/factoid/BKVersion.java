package me.tabinol.factoid;

import java.util.EnumSet;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * This class is for compatibility to BK 1.7.x 
 *
 */
public class BKVersion {
	
	private static boolean existPlayerInteractAtEntityEvent = false;
	
	private static GameMode spectatorMode = null;
	
	private static EnumSet<Material> doors = EnumSet.noneOf(Material.class);
	
	private static Material armorStand = null;
	
	private static EntityType armorStandEntity = null;
	
	
	protected static void initVersion() {
		
		// org.bukkit.event.player.PlayerInteractAtEntityEvent (for ArmorStand)
		try {
			Class<?> plInAtEnEv = Class.forName("org.bukkit.event.player.PlayerInteractAtEntityEvent");
			if(plInAtEnEv != null) {
				existPlayerInteractAtEntityEvent = true;
			}
			
		} catch (ClassNotFoundException ex) {
			// This is 1.7 version
		}
		
		// Spectator mode
		try {
			spectatorMode = GameMode.valueOf("SPECTATOR");
		} catch (IllegalArgumentException ex) {
			// This is 1.7 version 
		}
		
		// Doors
		doors.add(Material.WOODEN_DOOR);
		doors.add(Material.TRAP_DOOR);
		doors.add(Material.FENCE_GATE);
		try {
			doors.add(Material.valueOf("SPRUCE_DOOR"));
			doors.add(Material.valueOf("SPRUCE_FENCE_GATE"));
			doors.add(Material.valueOf("BIRCH_DOOR"));
			doors.add(Material.valueOf("BIRCH_FENCE_GATE"));
			doors.add(Material.valueOf("JUNGLE_DOOR"));
			doors.add(Material.valueOf("JUNGLE_FENCE_GATE"));
			doors.add(Material.valueOf("ACACIA_DOOR"));
			doors.add(Material.valueOf("ACACIA_FENCE_GATE"));
			doors.add(Material.valueOf("DARK_OAK_DOOR"));
			doors.add(Material.valueOf("DARK_OAK_FENCE_GATE"));
		} catch (IllegalArgumentException ex) {
			// This is 1.7 version 
		}
		
		// ArmorStand
		try {
			armorStand = Material.valueOf("ARMOR_STAND");
			armorStandEntity = EntityType.valueOf("ARMOR_STAND");
		} catch (IllegalArgumentException ex) {
			// This is 1.7 version 
		}
		
	}
	
	public static boolean isPlayerInteractAtEntityEventExist() {
		
		return existPlayerInteractAtEntityEvent;
	}
	
	public static boolean isSpectatorMode(Player player) {
		
		return player.getGameMode() == spectatorMode;
	}
	
	public static boolean isDoor(Material material) {
		
		return doors.contains(material);
	}
	
	public static boolean isArmorStand(Material material) {
		
		return armorStand != null ? material == armorStand : false;
	}

	public static boolean isArmorStand(EntityType entityType) {
		
		return armorStandEntity != null ? entityType == armorStandEntity : false;
	}
}
