package com.jozufozu.flywheel.api.instance;

/**
 * Compat stub for old Flywheel 0.6.x DynamicInstance.
 */
public interface DynamicInstance {
	void beginFrame();

	default boolean decreaseFramerateWithDistance() {
		return true;
	}
}
