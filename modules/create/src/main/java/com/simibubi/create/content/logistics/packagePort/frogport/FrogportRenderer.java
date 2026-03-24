package com.simibubi.create.content.logistics.packagePort.frogport;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;

import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class FrogportRenderer extends SmartBlockEntityRenderer<FrogportBlockEntity> {

	public FrogportRenderer(Context context) {
		super(context);
	}

	@Override
	protected void renderSafe(FrogportBlockEntity blockEntity, float partialTicks, PoseStack ms,
		MultiBufferSource buffer, int light, int overlay) {

		if (VisualizationManager.supportsVisualization(blockEntity.getLevel()))
			return;

		SuperByteBuffer body = CachedBufferer.partial(AllPartialModels.FROGPORT_BODY, blockEntity.getBlockState());

		float yaw = blockEntity.getYaw();

		float headPitch = 80;
		float tonguePitch = 0;
		float tongueLength = 0;
		float headPitchModifier = 1;

		boolean hasTarget = blockEntity.target != null;
		boolean animating = blockEntity.isAnimationInProgress();
		boolean depositing = blockEntity.currentlyDepositing;

		if (hasTarget) {
			Vec3 diff = blockEntity.target
				.getExactTargetLocation(blockEntity, blockEntity.getLevel(), blockEntity.getBlockPos())
				.subtract(0, animating && depositing ? 0 : 0.75, 0)
				.subtract(Vec3.atCenterOf(blockEntity.getBlockPos()));
			tonguePitch = (float) Mth.atan2(diff.y, diff.multiply(1, 0, 1)
				.length() + (3 / 16f)) * Mth.RAD_TO_DEG;
			tongueLength = Math.max((float) diff.length(), 1);
			headPitch = Mth.clamp(tonguePitch * 2, 60, 100);
		}

		if (animating) {
			float progress = blockEntity.animationProgress.getValue(partialTicks);

			if (depositing) {
				tongueLength *= Math.max(0, 1 - Math.pow((progress * 1.25 - 0.25) * 4 - 1, 4));
				headPitchModifier = (float) Math.max(0, 1 - Math.pow((progress * 1.25) * 2 - 1, 4));
			} else {
				tongueLength *= Math.pow(Math.max(0, 1 - progress * 1.25), 5);
				headPitchModifier = 1 - (float) Math.min(1, Math.max(0, (Math.pow(progress * 1.5, 2) - 0.5) * 2));
			}
		} else {
			tongueLength = 0;
			float anticipation = blockEntity.anticipationProgress.getValue(partialTicks);
			headPitchModifier =
				anticipation > 0 ? (float) Math.max(0, 1 - Math.pow((anticipation * 1.25) * 2 - 1, 4)) : 0;
		}

		headPitch *= headPitchModifier;
		headPitch = Math.max(headPitch, blockEntity.manualOpenAnimationProgress.getValue(partialTicks) * 60);
		tongueLength = Math.max(tongueLength, blockEntity.manualOpenAnimationProgress.getValue(partialTicks) * 0.25f);

		body.center()
			.rotateY(yaw)
			.uncenter()
			.light(light)
			.renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));

		SuperByteBuffer head = CachedBufferer.partial(
			blockEntity.goggles ? AllPartialModels.FROGPORT_HEAD_GOGGLES : AllPartialModels.FROGPORT_HEAD,
			blockEntity.getBlockState());

		head.center()
			.rotateY(yaw)
			.uncenter()
			.translate(8 / 16f, 10 / 16f, 11 / 16f)
			.rotateX(headPitch)
			.translateBack(8 / 16f, 10 / 16f, 11 / 16f)
			.light(light)
			.renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));

		SuperByteBuffer tongue = CachedBufferer.partial(AllPartialModels.FROGPORT_TONGUE, blockEntity.getBlockState());

		tongue.center()
			.rotateY(yaw)
			.uncenter()
			.translate(8 / 16f, 10 / 16f, 11 / 16f)
			.rotateX(tonguePitch)
			.scale(1f, 1f, tongueLength / (7 / 16f))
			.translateBack(8 / 16f, 10 / 16f, 11 / 16f)
			.light(light)
			.renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));
	}
}
