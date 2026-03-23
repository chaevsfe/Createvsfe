package com.jozufozu.flywheel.api;

import com.jozufozu.flywheel.api.struct.StructType;

/**
 * Compat stub for old Flywheel 0.6.x MaterialManager.
 * Provides minimum interface needed for compilation.
 */
public interface MaterialManager {
	default MaterialGroup defaultSolid() {
		return new MaterialGroup();
	}

	default MaterialGroup defaultCutout() {
		return new MaterialGroup();
	}

	default MaterialGroup defaultTransparent() {
		return new MaterialGroup();
	}

	/**
	 * Stub material group that returns dummy materials.
	 */
	class MaterialGroup {
		@SuppressWarnings("unchecked")
		public <D> Material<D> material(StructType<D> type) {
			return new Material<>();
		}
	}
}
