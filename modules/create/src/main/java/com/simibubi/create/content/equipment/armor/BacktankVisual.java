package com.simibubi.create.content.equipment.armor;

import dev.engine_room.flywheel.api.instance.Instancer;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;

import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.foundation.render.RotatingInstance;

/**
 * Visual for backtank shaft rotation. Replaces old BacktankInstance.
 */
public class BacktankVisual extends SingleAxisRotatingVisual<BacktankBlockEntity> {

	public BacktankVisual(VisualizationContext ctx, BacktankBlockEntity blockEntity, float partialTick) {
		super(ctx, blockEntity, partialTick);
	}

	@Override
	protected Instancer<RotatingInstance> getModel() {
		return getRotatingModel(BacktankRenderer.getShaftModel(blockState));
	}
}
