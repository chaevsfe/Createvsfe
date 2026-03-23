package com.simibubi.create.content.kinetics.simpleRelays;

import java.util.function.Consumer;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.instance.Instancer;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.foundation.render.RotatingInstance;

import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;

/**
 * Visual for bracketed cogwheel/shaft with optional additional shaft for large cogs.
 * Replaces old BracketedKineticBlockEntityInstance.
 */
public class BracketedKineticBlockEntityVisual extends SingleAxisRotatingVisual<BracketedKineticBlockEntity> {

	protected RotatingInstance additionalShaft;

	public BracketedKineticBlockEntityVisual(VisualizationContext ctx, BracketedKineticBlockEntity blockEntity, float partialTick) {
		super(ctx, blockEntity, partialTick);

		if (!ICogWheel.isLargeCog(blockEntity.getBlockState()))
			return;

		float speed = blockEntity.getSpeed();
		Direction.Axis axis = KineticBlockEntityRenderer.getRotationAxisOf(blockEntity);
		float offset = BracketedKineticBlockEntityRenderer.getShaftAngleOffset(axis, pos);
		Direction facing = Direction.fromAxisAndDirection(axis, AxisDirection.POSITIVE);

		additionalShaft = setup(
			getRotatingModel(AllPartialModels.COGWHEEL_SHAFT, blockState, facing).createInstance(),
			speed);
		additionalShaft.setOffset(offset);
		additionalShaft.handle().setChanged();
	}

	@Override
	protected Instancer<RotatingInstance> getModel() {
		if (!ICogWheel.isLargeCog(blockEntity.getBlockState()))
			return super.getModel();

		Direction.Axis axis = KineticBlockEntityRenderer.getRotationAxisOf(blockEntity);
		Direction facing = Direction.fromAxisAndDirection(axis, AxisDirection.POSITIVE);
		return getRotatingModel(AllPartialModels.SHAFTLESS_LARGE_COGWHEEL, blockState, facing);
	}

	@Override
	public void update(float partialTick) {
		super.update(partialTick);
		if (additionalShaft != null) {
			updateRotation(additionalShaft);
			additionalShaft.setOffset(BracketedKineticBlockEntityRenderer.getShaftAngleOffset(axis, pos));
			additionalShaft.handle().setChanged();
		}
	}

	@Override
	public void updateLight(float partialTick) {
		super.updateLight(partialTick);
		if (additionalShaft != null) {
			relight(additionalShaft);
			additionalShaft.handle().setChanged();
		}
	}

	@Override
	protected void _delete() {
		super._delete();
		if (additionalShaft != null)
			additionalShaft.handle().setDeleted();
	}

	@Override
	public void collectCrumblingInstances(Consumer<Instance> consumer) {
		super.collectCrumblingInstances(consumer);
		if (additionalShaft != null)
			consumer.accept(additionalShaft);
	}
}
