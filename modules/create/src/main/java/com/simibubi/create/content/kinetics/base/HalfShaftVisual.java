package com.simibubi.create.content.kinetics.base;

import dev.engine_room.flywheel.api.instance.Instancer;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.foundation.render.RotatingInstance;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Visual for half-shaft rendering (shaft poking out of a block face).
 * Replaces old HalfShaftInstance.
 */
public class HalfShaftVisual<T extends KineticBlockEntity> extends SingleAxisRotatingVisual<T> {

	public HalfShaftVisual(VisualizationContext ctx, T blockEntity, float partialTick) {
		super(ctx, blockEntity, partialTick);
	}

	@Override
	protected Instancer<RotatingInstance> getModel() {
		Direction dir = getShaftDirection();
		return getRotatingModel(AllPartialModels.SHAFT_HALF, blockState, dir);
	}

	protected Direction getShaftDirection() {
		return blockState.getValue(BlockStateProperties.FACING);
	}
}
