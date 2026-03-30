package com.simibubi.create.content.kinetics.base;

import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;

/**
 * Visual for rotating blocks that use cutout rendering (transparency).
 * Replaces old CutoutRotatingInstance.
 * Note: In Flywheel 1.0.6, the cutout vs solid distinction is handled differently
 * than in 0.6.x. The model's material properties determine transparency.
 * Note: Currently unused -- AllBlockEntityTypes uses SingleAxisRotatingVisual.of() with explicit partial models.
 */
public class CutoutRotatingVisual<T extends KineticBlockEntity> extends SingleAxisRotatingVisual<T> {

	public CutoutRotatingVisual(VisualizationContext ctx, T blockEntity, float partialTick, Model model) {
		super(ctx, blockEntity, partialTick, model);
	}
}
