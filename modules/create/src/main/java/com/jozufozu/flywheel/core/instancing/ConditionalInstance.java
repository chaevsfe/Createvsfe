package com.jozufozu.flywheel.core.instancing;

import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import com.jozufozu.flywheel.api.Instancer;

/**
 * Compat stub for old Flywheel 0.6.x ConditionalInstance.
 */
public class ConditionalInstance<D> {
	public ConditionalInstance(Instancer<D> instancer) {
	}

	public ConditionalInstance<D> withCondition(BooleanSupplier condition) {
		return this;
	}

	public ConditionalInstance<D> withSetup(Consumer<D> initializer) {
		return this;
	}

	public ConditionalInstance<D> update() {
		return this;
	}

	public Optional<D> get() {
		return Optional.empty();
	}

	public void delete() {
	}
}
