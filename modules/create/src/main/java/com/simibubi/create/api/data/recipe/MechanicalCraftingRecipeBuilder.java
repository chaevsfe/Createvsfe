package com.simibubi.create.api.data.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.simibubi.create.content.kinetics.crafter.MechanicalCraftingRecipe;
import com.simibubi.create.foundation.utility.RegisteredObjects;

import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;

/**
 * The builder for building Mechanical Crafting recipes.
 * @see MechanicalCraftingRecipeGen
 */
public class MechanicalCraftingRecipeBuilder {

	private final Item result;
	private final int count;
	private final List<String> pattern = Lists.newArrayList();
	private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
	private boolean acceptMirrored;
	private final List<ResourceCondition> recipeConditions;

	public MechanicalCraftingRecipeBuilder(ItemLike result, int resultCount) {
		this.result = result.asItem();
		count = resultCount;
		acceptMirrored = true;
		recipeConditions = new ArrayList<>();
	}

	/**
	 * Creates a new builder for a shaped recipe with the specified result with a count of 1
	 */
	public static MechanicalCraftingRecipeBuilder shapedRecipe(ItemLike result) {
		return shapedRecipe(result, 1);
	}

	/**
	 * Creates a new builder for a shaped recipe with the specified result and count.
	 */
	public static MechanicalCraftingRecipeBuilder shapedRecipe(ItemLike result, int resultCount) {
		return new MechanicalCraftingRecipeBuilder(result, resultCount);
	}

	/**
	 * Adds a new unique key to the recipe key for use in the pattern
	 */
	public MechanicalCraftingRecipeBuilder key(Character c, TagKey<Item> tag) {
		return this.key(c, Ingredient.of(tag));
	}

	/**
	 * Adds a new unique key to the recipe key for use in the pattern
	 */
	public MechanicalCraftingRecipeBuilder key(Character c, ItemLike item) {
		return this.key(c, Ingredient.of(item));
	}

	/**
	 * Adds a new unique key to the recipe key for use in the pattern
	 */
	public MechanicalCraftingRecipeBuilder key(Character c, Ingredient ingredient) {
		if (this.key.containsKey(c)) {
			throw new IllegalArgumentException("Symbol '" + c + "' is already defined!");
		} else if (c == ' ') {
			throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
		} else {
			this.key.put(c, ingredient);
			return this;
		}
	}

	/**
	 * Adds a new line to the pattern for this recipe. All lines
	 * for a pattern must be the same length, pad with spaces (empty slots)
	 * if necessary.
	 */
	public MechanicalCraftingRecipeBuilder patternLine(String line) {
		if (!this.pattern.isEmpty() && line.length() != this.pattern.get(0)
			.length()) {
			throw new IllegalArgumentException("Pattern must be the same width on every line!");
		} else {
			this.pattern.add(line);
			return this;
		}
	}

	/**
	 * Prevents the crafters from matching a vertically flipped version of the recipe
	 */
	public MechanicalCraftingRecipeBuilder disallowMirrored() {
		acceptMirrored = false;
		return this;
	}

	/**
	 * Builds this recipe into a {@link RecipeOutput}.
	 */
	public void build(RecipeOutput output) {
		this.build(output, RegisteredObjects.getKeyOrThrow(this.result));
	}

	/**
	 * Builds this recipe into a {@link RecipeOutput}. Use
	 * {@link #build(RecipeOutput)} if the recipe id is the same as the result item id
	 */
	public void build(RecipeOutput output, String id) {
		ResourceLocation resourcelocation = RegisteredObjects.getKeyOrThrow(this.result);
		ResourceLocation idRs = ResourceLocation.parse(id);
		if (idRs.equals(resourcelocation)) {
			throw new IllegalStateException("Shaped Recipe " + id + " should remove its 'id' argument");
		} else {
			this.build(output, idRs);
		}
	}

	/**
	 * Builds this recipe into a {@link RecipeOutput}.
	 */
	public void build(RecipeOutput consumer, ResourceLocation id) {
		validate(id);

		final ResourceCondition[] conds = recipeConditions.toArray(ResourceCondition[]::new);

		RecipeOutput consumer2 = new RecipeOutput() {
			@Override
			public void accept(ResourceLocation var1, Recipe<?> var2, AdvancementHolder var3) {
				if (conds.length != 0)
					FabricDataGenHelper.addConditions(var2, conds);
				consumer.accept(var1, var2, var3);
			}

			@Override
			public Advancement.Builder advancement() {
				return consumer.advancement();
			}
		};

		MechanicalCraftingRecipe recipe = MechanicalCraftingRecipe.fromShaped(
			new net.minecraft.world.item.crafting.ShapedRecipe("", CraftingBookCategory.MISC,
				ShapedRecipePattern.of(key, pattern), new ItemStack(result, count), acceptMirrored),
			acceptMirrored);
		consumer2.accept(id, recipe, null);
	}

	/**
	 * Makes sure that this recipe is valid.
	 * @param recipeId The id of this recipe, only used for error messages.
	 */
	private void validate(ResourceLocation recipeId) {
		if (pattern.isEmpty()) {
			throw new IllegalStateException("No pattern is defined for shaped recipe " + recipeId + "!");
		} else {
			Set<Character> set = Sets.newHashSet(key.keySet());
			set.remove(' ');

			for (String s : pattern) {
				for (int i = 0; i < s.length(); ++i) {
					char c0 = s.charAt(i);
					if (!key.containsKey(c0) && c0 != ' ')
						throw new IllegalStateException(
							"Pattern in recipe " + recipeId + " uses undefined symbol '" + c0 + "'");
					set.remove(c0);
				}
			}

			if (!set.isEmpty())
				throw new IllegalStateException(
					"Ingredients are defined but not used in pattern for recipe " + recipeId);
		}
	}

	/**
	 * Add a new condition so this recipe is only enabled when the specified mod is loaded.
	 */
	public MechanicalCraftingRecipeBuilder whenModLoaded(String modid) {
		return withCondition(ResourceConditions.allModsLoaded(modid));
	}

	/**
	 * Add a new condition so this recipe is only enabled when the specified mod is not loaded.
	 */
	public MechanicalCraftingRecipeBuilder whenModMissing(String modid) {
		return withCondition(ResourceConditions.not(ResourceConditions.allModsLoaded(modid)));
	}

	/**
	 * Add a new condition so this recipe is only enabled when the condition is true.
	 */
	public MechanicalCraftingRecipeBuilder withCondition(ResourceCondition condition) {
		recipeConditions.add(condition);
		return this;
	}
}
