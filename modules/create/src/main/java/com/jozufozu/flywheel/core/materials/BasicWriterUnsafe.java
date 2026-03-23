package com.jozufozu.flywheel.core.materials;

import com.jozufozu.flywheel.api.struct.StructType;
import com.jozufozu.flywheel.backend.gl.buffer.VecBuffer;
import com.jozufozu.flywheel.backend.struct.UnsafeBufferWriter;

/**
 * Compat stub for old Flywheel 0.6.x BasicWriterUnsafe.
 */
public class BasicWriterUnsafe<D extends BasicData> extends UnsafeBufferWriter<D> {
	public BasicWriterUnsafe(VecBuffer buffer, StructType<D> type) {
		super(buffer, type);
	}

	@Override
	protected void writeInternal(D d) {
		// no-op
	}
}
