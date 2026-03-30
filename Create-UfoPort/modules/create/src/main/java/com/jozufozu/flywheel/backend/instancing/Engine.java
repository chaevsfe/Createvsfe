package com.jozufozu.flywheel.backend.instancing;

import com.jozufozu.flywheel.api.MaterialManager;

/**
 * Compat stub for old Flywheel 0.6.x Engine.
 */
public interface Engine extends MaterialManager {
	void delete();

	default void render(Object taskEngine, Object event) {
	}
}
