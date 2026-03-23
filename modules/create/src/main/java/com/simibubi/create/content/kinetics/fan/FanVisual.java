package com.simibubi.create.content.kinetics.fan;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

import java.util.function.Consumer;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.foundation.render.RotatingInstance;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

/**
 * Visual for encased fan (shaft + fan blades). Replaces old FanInstance.
 */
public class FanVisual extends KineticBlockEntityVisual<EncasedFanBlockEntity> {

	protected final RotatingInstance shaft;
	protected final RotatingInstance fan;
	final Direction direction;
	private final Direction opposite;

	public FanVisual(VisualizationContext ctx, EncasedFanBlockEntity blockEntity, float partialTick) {
		super(ctx, blockEntity, partialTick);

		direction = blockState.getValue(FACING);
		opposite = direction.getOpposite();

		shaft = getRotatingModel(AllPartialModels.SHAFT_HALF, blockState, opposite).createInstance();
		fan = getRotatingModel(AllPartialModels.ENCASED_FAN_INNER, blockState, opposite).createInstance();

		setup(shaft);
		setup(fan, getFanSpeed());
	}

	private float getFanSpeed() {
		float speed = blockEntity.getSpeed() * 5;
		if (speed > 0)
			speed = Mth.clamp(speed, 80, 64 * 20);
		if (speed < 0)
			speed = Mth.clamp(speed, -64 * 20, -80);
		return speed;
	}

	@Override
	public void update(float partialTick) {
		updateRotation(shaft);
		updateRotation(fan, getFanSpeed());
	}

	@Override
	public void updateLight(float partialTick) {
		relight(shaft);
		relight(fan);
		shaft.handle().setChanged();
		fan.handle().setChanged();
	}

	@Override
	protected void _delete() {
		shaft.handle().setDeleted();
		fan.handle().setDeleted();
	}

	@Override
	public void collectCrumblingInstances(Consumer<Instance> consumer) {
		consumer.accept(shaft);
		consumer.accept(fan);
	}
}
