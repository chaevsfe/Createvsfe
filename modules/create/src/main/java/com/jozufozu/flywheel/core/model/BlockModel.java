package com.jozufozu.flywheel.core.model;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Compat stub for old Flywheel 0.6.x BlockModel.
 */
public class BlockModel {
	public static BlockModel of(BakedModel model, BlockState referenceState) {
		return new BlockModel();
	}

	public static BlockModel of(BakedModel model, BlockState referenceState, PoseStack transform) {
		return new BlockModel();
	}

	public static BlockModel of(BlockState state) {
		return new BlockModel();
	}
}
