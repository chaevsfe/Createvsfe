package com.jozufozu.flywheel.backend.gl.buffer;

/**
 * Compat stub for old Flywheel 0.6.x VecBuffer.
 */
public class VecBuffer {
	public VecBuffer putFloat(float value) {
		return this;
	}

	public VecBuffer putVec4(float x, float y, float z, float w) {
		return this;
	}

	public VecBuffer putVec3(float x, float y, float z) {
		return this;
	}

	public VecBuffer putVec2(float u, float v) {
		return this;
	}

	public VecBuffer put(byte[] data) {
		return this;
	}
}
