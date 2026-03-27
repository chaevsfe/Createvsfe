package com.simibubi.create.content.logistics.box;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AngleHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class PackageRenderer extends EntityRenderer<PackageEntity> {

	public PackageRenderer(Context pContext) {
		super(pContext);
		shadowRadius = 0.5f;
	}

	@Override
	public void render(PackageEntity entity, float yaw, float pt, PoseStack ms, MultiBufferSource buffer, int light) {
		ItemStack box = entity.box;
		PartialModel model = null;
		if (!box.isEmpty() && PackageItem.isPackage(box)) {
			ResourceLocation key = BuiltInRegistries.ITEM.getKey(box.getItem());
			model = AllPartialModels.PACKAGES.get(key);
		}
		// Fallback to first standard package style if no specific model found
		if (model == null && !AllPartialModels.PACKAGES_TO_HIDE_AS.isEmpty())
			model = AllPartialModels.PACKAGES_TO_HIDE_AS.get(0);
		renderBox(entity, yaw, ms, buffer, light, model);
		super.render(entity, yaw, pt, ms, buffer, light);
	}

	public static void renderBox(Entity entity, float yaw, PoseStack ms, MultiBufferSource buffer, int light,
		PartialModel model) {
		if (model == null)
			return;
		SuperByteBuffer sbb = CachedBufferer.partial(model, Blocks.AIR.defaultBlockState());
		sbb.translate(-.5, 0, -.5)
			.rotateCentered(-AngleHelper.rad(yaw + 90), Direction.UP)
			.light(light)
			.nudge(entity.getId());
		sbb.renderInto(ms, buffer.getBuffer(RenderType.solid()));
	}

	@Override
	public ResourceLocation getTextureLocation(PackageEntity pEntity) {
		return null;
	}

}
