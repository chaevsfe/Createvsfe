package com.jozufozu.flywheel.backend.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

/**
 * Compat stub for old Flywheel 0.6.x ModelTransformer.
 */
public class ModelTransformer {
	public interface Params {
		void getModel(PoseStack ms);
	}
}
