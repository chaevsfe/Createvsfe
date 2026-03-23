package com.simibubi.create.content.kinetics.saw;

import dev.engine_room.flywheel.api.instance.Instancer;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.foundation.render.RotatingInstance;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Visual for saw blade/shaft. Replaces old SawInstance.
 */
public class SawVisual extends SingleAxisRotatingVisual<SawBlockEntity> {

	public SawVisual(VisualizationContext ctx, SawBlockEntity blockEntity, float partialTick) {
		super(ctx, blockEntity, partialTick);
	}

	@Override
	protected Instancer<RotatingInstance> getModel() {
		if (blockState.getValue(BlockStateProperties.FACING).getAxis().isHorizontal()) {
			BlockState referenceState = blockState.rotate(Rotation.CLOCKWISE_180);
			Direction facing = referenceState.getValue(BlockStateProperties.FACING);
			return getRotatingModel(AllPartialModels.SHAFT_HALF, referenceState, facing);
		} else {
			return getRotatingModel(shaft());
		}
	}
}
