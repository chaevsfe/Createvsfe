package com.simibubi.create.content.contraptions.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class ContraptionEntityRenderer<C extends AbstractContraptionEntity> extends EntityRenderer<C> {

	public ContraptionEntityRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public ResourceLocation getTextureLocation(C entity) {
		return null;
	}

	@Override
	public boolean shouldRender(C entity, Frustum clippingHelper, double cameraX, double cameraY,
		double cameraZ) {
		if (entity.getContraption() == null)
			return false;
		if (!entity.isAliveOrStale())
			return false;
		if (!entity.isReadyForRender())
			return false;

		return super.shouldRender(entity, clippingHelper, cameraX, cameraY, cameraZ);
	}

	@Override
	public void render(C entity, float yaw, float partialTicks, PoseStack ms, MultiBufferSource buffers,
		int overlay) {
		super.render(entity, yaw, partialTicks, ms, buffers, overlay);

		Contraption contraption = entity.getContraption();
		if (contraption == null)
			return;

		Level level = entity.level();
		VirtualRenderWorld renderWorld = ContraptionRenderDispatcher.getOrCreateRenderWorld(level, contraption);
		ContraptionMatrices matrices = ContraptionRenderDispatcher.getMatrices();
		matrices.setup(ms, entity);

		// Render structure blocks via SBB (when Flywheel instancing is not active)
		for (RenderType renderType : RenderType.chunkBufferLayers()) {
			SuperByteBuffer sbb = ContraptionRenderDispatcher.getStructureBuffer(contraption, renderWorld, renderType);
			if (sbb != null && !sbb.isEmpty()) {
				VertexConsumer vc = buffers.getBuffer(renderType);
				sbb.transform(matrices.getModel())
					.light(matrices.getWorld())
					.hybridLight()
					.renderInto(matrices.getViewProjection(), vc);
			}
		}

		// Render block entities
		ContraptionRenderDispatcher.renderBlockEntities(level, renderWorld, contraption, matrices, buffers);

		if (buffers instanceof MultiBufferSource.BufferSource bufferSource)
			bufferSource.endBatch();

		// Render actors (harvesters, drills, deployers, etc.)
		ContraptionRenderDispatcher.renderActors(level, renderWorld, contraption, matrices, buffers);

		matrices.clear();
	}

}
