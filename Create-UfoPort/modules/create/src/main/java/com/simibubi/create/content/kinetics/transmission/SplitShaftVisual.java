package com.simibubi.create.content.kinetics.transmission;

import java.util.ArrayList;
import java.util.function.Consumer;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.foundation.render.RotatingInstance;
import com.simibubi.create.foundation.utility.Iterate;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;

/**
 * Visual for split shaft (two halves with different speeds). Replaces old SplitShaftInstance.
 */
public class SplitShaftVisual extends KineticBlockEntityVisual<SplitShaftBlockEntity> {

	protected final ArrayList<RotatingInstance> keys;

	public SplitShaftVisual(VisualizationContext ctx, SplitShaftBlockEntity blockEntity, float partialTick) {
		super(ctx, blockEntity, partialTick);

		keys = new ArrayList<>(2);
		float speed = blockEntity.getSpeed();

		for (Direction dir : Iterate.directionsInAxis(getRotationAxis())) {
			RotatingInstance half = getRotatingModel(AllPartialModels.SHAFT_HALF, blockState, dir).createInstance();
			half.rotateToFace(Direction.SOUTH, dir)
				.setPosition(getVisualPosition());
			float splitSpeed = speed * blockEntity.getRotationSpeedModifier(dir);
			keys.add(setup(half, splitSpeed));
		}
	}

	@Override
	public void update(float partialTick) {
		Block block = blockState.getBlock();
		final Direction.Axis boxAxis = ((IRotate) block).getRotationAxis(blockState);
		Direction[] directions = Iterate.directionsInAxis(boxAxis);

		for (int i : Iterate.zeroAndOne) {
			updateRotation(keys.get(i), blockEntity.getSpeed() * blockEntity.getRotationSpeedModifier(directions[i]));
		}
	}

	@Override
	public void updateLight(float partialTick) {
		for (RotatingInstance key : keys) {
			relight(key);
			key.handle().setChanged();
		}
	}

	@Override
	protected void _delete() {
		keys.forEach(k -> k.handle().setDeleted());
		keys.clear();
	}

	@Override
	public void collectCrumblingInstances(Consumer<Instance> consumer) {
		keys.forEach(consumer::accept);
	}
}
