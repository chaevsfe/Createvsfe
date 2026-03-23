package io.github.fabricators_of_create.porting_lib_ufo.extensions.extensions;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.GlazedTerracottaBlock;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiConsumer;

import io.github.fabricators_of_create.porting_lib_ufo.common.util.IPlantable;
import io.github.fabricators_of_create.porting_lib_ufo.common.util.PlantType;
import io.github.fabricators_of_create.porting_lib_ufo.extensions.ClientExtensionHooks;

/**
 * Extension interface injected into Block via loom:injected_interfaces.
 *
 * All default methods are prefixed with port_lib_ufo$ to avoid
 * IncompatibleClassChangeError when the standard Porting Lib
 * (porting_lib_extensions) is also loaded, since both inject interfaces
 * into Block with identically-signatured default methods.
 * See: https://github.com/vlad250906/Create-UfoPort/issues/17
 */
public interface BlockExtensions {
	default boolean port_lib_ufo$canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
		BlockState plant = plantable.getPlant(world, pos.relative(facing));
		PlantType type = plantable.getPlantType(world, pos.relative(facing));

		if (plant.getBlock() == Blocks.CACTUS)
			return state.is(Blocks.CACTUS) || state.is(Blocks.SAND) || state.is(Blocks.RED_SAND);

		if (plant.getBlock() == Blocks.SUGAR_CANE && this == Blocks.SUGAR_CANE)
			return true;

		if (plantable instanceof BushBlock && ((BushBlock)plantable).mayPlaceOn(state, world, pos))
			return true;

		if (PlantType.DESERT.equals(type)) {
			return this == Blocks.SAND || this == Blocks.TERRACOTTA || this instanceof GlazedTerracottaBlock;
		} else if (PlantType.NETHER.equals(type)) {
			return this == Blocks.SOUL_SAND;
		} else if (PlantType.CROP.equals(type)) {
			return state.is(Blocks.FARMLAND);
		} else if (PlantType.CAVE.equals(type)) {
			return state.isFaceSturdy(world, pos, Direction.UP);
		} else if (PlantType.PLAINS.equals(type)) {
			return this == Blocks.GRASS_BLOCK || ((Block)this).defaultBlockState().is(BlockTags.DIRT) || this == Blocks.FARMLAND;
		} else if (PlantType.BEACH.equals(type)) {
			boolean isBeach = state.is(Blocks.GRASS_BLOCK) || ((Block)this).defaultBlockState().is(BlockTags.DIRT) || state.is(Blocks.SAND) || state.is(Blocks.RED_SAND);
			boolean hasWater = false;
			for (Direction face : Direction.Plane.HORIZONTAL) {
				BlockState blockState = world.getBlockState(pos.relative(face));
				net.minecraft.world.level.material.FluidState fluidState = world.getFluidState(pos.relative(face));
				hasWater |= blockState.is(Blocks.FROSTED_ICE);
				hasWater |= fluidState.is(net.minecraft.tags.FluidTags.WATER);
				if (hasWater)
					break; //No point continuing.
			}
			return isBeach && hasWater;
		}
		return false;
	}

	/**
	 * Used to determine the state 'viewed' by an entity (see
	 * {@link Camera#getBlockAtCamera()}).
	 * Can be used by fluid blocks to determine if the viewpoint is within the fluid or not.
	 */
	default BlockState port_lib_ufo$getStateAtViewpoint(BlockState state, BlockGetter level, BlockPos pos, Vec3 viewpoint) {
		return state;
	}

	/**
	 * Called when a tree grows on top of this block and tries to set it to dirt by the trunk placer.
	 */
	default boolean port_lib_ufo$onTreeGrow(BlockState state, LevelReader level, BiConsumer<BlockPos, BlockState> placeFunction, RandomSource randomSource, BlockPos pos, TreeConfiguration config) {
		return false;
	}

	/**
	 * Whether this block hides the neighbors face pointed towards by the given direction.
	 */
	default boolean port_lib_ufo$hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir) {
		return false;
	}

	/**
	 * Whether this block allows a neighboring block to hide the face of this block it touches.
	 */
	default boolean port_lib_ufo$supportsExternalFaceHiding(BlockState state) {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			return ClientExtensionHooks.isBlockInSolidLayer(state);
		}
		return true;
	}
}
