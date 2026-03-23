package com.jozufozu.flywheel.core.materials;

import com.jozufozu.flywheel.api.InstanceData;

/**
 * Compat stub for old Flywheel 0.6.x FlatLit.
 */
public interface FlatLit<Self extends InstanceData & FlatLit<Self>> {
	Self setBlockLight(int blockLight);

	Self setSkyLight(int skyLight);

	default int getPackedLight() {
		return 0;
	}
}
