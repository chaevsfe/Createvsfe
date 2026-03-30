package com.simibubi.create.foundation.render;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import dev.engine_room.flywheel.api.instance.InstanceHandle;
import dev.engine_room.flywheel.api.instance.InstanceType;
import dev.engine_room.flywheel.lib.instance.ColoredLitOverlayInstance;

import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.utility.Color;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;

/**
 * GPU instance data for rotating kinetic blocks (gears, shafts, drills, etc.).
 * Replaces the old RotatingData/KineticData/BasicData chain from Flywheel 0.6.x.
 */
public class RotatingInstance extends ColoredLitOverlayInstance {
	/** Speed multiplier matching NeoForge — the shader consumes degrees/sec. */
	public static final float SPEED_MULTIPLIER = 6;

	// Position
	public float x;
	public float y;
	public float z;

	// Kinetic
	public float speed;
	public float offset;

	// Rotation axis (normalized)
	public byte axisX;
	public byte axisY;
	public byte axisZ;

	// Base rotation quaternion
	public final Quaternionf rotation = new Quaternionf();

	public RotatingInstance(InstanceType<? extends RotatingInstance> type, InstanceHandle handle) {
		super(type, handle);
	}

	// ---- Convenience setup methods (matching NeoForge pattern) ----

	public RotatingInstance setup(KineticBlockEntity blockEntity) {
		BlockState state = blockEntity.getBlockState();
		Direction.Axis axis = (state.getBlock() instanceof IRotate irotate)
			? irotate.getRotationAxis(state) : Direction.Axis.Y;
		return setup(blockEntity, axis, blockEntity.getSpeed());
	}

	public RotatingInstance setup(KineticBlockEntity blockEntity, Direction.Axis axis) {
		return setup(blockEntity, axis, blockEntity.getSpeed());
	}

	public RotatingInstance setup(KineticBlockEntity blockEntity, float speed) {
		BlockState state = blockEntity.getBlockState();
		Direction.Axis axis = (state.getBlock() instanceof IRotate irotate)
			? irotate.getRotationAxis(state) : Direction.Axis.Y;
		return setup(blockEntity, axis, speed);
	}

	public RotatingInstance setup(KineticBlockEntity blockEntity, Direction.Axis axis, float speed) {
		BlockState state = blockEntity.getBlockState();
		BlockPos pos = blockEntity.getBlockPos();
		return setRotationAxis(axis)
			.setSpeed(speed * SPEED_MULTIPLIER)
			.setOffset(rotationOffset(state, axis, pos) + blockEntity.getRotationAngleOffset(axis))
			.setColor(blockEntity);
	}

	// ---- Rotation offset calculation ----

	public static float rotationOffset(BlockState state, Direction.Axis axis, Vec3i pos) {
		float offset = ICogWheel.isLargeCog(state) ? 11.25f : 0;
		int x = (axis == Direction.Axis.X) ? 0 : pos.getX();
		int y = (axis == Direction.Axis.Y) ? 0 : pos.getY();
		int z = (axis == Direction.Axis.Z) ? 0 : pos.getZ();
		if (((x + y + z) % 2) == 0) {
			offset = 22.5f;
		}
		return offset;
	}

	// ---- Base rotation (rotateToFace) ----

	public RotatingInstance rotateToFace(Direction.Axis axis) {
		Direction orientation = Direction.get(Direction.AxisDirection.POSITIVE, axis);
		return rotateToFace(orientation);
	}

	public RotatingInstance rotateToFace(Direction orientation) {
		return rotateToFace(orientation.getStepX(), orientation.getStepY(), orientation.getStepZ());
	}

	public RotatingInstance rotateToFace(Direction from, Direction.Axis axis) {
		Direction orientation = Direction.get(Direction.AxisDirection.POSITIVE, axis);
		return rotateToFace(from, orientation);
	}

	public RotatingInstance rotateToFace(Direction from, Direction orientation) {
		return rotateTo(from.getStepX(), from.getStepY(), from.getStepZ(),
			orientation.getStepX(), orientation.getStepY(), orientation.getStepZ());
	}

	public RotatingInstance rotateToFace(float stepX, float stepY, float stepZ) {
		return rotateTo(0, 1, 0, stepX, stepY, stepZ);
	}

	public RotatingInstance rotateTo(float fromX, float fromY, float fromZ, float toX, float toY, float toZ) {
		rotation.rotateTo(fromX, fromY, fromZ, toX, toY, toZ);
		return this;
	}

	// ---- Position ----

	public RotatingInstance setPosition(BlockPos pos) {
		return setPosition(pos.getX(), pos.getY(), pos.getZ());
	}

	public RotatingInstance setPosition(Vec3i pos) {
		return setPosition(pos.getX(), pos.getY(), pos.getZ());
	}

	public RotatingInstance setPosition(Vector3f pos) {
		return setPosition(pos.x(), pos.y(), pos.z());
	}

	public RotatingInstance setPosition(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public RotatingInstance nudge(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	public RotatingInstance setSpeed(float speed) {
		this.speed = speed;
		return this;
	}

	public RotatingInstance setOffset(float offset) {
		this.offset = offset;
		return this;
	}

	public RotatingInstance setRotationAxis(Direction.Axis axis) {
		Direction dir = Direction.get(Direction.AxisDirection.POSITIVE, axis);
		return setRotationAxis(dir.step());
	}

	public RotatingInstance setRotationAxis(Vector3f axis) {
		return setRotationAxis(axis.x(), axis.y(), axis.z());
	}

	public RotatingInstance setRotationAxis(float x, float y, float z) {
		this.axisX = (byte) (x * 127);
		this.axisY = (byte) (y * 127);
		this.axisZ = (byte) (z * 127);
		return this;
	}

	public static int colorFromBE(KineticBlockEntity be) {
		if (be.hasNetwork())
			return Color.generateFromLong(be.network).getRGB();
		return 0xFFFFFF;
	}

	public RotatingInstance setColor(KineticBlockEntity be) {
		// Always white — network debug colors tint normal gameplay rendering
		setColor(0xFF, 0xFF, 0xFF);
		return this;
	}

	public RotatingInstance setColor(Color c) {
		return setColor(c.getRed(), c.getGreen(), c.getBlue());
	}

	public RotatingInstance setColor(int r, int g, int b) {
		this.red = (byte) r;
		this.green = (byte) g;
		this.blue = (byte) b;
		return this;
	}

	public RotatingInstance setBlockLight(int blockLight) {
		this.light = net.minecraft.client.renderer.LightTexture.pack(blockLight,
			net.minecraft.client.renderer.LightTexture.sky(this.light));
		return this;
	}

	public RotatingInstance setSkyLight(int skyLight) {
		this.light = net.minecraft.client.renderer.LightTexture.pack(
			net.minecraft.client.renderer.LightTexture.block(this.light), skyLight);
		return this;
	}

	public RotatingInstance setLight(int packedLight) {
		this.light = packedLight;
		return this;
	}

	public int getPackedLight() {
		return this.light;
	}
}
