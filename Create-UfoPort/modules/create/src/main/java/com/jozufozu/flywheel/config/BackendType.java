package com.jozufozu.flywheel.config;

/**
 * Compat stub for old Flywheel 0.6.x BackendType.
 */
public enum BackendType {
	OFF("Off"),
	BATCHING("Batching"),
	INSTANCING("Instancing");

	private final String name;

	BackendType(String name) {
		this.name = name;
	}

	public String getProperName() {
		return name;
	}

	public String getShortName() {
		return name;
	}
}
