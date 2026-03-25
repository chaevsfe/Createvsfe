package com.simibubi.create.api.data.recipe;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import org.jetbrains.annotations.NotNull;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import com.simibubi.create.foundation.utility.RegisteredObjects;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

/**
 * A base class for all processing recipes, containing helper methods
 * for datagenning processing recipes.
 * <p>
 * For processing recipes using custom params (like ItemApplicationRecipe),
 * extend this class directly and override {@link #getRecipeType()} and {@link #getBuilder(ResourceLocation)}.
 * <p>
 * Addons should usually extend {@link StandardProcessingRecipeGen} for standard processing recipes.
 */
public abstract class ProcessingRecipeGen<T extends ProcessingRecipe<?>> extends BaseRecipeProvider {

	public ProcessingRecipeGen(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
		super(output, registries, defaultNamespace);
	}

	/**
	 * Returns a builder for the recipe type handled by this generator.
	 * Subclasses must implement this to return the appropriate builder type.
	 */
	protected abstract ProcessingRecipeBuilder<T> getBuilder(ResourceLocation id);

	/**
	 * Create a processing recipe with a single itemstack ingredient, using its id
	 * as the name of the recipe
	 */
	protected GeneratedRecipe create(String namespace, Supplier<ItemLike> singleIngredient, UnaryOperator<ProcessingRecipeBuilder<T>> transform) {
		GeneratedRecipe generatedRecipe = c -> {
			ItemLike itemLike = singleIngredient.get();
			transform
				.apply(getBuilder(ResourceLocation.fromNamespaceAndPath(namespace, RegisteredObjects.getKeyOrThrow(itemLike.asItem()).getPath()))
					.withItemIngredients(Ingredient.of(itemLike)))
				.build(c);
		};
		all.add(generatedRecipe);
		return generatedRecipe;
	}

	/**
	 * Create a processing recipe with a single itemstack ingredient, using its id
	 * as the name of the recipe
	 */
	protected GeneratedRecipe create(Supplier<ItemLike> singleIngredient, UnaryOperator<ProcessingRecipeBuilder<T>> transform) {
		return create(modid, singleIngredient, transform);
	}

	protected GeneratedRecipe createWithDeferredId(Supplier<ResourceLocation> name, UnaryOperator<ProcessingRecipeBuilder<T>> transform) {
		GeneratedRecipe generatedRecipe = c -> transform.apply(getBuilder(name.get())).build(c);
		all.add(generatedRecipe);
		return generatedRecipe;
	}

	/**
	 * Create a new processing recipe, with recipe definitions provided by the
	 * function
	 */
	protected GeneratedRecipe create(ResourceLocation name, UnaryOperator<ProcessingRecipeBuilder<T>> transform) {
		return createWithDeferredId(() -> name, transform);
	}

	/**
	 * Create a new processing recipe, with recipe definitions provided by the
	 * function, under the default namespace
	 */
	protected GeneratedRecipe create(String name, UnaryOperator<ProcessingRecipeBuilder<T>> transform) {
		return create(asResource(name), transform);
	}

	protected abstract IRecipeTypeInfo getRecipeType();

	protected Supplier<ResourceLocation> idWithSuffix(Supplier<ItemLike> item, String suffix) {
		return () -> {
			ResourceLocation registryName = RegisteredObjects.getKeyOrThrow(item.get()
					.asItem());
			return asResource(registryName.getPath() + suffix);
		};
	}

	/**
	 * Gets a display name for this recipe generator.
	 */
	@NotNull
	@Override
	public String getName() {
		return modid + "'s processing recipes: " + getRecipeType().getId()
			.getPath();
	}

}
