package com.jozufozu.flywheel.api.struct;

import com.jozufozu.flywheel.api.struct.StructWriter;
import com.jozufozu.flywheel.backend.gl.buffer.VecBuffer;
import com.jozufozu.flywheel.core.layout.BufferLayout;

import net.minecraft.resources.ResourceLocation;

/**
 * Compat stub for old Flywheel 0.6.x Instanced.
 */
public interface Instanced<S> extends StructType<S> {
	default StructWriter<S> getWriter(VecBuffer backing) {
		return new StructWriter<>() {
			@Override
			public void write(S instance) {}
		};
	}

	default ResourceLocation getProgramSpec() {
		return ResourceLocation.withDefaultNamespace("noop");
	}
}
