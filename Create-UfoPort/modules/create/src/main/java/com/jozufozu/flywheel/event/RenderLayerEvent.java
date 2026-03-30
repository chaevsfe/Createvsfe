package com.jozufozu.flywheel.event;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

/**
 * Compat stub for old Flywheel 0.6.x RenderLayerEvent.
 */
public class RenderLayerEvent {
	public PoseStack stack = new PoseStack();
	public double camX, camY, camZ;
	public RenderType type;
	public Matrix4f viewProjection = new Matrix4f();
	public Buffers buffers = new Buffers();

	public RenderType getType() {
		return type;
	}

	public RenderType getLayer() {
		return type;
	}

	public net.minecraft.world.level.LevelAccessor getWorld() {
		return null;
	}

	public static class Buffers {
		public MultiBufferSource.BufferSource bufferSource() {
			return null;
		}
	}
}
