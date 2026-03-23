package com.simibubi.create.content.fluids.pump;

import dev.engine_room.flywheel.api.instance.Instancer;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.foundation.render.RotatingInstance;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Visual for pump cog rotation. Replaces old PumpCogInstance.
 */
public class PumpCogVisual extends SingleAxisRotatingVisual<PumpBlockEntity> {

	public PumpCogVisual(VisualizationContext ctx, PumpBlockEntity blockEntity, float partialTick) {
		super(ctx, blockEntity, partialTick);
	}

	@Override
	protected Instancer<RotatingInstance> getModel() {
		BlockState referenceState = blockEntity.getBlockState();
		Direction facing = referenceState.getValue(BlockStateProperties.FACING);
		return getRotatingModel(AllPartialModels.MECHANICAL_PUMP_COG, referenceState, facing);
	}
}
