package com.jozufozu.flywheel.backend.gl;

/**
 * Compat stub for old Flywheel 0.6.x GlStateTracker.
 */
public class GlStateTracker {
	public static State getRestoreState() {
		return new State();
	}

	public static class State implements AutoCloseable {
		@Override
		public void close() {
		}
	}
}
