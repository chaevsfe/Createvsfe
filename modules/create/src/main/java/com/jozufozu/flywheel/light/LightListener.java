package com.jozufozu.flywheel.light;

import com.jozufozu.flywheel.util.box.ImmutableBox;

import net.minecraft.world.level.LightLayer;

/**
 * Compat stub for old Flywheel 0.6.x LightListener.
 */
public interface LightListener {
	ImmutableBox getVolume();

	default boolean isListenerInvalid() {
		return false;
	}

	default void onLightUpdate(LightLayer type, ImmutableBox changed) {
	}

	default void onLightPacket(int chunkX, int chunkZ) {
	}
}
