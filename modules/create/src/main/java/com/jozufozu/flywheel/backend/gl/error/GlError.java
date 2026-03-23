package com.jozufozu.flywheel.backend.gl.error;

/**
 * Compat stub for old Flywheel 0.6.x GlError.
 */
public class GlError {
	public static GlError poll() {
		return null;
	}

	public CharSequence getName() {
		return "NONE";
	}

	public static void pollAndThrow(java.util.function.Supplier<String> messageSupplier) {
		// no-op
	}
}
