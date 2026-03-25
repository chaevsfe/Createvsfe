package com.simibubi.create.foundation.mixin.compat.xaeros;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.Create;
import com.simibubi.create.compat.trainmap.XaeroTrainMap;

import net.minecraft.client.gui.GuiGraphics;

// Use targets string form to avoid loading GuiMap's class hierarchy at compile time.
// GuiMap extends ScreenBase which extends xaero.lib.client.gui.ScreenBase (separate jar),
// causing "cannot access ScreenBase" compile errors without the xaerolib transitive dep.
@Mixin(targets = "xaero.map.gui.GuiMap")
public abstract class XaeroFullscreenMapMixin {
	@Unique
	private boolean create$failedToRenderTrainMap = false;

	@Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"), require = 0)
	private void create$xaeroMapFullscreenRender(GuiGraphics guiGraphics, int scaledMouseX, int scaledMouseY, float partialTicks, CallbackInfo ci) {
		try {
			if (!create$failedToRenderTrainMap)
				XaeroTrainMap.onRender(guiGraphics, this, scaledMouseX, scaledMouseY, partialTicks);
		} catch (Throwable e) {
			Create.LOGGER.error("Failed to render Xaero's World Map train map integration:", e);
			create$failedToRenderTrainMap = true;
		}
	}
}
