/*
 * Steam 'n' Rails
 * Copyright (c) 2023-2025 The Railways Team
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

package com.railwayteam.railways.registry;

import com.railwayteam.railways.registry.advancement.CRAdvancement;
import com.railwayteam.railways.registry.advancement.CRAdvancement.Builder;
import com.simibubi.create.AllItems;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;

import static com.railwayteam.railways.registry.advancement.CRAdvancement.TaskType.SECRET;
import static com.railwayteam.railways.registry.advancement.CRAdvancement.TaskType.SILENT;

public class CRAdvancements implements DataProvider {

	public static final List<CRAdvancement> ENTRIES = new ArrayList<>();
	public static final CRAdvancement START = null,

	/*
	 * Some ids have trailing 0's to modify their vertical position on the tree
	 * (Advancement ordering seems to be deterministic but hash based)
	 */

	ROOT = create("root", b -> b.icon(CRBlocks.HANDCAR)
		.title("Welcome to Steam 'n' Rails")
		.description("Here Be Trains")
		.awardedForFree()
		.special(SILENT)),

	// Special advancements

	STRANGE_TEA = create("strange_tea", b -> b.icon(AllItems.BUILDERS_TEA)
		.title("That's not Earl Grey")
		.description("Accidentally drink paint")
		.after(ROOT)
		.special(SECRET)
	),

	//
	END = null;

	private static CRAdvancement create(String id, UnaryOperator<Builder> b) {
		return new CRAdvancement(id, b);
	}

	// Datagen

	private final PackOutput output;

	public CRAdvancements(PackOutput output) {
		this.output = output;
	}

	@Override
	public CompletableFuture<?> run(CachedOutput cache) {
		/* TODO: port advancement datagen serialization to 1.21.1 Codec-based API */
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public String getName() {
		return "Steam 'n' Rails' Advancements";
	}

	public static void provideLang(BiConsumer<String, String> consumer) {
		for (CRAdvancement advancement : ENTRIES)
			advancement.provideLang(consumer);
	}

	public static void register() {}

}
