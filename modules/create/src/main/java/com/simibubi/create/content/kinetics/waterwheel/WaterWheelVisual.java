package com.simibubi.create.content.kinetics.waterwheel;

import dev.engine_room.flywheel.api.instance.Instancer;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.baked.BakedModelBuilder;

import com.simibubi.create.content.kinetics.base.CutoutRotatingVisual;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import com.simibubi.create.foundation.render.RotatingInstance;

import net.minecraft.client.resources.model.BakedModel;

/**
 * Visual for water wheel rendering. Replaces old WaterWheelInstance.
 * Uses a custom model key for cached model generation based on wheel material.
 */
public class WaterWheelVisual<T extends WaterWheelBlockEntity> extends CutoutRotatingVisual<T> {

	protected final boolean large;
	protected final WaterWheelModelKey key;

	public WaterWheelVisual(VisualizationContext ctx, T blockEntity, boolean large, float partialTick) {
		super(ctx, blockEntity, partialTick);
		this.large = large;
		key = new WaterWheelModelKey(large, blockState, blockEntity.material);
	}

	public static <T extends WaterWheelBlockEntity> WaterWheelVisual<T> standard(VisualizationContext ctx, T blockEntity, float partialTick) {
		return new WaterWheelVisual<>(ctx, blockEntity, false, partialTick);
	}

	public static <T extends WaterWheelBlockEntity> WaterWheelVisual<T> large(VisualizationContext ctx, T blockEntity, float partialTick) {
		return new WaterWheelVisual<>(ctx, blockEntity, true, partialTick);
	}

	@Override
	protected Instancer<RotatingInstance> getModel() {
		BakedModel model = WaterWheelRenderer.generateModel(key);
		return instancerProvider().instancer(AllInstanceTypes.ROTATING, new BakedModelBuilder(model).build());
	}
}
