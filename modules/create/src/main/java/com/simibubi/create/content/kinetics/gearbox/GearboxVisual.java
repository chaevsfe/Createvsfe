package com.simibubi.create.content.kinetics.gearbox;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.foundation.render.RotatingInstance;
import com.simibubi.create.foundation.utility.Iterate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Visual for gearbox (4 half-shafts with speed/direction logic). Replaces old GearboxInstance.
 */
public class GearboxVisual extends KineticBlockEntityVisual<GearboxBlockEntity> {

	protected final EnumMap<Direction, RotatingInstance> keys;
	protected Direction sourceFacing;

	public GearboxVisual(VisualizationContext ctx, GearboxBlockEntity blockEntity, float partialTick) {
		super(ctx, blockEntity, partialTick);

		keys = new EnumMap<>(Direction.class);
		final Direction.Axis boxAxis = blockState.getValue(BlockStateProperties.AXIS);
		updateSourceFacing();

		for (Direction direction : Iterate.directions) {
			final Direction.Axis axis = direction.getAxis();
			if (boxAxis == axis)
				continue;

			RotatingInstance key = getRotatingModel(AllPartialModels.SHAFT_HALF, blockState, direction).createInstance();
			key.setRotationAxis(Direction.get(Direction.AxisDirection.POSITIVE, axis).step())
				.setSpeed(getSpeed(direction))
				.setOffset(getRotationOffset(axis))
				.setColor(blockEntity)
				.setPosition(pos);
			relight(key);

			keys.put(direction, key);
		}
	}

	private float getSpeed(Direction direction) {
		float speed = blockEntity.getSpeed();
		if (speed != 0 && sourceFacing != null) {
			if (sourceFacing.getAxis() == direction.getAxis())
				speed *= sourceFacing == direction ? 1 : -1;
			else if (sourceFacing.getAxisDirection() == direction.getAxisDirection())
				speed *= -1;
		}
		return speed;
	}

	protected void updateSourceFacing() {
		if (blockEntity.hasSource()) {
			BlockPos source = blockEntity.source.subtract(pos);
			sourceFacing = Direction.getNearest(source.getX(), source.getY(), source.getZ());
		} else {
			sourceFacing = null;
		}
	}

	@Override
	public void update(float partialTick) {
		updateSourceFacing();
		for (Map.Entry<Direction, RotatingInstance> entry : keys.entrySet()) {
			Direction direction = entry.getKey();
			Direction.Axis axis = direction.getAxis();
			updateRotation(entry.getValue(), axis, getSpeed(direction));
		}
	}

	@Override
	public void updateLight(float partialTick) {
		for (RotatingInstance key : keys.values()) {
			relight(key);
			key.handle().setChanged();
		}
	}

	@Override
	protected void _delete() {
		keys.values().forEach(k -> k.handle().setDeleted());
		keys.clear();
	}

	@Override
	public void collectCrumblingInstances(Consumer<Instance> consumer) {
		keys.values().forEach(consumer::accept);
	}
}
