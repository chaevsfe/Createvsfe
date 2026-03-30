package com.simibubi.create.content.kinetics.base;

import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;

import com.simibubi.create.AllPartialModels;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Visual for half-shaft rendering (shaft poking out of a block face).
 * Replaces old HalfShaftInstance.
 */
public class HalfShaftVisual<T extends KineticBlockEntity> extends SingleAxisRotatingVisual<T> {

	public HalfShaftVisual(VisualizationContext ctx, T blockEntity, float partialTick) {
		super(ctx, blockEntity, partialTick, getShaftDirection(blockEntity),
			Models.partial(AllPartialModels.SHAFT_HALF));
	}

	/**
	 * Protected constructor for subclasses that provide their own shaft direction.
	 */
	protected HalfShaftVisual(VisualizationContext ctx, T blockEntity, float partialTick, Direction shaftDirection) {
		super(ctx, blockEntity, partialTick, shaftDirection,
			Models.partial(AllPartialModels.SHAFT_HALF));
	}

	protected static Direction getShaftDirection(KineticBlockEntity blockEntity) {
		return blockEntity.getBlockState().getValue(BlockStateProperties.FACING);
	}
}
