package com.simibubi.create.content.kinetics.crafter;

import dev.engine_room.flywheel.api.instance.Instancer;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.foundation.render.RotatingInstance;

import net.minecraft.core.Direction;

/**
 * Visual for shaftless cogwheel in mechanical crafters. Replaces old ShaftlessCogwheelInstance.
 */
public class ShaftlessCogwheelVisual extends SingleAxisRotatingVisual<KineticBlockEntity> {

	public ShaftlessCogwheelVisual(VisualizationContext ctx, KineticBlockEntity blockEntity, float partialTick) {
		super(ctx, blockEntity, partialTick);
	}

	@Override
	protected Instancer<RotatingInstance> getModel() {
		Direction facing = blockState.getValue(MechanicalCrafterBlock.HORIZONTAL_FACING);
		return getRotatingModel(AllPartialModels.SHAFTLESS_COGWHEEL, blockState, facing);
	}
}
