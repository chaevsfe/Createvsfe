package com.jozufozu.flywheel.config;

/**
 * Compat stub for old Flywheel 0.6.x FlwConfig.
 */
public class FlwConfig {
	private static final FlwConfig INSTANCE = new FlwConfig();

	public static FlwConfig get() {
		return INSTANCE;
	}

	public BackendType getBackendType() {
		return BackendType.OFF;
	}

	public java.util.Map<String, Option<?>> getOptionMapView() {
		return java.util.Collections.emptyMap();
	}

	public void save() {
		// no-op
	}
}
