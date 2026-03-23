package com.jozufozu.flywheel.backend.struct;

import com.jozufozu.flywheel.api.struct.StructType;
import com.jozufozu.flywheel.api.struct.StructWriter;
import com.jozufozu.flywheel.backend.gl.buffer.VecBuffer;

/**
 * Compat stub for old Flywheel 0.6.x UnsafeBufferWriter.
 */
public class UnsafeBufferWriter<S> implements StructWriter<S> {
	protected VecBuffer backingBuffer = new VecBuffer();
	protected long writePointer;

	public UnsafeBufferWriter(VecBuffer buffer, StructType<S> type) {
	}

	@Override
	public void write(S instance) {
		writeInternal(instance);
	}

	protected void writeInternal(S d) {
		// no-op
	}
}
