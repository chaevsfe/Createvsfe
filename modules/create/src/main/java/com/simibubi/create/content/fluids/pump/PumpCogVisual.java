package com.simibubi.create.content.fluids.pump;

import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;

import net.minecraft.core.Direction;

/**
 * Visual for pump cog rotation. Replaces old PumpCogInstance.
 * Note: Currently unused -- AllBlockEntityTypes uses SingleAxisRotatingVisual.ofZ(MECHANICAL_PUMP_COG) directly.
 */
public class PumpCogVisual extends SingleAxisRotatingVisual<PumpBlockEntity> {

	public PumpCogVisual(VisualizationContext ctx, PumpBlockEntity blockEntity, float partialTick) {
		super(ctx, blockEntity, partialTick, Direction.SOUTH, Models.partial(AllPartialModels.MECHANICAL_PUMP_COG));
	}
}
