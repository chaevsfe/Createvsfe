package com.simibubi.create.foundation.render;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import dev.engine_room.flywheel.api.instance.InstanceHandle;
import dev.engine_room.flywheel.api.instance.InstanceType;
import dev.engine_room.flywheel.lib.instance.AbstractInstance;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.utility.Color;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

/**
 * GPU instance data for rotating kinetic blocks (gears, shafts, drills, etc.).
 * Replaces the old RotatingData/KineticData/BasicData chain from Flywheel 0.6.x.
 */
public class RotatingInstance extends AbstractInstance {
	// Light
	public byte blockLight;
	public byte skyLight;

	// Color (network debug)
	public byte r = (byte) 0xFF;
	public byte g = (byte) 0xFF;
	public byte b = (byte) 0xFF;
	public byte a = (byte) 0xFF;

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

	public RotatingInstance(InstanceType<?> type, InstanceHandle handle) {
		super(type, handle);
	}

	public RotatingInstance setPosition(BlockPos pos) {
		return setPosition(pos.getX(), pos.getY(), pos.getZ());
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
		this.r = (byte) r;
		this.g = (byte) g;
		this.b = (byte) b;
		return this;
	}

	public RotatingInstance setBlockLight(int blockLight) {
		this.blockLight = (byte) (blockLight & 0xF);
		return this;
	}

	public RotatingInstance setSkyLight(int skyLight) {
		this.skyLight = (byte) (skyLight & 0xF);
		return this;
	}

	public RotatingInstance setLight(int packedLight) {
		this.blockLight = (byte) (packedLight & 0xF);
		this.skyLight = (byte) ((packedLight >> 16) & 0xF);
		return this;
	}

	public int getPackedLight() {
		return (blockLight & 0xFF) | ((skyLight & 0xFF) << 16);
	}

	/**
	 * Writes this instance's data to native memory for GPU upload.
	 * Layout must match the LayoutBuilder definition in AllInstanceTypes.ROTATING.
	 */
	public static void write(long ptr, RotatingInstance i) {
		MemoryUtil.memPutShort(ptr, (short) (i.blockLight & 0xFF));
		MemoryUtil.memPutShort(ptr + 2, (short) (i.skyLight & 0xFF));
		MemoryUtil.memPutByte(ptr + 4, i.r);
		MemoryUtil.memPutByte(ptr + 5, i.g);
		MemoryUtil.memPutByte(ptr + 6, i.b);
		MemoryUtil.memPutByte(ptr + 7, i.a);
		MemoryUtil.memPutFloat(ptr + 8, i.x);
		MemoryUtil.memPutFloat(ptr + 12, i.y);
		MemoryUtil.memPutFloat(ptr + 16, i.z);
		MemoryUtil.memPutFloat(ptr + 20, i.speed);
		MemoryUtil.memPutFloat(ptr + 24, i.offset);
		MemoryUtil.memPutByte(ptr + 28, i.axisX);
		MemoryUtil.memPutByte(ptr + 29, i.axisY);
		MemoryUtil.memPutByte(ptr + 30, i.axisZ);
	}
}
