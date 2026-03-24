package com.simibubi.create.content.logistics.tableCloth;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class TableClothRenderer extends SmartBlockEntityRenderer<TableClothBlockEntity> {

	public TableClothRenderer(Context context) {
		super(context);
	}

	@Override
	protected void renderSafe(TableClothBlockEntity blockEntity, float partialTicks, PoseStack ms,
		MultiBufferSource buffer, int light, int overlay) {
		super.renderSafe(blockEntity, partialTicks, ms, buffer, light, overlay);
		List<ItemStack> stacks = blockEntity.manuallyAddedItems;

		if (stacks.isEmpty())
			return;

		float rotationInRadians = Mth.DEG_TO_RAD * (180 - blockEntity.facing.toYRot());

		for (int i = 0; i < stacks.size(); i++) {
			ItemStack entry = stacks.get(i);
			if (entry.isEmpty())
				continue;

			ms.pushPose();
			ms.translate(0.5f, 3 / 16f, 0.5f);
			ms.mulPose(Axis.YP.rotation(rotationInRadians));

			if (stacks.size() > 1) {
				ms.mulPose(Axis.YP.rotationDegrees(i * (360f / stacks.size()) + 45f));
				ms.translate(0, i % 2 == 0 ? -0.005 : 0, 5 / 16f);
				ms.mulPose(Axis.YP.rotationDegrees(-i * (360f / stacks.size()) - 45f));
			}

			ms.scale(0.5f, 0.5f, 0.5f);
			Minecraft.getInstance().getItemRenderer()
				.renderStatic(entry, net.minecraft.world.item.ItemDisplayContext.FIXED, light,
					OverlayTexture.NO_OVERLAY, ms, buffer, blockEntity.getLevel(), 0);
			ms.popPose();
		}
	}
}
