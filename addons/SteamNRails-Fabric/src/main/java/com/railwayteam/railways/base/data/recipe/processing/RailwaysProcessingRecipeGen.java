/*
 * Steam 'n' Rails
 * Copyright (c) 2025 The Railways Team
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

package com.railwayteam.railways.base.data.recipe.processing;

import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/* TODO: port to 1.21.1 - Processing recipe datagen stubbed out */
public abstract class RailwaysProcessingRecipeGen {

	public static DataProvider registerAll(PackOutput output) {
		return new DataProvider() {
			@Override
			public @NotNull String getName() {
				return "Railways' Processing Recipes (stubbed)";
			}

			@Override
			public @NotNull CompletableFuture<?> run(@NotNull net.minecraft.data.CachedOutput dc) {
				return CompletableFuture.completedFuture(null);
			}
		};
	}
}
