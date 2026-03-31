package com.simibubi.create.content.contraptions.render;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.jozufozu.flywheel.core.model.ShadeSeparatedBufferedData;
import com.jozufozu.flywheel.core.model.WorldModelBuilder;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllMovementBehaviours;
import com.simibubi.create.Create;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ContraptionWorld;
import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.foundation.render.BlockEntityRenderHelper;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.render.SuperByteBufferCache;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

@Environment(EnvType.CLIENT)
public class ContraptionRenderDispatcher {

	public static final SuperByteBufferCache.Compartment<com.simibubi.create.foundation.utility.Pair<Contraption, RenderType>> CONTRAPTION = new SuperByteBufferCache.Compartment<>();

	/**
	 * Per-contraption render worlds, keyed by contraption entity ID.
	 * Created lazily when the entity is first rendered, removed when the contraption dies.
	 */
	private static final Map<Integer, VirtualRenderWorld> RENDER_WORLDS = new HashMap<>();

	/**
	 * Reusable ContraptionMatrices instance. Set up and cleared each frame by
	 * ContraptionEntityRenderer.render().
	 */
	private static final ContraptionMatrices MATRICES = new ContraptionMatrices();

	/**
	 * Get the shared ContraptionMatrices instance for the current frame.
	 */
	public static ContraptionMatrices getMatrices() {
		return MATRICES;
	}

	/**
	 * Get or create a VirtualRenderWorld for the given contraption.
	 * The render world is cached per contraption entity ID.
	 */
	public static VirtualRenderWorld getOrCreateRenderWorld(Level world, Contraption contraption) {
		int entityId = contraption.entity.getId();
		return RENDER_WORLDS.computeIfAbsent(entityId, id -> setupRenderWorld(world, contraption));
	}

	/**
	 * Get the cached structure SBB for the given contraption and render type.
	 */
	public static SuperByteBuffer getStructureBuffer(Contraption contraption, VirtualRenderWorld renderWorld, RenderType renderType) {
		return CreateClient.BUFFER_CACHE.get(CONTRAPTION, com.simibubi.create.foundation.utility.Pair.of(contraption, renderType),
			() -> buildStructureBuffer(renderWorld, contraption, renderType));
	}

	/**
	 * Reset a contraption's renderer.
	 *
	 * @param contraption The contraption to invalidate.
	 * @return true if there was a renderer associated with the given contraption.
	 */
	public static boolean invalidate(Contraption contraption) {
		int entityId = contraption.entity.getId();
		VirtualRenderWorld removed = RENDER_WORLDS.remove(entityId);
		for (RenderType chunkBufferLayer : RenderType.chunkBufferLayers()) {
			CreateClient.BUFFER_CACHE.invalidate(CONTRAPTION, com.simibubi.create.foundation.utility.Pair.of(contraption, chunkBufferLayer));
		}
		return removed != null;
	}

	public static void tick(Level world) {
		if (Minecraft.getInstance()
			.isPaused())
			return;

		// Remove render worlds for dead contraptions
		RENDER_WORLDS.entrySet().removeIf(entry -> {
			var entity = world.getEntity(entry.getKey());
			return entity == null || !entity.isAlive();
		});
	}

	public static VirtualRenderWorld setupRenderWorld(Level world, Contraption c) {
		ContraptionWorld contraptionWorld = c.getContraptionWorld();

		BlockPos origin = c.anchor;
		int minBuildHeight = contraptionWorld.getMinBuildHeight();
		int height = contraptionWorld.getHeight();
		VirtualRenderWorld renderWorld = new VirtualRenderWorld(world, minBuildHeight, height, origin) {
			@Override
			public boolean supportsFlywheel() {
				return VisualizationManager.supportsVisualization(world);
			}
		};

		renderWorld.setBlockEntities(c.presentBlockEntities.values());
		for (StructureTemplate.StructureBlockInfo info : c.getBlocks()
			.values())
			renderWorld.setBlock(info.pos(), info.state(), 0);

		renderWorld.runLightEngine();
		return renderWorld;
	}

	public static void renderBlockEntities(Level world, VirtualRenderWorld renderWorld, Contraption c,
		ContraptionMatrices matrices, MultiBufferSource buffer) {
		BlockEntityRenderHelper.renderBlockEntities(world, renderWorld, c.getSpecialRenderedBEs(),
			matrices.getModelViewProjection(), matrices.getLight(), buffer);
	}

	public static void renderActors(Level world, VirtualRenderWorld renderWorld, Contraption c,
		ContraptionMatrices matrices, MultiBufferSource buffer) {
		PoseStack m = matrices.getModel();

		for (Pair<StructureTemplate.StructureBlockInfo, MovementContext> actor : c.getActors()) {
			MovementContext context = actor.getRight();
			if (context == null)
				continue;
			if (context.world == null)
				context.world = world;
			StructureTemplate.StructureBlockInfo blockInfo = actor.getLeft();

			MovementBehaviour movementBehaviour = AllMovementBehaviours.getBehaviour(blockInfo.state());
			if (movementBehaviour != null) {
				if (c.isHiddenInPortal(blockInfo.pos()))
					continue;
				m.pushPose();
				TransformStack.of(m)
					.translate(blockInfo.pos());
				movementBehaviour.renderInContraption(context, renderWorld, matrices, buffer);
				m.popPose();
			}
		}
	}

	public static SuperByteBuffer buildStructureBuffer(VirtualRenderWorld renderWorld, Contraption c,
		RenderType layer) {
		Collection<StructureTemplate.StructureBlockInfo> values = c.getRenderedBlocks();
		ShadeSeparatedBufferedData data = new WorldModelBuilder(layer).withRenderWorld(renderWorld)
				.withBlocks(values)
				.build();
		SuperByteBuffer sbb = new SuperByteBuffer(data);
		data.release();
		return sbb;
	}

	public static int getLight(Level world, float lx, float ly, float lz) {
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		float block = 0, sky = 0;
		float offset = 1 / 8f;

		for (float zOffset = offset; zOffset >= -offset; zOffset -= 2 * offset)
			for (float yOffset = offset; yOffset >= -offset; yOffset -= 2 * offset)
				for (float xOffset = offset; xOffset >= -offset; xOffset -= 2 * offset) {
					pos.set(lx + xOffset, ly + yOffset, lz + zOffset);
					block += world.getBrightness(LightLayer.BLOCK, pos) / 8f;
					sky += world.getBrightness(LightLayer.SKY, pos) / 8f;
				}

		return LightTexture.pack((int) block, (int) sky);
	}

	public static int getContraptionWorldLight(MovementContext context, VirtualRenderWorld renderWorld) {
		return LevelRenderer.getLightColor(renderWorld, context.localPos);
	}

	public static void reset() {
		RENDER_WORLDS.clear();
		CreateClient.BUFFER_CACHE.invalidate(CONTRAPTION);
	}

	public static boolean canInstance() {
		Level level = Minecraft.getInstance().level;
		return level != null && VisualizationManager.supportsVisualization(level);
	}
}
