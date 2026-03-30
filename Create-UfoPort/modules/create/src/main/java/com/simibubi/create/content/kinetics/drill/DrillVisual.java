package com.simibubi.create.content.kinetics.drill;

import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.OrientedRotatingVisual;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Visual for drill head rotation. Replaces old DrillInstance.
 * Note: Currently unused -- AllBlockEntityTypes uses OrientedRotatingVisual.of(DRILL_HEAD) directly.
 */
public class DrillVisual extends OrientedRotatingVisual<DrillBlockEntity> {

	public DrillVisual(VisualizationContext ctx, DrillBlockEntity blockEntity, float partialTick) {
		super(ctx, blockEntity, partialTick, Direction.SOUTH,
			blockEntity.getBlockState().getValue(BlockStateProperties.FACING),
			Models.partial(AllPartialModels.DRILL_HEAD));
	}
}
