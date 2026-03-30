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

package com.railwayteam.railways.fabric;

// EarlyRiser previously used com.chocohead.mm.api.ClassTinkerers to dynamically
// extend RollerBlockEntity.RollingMode with TRACK_REPLACE. In MC 1.21.1 with
// Fabric Loader, ClassTinkerers (Mixin Map) is not available. The TRACK_REPLACE
// functionality is left for a future direct enum addition to UfoPort's RollerBlockEntity.
public class EarlyRiser implements Runnable {
    @Override
    public void run() {
        // No-op: dynamic enum extension not supported in MC 1.21.1 Fabric toolchain
    }
}
