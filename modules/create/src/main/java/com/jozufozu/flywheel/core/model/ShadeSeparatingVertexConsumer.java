package com.jozufozu.flywheel.core.model;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.resources.model.BakedModel;

/**
 * Compat stub for old Flywheel 0.6.x ShadeSeparatingVertexConsumer.
 * Delegates all vertex writing to the shaded builder (no shade separation).
 */
public class ShadeSeparatingVertexConsumer implements VertexConsumer {
	private BufferBuilder shadedBuilder;
	private BufferBuilder unshadedBuilder;

	public void prepare(BufferBuilder shadedBuilder, BufferBuilder unshadedBuilder) {
		this.shadedBuilder = shadedBuilder;
		this.unshadedBuilder = unshadedBuilder;
	}

	public void clear() {
		this.shadedBuilder = null;
		this.unshadedBuilder = null;
	}

	/**
	 * In the old Flywheel, this wrapped the model to separate shaded/unshaded quads.
	 * Here we just return the model unchanged.
	 */
	public BakedModel wrapModel(BakedModel model) {
		return model;
	}

	@Override
	public VertexConsumer addVertex(float x, float y, float z) {
		if (shadedBuilder != null) {
			shadedBuilder.addVertex(x, y, z);
		}
		return this;
	}

	@Override
	public VertexConsumer setColor(int r, int g, int b, int a) {
		if (shadedBuilder != null) {
			shadedBuilder.setColor(r, g, b, a);
		}
		return this;
	}

	@Override
	public VertexConsumer setUv(float u, float v) {
		if (shadedBuilder != null) {
			shadedBuilder.setUv(u, v);
		}
		return this;
	}

	@Override
	public VertexConsumer setUv1(int u, int v) {
		if (shadedBuilder != null) {
			shadedBuilder.setUv1(u, v);
		}
		return this;
	}

	@Override
	public VertexConsumer setUv2(int u, int v) {
		if (shadedBuilder != null) {
			shadedBuilder.setUv2(u, v);
		}
		return this;
	}

	@Override
	public VertexConsumer setNormal(float x, float y, float z) {
		if (shadedBuilder != null) {
			shadedBuilder.setNormal(x, y, z);
		}
		return this;
	}
}
