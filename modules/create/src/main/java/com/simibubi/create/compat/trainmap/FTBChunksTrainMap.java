package com.simibubi.create.compat.trainmap;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.gui.RemovedGuiUtils;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.infrastructure.config.AllConfigs;

import dev.ftb.mods.ftbchunks.client.gui.LargeMapScreen;
import dev.ftb.mods.ftbchunks.client.gui.RegionMapPanel;
import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.ScreenWrapper;
import dev.ftb.mods.ftblibrary.ui.Widget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.Mth;

// Fabric note: NeoForge reflection helper replaced with standard Java reflection
import java.lang.reflect.Field;

@Environment(EnvType.CLIENT)
public class FTBChunksTrainMap {

	private static boolean renderingTooltip = false;
	private static boolean requesting;

	public static void tick() {
		LargeMapScreen mapScreen = getAsLargeMapScreen(Minecraft.getInstance().screen);

		if (!AllConfigs.client().showTrainMapOverlay.get() || mapScreen == null) {
			if (requesting)
				TrainMapSyncClient.stopRequesting();
			requesting = false;
			return;
		}

		TrainMapManager.tick(mapScreen.currentDimension());
		requesting = true;
		TrainMapSyncClient.requestData();
	}

	public static void mouseClick(Screen screen, double mouseX, double mouseY, int button) {
		LargeMapScreen mapScreen = getAsLargeMapScreen(screen);
		if (mapScreen == null)
			return;
		TrainMapManager.handleToggleWidgetClick(mapScreen.getMouseX(), mapScreen.getMouseY(), 20, 2);
	}

	public static void renderGui(Screen screen, GuiGraphics graphics, int mouseX, int mouseY) {
		LargeMapScreen largeMapScreen = getAsLargeMapScreen(screen);
		if (largeMapScreen == null)
			return;

		Object panel = getPrivateValue(LargeMapScreen.class, largeMapScreen, "regionPanel");
		if (!(panel instanceof RegionMapPanel regionMapPanel))
			return;

		if (!AllConfigs.client().showTrainMapOverlay.get()) {
			renderToggleWidgetAndTooltip(largeMapScreen, graphics, mouseX, mouseY);
			return;
		}

		int blocksPerRegion = 16 * 32;
		int minX = Mth.floor(regionMapPanel.getScrollX());
		int minY = Mth.floor(regionMapPanel.getScrollY());
		float regionTileSize = largeMapScreen.getRegionTileSize() / (float) blocksPerRegion;
		Integer regionMinX = getPrivateValue(RegionMapPanel.class, regionMapPanel, "regionMinX");
		Integer regionMinZ = getPrivateValue(RegionMapPanel.class, regionMapPanel, "regionMinZ");

		if (regionMinX == null || regionMinZ == null)
			return;

		float mX = mouseX;
		float mY = mouseY;

		boolean linearFiltering = largeMapScreen.getRegionTileSize() * Minecraft.getInstance()
			.getWindow()
			.getGuiScale() < 512D;

		PoseStack pose = graphics.pose();
		pose.pushPose();

		pose.translate(-minX, -minY, 0);
		pose.scale(regionTileSize, regionTileSize, 1);
		pose.translate(-regionMinX * blocksPerRegion, -regionMinZ * blocksPerRegion, 0);

		mX += minX;
		mY += minY;
		mX /= regionTileSize;
		mY /= regionTileSize;
		mX += regionMinX * blocksPerRegion;
		mY += regionMinZ * blocksPerRegion;

		Rect2i bounds = new Rect2i(Mth.floor(minX / regionTileSize + regionMinX * blocksPerRegion),
			Mth.floor(minY / regionTileSize + regionMinZ * blocksPerRegion),
			Mth.floor(largeMapScreen.width / regionTileSize), Mth.floor(largeMapScreen.height / regionTileSize));

		List<FormattedText> tooltip = TrainMapManager.renderAndPick(graphics, Mth.floor(mX), Mth.floor(mY),
			linearFiltering, bounds);

		pose.popPose();

		if (!renderToggleWidgetAndTooltip(largeMapScreen, graphics, mouseX, mouseY) && tooltip != null) {
			renderingTooltip = true;
			RemovedGuiUtils.drawHoveringText(graphics, tooltip, mouseX, mouseY,
				largeMapScreen.width, largeMapScreen.height, 256, Minecraft.getInstance().font);
			renderingTooltip = false;
		}

		pose.pushPose();
		pose.translate(0, 0, 300);
		for (Widget widget : largeMapScreen.getWidgets()) {
			if (!widget.isEnabled())
				continue;
			if (widget == panel)
				continue;
			widget.draw(graphics, largeMapScreen.getTheme(), widget.getPosX(), widget.getPosY(), widget.getWidth(),
				widget.getHeight());
		}
		pose.popPose();
	}

	private static boolean renderToggleWidgetAndTooltip(LargeMapScreen largeMapScreen, GuiGraphics graphics,
		int mouseX, int mouseY) {
		TrainMapManager.renderToggleWidget(graphics, 20, 2);
		if (!TrainMapManager.isToggleWidgetHovered(mouseX, mouseY, 20, 2))
			return false;

		renderingTooltip = true;
		RemovedGuiUtils.drawHoveringText(graphics, List.of(Lang.translate("train_map.toggle")
			.component()), mouseX, mouseY + 20, largeMapScreen.width, largeMapScreen.height, 256,
			Minecraft.getInstance().font);
		renderingTooltip = false;
		return true;
	}

	public static LargeMapScreen getAsLargeMapScreen(Screen screen) {
		if (!(screen instanceof ScreenWrapper screenWrapper))
			return null;
		BaseScreen wrapped = screenWrapper.getGui();
		if (!(wrapped instanceof LargeMapScreen largeMapScreen))
			return null;
		return largeMapScreen;
	}

	@SuppressWarnings("unchecked")
	private static <T> T getPrivateValue(Class<?> clazz, Object instance, String fieldName) {
		try {
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			return (T) field.get(instance);
		} catch (Exception e) {
			return null;
		}
	}

}
