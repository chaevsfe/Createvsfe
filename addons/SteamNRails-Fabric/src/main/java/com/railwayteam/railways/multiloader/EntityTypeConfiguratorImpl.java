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

package com.railwayteam.railways.multiloader;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.world.entity.EntityDimensions;

class EntityTypeConfiguratorImpl extends EntityTypeConfigurator {
    private final FabricEntityTypeBuilder<?> builder;

    EntityTypeConfiguratorImpl(FabricEntityTypeBuilder<?> builder) {
        this.builder = builder;
    }

    @Override
    public EntityTypeConfigurator size(float width, float height) {
        builder.dimensions(EntityDimensions.fixed(width, height));
        return this;
    }

    @Override
    public EntityTypeConfigurator fireImmune() {
        builder.fireImmune();
        return this;
    }
}
