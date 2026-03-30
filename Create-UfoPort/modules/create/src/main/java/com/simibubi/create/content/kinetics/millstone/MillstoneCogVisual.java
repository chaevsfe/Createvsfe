package com.simibubi.create.content.kinetics.millstone;

import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;

/**
 * Visual for millstone cog rotation. Replaces old MillstoneCogInstance.
 * Note: Currently unused -- AllBlockEntityTypes uses SingleAxisRotatingVisual.of(MILLSTONE_COG) directly.
 */
public class MillstoneCogVisual extends SingleAxisRotatingVisual<MillstoneBlockEntity> {

	public MillstoneCogVisual(VisualizationContext ctx, MillstoneBlockEntity blockEntity, float partialTick) {
		super(ctx, blockEntity, partialTick, Models.partial(AllPartialModels.MILLSTONE_COG));
	}
}
