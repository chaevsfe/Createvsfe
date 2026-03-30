package com.simibubi.create.foundation.ponder.element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.jozufozu.flywheel.core.model.ModelUtil;
import com.jozufozu.flywheel.core.model.ShadeSeparatedBufferedData;
import com.jozufozu.flywheel.core.model.ShadeSeparatingVertexConsumer;
import com.jozufozu.flywheel.fabric.model.LayerFilteringBakedModel;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.simibubi.create.foundation.outliner.AABBOutline;
import com.simibubi.create.foundation.ponder.PonderScene;
import com.simibubi.create.foundation.ponder.PonderWorld;
import com.simibubi.create.foundation.ponder.Selection;
import com.simibubi.create.foundation.render.BlockEntityRenderHelper;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.render.SuperRenderTypeBuffer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.Pair;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WorldSectionElement extends AnimatedSceneElement {

	private static final ThreadLocal<ThreadLocalObjects> THREAD_LOCAL_OBJECTS = ThreadLocal.withInitial(ThreadLocalObjects::new);

	List<BlockEntity> renderedBlockEntities;
	List<Pair<BlockEntity, Consumer<Level>>> tickableBlockEntities;
	Selection section;
	boolean redraw;

	// Cached SBBs for each render layer, built in a single pass through all blocks.
	// Null means the buffers have not been built yet (or need rebuilding).
	private Map<RenderType, SuperByteBuffer> cachedBuffers;

	Vec3 prevAnimatedOffset = Vec3.ZERO;
	Vec3 animatedOffset = Vec3.ZERO;
	Vec3 prevAnimatedRotation = Vec3.ZERO;
	Vec3 animatedRotation = Vec3.ZERO;
	Vec3 centerOfRotation = Vec3.ZERO;
	Vec3 stabilizationAnchor = null;

	BlockPos selectedBlock;

	public WorldSectionElement() {}

	public WorldSectionElement(Selection section) {
		this.section = section.copy();
		centerOfRotation = section.getCenter();
	}

	public void mergeOnto(WorldSectionElement other) {
		setVisible(false);
		if (other.isEmpty())
			other.set(section);
		else
			other.add(section);
	}

	public void set(Selection selection) {
		applyNewSelection(selection.copy());
	}

	public void add(Selection toAdd) {
		applyNewSelection(this.section.add(toAdd));
	}

	public void erase(Selection toErase) {
		applyNewSelection(this.section.substract(toErase));
	}

	private void applyNewSelection(Selection selection) {
		this.section = selection;
		queueRedraw();
	}

	public void setCenterOfRotation(Vec3 center) {
		centerOfRotation = center;
	}

	public void stabilizeRotation(Vec3 anchor) {
		stabilizationAnchor = anchor;
	}

	@Override
	public void reset(PonderScene scene) {
		super.reset(scene);
		resetAnimatedTransform();
		resetSelectedBlock();
	}

	public void selectBlock(BlockPos pos) {
		selectedBlock = pos;
	}

	public void resetSelectedBlock() {
		selectedBlock = null;
	}

	public void resetAnimatedTransform() {
		prevAnimatedOffset = Vec3.ZERO;
		animatedOffset = Vec3.ZERO;
		prevAnimatedRotation = Vec3.ZERO;
		animatedRotation = Vec3.ZERO;
	}

	public void queueRedraw() {
		redraw = true;
		invalidateCachedBuffers();
	}

	private void invalidateCachedBuffers() {
		if (cachedBuffers != null) {
			for (SuperByteBuffer sbb : cachedBuffers.values()) {
				sbb.delete();
			}
			cachedBuffers = null;
		}
	}

	public boolean isEmpty() {
		return section == null;
	}

	public void setEmpty() {
		section = null;
		invalidateCachedBuffers();
	}

	public void setAnimatedRotation(Vec3 eulerAngles, boolean force) {
		this.animatedRotation = eulerAngles;
		if (force)
			prevAnimatedRotation = animatedRotation;
	}

	public Vec3 getAnimatedRotation() {
		return animatedRotation;
	}

	public void setAnimatedOffset(Vec3 offset, boolean force) {
		this.animatedOffset = offset;
		if (force)
			prevAnimatedOffset = animatedOffset;
	}

	public Vec3 getAnimatedOffset() {
		return animatedOffset;
	}

	@Override
	public boolean isVisible() {
		return super.isVisible() && !isEmpty();
	}

	class WorldSectionRayTraceResult {
		Vec3 actualHitVec;
		BlockPos worldPos;
	}

	public Pair<Vec3, BlockHitResult> rayTrace(PonderWorld world, Vec3 source, Vec3 target) {
		world.setMask(this.section);
		Vec3 transformedTarget = reverseTransformVec(target);
		BlockHitResult rayTraceBlocks = world.clip(new ClipContext(reverseTransformVec(source), transformedTarget,
				ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, CollisionContext.empty()));
		world.clearMask();

		if (rayTraceBlocks == null)
			return null;
		if (rayTraceBlocks.getLocation() == null)
			return null;

		double t = rayTraceBlocks.getLocation()
				.subtract(transformedTarget)
				.lengthSqr()
				/ source.subtract(target)
				.lengthSqr();
		Vec3 actualHit = VecHelper.lerp((float) t, target, source);
		return Pair.of(actualHit, rayTraceBlocks);
	}

	private Vec3 reverseTransformVec(Vec3 in) {
		float pt = AnimationTickHolder.getPartialTicks();
		in = in.subtract(VecHelper.lerp(pt, prevAnimatedOffset, animatedOffset));
		if (!animatedRotation.equals(Vec3.ZERO) || !prevAnimatedRotation.equals(Vec3.ZERO)) {
			if (centerOfRotation == null)
				centerOfRotation = section.getCenter();
			double rotX = Mth.lerp(pt, prevAnimatedRotation.x, animatedRotation.x);
			double rotZ = Mth.lerp(pt, prevAnimatedRotation.z, animatedRotation.z);
			double rotY = Mth.lerp(pt, prevAnimatedRotation.y, animatedRotation.y);
			in = in.subtract(centerOfRotation);
			in = VecHelper.rotate(in, -rotX, Axis.X);
			in = VecHelper.rotate(in, -rotZ, Axis.Z);
			in = VecHelper.rotate(in, -rotY, Axis.Y);
			in = in.add(centerOfRotation);
			if (stabilizationAnchor != null) {
				in = in.subtract(stabilizationAnchor);
				in = VecHelper.rotate(in, rotX, Axis.X);
				in = VecHelper.rotate(in, rotZ, Axis.Z);
				in = VecHelper.rotate(in, rotY, Axis.Y);
				in = in.add(stabilizationAnchor);
			}
		}
		return in;
	}

	public void transformMS(PoseStack ms, float pt) {
		TransformStack.of(ms)
				.translate(VecHelper.lerp(pt, prevAnimatedOffset, animatedOffset));
		if (!animatedRotation.equals(Vec3.ZERO) || !prevAnimatedRotation.equals(Vec3.ZERO)) {
			if (centerOfRotation == null)
				centerOfRotation = section.getCenter();
			double rotX = Mth.lerp(pt, prevAnimatedRotation.x, animatedRotation.x);
			double rotZ = Mth.lerp(pt, prevAnimatedRotation.z, animatedRotation.z);
			double rotY = Mth.lerp(pt, prevAnimatedRotation.y, animatedRotation.y);
			TransformStack.of(ms)
					.translate(centerOfRotation)
					.rotateXDegrees((float)rotX)
					.rotateZDegrees((float)rotZ)
					.rotateYDegrees((float)rotY)
					.translateBack(centerOfRotation);
			if (stabilizationAnchor != null) {
				TransformStack.of(ms)
						.translate(stabilizationAnchor)
						.rotateXDegrees((float)-rotX)
						.rotateZDegrees((float)-rotZ)
						.rotateYDegrees((float)-rotY)
						.translateBack(stabilizationAnchor);
			}
		}
	}

	public void tick(PonderScene scene) {
		prevAnimatedOffset = animatedOffset;
		prevAnimatedRotation = animatedRotation;
		if (!isVisible())
			return;
		loadBEsIfMissing(scene.getWorld());
		renderedBlockEntities.removeIf(be -> scene.getWorld()
				.getBlockEntity(be.getBlockPos()) != be);
		tickableBlockEntities.removeIf(be -> scene.getWorld()
				.getBlockEntity(be.getFirst()
						.getBlockPos()) != be.getFirst());
		tickableBlockEntities.forEach(be -> be.getSecond()
				.accept(scene.getWorld()));
	}

	@Override
	public void whileSkipping(PonderScene scene) {
		if (redraw) {
			renderedBlockEntities = null;
			tickableBlockEntities = null;
			invalidateCachedBuffers();
		}
		redraw = false;
	}

	protected void loadBEsIfMissing(PonderWorld world) {
		if (renderedBlockEntities != null)
			return;
		tickableBlockEntities = new ArrayList<>();
		renderedBlockEntities = new ArrayList<>();
		section.forEach(pos -> {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			BlockState blockState = world.getBlockState(pos);
			Block block = blockState.getBlock();
			if (blockEntity == null)
				return;
			if (!(block instanceof EntityBlock))
				return;
			blockEntity.setBlockState(world.getBlockState(pos));
			BlockEntityTicker<?> ticker = ((EntityBlock) block).getTicker(world, blockState, blockEntity.getType());
			if (ticker != null)
				addTicker(blockEntity, ticker);
			renderedBlockEntities.add(blockEntity);
		});
	}

	@SuppressWarnings("unchecked")
	private <T extends BlockEntity> void addTicker(T blockEntity, BlockEntityTicker<?> ticker) {
		tickableBlockEntities.add(Pair.of(blockEntity, w -> ((BlockEntityTicker<T>) ticker).tick(w,
				blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity)));
	}

	@Override
	public void renderFirst(PonderWorld world, MultiBufferSource buffer, PoseStack ms, float fade, float pt) {
		int light = -1;
		if (fade != 1)
			light = (int) (Mth.lerp(fade, 5, 14));
		if (redraw) {
			renderedBlockEntities = null;
			tickableBlockEntities = null;
			invalidateCachedBuffers();
		}

		ms.pushPose();
		transformMS(ms, pt);
		world.pushFakeLight(light);
		renderBlockEntities(world, ms, buffer, pt);
		world.popLight();

		Map<BlockPos, Integer> blockBreakingProgressions = world.getBlockBreakingProgressions();
		PoseStack overlayMS = null;

		BlockRenderDispatcher renderer = Minecraft.getInstance().getBlockRenderer();
		for (Entry<BlockPos, Integer> entry : blockBreakingProgressions.entrySet()) {
			BlockPos pos = entry.getKey();
			if (!section.test(pos))
				continue;

			if (overlayMS == null) {
				overlayMS = new PoseStack();
				overlayMS.last().pose().set(ms.last().pose());
				overlayMS.last().normal().set(ms.last().normal());
			}

			VertexConsumer builder = new SheetedDecalTextureGenerator(
					buffer.getBuffer(ModelBakery.DESTROY_TYPES.get(entry.getValue())), overlayMS.last(), 1);

			ms.pushPose();
			ms.translate(pos.getX(), pos.getY(), pos.getZ());
			renderer
				.renderBreakingTexture(world.getBlockState(pos), pos, world, ms, builder);
			ms.popPose();
		}

		ms.popPose();
	}

	@Override
	protected void renderLayer(PonderWorld world, MultiBufferSource buffer, RenderType type, PoseStack ms, float fade,
							   float pt) {
		// Build all layers in a single pass when buffers are missing or invalidated
		if (cachedBuffers == null) {
			cachedBuffers = buildAllStructureBuffers(world);
		}

		SuperByteBuffer contraptionBuffer = cachedBuffers.get(type);
		if (contraptionBuffer == null || contraptionBuffer.isEmpty())
			return;

		transformMS(contraptionBuffer.getTransforms(), pt);
		int light = lightCoordsFromFade(fade);
		contraptionBuffer
				.light(light)
				.renderInto(ms, buffer.getBuffer(type));
	}

	@Override
	protected void renderLast(PonderWorld world, MultiBufferSource buffer, PoseStack ms, float fade, float pt) {
		redraw = false;
		if (selectedBlock == null)
			return;
		BlockState blockState = world.getBlockState(selectedBlock);
		if (blockState.isAir())
			return;
		VoxelShape shape =
				blockState.getShape(world, selectedBlock, CollisionContext.of(Minecraft.getInstance().player));
		if (shape.isEmpty())
			return;

		ms.pushPose();
		transformMS(ms, pt);
		ms.translate(selectedBlock.getX(), selectedBlock.getY(), selectedBlock.getZ());

		AABBOutline aabbOutline = new AABBOutline(shape.bounds());
		aabbOutline.getParams()
				.lineWidth(1 / 64f)
				.colored(0xefefef)
				.disableLineNormals();
		aabbOutline.render(ms, (SuperRenderTypeBuffer) buffer, Vec3.ZERO, pt);

		ms.popPose();
	}

	private void renderBlockEntities(PonderWorld world, PoseStack ms, MultiBufferSource buffer, float pt) {
		loadBEsIfMissing(world);
		BlockEntityRenderHelper.renderBlockEntities(world, renderedBlockEntities, ms, buffer, pt);
	}

	/**
	 * Build SuperByteBuffers for ALL render layers in a single pass through the section's blocks.
	 * This avoids iterating every block position 4 times (once per RenderType layer).
	 * Only layers that actually contain geometry are included in the returned map.
	 */
	private Map<RenderType, SuperByteBuffer> buildAllStructureBuffers(PonderWorld world) {
		BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
		ThreadLocalObjects objects = THREAD_LOCAL_OBJECTS.get();
		List<RenderType> chunkLayers = RenderType.chunkBufferLayers();

		PoseStack poseStack = objects.poseStack;
		RandomSource random = objects.random;

		// Create per-layer builders. We use a map keyed by RenderType for O(1) lookup.
		Map<RenderType, LayerBuilders> layerBuildersMap = new HashMap<>();
		for (RenderType layer : chunkLayers) {
			layerBuildersMap.put(layer, new LayerBuilders());
		}

		world.setMask(this.section);
		ModelBlockRenderer.enableCaching();

		section.forEach(pos -> {
			BlockState state = world.getBlockState(pos);
			FluidState fluidState = world.getFluidState(pos);

			boolean hasModel = state.getRenderShape() == RenderShape.MODEL;
			boolean hasFluid = !fluidState.isEmpty();
			if (!hasModel && !hasFluid)
				return;

			poseStack.pushPose();
			poseStack.translate(pos.getX(), pos.getY(), pos.getZ());

			if (hasModel) {
				BakedModel originalModel = dispatcher.getBlockModel(state);
				boolean isVanilla = originalModel.isVanillaAdapter();

				if (isVanilla) {
					// Vanilla models render on exactly one layer -- find it and only build for that layer
					RenderType blockLayer = ItemBlockRenderTypes.getChunkRenderType(state);
					LayerBuilders lb = layerBuildersMap.get(blockLayer);
					if (lb != null) {
						lb.ensureStarted();
						BakedModel wrapped = lb.shadeSeparatingWrapper.wrapModel(originalModel);
						dispatcher.getModelRenderer()
							.tesselateBlock(world, wrapped, state, pos, poseStack, lb.shadeSeparatingWrapper,
								true, random, state.getSeed(pos), OverlayTexture.NO_OVERLAY);
					}
				} else {
					// Non-vanilla (e.g. FRAPI) models may render on multiple layers -- filter per layer
					for (RenderType layer : chunkLayers) {
						BakedModel filtered = LayerFilteringBakedModel.wrap(originalModel, layer);
						if (filtered != null) {
							LayerBuilders lb = layerBuildersMap.get(layer);
							lb.ensureStarted();
							BakedModel wrapped = lb.shadeSeparatingWrapper.wrapModel(filtered);
							dispatcher.getModelRenderer()
								.tesselateBlock(world, wrapped, state, pos, poseStack, lb.shadeSeparatingWrapper,
									true, random, state.getSeed(pos), OverlayTexture.NO_OVERLAY);
						}
					}
				}
			}

			if (hasFluid) {
				RenderType fluidLayer = ItemBlockRenderTypes.getRenderLayer(fluidState);
				LayerBuilders lb = layerBuildersMap.get(fluidLayer);
				if (lb != null) {
					lb.ensureStarted();
					dispatcher.renderLiquid(pos, world, lb.shadedBuilder, state, fluidState);
				}
			}

			poseStack.popPose();
		});

		ModelBlockRenderer.clearCache();
		world.clearMask();

		// Finalize: end builders, create SBBs, skip empty layers
		Map<RenderType, SuperByteBuffer> result = new HashMap<>();
		for (Map.Entry<RenderType, LayerBuilders> entry : layerBuildersMap.entrySet()) {
			LayerBuilders lb = entry.getValue();
			if (!lb.started)
				continue; // No blocks rendered into this layer at all

			lb.shadeSeparatingWrapper.clear();
			ShadeSeparatedBufferedData bufferedData = ModelUtil.endAndCombine(lb.shadedBuilder, lb.unshadedBuilder);

			SuperByteBuffer sbb = new SuperByteBuffer(bufferedData);
			bufferedData.release();
			lb.close();

			if (!sbb.isEmpty()) {
				result.put(entry.getKey(), sbb);
			} else {
				sbb.delete();
			}
		}

		// Clean up builders that were never started
		for (LayerBuilders lb : layerBuildersMap.values()) {
			lb.close();
		}

		return result;
	}

	/**
	 * Per-layer builder state used during the single-pass build.
	 * Lazily initialized so layers with no blocks incur no BufferBuilder overhead.
	 */
	private static class LayerBuilders {
		ByteBufferBuilder shadedByte;
		ByteBufferBuilder unshadedByte;
		BufferBuilder shadedBuilder;
		BufferBuilder unshadedBuilder;
		ShadeSeparatingVertexConsumer shadeSeparatingWrapper;
		boolean started;

		void ensureStarted() {
			if (started)
				return;
			started = true;
			shadedByte = new ByteBufferBuilder(512);
			unshadedByte = new ByteBufferBuilder(512);
			shadedBuilder = new BufferBuilder(shadedByte, VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
			unshadedBuilder = new BufferBuilder(unshadedByte, VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
			shadeSeparatingWrapper = new ShadeSeparatingVertexConsumer();
			shadeSeparatingWrapper.prepare(shadedBuilder, unshadedBuilder);
		}

		void close() {
			if (shadedByte != null) {
				shadedByte.close();
				shadedByte = null;
			}
			if (unshadedByte != null) {
				unshadedByte.close();
				unshadedByte = null;
			}
		}
	}

	private static class ThreadLocalObjects {
		public final PoseStack poseStack = new PoseStack();
		public final RandomSource random = RandomSource.createNewThreadLocalInstance();
	}

}
