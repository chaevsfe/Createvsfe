package com.simibubi.create.content.kinetics.base;

import dev.engine_room.flywheel.api.visualization.VisualizationContext;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Visual for horizontal half shaft. Replaces old HorizontalHalfShaftInstance.
 */
public class HorizontalHalfShaftVisual<T extends KineticBlockEntity> extends HalfShaftVisual<T> {

	public HorizontalHalfShaftVisual(VisualizationContext ctx, T blockEntity, float partialTick) {
		super(ctx, blockEntity, partialTick,
			blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite());
	}
}
