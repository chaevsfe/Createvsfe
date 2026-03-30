/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
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

package com.railwayteam.railways;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.registry.CRCreativeModeTabs;
import com.railwayteam.railways.compat.tracks.mods.*;
import com.railwayteam.railways.content.custom_tracks.casing.CasingCollisionUtils;
import com.railwayteam.railways.registry.*;
import com.railwayteam.railways.registry.fabric.CRBlocksImpl;
import com.railwayteam.railways.registry.fabric.CRBlockEntitiesImpl;

public class ModSetup {

  public static void useBaseTab()  {
        Railways.registrate().setCreativeTab(CRCreativeModeTabs.getBaseTabKey());
    }

  public static void useTracksTab()  {
        Railways.registrate().setCreativeTab(CRCreativeModeTabs.getTracksTabKey());
    }

  public static void usePalettesTab()  {
        Railways.registrate().setCreativeTab(CRCreativeModeTabs.getPalettesTabKey());
    }

  public static void register() {
    useBaseTab();
    CRBlockSetTypes.register();
    CRTrackMaterials.register();
    CRBogeyStyles.register();
    CRCreativeModeTabs.register();
    CRItems.register();
    CRFluids.register();
    CRSpriteShifts.register();
    // Force-load platform-specific Impl classes so Fuel Tank and Portable Fuel Interface
    // blocks/block entities are registered (they live only in the Impl classes from the
    // original multi-loader architecture). CRBlocksImpl must load before CRBlockEntitiesImpl
    // because the block entity entries reference the block entries.
    CRBlocksImpl.platformBasedRegistration();
    CRBlockEntitiesImpl.platformBasedRegistration();
    CRBlockEntities.register();
    CRBlocks.register();
    CRPalettes.register();
    CRContainerTypes.register();
    CREntities.register();
    CRSounds.register();
    CRTags.register();
    CREdgePointTypes.register();
    CRSchedule.register();
    CRDataFixers.register();
    CRExtraRegistration.register();
    CasingCollisionUtils.register();
    CRInteractionBehaviours.register();
    CRMovementBehaviours.register();
    CRPortalTracks.register();

    // Compat
    useTracksTab();
    HexCastingTrackCompat.register();
    BygTrackCompat.register();
    BlueSkiesTrackCompat.register();
    TwilightForestTrackCompat.register();
    BiomesOPlentyTrackCompat.register();
    NaturesSpiritTrackCompat.register();
    DreamsAndDesiresTrackCompat.register();
    QuarkTrackCompat.register();
    TFCTrackCompat.register();
  }
}
