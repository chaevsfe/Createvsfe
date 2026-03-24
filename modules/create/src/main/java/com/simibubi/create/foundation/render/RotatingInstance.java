package com.simibubi.create.foundation.render;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import dev.engine_room.flywheel.api.instance.InstanceHandle;
import dev.engine_room.flywheel.api.instance.InstanceType;
import dev.engine_room.flywheel.lib.instance.ColoredLitOverlayInstance;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.utility.Color;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;

/**
 * GPU instance data for rotating kinetic blocks (gears, shafts, drills, etc.).
 * Replaces the old RotatingData/KineticData/BasicData chain from Flywheel 0.6.x.
 */
public class RotatingInstance extends ColoredLitOverlayInstance {
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

	public RotatingInstance setColor(KineticBlockEntity be) {
		if (be.hasNetwork()) {
			setColor(Color.generateFromLong(be.network));
		} else {
			setColor(0xFF, 0xFF, 0xFF);
		}
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
		this.light = (this.light & 0xFFFF0000) | (blockLight & 0xFFFF);
		return this;
	}

	public RotatingInstance setSkyLight(int skyLight) {
		this.light = (this.light & 0x0000FFFF) | ((skyLight & 0xFFFF) << 16);
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
