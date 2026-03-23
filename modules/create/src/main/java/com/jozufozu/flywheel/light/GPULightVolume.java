package com.jozufozu.flywheel.light;

import com.jozufozu.flywheel.util.box.ImmutableBox;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;

/**
 * Compat stub for old Flywheel 0.6.x GPULightVolume.
 * All methods are no-ops since instancing is disabled.
 */
public class GPULightVolume {
	public GPULightVolume(LevelAccessor level, ImmutableBox bounds) {
	}

	public void initialize() {
	}

	public boolean isListenerInvalid() {
		return false;
	}

	public void onLightUpdate(LightLayer type, ImmutableBox changed) {
	}

	public void onLightPacket(int chunkX, int chunkZ) {
	}

	public void delete() {
	}

	public void move(ImmutableBox newBounds) {
	}

	public void bind() {
	}
}
