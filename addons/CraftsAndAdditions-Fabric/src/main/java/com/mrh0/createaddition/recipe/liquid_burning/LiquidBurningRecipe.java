package com.mrh0.createaddition.recipe.liquid_burning;
import com.mrh0.createaddition.index.CARecipes;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
public class LiquidBurningRecipe extends ProcessingRecipe<RecipeInput> {
    public LiquidBurningRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
        super(com.mrh0.createaddition.index.CARecipes.createTypeInfo("liquid_burning", CARecipes.LIQUID_BURNING_TYPE), params);
    }
    @Override protected int getMaxInputCount() { return 1; }
    @Override protected int getMaxOutputCount() { return 0; }
    @Override public boolean matches(RecipeInput inv, Level level) { return false; }
    public int getBurnTime() { return 0; }
}
