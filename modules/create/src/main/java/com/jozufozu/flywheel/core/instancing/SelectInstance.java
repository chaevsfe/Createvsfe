package com.jozufozu.flywheel.core.instancing;

import java.util.Optional;
import java.util.function.IntSupplier;

import com.jozufozu.flywheel.api.Instancer;

/**
 * Compat stub for old Flywheel 0.6.x SelectInstance.
 */
public class SelectInstance<D> {
	private IntSupplier indexSupplier;

	public SelectInstance(IntSupplier indexSupplier) {
		this.indexSupplier = indexSupplier;
	}

	public SelectInstance(Instancer<D> instancer) {
	}

	public SelectInstance<D> addModel(Instancer<D> instancer) {
		return this;
	}

	public SelectInstance<D> update() {
		return this;
	}

	public Optional<D> get() {
		return Optional.empty();
	}

	public void delete() {
	}
}
