package com.jozufozu.flywheel.config;

/**
 * Compat stub for old Flywheel 0.6.x FlwConfigOption.
 */
public interface FlwConfigOption<T> {
	T get();
	void set(T value);
}
