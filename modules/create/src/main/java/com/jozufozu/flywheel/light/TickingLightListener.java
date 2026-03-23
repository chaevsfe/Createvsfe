package com.jozufozu.flywheel.light;

/**
 * Compat stub for old Flywheel 0.6.x TickingLightListener.
 */
public interface TickingLightListener extends LightListener {
	boolean tickLightListener();

	default boolean decreaseFramerateWithDistance() {
		return true;
	}
}
