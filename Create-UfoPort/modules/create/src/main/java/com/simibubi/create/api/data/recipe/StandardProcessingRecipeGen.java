package com.simibubi.create.api.data.recipe;

import java.util.concurrent.CompletableFuture;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;

/**
 * A base class for standard processing recipes (those using the default ProcessingRecipeBuilder).
 * <p>
 * Addons should extend this and implement {@link #getRecipeType()} and optionally
 * override other methods to provide recipe helpers.
 */
public abstract class StandardProcessingRecipeGen<R extends ProcessingRecipe<?>> extends ProcessingRecipeGen<R> {

	public StandardProcessingRecipeGen(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
		super(output, registries, defaultNamespace);
	}

	@Override
	protected ProcessingRecipeBuilder<R> getBuilder(ResourceLocation id) {
		ProcessingRecipeSerializer<R> serializer = getRecipeType().getSerializer();
		return new ProcessingRecipeBuilder<>(serializer.getFactory(), id);
	}
}
