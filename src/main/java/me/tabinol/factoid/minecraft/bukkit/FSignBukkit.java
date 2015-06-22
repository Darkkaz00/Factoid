package me.tabinol.factoid.minecraft.bukkit;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.exceptions.SignException;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.areas.Point;
import me.tabinol.factoid.minecraft.FSign;

public class FSignBukkit implements FSign {

	private final Block block;
	
	/**
	 * For Server class only (already existing sign)
	 * @param block
	 */
	public FSignBukkit(Block block) {
    
		this.block = block;
	}
	
	public FSignBukkit(Point point, float yaw, String[] lines, Land land,
            boolean isWallSign) throws SignException {

		block = BukkitUtils.toLocation(((FWorldBukkit) point.getWorld()).getWorld(), point).getBlock();
		BlockFace facing = signFacing(yaw, isWallSign);

		// Impossible to create the sign here
		if (Factoid.getLands().getLand(point) != land) {
			throw new SignException();
		}

		// Check if the facing block is solid
		if (isWallSign) {
			if(!block.getRelative(facing.getOppositeFace()).getType().isSolid()) {
				throw new SignException();
			}
		} else {
			if(!block.getRelative(BlockFace.DOWN).getType().isSolid()) {
				throw new SignException();
			}
		}
		
		// Determinate material
		Material mat;
		if (isWallSign) {

			mat = Material.WALL_SIGN;
		} else {
			mat = Material.SIGN_POST;
		}

		// Create sign
		block.setType(mat);

		Sign sign = (Sign) block.getState();

		// Add lines
		for (int t = 0; t <= 3; t++) {
			sign.setLine(t, lines[t]);
		}

		// Set facing
		((org.bukkit.material.Sign) sign.getData()).setFacingDirection(facing);
		
		sign.update();
	}

	@Override
    public boolean isWallSign() {

		if(block.getType() == Material.WALL_SIGN) {
			return true;
		} else {
			return false;
		}
    }

	@Override
    public float getYaw() {

		BlockFace facing = ((org.bukkit.material.Sign)((Sign) block.getState()).getData()).getFacing();
		
		if(isWallSign()) {
			if(facing == BlockFace.NORTH) {
				return 0;
			} else if(facing == BlockFace.EAST) {
				return 90;
			} else if(facing == BlockFace.SOUTH) {
				return 180;
			} else {
				return 270;
			}
		} else {
			if (facing == BlockFace.NORTH) {
				return 0;
			} else if (facing == BlockFace.NORTH_NORTH_EAST) {
				return 360 / 16 * 1;
			} else if (facing == BlockFace.NORTH_EAST) {
				return 360 / 16 * 2;
			} else if (facing == BlockFace.EAST_NORTH_EAST) {
				return 360 / 16 * 3;
			} else if (facing == BlockFace.EAST) {
				return 360 / 16 * 4;
			} else if (facing == BlockFace.EAST_SOUTH_EAST) {
				return 360 / 16 * 5;
			} else if (facing == BlockFace.SOUTH_EAST) {
				return 360 / 16 * 6;
			} else if (facing == BlockFace.SOUTH_SOUTH_EAST) {
				return 360 / 16 * 7;
			} else if (facing == BlockFace.SOUTH) {
				return 360 / 16 * 8;
			} else if (facing == BlockFace.SOUTH_SOUTH_WEST) {
				return 360 / 16 * 9;
			} else if (facing == BlockFace.SOUTH_WEST) {
				return 360 / 16 * 10;
			} else if (facing == BlockFace.WEST_SOUTH_WEST) {
				return 360 / 16 * 11;
			} else if (facing == BlockFace.WEST) {
				return 360 / 16 * 12;
			} else if (facing == BlockFace.WEST_NORTH_WEST) {
				return 360 / 16 * 13;
			} else if (facing == BlockFace.NORTH_WEST) {
				return 360 / 16 * 14;
			} else {
				return 360 / 16 * 15;
			}
		}
    }

	private BlockFace signFacing(float yaw, boolean isWallSign) {
		
		BlockFace facing;
		
		if(yaw < 0) {
			yaw += 360;
		}
		
		if(isWallSign) {
			if(yaw > 315 || yaw <= 45) {
				facing = BlockFace.NORTH;
			} else if(yaw <= 135) {
				facing = BlockFace.EAST;
			} else if(yaw <= 225) {
				facing = BlockFace.SOUTH;
			} else {
				facing = BlockFace.WEST;
			}
		} else {
			if (yaw > 360 -11.25 || yaw <= 11.25) {
				facing = BlockFace.NORTH;
			} else if (yaw <= (360/16*2) - 11.25) {
				facing = BlockFace.NORTH_NORTH_EAST;
			} else if (yaw <= (360/16*3) - 11.25) {
				facing = BlockFace.NORTH_EAST;
			} else if (yaw <= (360/16*4) - 11.25) {
				facing = BlockFace.EAST_NORTH_EAST;
			} else if (yaw <= (360/16*5) - 11.25) {
				facing = BlockFace.EAST;
			} else if (yaw <= (360/16*6) - 11.25) {
				facing = BlockFace.EAST_SOUTH_EAST;
			} else if (yaw <= (360/16*7) - 11.25) {
				facing = BlockFace.SOUTH_EAST;
			} else if (yaw <= (360/16*8) - 11.25) {
				facing = BlockFace.SOUTH_SOUTH_EAST;
			} else if (yaw <= (360/16*9) - 11.25) {
				facing = BlockFace.SOUTH;
			} else if (yaw <= (360/16*10) - 11.25) {
				facing = BlockFace.SOUTH_SOUTH_WEST;
			} else if (yaw <= (360/16*11) - 11.25) {
				facing = BlockFace.SOUTH_WEST;
			} else if (yaw <= (360/16*12) - 11.25) {
				facing = BlockFace.WEST_SOUTH_WEST;
			} else if (yaw <= (360/16*13) - 11.25) {
				facing = BlockFace.WEST;
			} else if (yaw <= (360/16*14) - 11.25) {
				facing = BlockFace.WEST_NORTH_WEST;
			} else if (yaw <= (360/16*15) - 11.25) {
				facing = BlockFace.NORTH_WEST;
			} else {
				facing = BlockFace.NORTH_NORTH_WEST;
			}
		}
		return facing;
	}
}
