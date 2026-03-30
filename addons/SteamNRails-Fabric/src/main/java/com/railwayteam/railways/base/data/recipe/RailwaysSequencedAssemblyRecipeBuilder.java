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

package com.railwayteam.railways.base.data.recipe;

import com.railwayteam.railways.Railways;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeBuilder;
import com.simibubi.create.content.trains.track.TrackMaterial;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.resources.ResourceLocation;

/**
 * Extends Create's Fabric recipe builder with Fabric conditional recipe support.
 */
public class RailwaysSequencedAssemblyRecipeBuilder extends SequencedAssemblyRecipeBuilder {
    public RailwaysSequencedAssemblyRecipeBuilder(ResourceLocation id) {
        super(id);
    }

    /**
     * If the material is from another mod, add a recipe condition for the mod.
     * @param trackMaterial the material
     * @return this
     */
    public RailwaysSequencedAssemblyRecipeBuilder conditionalMaterial(TrackMaterial trackMaterial) {
        String namespace = trackMaterial.id.getNamespace();
        if (!Railways.MOD_ID.equals(namespace)) {
            recipeConditions.add(ResourceConditions.allModsLoaded(namespace));
        }
        return this;
    }
}
