package com.simibubi.create.api.contraption.transformable;

import com.simibubi.create.content.contraptions.StructureTransform;

import net.minecraft.world.level.block.state.BlockState;

/**
 * Interface for blocks that support custom transformations when moved by a contraption.
 * Fabric equivalent of the NeoForge TransformableBlock API.
 * <p>
 * Alternatively, register a {@link MovedBlockTransformerRegistries.BlockTransformer} for your block.
 */
@FunctionalInterface
public interface TransformableBlock extends com.simibubi.create.content.contraptions.ITransformableBlock {
	@Override
	BlockState transform(BlockState state, StructureTransform transform);
}
