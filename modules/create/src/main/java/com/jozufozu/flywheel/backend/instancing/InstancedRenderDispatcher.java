package com.jozufozu.flywheel.backend.instancing;

/**
 * Compat stub for old Flywheel 0.6.x InstancedRenderDispatcher.
 * All methods are no-ops; BER fallback renderers handle rendering.
 */
public class InstancedRenderDispatcher {
	public static void enqueueUpdate(Object blockEntity) {
		// no-op: Flywheel instancing disabled, BER handles rendering
	}
}
