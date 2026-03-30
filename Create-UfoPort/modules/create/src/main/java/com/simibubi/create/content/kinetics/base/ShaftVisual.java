package com.simibubi.create.content.kinetics.base;

import com.simibubi.create.AllPartialModels;

import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;

/**
 * Visual for shaft rendering. Replaces old ShaftInstance.
 */
public class ShaftVisual<T extends KineticBlockEntity> extends SingleAxisRotatingVisual<T> {

	public ShaftVisual(VisualizationContext ctx, T blockEntity, float partialTick) {
		super(ctx, blockEntity, partialTick, Models.partial(AllPartialModels.SHAFT));
	}
}
