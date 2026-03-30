package com.mrh0.createaddition.recipe.rolling;
import com.mrh0.createaddition.index.CARecipes;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
public class RollingRecipe extends ProcessingRecipe<RecipeInput> {
    public RollingRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
        super(com.mrh0.createaddition.index.CARecipes.createTypeInfo("rolling", CARecipes.ROLLING_TYPE), params);
    }
    @Override protected int getMaxInputCount() { return 1; }
    @Override protected int getMaxOutputCount() { return 1; }
    @Override public boolean matches(RecipeInput inv, Level level) { return false; }
}
