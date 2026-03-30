package com.simibubi.create.api.contraption.transformable;

import com.simibubi.create.content.contraptions.StructureTransform;

import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Interface for block entities that support custom transformations when moved by a contraption.
 * Fabric equivalent of the NeoForge TransformableBlockEntity API.
 * <p>
 * Alternatively, register a {@link MovedBlockTransformerRegistries.BlockEntityTransformer} for your block entity type.
 */
public interface TransformableBlockEntity extends com.simibubi.create.content.contraptions.ITransformableBlockEntity {
	@Override
	void transform(StructureTransform transform);

	/**
	 * Adapter to match the NeoForge API signature {@code void transform(BlockEntity, StructureTransform)}.
	 */
	static void transform(BlockEntity be, StructureTransform transform) {
		if (be instanceof TransformableBlockEntity transformable) {
			transformable.transform(transform);
		}
	}
}
