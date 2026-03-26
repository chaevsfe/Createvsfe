package com.jozufozu.flywheel.core.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
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
			// Both builders produced nothing (model has no quads).
			// Return null drawState — SuperByteBuffer handles this by treating it as empty.
			ByteBuffer empty = ByteBuffer.allocate(0).order(ByteOrder.nativeOrder());
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
		MeshData.DrawState unshadedState = unshadedMesh.drawState();
		ByteBuffer shadedBuf = shadedMesh.vertexBuffer();
		ByteBuffer unshadedBuf = unshadedMesh.vertexBuffer();

		int shadedVertexCount = shadedState.vertexCount();
		int totalVertexCount = shadedVertexCount + unshadedState.vertexCount();

		// Use native byte order to match the source buffers (ByteBufferBuilder uses native/LITTLE_ENDIAN on x86).
		// Without this, getFloat() calls in BlockVertexList would byte-swap all float reads on x86.
		ByteBuffer combined = ByteBuffer.allocate(shadedBuf.remaining() + unshadedBuf.remaining())
				.order(ByteOrder.nativeOrder());
		combined.put(shadedBuf);
		combined.put(unshadedBuf);
		combined.flip();

		// Build a combined DrawState with total vertex count so SuperByteBuffer iterates all vertices.
		// SuperByteBuffer only uses format() and vertexCount(), so indexCount is only informational.
		MeshData.DrawState combinedState = new MeshData.DrawState(
				shadedState.format(), totalVertexCount,
				shadedState.indexCount() + unshadedState.indexCount(),
				shadedState.mode(), shadedState.indexType());

		return new ShadeSeparatedBufferedData(combined, combinedState, shadedVertexCount);
	}

	/**
	 * Get buffered data for a baked model with a reference state.
	 */
	public static ShadeSeparatedBufferedData getBufferedData(BakedModel model, BlockState referenceState) {
		return getBufferedData(model, referenceState, new PoseStack());
	}

	/**
	 * Get buffered data for a baked model with a reference state and pose stack.
	 * Renders the model using MC's block renderer into a ByteBuffer suitable for SuperByteBuffer.
	 */
	public static ShadeSeparatedBufferedData getBufferedData(BakedModel model, BlockState referenceState, PoseStack ms) {
		ModelBlockRenderer blockRenderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
		RandomSource random = RandomSource.createNewThreadLocalInstance();

		// Use the current client level for ambient occlusion and tinting.
		// Null is safe when the model doesn't use AO, but we pass the real level when available.
		BlockAndTintGetter renderLevel = Minecraft.getInstance().level;

		ByteBufferBuilder shadedByteBuffer = new ByteBufferBuilder(2048);
		ByteBufferBuilder unshadedByteBuffer = new ByteBufferBuilder(512);
		BufferBuilder shadedBuilder = new BufferBuilder(shadedByteBuffer, VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
		BufferBuilder unshadedBuilder = new BufferBuilder(unshadedByteBuffer, VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);

		ShadeSeparatingVertexConsumer wrapper = new ShadeSeparatingVertexConsumer();
		wrapper.prepare(shadedBuilder, unshadedBuilder);

		ModelBlockRenderer.enableCaching();
		blockRenderer.tesselateBlock(renderLevel, model, referenceState, BlockPos.ZERO, ms, wrapper, false, random,
				referenceState.getSeed(BlockPos.ZERO), OverlayTexture.NO_OVERLAY);
		ModelBlockRenderer.clearCache();

		wrapper.clear();
		// Note: shadedByteBuffer/unshadedByteBuffer are intentionally not closed here.
		// The MeshData returned by build() holds a view into their native memory.
		// The memory is valid as long as the ShadeSeparatedBufferedData is in use.
		return endAndCombine(shadedBuilder, unshadedBuilder);
	}
}
