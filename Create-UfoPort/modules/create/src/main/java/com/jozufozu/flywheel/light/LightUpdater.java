package com.jozufozu.flywheel.light;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.level.LevelAccessor;

/**
 * Compat stub for old Flywheel 0.6.x LightUpdater.
 * Listeners are tracked but never notified (light updates are no-ops).
 */
public class LightUpdater {
	private static final Map<LevelAccessor, LightUpdater> INSTANCES = new HashMap<>();

	public static LightUpdater get(LevelAccessor level) {
		return INSTANCES.computeIfAbsent(level, l -> new LightUpdater());
	}

	public void addListener(LightListener listener) {
		// no-op: instancing disabled
	}

	public void removeListener(LightListener listener) {
		// no-op
	}
}
