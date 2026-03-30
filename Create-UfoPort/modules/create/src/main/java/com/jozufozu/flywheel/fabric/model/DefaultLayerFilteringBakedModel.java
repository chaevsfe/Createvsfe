package com.jozufozu.flywheel.fabric.model;

import net.minecraft.client.resources.model.BakedModel;

/**
 * Compat stub for old Flywheel 0.6.x DefaultLayerFilteringBakedModel.
 * Simply returns the model as-is (no layer filtering).
 */
public class DefaultLayerFilteringBakedModel {
	public static BakedModel wrap(BakedModel model) {
		return model;
	}
}
