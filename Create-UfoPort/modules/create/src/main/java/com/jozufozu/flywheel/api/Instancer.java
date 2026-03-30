package com.jozufozu.flywheel.api;

/**
 * Compat stub for old Flywheel 0.6.x Instancer.
 */
public class Instancer<D> {
	@SuppressWarnings("unchecked")
	public D createInstance() {
		return null;
	}

	public void stealInstance(D inOther) {
		// no-op
	}

	public void notifyDirty() {
	}

	public void notifyRemoval() {
	}
}
