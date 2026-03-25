package com.simibubi.create.impl.contraption;

import com.simibubi.create.api.contraption.BlockMovementChecks.AttachedCheck;
import com.simibubi.create.api.contraption.BlockMovementChecks.BrittleCheck;
import com.simibubi.create.api.contraption.BlockMovementChecks.MovementAllowedCheck;
import com.simibubi.create.api.contraption.BlockMovementChecks.MovementNecessaryCheck;
import com.simibubi.create.api.contraption.BlockMovementChecks.NotSupportiveCheck;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Implementation facade for block movement checks.
 * Delegates to the content-layer BlockMovementChecks which contains the actual logic.
 */
public class BlockMovementChecksImpl {
	public static synchronized void registerMovementNecessaryCheck(MovementNecessaryCheck check) {
		com.simibubi.create.content.contraptions.BlockMovementChecks.registerMovementNecessaryCheck(check);
	}

	public static synchronized void registerMovementAllowedCheck(MovementAllowedCheck check) {
		com.simibubi.create.content.contraptions.BlockMovementChecks.registerMovementAllowedCheck(check);
	}

	public static synchronized void registerBrittleCheck(BrittleCheck check) {
		com.simibubi.create.content.contraptions.BlockMovementChecks.registerBrittleCheck(check);
	}

	public static synchronized void registerAttachedCheck(AttachedCheck check) {
		com.simibubi.create.content.contraptions.BlockMovementChecks.registerAttachedCheck(check);
	}

	public static synchronized void registerNotSupportiveCheck(NotSupportiveCheck check) {
		com.simibubi.create.content.contraptions.BlockMovementChecks.registerNotSupportiveCheck(check);
	}

	public static boolean isMovementNecessary(BlockState state, Level world, BlockPos pos) {
		return com.simibubi.create.content.contraptions.BlockMovementChecks.isMovementNecessary(state, world, pos);
	}

	public static boolean isMovementAllowed(BlockState state, Level world, BlockPos pos) {
		return com.simibubi.create.content.contraptions.BlockMovementChecks.isMovementAllowed(state, world, pos);
	}

	public static boolean isBrittle(BlockState state) {
		return com.simibubi.create.content.contraptions.BlockMovementChecks.isBrittle(state);
	}

	public static boolean isBlockAttachedTowards(BlockState state, Level world, BlockPos pos, Direction direction) {
		return com.simibubi.create.content.contraptions.BlockMovementChecks.isBlockAttachedTowards(state, world, pos, direction);
	}

	public static boolean isNotSupportive(BlockState state, Direction facing) {
		return com.simibubi.create.content.contraptions.BlockMovementChecks.isNotSupportive(state, facing);
	}
}
