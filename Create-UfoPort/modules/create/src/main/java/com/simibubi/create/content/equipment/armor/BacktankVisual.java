package com.simibubi.create.content.equipment.armor;

import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;

import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;

/**
 * Visual for backtank shaft rotation. Replaces old BacktankInstance.
 */
public class BacktankVisual extends SingleAxisRotatingVisual<BacktankBlockEntity> {

	public BacktankVisual(VisualizationContext ctx, BacktankBlockEntity blockEntity, float partialTick) {
		super(ctx, blockEntity, partialTick, Models.partial(BacktankRenderer.getShaftModel(blockEntity.getBlockState())));
	}
}
