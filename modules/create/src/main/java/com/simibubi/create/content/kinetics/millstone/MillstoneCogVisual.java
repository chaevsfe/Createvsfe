package com.simibubi.create.content.kinetics.millstone;

import dev.engine_room.flywheel.api.instance.Instancer;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.foundation.render.RotatingInstance;

/**
 * Visual for millstone cog rotation. Replaces old MillstoneCogInstance.
 */
public class MillstoneCogVisual extends SingleAxisRotatingVisual<MillstoneBlockEntity> {

	public MillstoneCogVisual(VisualizationContext ctx, MillstoneBlockEntity blockEntity, float partialTick) {
		super(ctx, blockEntity, partialTick);
	}

	@Override
	protected Instancer<RotatingInstance> getModel() {
		return getRotatingModel(AllPartialModels.MILLSTONE_COG);
	}
}
