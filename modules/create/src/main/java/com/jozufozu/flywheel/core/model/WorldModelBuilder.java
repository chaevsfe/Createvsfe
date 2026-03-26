package com.jozufozu.flywheel.core.model;

import java.util.Collection;

import com.jozufozu.flywheel.fabric.model.LayerFilteringBakedModel;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.FluidState;

/**
 * Compat stub for old Flywheel 0.6.x WorldModelBuilder.
 * Renders a collection of structure blocks into a ShadeSeparatedBufferedData.
 */
public class WorldModelBuilder {
	private final RenderType layer;
	private BlockAndTintGetter renderWorld;
	private Collection<StructureTemplate.StructureBlockInfo> blocks;

	public WorldModelBuilder(RenderType layer) {
		this.layer = layer;
	}

	public WorldModelBuilder withRenderWorld(LevelAccessor level) {
		if (level instanceof BlockAndTintGetter getter) {
			this.renderWorld = getter;
		}
		return this;
	}

	public WorldModelBuilder withBlocks(Collection<StructureTemplate.StructureBlockInfo> blocks) {
		this.blocks = blocks;
		return this;
	}

	public ShadeSeparatedBufferedData build() {
		if (renderWorld == null || blocks == null || blocks.isEmpty()) {
			return new ShadeSeparatedBufferedData(java.nio.ByteBuffer.allocate(0), null, 0);
		}

		var dispatcher = Minecraft.getInstance().getBlockRenderer();
		RandomSource random = RandomSource.createNewThreadLocalInstance();

		ByteBufferBuilder shadedByteBuffer = new ByteBufferBuilder(8192);
		ByteBufferBuilder unshadedByteBuffer = new ByteBufferBuilder(512);
		BufferBuilder shadedBuilder = new BufferBuilder(shadedByteBuffer, VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
		BufferBuilder unshadedBuilder = new BufferBuilder(unshadedByteBuffer, VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);

		ShadeSeparatingVertexConsumer wrapper = new ShadeSeparatingVertexConsumer();
		wrapper.prepare(shadedBuilder, unshadedBuilder);

		PoseStack poseStack = new PoseStack();

		ModelBlockRenderer.enableCaching();
		for (StructureTemplate.StructureBlockInfo info : blocks) {
			BlockState state = info.state();
			BlockPos pos = info.pos();
			FluidState fluidState = state.getFluidState();

			if (state.getRenderShape() == RenderShape.MODEL) {
				BakedModel model = dispatcher.getBlockModel(state);

				if (model.isVanillaAdapter()) {
					if (ItemBlockRenderTypes.getChunkRenderType(state) != layer) {
						model = null;
					}
				} else {
					model = LayerFilteringBakedModel.wrap(model, layer);
				}

				if (model != null) {
					poseStack.pushPose();
					poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
					dispatcher.getModelRenderer().tesselateBlock(renderWorld, model, state, pos, poseStack,
							wrapper, false, random, state.getSeed(pos), OverlayTexture.NO_OVERLAY);
					poseStack.popPose();
				}
			}

			if (!fluidState.isEmpty() && ItemBlockRenderTypes.getRenderLayer(fluidState) == layer) {
				dispatcher.renderLiquid(pos, renderWorld, shadedBuilder, state, fluidState);
			}
		}
		ModelBlockRenderer.clearCache();

		wrapper.clear();
		// Note: ByteBufferBuilders are intentionally not closed; their native memory
		// backs the returned ShadeSeparatedBufferedData and must remain valid.
		return ModelUtil.endAndCombine(shadedBuilder, unshadedBuilder);
	}

	public Model toModel(String name) {
		return new Model() {};
	}
}
