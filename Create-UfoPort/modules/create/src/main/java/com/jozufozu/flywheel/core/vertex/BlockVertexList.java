package com.jozufozu.flywheel.core.vertex;

import java.nio.ByteBuffer;

import com.jozufozu.flywheel.api.vertex.ShadedVertexList;
import com.jozufozu.flywheel.api.vertex.VertexList;

/**
 * Compat stub for old Flywheel 0.6.x BlockVertexList.
 * Reads BLOCK vertex format data from a raw ByteBuffer.
 *
 * DefaultVertexFormat.BLOCK in MC 1.21.1 per vertex (32 bytes total):
 * - 3 floats (position):    offsets  0-11  (12 bytes)
 * - 4 bytes  (color RGBA):  offsets 12-15  ( 4 bytes)
 * - 2 floats (UV0):         offsets 16-23  ( 8 bytes)
 * - 2 shorts (UV2/light):   offsets 24-27  ( 4 bytes)  ← NO overlay channel in BLOCK
 * - 3 bytes + 1 pad (normal): offsets 28-31 ( 4 bytes)
 * Total: 32 bytes per vertex
 *
 * NOTE: The old stub incorrectly assumed a 36-byte layout (with an extra overlay
 * UV2 channel), which is actually DefaultVertexFormat.NEW_ENTITY, not BLOCK.
 * This caused getNX/NY/NZ to read past the end of the last vertex (IOOBE).
 */
public class BlockVertexList implements VertexList {
	protected final ByteBuffer buffer;
	protected final int vertexCount;
	protected final int stride;

	public BlockVertexList(ByteBuffer buffer, int vertexCount, int stride) {
		this.buffer = buffer;
		this.vertexCount = vertexCount;
		this.stride = stride;
	}

	@Override
	public float getX(int index) {
		return buffer.getFloat(index * stride);
	}

	@Override
	public float getY(int index) {
		return buffer.getFloat(index * stride + 4);
	}

	@Override
	public float getZ(int index) {
		return buffer.getFloat(index * stride + 8);
	}

	@Override
	public byte getR(int index) {
		return buffer.get(index * stride + 12);
	}

	@Override
	public byte getG(int index) {
		return buffer.get(index * stride + 13);
	}

	@Override
	public byte getB(int index) {
		return buffer.get(index * stride + 14);
	}

	@Override
	public byte getA(int index) {
		return buffer.get(index * stride + 15);
	}

	@Override
	public float getU(int index) {
		return buffer.getFloat(index * stride + 16);
	}

	@Override
	public float getV(int index) {
		return buffer.getFloat(index * stride + 20);
	}

	@Override
	public int getLight(int index) {
		return buffer.getInt(index * stride + 24);
	}

	@Override
	public float getNX(int index) {
		return toNormal(buffer.get(index * stride + 28));
	}

	@Override
	public float getNY(int index) {
		return toNormal(buffer.get(index * stride + 29));
	}

	@Override
	public float getNZ(int index) {
		return toNormal(buffer.get(index * stride + 30));
	}

	@Override
	public int getVertexCount() {
		return vertexCount;
	}

	private static float toNormal(byte b) {
		return b / 127.0f;
	}

	/**
	 * A vertex list that also tracks which vertices are "shaded" (before unshadedStartVertex).
	 */
	public static class Shaded extends BlockVertexList implements ShadedVertexList {
		private final int unshadedStartVertex;

		public Shaded(ByteBuffer buffer, int vertexCount, int stride, int unshadedStartVertex) {
			super(buffer, vertexCount, stride);
			this.unshadedStartVertex = unshadedStartVertex;
		}

		@Override
		public boolean isShaded(int index) {
			return index < unshadedStartVertex;
		}
	}
}
