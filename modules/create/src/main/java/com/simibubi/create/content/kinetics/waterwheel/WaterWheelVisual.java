package com.simibubi.create.content.kinetics.waterwheel;

import java.util.function.Consumer;

import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import com.simibubi.create.foundation.render.RotatingInstance;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.baked.BakedModelBuilder;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Visual for water wheel rendering. Replaces old WaterWheelInstance.
 * Matches NeoForge pattern: extends KineticBlockEntityVisual directly,
 * creates the rotating instance after all fields are initialized
 * (avoids super-constructor calling overridden getModel() before subclass fields are set).
 */
public class WaterWheelVisual<T extends WaterWheelBlockEntity> extends KineticBlockEntityVisual<T> {

	protected final boolean large;
	protected BlockState lastMaterial;
	protected RotatingInstance rotatingModel;

	public WaterWheelVisual(VisualizationContext ctx, T blockEntity, boolean large, float partialTick) {
		super(ctx, blockEntity, partialTick);
		this.large = large;
		setupInstance();
	}

	public static <T extends WaterWheelBlockEntity> WaterWheelVisual<T> standard(VisualizationContext ctx, T blockEntity, float partialTick) {
		return new WaterWheelVisual<>(ctx, blockEntity, false, partialTick);
	}

	public static <T extends WaterWheelBlockEntity> WaterWheelVisual<T> large(VisualizationContext ctx, T blockEntity, float partialTick) {
		return new WaterWheelVisual<>(ctx, blockEntity, true, partialTick);
	}

	private void setupInstance() {
		lastMaterial = blockEntity.material;
		WaterWheelModelKey key = new WaterWheelModelKey(large, blockState, blockEntity.material);
		BakedModel model = WaterWheelRenderer.generateModel(key);
		Model flywheelModel = new BakedModelBuilder(model).build();

		rotatingModel = instancerProvider().instancer(AllInstanceTypes.ROTATING, flywheelModel)
			.createInstance();
		rotatingModel.setup(blockEntity)
			.setPosition(getVisualPosition())
			.rotateToFace(rotationAxis())
			.setChanged();
	}

	@Override
	public void update(float partialTick) {
		if (lastMaterial != blockEntity.material) {
			rotatingModel.delete();
			setupInstance();
		} else {
			rotatingModel.setup(blockEntity)
				.setChanged();
		}
	}

	@Override
	public void updateLight(float partialTick) {
		relight(rotatingModel);
	}

	@Override
	protected void _delete() {
		rotatingModel.delete();
	}

	@Override
	public void collectCrumblingInstances(Consumer<Instance> consumer) {
		consumer.accept(rotatingModel);
	}
}
