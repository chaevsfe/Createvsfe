package com.jozufozu.flywheel.api.struct;

import com.jozufozu.flywheel.core.layout.BufferLayout;

/**
 * Compat stub for old Flywheel 0.6.x StructType.
 */
public interface StructType<S> {
	S create();

	default BufferLayout getLayout() {
		return new BufferLayout();
	}
}
