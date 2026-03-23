package com.jozufozu.flywheel.api.struct;

import com.jozufozu.flywheel.core.model.ModelTransformer;

/**
 * Compat stub for old Flywheel 0.6.x Batched.
 */
public interface Batched<S> extends StructType<S> {
	default void transform(S d, ModelTransformer.Params b) {}
}
