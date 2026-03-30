package com.simibubi.create.compat.ftb;

import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;

import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import net.minecraft.client.gui.screens.Screen;

public class FTBIntegration {

	// Disabled until newer ftb library with their new config system has settled a bit

	private static boolean buttonStatePreviously;

	public static void init() {
		// Fabric: event registration left commented out as in NeoForge reference
		// forgeEventBus.addListener(EventPriority.HIGH, FTBIntegration::removeGUIClutterOpen);
		// forgeEventBus.addListener(EventPriority.LOW, FTBIntegration::removeGUIClutterClose);
	}

	private static void removeGUIClutterOpen(Screen currentScreen, Screen newScreen) {
		if (isCreate(currentScreen))
			return;
		if (!isCreate(newScreen))
			return;
//		buttonStatePreviously = FTBLibraryClientConfig.SIDEBAR_ENABLED.get();
//		FTBLibraryClientConfig.SIDEBAR_ENABLED.set(false);
	}

	private static void removeGUIClutterClose(Screen screen) {
		if (!isCreate(screen))
			return;
//		FTBLibraryClientConfig.SIDEBAR_ENABLED.set(buttonStatePreviously);
	}

	private static boolean isCreate(Screen screen) {
		return screen instanceof AbstractSimiContainerScreen<?> || screen instanceof AbstractSimiScreen;
	}

}
