package com.simibubi.create.content.kinetics.base;

import java.util.function.Consumer;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.instance.Instancer;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import com.simibubi.create.foundation.render.RotatingInstance;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Base class for Flywheel 1.0.6 Visual implementations of kinetic block entities.
 * Replaces old KineticBlockEntityInstance from Flywheel 0.6.x.
 *
 * <p>Provides common rotation setup, speed, offset, and color utilities
 * that all kinetic block entity visuals share.
 */
public abstract class KineticBlockEntityVisual<T extends KineticBlockEntity> extends AbstractBlockEntityVisual<T> {

	protected final Direction.Axis axis;

	public KineticBlockEntityVisual(VisualizationContext ctx, T blockEntity, float partialTick) {
		super(ctx, blockEntity, partialTick);
		axis = (blockState.getBlock() instanceof IRotate irotate) ? irotate.getRotationAxis(blockState) : Axis.Y;
	}

	// ---- Rotation update helpers ----

	protected final void updateRotation(RotatingInstance instance) {
		updateRotation(instance, getRotationAxis(), getBlockEntitySpeed());
	}

	protected final void updateRotation(RotatingInstance instance, Direction.Axis axis) {
		updateRotation(instance, axis, getBlockEntitySpeed());
	}

	protected final void updateRotation(RotatingInstance instance, float speed) {
		updateRotation(instance, getRotationAxis(), speed);
	}

	protected final void updateRotation(RotatingInstance instance, Direction.Axis axis, float speed) {
		instance.setRotationAxis(axis)
			.setOffset(getRotationOffset(axis))
			.setSpeed(speed)
			.setColor(blockEntity);
		instance.handle().setChanged();
	}

	// ---- Rotation setup helpers (for init) ----

	protected final RotatingInstance setup(RotatingInstance key) {
		return setup(key, getRotationAxis(), getBlockEntitySpeed());
	}

	protected final RotatingInstance setup(RotatingInstance key, Direction.Axis axis) {
		return setup(key, axis, getBlockEntitySpeed());
	}

	protected final RotatingInstance setup(RotatingInstance key, float speed) {
		return setup(key, getRotationAxis(), speed);
	}

	protected final RotatingInstance setup(RotatingInstance key, Direction.Axis axis, float speed) {
		key.setRotationAxis(axis)
			.setSpeed(speed)
			.setOffset(getRotationOffset(axis))
			.setColor(blockEntity)
			.setPosition(pos);
		relight(key);
		return key;
	}

	// ---- Rotation parameters ----

	protected float getRotationOffset(final Direction.Axis axis) {
		float offset = ICogWheel.isLargeCog(blockState) ? 11.25f : 0;
		double d = (((axis == Direction.Axis.X) ? 0 : pos.getX()) + ((axis == Direction.Axis.Y) ? 0 : pos.getY())
			+ ((axis == Direction.Axis.Z) ? 0 : pos.getZ())) % 2;
		if (d == 0) {
			offset = 22.5f;
		}
		return offset;
	}

	protected Direction.Axis getRotationAxis() {
		return axis;
	}

	protected float getBlockEntitySpeed() {
		return blockEntity.getSpeed();
	}

	// ---- Model helpers ----

	protected BlockState shaft() {
		return shaft(getRotationAxis());
	}

	public static BlockState shaft(Direction.Axis axis) {
		return AllBlocks.SHAFT.getDefaultState()
			.setValue(ShaftBlock.AXIS, axis);
	}

	/**
	 * Gets a rotating instancer for the given block state.
	 * This is the Flywheel 1.0.6 equivalent of getRotatingMaterial().getModel(state).
	 */
	protected Instancer<RotatingInstance> getRotatingModel(BlockState state) {
		return instancerProvider().instancer(AllInstanceTypes.ROTATING,
			new dev.engine_room.flywheel.lib.model.baked.BakedModelBuilder(
				net.minecraft.client.Minecraft.getInstance().getBlockRenderer()
					.getBlockModel(state)).build());
	}

	/**
	 * Gets a rotating instancer for the default block state of this visual.
	 */
	protected Instancer<RotatingInstance> getRotatingModel() {
		return getRotatingModel(blockState);
	}

	// ---- Light helpers ----

	protected void relight(RotatingInstance instance) {
		if (level != null) {
			instance.setBlockLight(level.getBrightness(LightLayer.BLOCK, pos));
			instance.setSkyLight(level.getBrightness(LightLayer.SKY, pos));
		}
	}

	protected void relightAll(RotatingInstance... instances) {
		for (RotatingInstance instance : instances) {
			relight(instance);
		}
	}
}
