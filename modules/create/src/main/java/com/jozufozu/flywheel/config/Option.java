package com.jozufozu.flywheel.config;

/**
 * Compat stub for old Flywheel 0.6.x Option.
 */
public interface Option<T> {
	T get();
	void set(T value);
	String getKey();
}
