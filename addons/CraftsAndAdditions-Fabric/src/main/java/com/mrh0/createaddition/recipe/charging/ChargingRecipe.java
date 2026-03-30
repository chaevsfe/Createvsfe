package com.mrh0.createaddition.recipe.charging;
import com.mrh0.createaddition.index.CARecipes;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
public class ChargingRecipe extends ProcessingRecipe<RecipeInput> {
    public ChargingRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
        super(com.mrh0.createaddition.index.CARecipes.createTypeInfo("charging", CARecipes.CHARGING_TYPE), params);
    }
    @Override protected int getMaxInputCount() { return 1; }
    @Override protected int getMaxOutputCount() { return 1; }
    @Override public boolean matches(RecipeInput inv, Level level) { return false; }
    public int getChargeTime() { return 0; }
    public int getEnergy() { return 0; }
}
