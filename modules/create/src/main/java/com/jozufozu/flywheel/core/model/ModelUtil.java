package com.jozufozu.flywheel.core.model;

import java.nio.ByteBuffer;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Compat stub for old Flywheel 0.6.x ModelUtil.
 * Provides utility for combining shaded/unshaded mesh data.
 */
public class ModelUtil {
	/**
	 * End both builders and combine their results into a ShadeSeparatedBufferedData.
	 */
	public static ShadeSeparatedBufferedData endAndCombine(BufferBuilder shadedBuilder, BufferBuilder unshadedBuilder) {
		MeshData shadedMesh = shadedBuilder.build();
		MeshData unshadedMesh = unshadedBuilder.build();

		if (shadedMesh == null && unshadedMesh == null) {
			ByteBuffer empty = ByteBuffer.allocate(0);
			return new ShadeSeparatedBufferedData(empty, null, 0);
		}

		if (shadedMesh == null) {
			MeshData.DrawState drawState = unshadedMesh.drawState();
			ByteBuffer vertexBuffer = unshadedMesh.vertexBuffer();
			return new ShadeSeparatedBufferedData(vertexBuffer, drawState, 0);
		}

		if (unshadedMesh == null) {
			MeshData.DrawState drawState = shadedMesh.drawState();
			ByteBuffer vertexBuffer = shadedMesh.vertexBuffer();
			int vertexCount = drawState.vertexCount();
			return new ShadeSeparatedBufferedData(vertexBuffer, drawState, vertexCount);
		}

		// Combine both: shaded first, then unshaded
		MeshData.DrawState shadedState = shadedMesh.drawState();
		ByteBuffer shadedBuf = shadedMesh.vertexBuffer();
		ByteBuffer unshadedBuf = unshadedMesh.vertexBuffer();

		int shadedVertexCount = shadedState.vertexCount();

		ByteBuffer combined = ByteBuffer.allocate(shadedBuf.remaining() + unshadedBuf.remaining());
		combined.put(shadedBuf);
		combined.put(unshadedBuf);
		combined.flip();

		return new ShadeSeparatedBufferedData(combined, shadedState, shadedVertexCount);
	}

	/**
	 * Get buffered data for a baked model with a reference state.
	 */
	public static ShadeSeparatedBufferedData getBufferedData(BakedModel model, BlockState referenceState) {
		ByteBuffer empty = ByteBuffer.allocate(0);
		return new ShadeSeparatedBufferedData(empty, null, 0);
	}

	/**
	 * Get buffered data for a baked model with a reference state and pose stack.
	 */
	public static ShadeSeparatedBufferedData getBufferedData(BakedModel model, BlockState referenceState, PoseStack ms) {
		ByteBuffer empty = ByteBuffer.allocate(0);
		return new ShadeSeparatedBufferedData(empty, null, 0);
	}
}
