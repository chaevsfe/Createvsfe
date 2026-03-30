/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.compat.journeymap;

import com.railwayteam.railways.Railways;
import journeymap.api.v2.client.event.FullscreenDisplayEvent;
import journeymap.api.v2.client.fullscreen.IThemeButton;
import journeymap.api.v2.client.fullscreen.ThemeButtonDisplay;
import journeymap.api.v2.common.event.ClientEventRegistry;
import net.minecraft.resources.ResourceLocation;

public class JourneymapPlatformEventListener {

    private static final ResourceLocation TRAIN_ICON = Railways.asResource("journeymap_train");

    public static void createAndRegister() {
        JourneymapPlatformEventListener listener = new JourneymapPlatformEventListener();
        ClientEventRegistry.ADDON_BUTTON_DISPLAY_EVENT.subscribe(
            Railways.MOD_ID,
            listener::onAddonButtonDisplayEvent
        );
    }

    private void onAddonButtonDisplayEvent(FullscreenDisplayEvent.AddonButtonDisplayEvent event) {
        onAddonButtonDisplay(event.getThemeButtonDisplay());
    }

    protected void onAddonButtonDisplay(ThemeButtonDisplay buttonDisplay) {
        buttonDisplay.addThemeToggleButton(
            "railways.journeymap.train_marker_toggle",
            "journeymap_train",
            TRAIN_ICON,
            DummyRailwayMarkerHandler.getInstance().isEnabled(),
            (IThemeButton button) -> {
                if (!DummyRailwayMarkerHandler.getInstance().isEnabled()) {
                    DummyRailwayMarkerHandler.getInstance().enable();
                } else {
                    DummyRailwayMarkerHandler.getInstance().disable();
                }
            }
        );
    }
}
