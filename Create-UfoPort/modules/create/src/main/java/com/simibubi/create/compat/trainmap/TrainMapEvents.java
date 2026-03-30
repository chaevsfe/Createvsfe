package com.simibubi.create.compat.trainmap;

import com.mojang.blaze3d.platform.InputConstants;
import com.simibubi.create.compat.Mods;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

@Environment(EnvType.CLIENT)
public class TrainMapEvents {

	public static void register() {
		ClientTickEvents.END_CLIENT_TICK.register(TrainMapEvents::tick);

		// Register per-screen mouse click hooks for map compat
		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			ScreenMouseEvents.allowMouseClick(screen).register((s, mouseX, mouseY, button) -> {
				mouseClick(s, mouseX, mouseY, button);
				return true; // don't cancel from here — individual compat handles cancellation internally
			});
		});
	}

	private static void tick(Minecraft mc) {
		if (mc.level == null)
			return;

		if (Mods.FTBCHUNKS.isLoaded())
			FTBChunksTrainMap.tick();
		if (Mods.JOURNEYMAP.isLoaded())
			JourneyTrainMap.tick();
		if (Mods.XAEROWORLDMAP.isLoaded())
			XaeroTrainMap.tick();
	}

	private static void mouseClick(Screen screen, double mouseX, double mouseY, int button) {
		if (button != InputConstants.MOUSE_BUTTON_LEFT && button != InputConstants.MOUSE_BUTTON_RIGHT)
			return;

		if (Mods.FTBCHUNKS.isLoaded())
			FTBChunksTrainMap.mouseClick(screen, mouseX, mouseY, button);
		if (Mods.JOURNEYMAP.isLoaded())
			JourneyTrainMap.mouseClick(screen, mouseX, mouseY, button);
		if (Mods.XAEROWORLDMAP.isLoaded())
			XaeroTrainMap.mouseClick(screen, mouseX, mouseY, button);
	}

}
