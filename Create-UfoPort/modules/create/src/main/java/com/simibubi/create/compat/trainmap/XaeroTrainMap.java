package com.simibubi.create.compat.trainmap;

import java.lang.reflect.Field;
import java.util.List;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.gui.RemovedGuiUtils;
import com.simibubi.create.foundation.mixin.compat.xaeros.XaeroFullscreenMapAccessor;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.infrastructure.config.AllConfigs;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

/**
 * Xaero's World Map train map integration.
 *
 * Note: GuiMap (xaero.map.gui.GuiMap) is NOT imported directly because its parent class
 * (xaero.map.gui.ScreenBase -> xaero.lib.client.gui.ScreenBase) lives in a separate xaerolib jar
 * that is not extracted by Fabric Loom, causing "cannot access ScreenBase" compile errors.
 * Instead, we use Object/Screen types and reflection/instanceof checks by class name.
 *
 * At runtime, Xaero classes are available (xaerominimap/xaeroworldmap is loaded).
 */
@Environment(EnvType.CLIENT)
public class XaeroTrainMap {
	private static boolean requesting;
	private static ResourceKey<Level> renderedDimension;
	private static boolean encounteredException = false;

	// Lazily resolved GuiMap class to avoid ClassNotFoundException at startup
	private static Class<?> guiMapClass = null;

	private static Class<?> getGuiMapClass() {
		if (guiMapClass != null) return guiMapClass;
		try {
			guiMapClass = Class.forName("xaero.map.gui.GuiMap");
		} catch (ClassNotFoundException ignored) {
			// Xaero not loaded
		}
		return guiMapClass;
	}

	public static void tick() {
		if (!AllConfigs.client().showTrainMapOverlay.get() || !isMapOpen(Minecraft.getInstance().screen)) {
			if (requesting)
				TrainMapSyncClient.stopRequesting();
			requesting = false;
			return;
		}
		TrainMapManager.tick();
		requesting = true;
		TrainMapSyncClient.requestData();
	}

	public static void mouseClick(Screen screen, double mouseX, double mouseY, int button) {
		if (encounteredException)
			return;

		Minecraft mc = Minecraft.getInstance();
		try {
			Class<?> cls = getGuiMapClass();
			if (cls == null || !cls.isInstance(mc.screen))
				return;
		} catch (Throwable e) {
			Create.LOGGER.error("Failed to handle mouseClick for Xaero's World Map train map integration:", e);
			encounteredException = true;
			return;
		}

		Window window = mc.getWindow();
		double mX = mc.mouseHandler.xpos() * window.getGuiScaledWidth() / window.getScreenWidth();
		double mY = mc.mouseHandler.ypos() * window.getGuiScaledHeight() / window.getScreenHeight();

		TrainMapManager.handleToggleWidgetClick(Mth.floor(mX), Mth.floor(mY), 3, 30);
	}

	/**
	 * Called by XaeroFullscreenMapMixin. The guiMap parameter is a xaero.map.gui.GuiMap instance
	 * passed as Object to avoid direct compile-time dependency on GuiMap's class hierarchy.
	 */
	public static void onRender(GuiGraphics graphics, Object guiMapObj, int mX, int mY, float pt) {
		XaeroFullscreenMapAccessor accessor = (XaeroFullscreenMapAccessor) guiMapObj;
		double x = accessor.create$getCameraX();
		double z = accessor.create$getCameraZ();
		double mapScale = accessor.create$getScale();
		renderedDimension = accessor.create$getMapProcessor().getMapWorld().getCurrentDimension().getDimId();

		// Access Screen.width/height — GuiMap extends Screen through the ScreenBase chain at runtime
		Screen screen = (Screen) guiMapObj;
		int screenWidth = screen.width;
		int screenHeight = screen.height;

		if (!AllConfigs.client().showTrainMapOverlay.get()) {
			renderToggleWidgetAndTooltip(graphics, screenWidth, screenHeight, mX, mY);
			return;
		}

		Minecraft mc = Minecraft.getInstance();
		Window window = mc.getWindow();

		double guiScale = (double) window.getScreenWidth() / window.getGuiScaledWidth();
		double interfaceScale = (double) window.getWidth() / window.getScreenWidth();
		double scale = mapScale / guiScale / interfaceScale;

		PoseStack pose = graphics.pose();
		pose.pushPose();

		pose.translate(screenWidth / 2.0f, screenHeight / 2.0f, 0);
		pose.scale((float) scale, (float) scale, 1);
		pose.translate(-x, -z, 0);

		float mouseX = mX - screenWidth / 2.0f;
		float mouseY = mY - screenHeight / 2.0f;
		mouseX /= scale;
		mouseY /= scale;
		mouseX += x;
		mouseY += z;

		Rect2i bounds =
			new Rect2i(Mth.floor(-screenWidth / 2.0f / scale + x), Mth.floor(-screenHeight / 2.0f / scale + z),
				Mth.floor(screenWidth / scale), Mth.floor(screenHeight / scale));

		List<FormattedText> tooltip =
			TrainMapManager.renderAndPick(graphics, Mth.floor(mouseX), Mth.floor(mouseY), false, bounds);

		pose.popPose();

		if (!renderToggleWidgetAndTooltip(graphics, screenWidth, screenHeight, mX, mY) && tooltip != null)
			RemovedGuiUtils.drawHoveringText(graphics, tooltip, mX, mY, screenWidth, screenHeight, 256, mc.font);
	}

	private static boolean renderToggleWidgetAndTooltip(GuiGraphics graphics, int screenWidth, int screenHeight,
														int mouseX, int mouseY) {
		TrainMapManager.renderToggleWidget(graphics, 3, 30);
		if (!TrainMapManager.isToggleWidgetHovered(mouseX, mouseY, 3, 30))
			return false;

		RemovedGuiUtils.drawHoveringText(graphics, List.of(Lang.translate("train_map.toggle")
			.component()), mouseX, mouseY + 20, screenWidth, screenHeight, 256, Minecraft.getInstance().font);
		return true;
	}

	public static ResourceKey<Level> getRenderedDimension() {
		return renderedDimension;
	}

	public static boolean isMapOpen(Screen screen) {
		if (encounteredException)
			return false;

		try {
			Class<?> cls = getGuiMapClass();
			if (cls == null || screen == null) return false;
			if (cls.isInstance(screen)) return true;
			// Also check if screen's parent is a GuiMap (the ScreenBase.parent field pattern)
			try {
				Field parentField = screen.getClass().getField("parent");
				Object parent = parentField.get(screen);
				if (parent != null && cls.isInstance(parent)) return true;
			} catch (NoSuchFieldException ignored) {
				// Screen doesn't have a parent field
			}
			return false;
		} catch (Throwable e) {
			Create.LOGGER.error("Failed to check if Xaero's World Map was open for train map integration:", e);
			encounteredException = true;
			return false;
		}
	}
}
