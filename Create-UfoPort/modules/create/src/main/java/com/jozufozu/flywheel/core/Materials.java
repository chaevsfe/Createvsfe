package com.jozufozu.flywheel.core;

import com.jozufozu.flywheel.api.struct.StructType;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.jozufozu.flywheel.core.materials.oriented.OrientedData;

/**
 * Compat stub for old Flywheel 0.6.x Materials.
 * Provides material type constants with proper generic typing.
 */
public class Materials {
	public static final StructType<ModelData> TRANSFORMED = new StructType<>() {
		@Override
		public ModelData create() {
			return new ModelData();
		}
	};
	public static final StructType<OrientedData> ORIENTED = new StructType<>() {
		@Override
		public OrientedData create() {
			return new OrientedData();
		}
	};
}
