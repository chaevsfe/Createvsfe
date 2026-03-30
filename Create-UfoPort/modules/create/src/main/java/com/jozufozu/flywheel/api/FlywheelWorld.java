package com.jozufozu.flywheel.api;

/**
 * Compat stub for old Flywheel 0.6.x FlywheelWorld.
 * Marker interface for levels that support Flywheel rendering.
 */
public interface FlywheelWorld {
	default boolean supportsFlywheel() {
		return true;
	}
}
