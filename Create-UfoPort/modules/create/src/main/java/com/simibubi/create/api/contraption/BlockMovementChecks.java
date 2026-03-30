package com.simibubi.create.api.contraption;

import com.simibubi.create.content.contraptions.BlockMovementChecks.AttachedCheck;
import com.simibubi.create.content.contraptions.BlockMovementChecks.BrittleCheck;
import com.simibubi.create.content.contraptions.BlockMovementChecks.CheckResult;
import com.simibubi.create.content.contraptions.BlockMovementChecks.MovementAllowedCheck;
import com.simibubi.create.content.contraptions.BlockMovementChecks.MovementNecessaryCheck;
import com.simibubi.create.content.contraptions.BlockMovementChecks.NotSupportiveCheck;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * API facade for block movement checks. Delegates to the content-layer implementation.
 * <p>
 * Provides several interfaces that can define the behavior of blocks when mounting onto contraptions.
 * See each interface for details.
 * <p>
 * For each interface, checks can be registered and queried.
 * Registration is thread-safe and can be done in parallel mod init.
 * Each query will iterate all registered checks in reverse-registration order. If a check returns
 * a non-{@link CheckResult#PASS PASS} result, that is the result of the query. If no check catches
 * a query, then a best-effort fallback is used.
 */
public class BlockMovementChecks {
	public static void registerMovementNecessaryCheck(MovementNecessaryCheck check) {
		com.simibubi.create.content.contraptions.BlockMovementChecks.registerMovementNecessaryCheck(check);
	}

	public static void registerMovementAllowedCheck(MovementAllowedCheck check) {
		com.simibubi.create.content.contraptions.BlockMovementChecks.registerMovementAllowedCheck(check);
	}

	public static void registerBrittleCheck(BrittleCheck check) {
		com.simibubi.create.content.contraptions.BlockMovementChecks.registerBrittleCheck(check);
	}

	public static void registerAttachedCheck(AttachedCheck check) {
		com.simibubi.create.content.contraptions.BlockMovementChecks.registerAttachedCheck(check);
	}

	public static void registerNotSupportiveCheck(NotSupportiveCheck check) {
		com.simibubi.create.content.contraptions.BlockMovementChecks.registerNotSupportiveCheck(check);
	}

	// queries

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

	// Re-export nested types for API consumers

	public interface MovementNecessaryCheck extends com.simibubi.create.content.contraptions.BlockMovementChecks.MovementNecessaryCheck {
	}

	public interface MovementAllowedCheck extends com.simibubi.create.content.contraptions.BlockMovementChecks.MovementAllowedCheck {
	}

	public interface BrittleCheck extends com.simibubi.create.content.contraptions.BlockMovementChecks.BrittleCheck {
	}

	public interface AttachedCheck extends com.simibubi.create.content.contraptions.BlockMovementChecks.AttachedCheck {
	}

	public interface NotSupportiveCheck extends com.simibubi.create.content.contraptions.BlockMovementChecks.NotSupportiveCheck {
	}

	public enum CheckResult {
		SUCCESS, FAIL, PASS;

		public boolean toBoolean() {
			if (this == PASS) {
				throw new IllegalStateException("PASS does not have a boolean value");
			}
			return this == SUCCESS;
		}

		public static CheckResult of(boolean b) {
			return b ? SUCCESS : FAIL;
		}

		public static CheckResult of(Boolean b) {
			return b == null ? PASS : (b ? SUCCESS : FAIL);
		}
	}

	private BlockMovementChecks() {
		throw new AssertionError("This class should not be instantiated");
	}
}
