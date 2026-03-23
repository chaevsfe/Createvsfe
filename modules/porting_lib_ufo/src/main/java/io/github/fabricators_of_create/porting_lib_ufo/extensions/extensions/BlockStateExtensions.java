package io.github.fabricators_of_create.porting_lib_ufo.extensions.extensions;

import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

import io.github.fabricators_of_create.porting_lib_ufo.common.util.IPlantable;

import java.util.function.BiConsumer;

/**
 * Extension interface injected into BlockState via loom:injected_interfaces.
 *
 * All default methods are prefixed with port_lib_ufo$ to avoid
 * IncompatibleClassChangeError when the standard Porting Lib
 * (porting_lib_extensions) is also loaded, since both inject interfaces
 * into BlockState with identically-signatured default methods.
 * See: https://github.com/vlad250906/Create-UfoPort/issues/17
 */
public interface BlockStateExtensions {
	/**
	 * Determines if this block can support the passed in plant, allowing it to be planted and grow.
	 */
	default boolean port_lib_ufo$canSustainPlant(BlockGetter level, BlockPos pos, Direction facing, IPlantable plantable) {
		return ((BlockState)this).getBlock().port_lib_ufo$canSustainPlant(((BlockState)this), level, pos, facing, plantable);
	}

	/**
	 * Used to determine the state 'viewed' by an entity (see
	 * {@link Camera#getBlockAtCamera()}).
	 * Can be used by fluid blocks to determine if the viewpoint is within the fluid or not.
	 */
	default BlockState port_lib_ufo$getStateAtViewpoint(BlockGetter level, BlockPos pos, Vec3 viewpoint) {
		return ((BlockState)this).getBlock().port_lib_ufo$getStateAtViewpoint(((BlockState)this), level, pos, viewpoint);
	}

	/**
	 * Called when a tree grows on top of this block and tries to set it to dirt by the trunk placer.
	 * An override that returns true is responsible for using the place function to
	 * set blocks in the world properly during generation.
	 */
	default boolean port_lib_ufo$onTreeGrow(LevelReader level, BiConsumer<BlockPos, BlockState> placeFunction, RandomSource randomSource, BlockPos pos, TreeConfiguration config) {
		return ((BlockState)this).getBlock().port_lib_ufo$onTreeGrow(((BlockState)this), level, placeFunction, randomSource, pos, config);
	}

	/**
	 * Whether this block hides the neighbors face pointed towards by the given direction.
	 */
	default boolean port_lib_ufo$hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState neighborState, Direction dir) {
		return ((BlockState)this).getBlock().port_lib_ufo$hidesNeighborFace(level, pos, ((BlockState)this), neighborState, dir);
	}

	/**
	 * Whether this block allows a neighboring block to hide the face of this block it touches.
	 * If this returns true, {@link BlockStateExtensions#port_lib_ufo$hidesNeighborFace(BlockGetter, BlockPos, BlockState, Direction)}
	 * will be called on the neighboring block.
	 */
	default boolean port_lib_ufo$supportsExternalFaceHiding() {
		return ((BlockState)this).getBlock().port_lib_ufo$supportsExternalFaceHiding(((BlockState)this));
	}
}
