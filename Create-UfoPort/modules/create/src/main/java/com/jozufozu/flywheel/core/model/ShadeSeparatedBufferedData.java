package com.jozufozu.flywheel.core.model;

import java.nio.ByteBuffer;

import com.mojang.blaze3d.vertex.MeshData;

/**
 * Compat stub for old Flywheel 0.6.x ShadeSeparatedBufferedData.
 * Holds vertex data with shade separation info.
 */
public class ShadeSeparatedBufferedData {
	private final ByteBuffer vertexBuffer;
	private final MeshData.DrawState drawState;
	private final int unshadedStartVertex;

	public ShadeSeparatedBufferedData(ByteBuffer vertexBuffer, MeshData.DrawState drawState, int unshadedStartVertex) {
		this.vertexBuffer = vertexBuffer;
		this.drawState = drawState;
		this.unshadedStartVertex = unshadedStartVertex;
	}

	public ByteBuffer vertexBuffer() {
		return vertexBuffer;
	}

	public MeshData.DrawState drawState() {
		return drawState;
	}

	public int unshadedStartVertex() {
		return unshadedStartVertex;
	}

	public void release() {
		// no-op
	}
}
