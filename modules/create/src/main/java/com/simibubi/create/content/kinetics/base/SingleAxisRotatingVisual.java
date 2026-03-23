package com.simibubi.create.content.kinetics.base;

import java.util.function.Consumer;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.instance.Instancer;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;

import com.simibubi.create.foundation.render.RotatingInstance;

import net.minecraft.world.level.block.state.BlockState;

/**
 * Simple Visual for kinetic blocks that have a single rotating model on one axis.
 * Flywheel 1.0.6 replacement for SingleRotatingInstance.
 *
 * <p>This handles the common case of blocks like shafts, drills, fans, etc.
 * that just rotate a single model around their axis.
 */
public class SingleAxisRotatingVisual<T extends KineticBlockEntity> extends KineticBlockEntityVisual<T> {

	protected RotatingInstance rotatingModel;

	public SingleAxisRotatingVisual(VisualizationContext ctx, T blockEntity, float partialTick) {
		super(ctx, blockEntity, partialTick);
		rotatingModel = setup(getModel().createInstance());
	}

	@Override
	public void update(float partialTick) {
		updateRotation(rotatingModel);
	}

	@Override
	public void updateLight(float partialTick) {
		relight(rotatingModel);
		rotatingModel.handle().setChanged();
	}

	@Override
	protected void _delete() {
		if (rotatingModel != null) {
			rotatingModel.handle().setDeleted();
		}
	}

	@Override
	public void collectCrumblingInstances(Consumer<Instance> consumer) {
		if (rotatingModel != null) {
			consumer.accept(rotatingModel);
		}
	}

	protected BlockState getRenderedBlockState() {
		return blockState;
	}

	protected Instancer<RotatingInstance> getModel() {
		return getRotatingModel(getRenderedBlockState());
	}
}
