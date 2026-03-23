package com.simibubi.create.foundation.render;

import org.lwjgl.system.MemoryUtil;

import dev.engine_room.flywheel.api.instance.InstanceHandle;
import dev.engine_room.flywheel.api.instance.InstanceType;
import dev.engine_room.flywheel.lib.instance.AbstractInstance;

import net.minecraft.core.BlockPos;

/**
 * GPU instance data for fluid rendering in contraptions and fluid blocks.
 * Each instance represents a fluid surface quad with position, light, color, and overlay.
 */
public class FluidInstance extends AbstractInstance {
	// Light
	public byte blockLight;
	public byte skyLight;

	// Color (tinted by fluid color)
	public byte r = (byte) 0xFF;
	public byte g = (byte) 0xFF;
	public byte b = (byte) 0xFF;
	public byte a = (byte) 0xFF;

	// Position
	public float x;
	public float y;
	public float z;

	// Overlay (for damage/selection)
	public int overlay;

	public FluidInstance(InstanceType<?> type, InstanceHandle handle) {
		super(type, handle);
	}

	public FluidInstance setPosition(BlockPos pos) {
		return setPosition(pos.getX(), pos.getY(), pos.getZ());
	}

	public FluidInstance setPosition(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public FluidInstance setColor(int r, int g, int b, int a) {
		this.r = (byte) r;
		this.g = (byte) g;
		this.b = (byte) b;
		this.a = (byte) a;
		return this;
	}

	public FluidInstance setColor(int color) {
		this.r = (byte) ((color >> 16) & 0xFF);
		this.g = (byte) ((color >> 8) & 0xFF);
		this.b = (byte) (color & 0xFF);
		this.a = (byte) ((color >> 24) & 0xFF);
		return this;
	}

	public FluidInstance setBlockLight(int blockLight) {
		this.blockLight = (byte) (blockLight & 0xF);
		return this;
	}

	public FluidInstance setSkyLight(int skyLight) {
		this.skyLight = (byte) (skyLight & 0xF);
		return this;
	}

	public FluidInstance setLight(int packedLight) {
		this.blockLight = (byte) (packedLight & 0xF);
		this.skyLight = (byte) ((packedLight >> 16) & 0xF);
		return this;
	}

	public FluidInstance setOverlay(int overlay) {
		this.overlay = overlay;
		return this;
	}

	public int getPackedLight() {
		return (blockLight & 0xFF) | ((skyLight & 0xFF) << 16);
	}

	public static void write(long ptr, FluidInstance i) {
		MemoryUtil.memPutShort(ptr, (short) (i.blockLight & 0xFF));
		MemoryUtil.memPutShort(ptr + 2, (short) (i.skyLight & 0xFF));
		MemoryUtil.memPutByte(ptr + 4, i.r);
		MemoryUtil.memPutByte(ptr + 5, i.g);
		MemoryUtil.memPutByte(ptr + 6, i.b);
		MemoryUtil.memPutByte(ptr + 7, i.a);
		MemoryUtil.memPutFloat(ptr + 8, i.x);
		MemoryUtil.memPutFloat(ptr + 12, i.y);
		MemoryUtil.memPutFloat(ptr + 16, i.z);
		MemoryUtil.memPutInt(ptr + 20, i.overlay);
	}
}
