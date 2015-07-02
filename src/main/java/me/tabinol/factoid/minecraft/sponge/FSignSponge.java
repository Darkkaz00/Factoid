package me.tabinol.factoid.minecraft.sponge;

import me.tabinol.factoid.Factoid;
import me.tabinol.factoid.exceptions.SignException;
import me.tabinol.factoid.lands.Land;
import me.tabinol.factoid.lands.areas.Point;
import me.tabinol.factoid.minecraft.FSign;

import org.spongepowered.api.Game;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.manipulator.block.DirectionalData;
import org.spongepowered.api.data.manipulator.tileentity.SignData;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;

public class FSignSponge implements FSign {

	private final Location loc;
	
	/**
	 * For Server class only (already existing sign)
	 * @param block
	 */
	public FSignSponge(Location loc) {
    
		this.loc = loc;
	}
	
	public FSignSponge(Game game, Point point, float yaw, String[] lines, Land land,
            boolean isWallSign) throws SignException {

		loc = SpongeUtils.toLocation(((FWorldSponge) point.getWorld()).getWorld(), point);
		Direction facing = signFacing(yaw, isWallSign);
		BlockState state;
	    
		// Impossible to create the sign here
		if (Factoid.getLands().getLand(point) != land) {
			throw new SignException();
		}

		// create the right sign type
		if(isWallSign) {
			state = BlockTypes.WALL_SIGN.getDefaultState();
		} else {
			state = BlockTypes.STANDING_SIGN.getDefaultState();
		}
		
		// set lines
		SignData sign = game.getRegistry().getManipulatorRegistry()
		        .getBuilder(SignData.class).get().create();
		for(int t = 0; t < lines.length; t++) {
			sign.setLine(t, Texts.builder(lines[t]).build());
		}
		state.withData(sign).get();
		
		// set Directional
		DirectionalData direction = game.getRegistry().getManipulatorRegistry()
		        .getBuilder(DirectionalData.class).get().create();
		direction.setValue(facing);
		state.withData(direction).get();
		
		loc.setBlock(state);
	}

	@Override
    public boolean isWallSign() {

		if(loc.getBlockType() == BlockTypes.WALL_SIGN) {
			return true;
		} else {
			return false;
		}
    }

	@Override
    public float getYaw() {
		
		DirectionalData data = loc.getData(DirectionalData.class).get();
		Direction facing = data.getValue();
		
		if(isWallSign()) {
			if(facing == Direction.NORTH) {
				return 0;
			} else if(facing == Direction.EAST) {
				return 90;
			} else if(facing == Direction.SOUTH) {
				return 180;
			} else {
				return 270;
			}
		} else {
			if (facing == Direction.NORTH) {
				return 0;
			} else if (facing == Direction.NORTH_NORTHEAST) {
				return 360 / 16 * 1;
			} else if (facing == Direction.NORTHEAST) {
				return 360 / 16 * 2;
			} else if (facing == Direction.EAST_NORTHEAST) {
				return 360 / 16 * 3;
			} else if (facing == Direction.EAST) {
				return 360 / 16 * 4;
			} else if (facing == Direction.EAST_SOUTHEAST) {
				return 360 / 16 * 5;
			} else if (facing == Direction.SOUTHEAST) {
				return 360 / 16 * 6;
			} else if (facing == Direction.SOUTH_SOUTHEAST) {
				return 360 / 16 * 7;
			} else if (facing == Direction.SOUTH) {
				return 360 / 16 * 8;
			} else if (facing == Direction.SOUTH_SOUTHWEST) {
				return 360 / 16 * 9;
			} else if (facing == Direction.SOUTHWEST) {
				return 360 / 16 * 10;
			} else if (facing == Direction.WEST_SOUTHWEST) {
				return 360 / 16 * 11;
			} else if (facing == Direction.WEST) {
				return 360 / 16 * 12;
			} else if (facing == Direction.WEST_NORTHWEST) {
				return 360 / 16 * 13;
			} else if (facing == Direction.NORTHWEST) {
				return 360 / 16 * 14;
			} else {
				return 360 / 16 * 15;
			}
		}
    }

	private Direction signFacing(float yaw, boolean isWallSign) {
		
		Direction facing;
		
		if(yaw < 0) {
			yaw += 360;
		}
		
		if(isWallSign) {
			if(yaw > 315 || yaw <= 45) {
				facing = Direction.NORTH;
			} else if(yaw <= 135) {
				facing = Direction.EAST;
			} else if(yaw <= 225) {
				facing = Direction.SOUTH;
			} else {
				facing = Direction.WEST;
			}
		} else {
			if (yaw > 360 -11.25 || yaw <= 11.25) {
				facing = Direction.NORTH;
			} else if (yaw <= (360/16*2) - 11.25) {
				facing = Direction.NORTH_NORTHEAST;
			} else if (yaw <= (360/16*3) - 11.25) {
				facing = Direction.NORTHEAST;
			} else if (yaw <= (360/16*4) - 11.25) {
				facing = Direction.EAST_NORTHEAST;
			} else if (yaw <= (360/16*5) - 11.25) {
				facing = Direction.EAST;
			} else if (yaw <= (360/16*6) - 11.25) {
				facing = Direction.EAST_SOUTHEAST;
			} else if (yaw <= (360/16*7) - 11.25) {
				facing = Direction.SOUTHEAST;
			} else if (yaw <= (360/16*8) - 11.25) {
				facing = Direction.SOUTH_SOUTHEAST;
			} else if (yaw <= (360/16*9) - 11.25) {
				facing = Direction.SOUTH;
			} else if (yaw <= (360/16*10) - 11.25) {
				facing = Direction.SOUTH_SOUTHWEST;
			} else if (yaw <= (360/16*11) - 11.25) {
				facing = Direction.SOUTHWEST;
			} else if (yaw <= (360/16*12) - 11.25) {
				facing = Direction.WEST_SOUTHWEST;
			} else if (yaw <= (360/16*13) - 11.25) {
				facing = Direction.WEST;
			} else if (yaw <= (360/16*14) - 11.25) {
				facing = Direction.WEST_NORTHWEST;
			} else if (yaw <= (360/16*15) - 11.25) {
				facing = Direction.NORTHWEST;
			} else {
				facing = Direction.NORTH_NORTHWEST;
			}
		}
		return facing;
	}
}
