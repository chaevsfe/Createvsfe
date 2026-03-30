/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2026 The Railways Team
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

package com.railwayteam.railways.content.custom_tracks.casing;

import com.railwayteam.railways.mixin.client.AccessorPartialModel;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

import java.time.Clock;

/**
 * Utility class for creating runtime PartialModel instances with injected BakedModels.
 * In Flywheel 1.0.6, PartialModel is final, so we use the accessor mixin to inject the model directly.
 */
public class RuntimeFakePartialModel {

    private static ResourceLocation runtimeify(ResourceLocation loc, BakedModel model) {
        return ResourceLocation.fromNamespaceAndPath(loc.getNamespace(),
                "runtime/" + Clock.systemUTC().millis() + "/" + model.hashCode() + "/" + loc.getPath());
    }

    /**
     * Creates a PartialModel at runtime with the given BakedModel injected.
     * The model is removed from the global registry so it isn't re-baked on resource reload.
     */
    public static PartialModel make(ResourceLocation loc, BakedModel bakedModel) {
        boolean wasPopulateOnInit = AccessorPartialModel.isPopulateOnInit();
        AccessorPartialModel.setPopulateOnInit(false);

        ResourceLocation runtimeLoc = runtimeify(loc, bakedModel);
        PartialModel partialModel = PartialModel.of(runtimeLoc);
        ((AccessorPartialModel) (Object) partialModel).setBakedModel(bakedModel);

        // Remove from ALL registry so it isn't re-baked on reload
        AccessorPartialModel.getALL().remove(runtimeLoc);

        AccessorPartialModel.setPopulateOnInit(wasPopulateOnInit);

        return partialModel;
    }
}
