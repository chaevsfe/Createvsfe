package com.simibubi.create.content.kinetics.base;

import dev.engine_room.flywheel.api.visualization.VisualizationContext;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Visual for back-facing half shaft. Replaces old BackHalfShaftInstance.
 */
public class BackHalfShaftVisual<T extends KineticBlockEntity> extends HalfShaftVisual<T> {

	public BackHalfShaftVisual(VisualizationContext ctx, T blockEntity, float partialTick) {
		super(ctx, blockEntity, partialTick,
			blockEntity.getBlockState().getValue(BlockStateProperties.FACING).getOpposite());
	}
}
