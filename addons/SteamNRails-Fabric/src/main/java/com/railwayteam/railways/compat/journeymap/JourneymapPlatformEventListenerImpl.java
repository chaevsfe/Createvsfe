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

/**
 * Retained for compatibility; logic has been merged into JourneymapPlatformEventListener.
 * @deprecated Use {@link JourneymapPlatformEventListener#createAndRegister()} directly.
 */
@Deprecated
public class JourneymapPlatformEventListenerImpl extends JourneymapPlatformEventListener {
    // No additional logic needed; registration is handled by JourneymapPlatformEventListener.createAndRegister().
}
