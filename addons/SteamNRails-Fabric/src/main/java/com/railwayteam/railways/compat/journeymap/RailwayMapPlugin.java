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
import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.client.IClientPlugin;
import journeymap.api.v2.client.JourneyMapPlugin;
import journeymap.api.v2.client.event.MappingEvent;
import journeymap.api.v2.common.event.ClientEventRegistry;
import org.jetbrains.annotations.NotNull;

@JourneyMapPlugin(apiVersion = "2.0.0")
public class RailwayMapPlugin implements IClientPlugin {
    private IClientAPI api;

    @Override
    public void initialize(@NotNull IClientAPI api) {
        this.api = api;
        ClientEventRegistry.MAPPING_EVENT.subscribe(Railways.MOD_ID, this::onMappingEvent);
        RailwayMarkerHandler.init(api);
        JourneymapPlatformEventListener.createAndRegister();
    }

    @Override
    public String getModId() {
        return Railways.MOD_ID;
    }

    private void onMappingEvent(MappingEvent event) {
        if (event.getStage() == MappingEvent.Stage.MAPPING_STOPPED) {
            this.api.removeAll(getModId());
        }
    }

    public static void load() {
        Railways.LOGGER.info("Loaded JourneyMap plugin");
    }
}
