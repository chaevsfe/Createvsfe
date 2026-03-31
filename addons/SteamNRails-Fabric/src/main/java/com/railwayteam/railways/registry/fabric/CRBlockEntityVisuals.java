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

package com.railwayteam.railways.registry.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Client-only class that registers Flywheel visuals for SNR block entities.
 * Separated from CRBlockEntitiesImpl to avoid loading Flywheel classes on dedicated servers,
 * since the JVM resolves ALL class references in a class's constant pool when the class is loaded.
 */
@Environment(EnvType.CLIENT)
public class CRBlockEntityVisuals {

    public static void register() {
        dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer
            .builder(CRBlockEntitiesImpl.PORTABLE_FUEL_INTERFACE.get())
            .factory(com.simibubi.create.content.contraptions.actors.psi.PSIVisual::new)
            .apply();
    }
}
