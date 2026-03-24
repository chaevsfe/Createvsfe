package com.simibubi.create.foundation.render;

import dev.engine_room.flywheel.api.instance.InstanceHandle;
import dev.engine_room.flywheel.api.instance.InstanceType;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;

import net.minecraft.core.BlockPos;

/**
 * GPU instance data for fluid rendering in contraptions and fluid blocks.
 * Each instance represents a fluid surface quad with position, light, color, overlay,
 * and fluid fill progress.
 */
public class FluidInstance extends TransformedInstance {
	public float progress;
	public float vScale;
	public float v0;

	public FluidInstance(InstanceType<? extends FluidInstance> type, InstanceHandle handle) {
		super(type, handle);
	}

	// Backward-compatible setPosition: sets pose to identity + translation
	public FluidInstance setPosition(BlockPos pos) {
		return setPosition(pos.getX(), pos.getY(), pos.getZ());
	}

	public FluidInstance setPosition(float x, float y, float z) {
		this.pose.identity().translate(x, y, z);
		return this;
	}

	public FluidInstance setColor(int r, int g, int b, int a) {
		this.red = (byte) r;
		this.green = (byte) g;
		this.blue = (byte) b;
		this.alpha = (byte) a;
		return this;
	}

	public FluidInstance setColor(int color) {
		this.red = (byte) ((color >> 16) & 0xFF);
		this.green = (byte) ((color >> 8) & 0xFF);
		this.blue = (byte) (color & 0xFF);
		this.alpha = (byte) ((color >> 24) & 0xFF);
		return this;
	}

	public FluidInstance setBlockLight(int blockLight) {
		this.light = (this.light & 0xFFFF0000) | (blockLight & 0xFFFF);
		return this;
	}

	public FluidInstance setSkyLight(int skyLight) {
		this.light = (this.light & 0x0000FFFF) | ((skyLight & 0xFFFF) << 16);
		return this;
	}

	public FluidInstance setLight(int packedLight) {
		this.light = packedLight;
		return this;
	}

	public FluidInstance setOverlay(int overlay) {
		this.overlay = overlay;
		return this;
	}

	public int getPackedLight() {
		return this.light;
	}
}
