package com.simibubi.create.content.equipment.armor;

import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.Create;

import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import com.simibubi.create.foundation.utility.animation.LerpedFloat.Chaser;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class CardboardArmorStealthOverlay {

	private static final ResourceLocation PACKAGE_BLUR_LOCATION = Create.asResource("textures/misc/package_blur.png");

	private static LerpedFloat opacity = LerpedFloat.linear()
		.startWithValue(0)
		.chase(0, 0.25f, Chaser.EXP);

	public static void clientTick() {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null)
			return;

		opacity.tickChaser();
		opacity.updateChaseTarget(CardboardArmorHandler.testForStealth(player) ? 1 : 0);
	}

	/**
	 * Called from CreateClient HudRenderCallback to render the package blur overlay.
	 */
	public static void render(GuiGraphics graphics, float partialTick, int width, int height) {
		float value = opacity.getValue(partialTick);
		if (value == 0)
			return;

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShaderColor(1f, 1f, 1f, value);
		graphics.blit(PACKAGE_BLUR_LOCATION, 0, 0, 0, 0, width, height, width, height);
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		RenderSystem.disableBlend();
	}

}
