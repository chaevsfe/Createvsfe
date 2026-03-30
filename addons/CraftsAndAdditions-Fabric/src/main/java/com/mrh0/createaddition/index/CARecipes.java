package com.mrh0.createaddition.index;
import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.recipe.charging.ChargingRecipe;
import com.mrh0.createaddition.recipe.liquid_burning.LiquidBurningRecipe;
import com.mrh0.createaddition.recipe.rolling.RollingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
public class CARecipes {
    public static RecipeType<RollingRecipe> ROLLING_TYPE;
    public static RecipeSerializer<?> ROLLING;
    public static RecipeType<ChargingRecipe> CHARGING_TYPE;
    public static RecipeSerializer<?> CHARGING;
    public static RecipeType<LiquidBurningRecipe> LIQUID_BURNING_TYPE;
    public static RecipeSerializer<?> LIQUID_BURNING;
    private static <T extends Recipe<?>> RecipeType<T> registerType(String id) {
        return Registry.register(BuiltInRegistries.RECIPE_TYPE, CreateAddition.asResource(id), new RecipeType<T>() { public String toString() { return id; } });
    }
    public static IRecipeTypeInfo createTypeInfo(String id, RecipeType<?> type) {
        return new IRecipeTypeInfo() {
            final ResourceLocation rl = CreateAddition.asResource(id);
            @Override public ResourceLocation getId() { return rl; }
            @Override public <T extends RecipeSerializer<?>> T getSerializer() { return null; }
            @Override public <T extends RecipeType<?>> T getType() { return (T) type; }
        };
    }
    @SuppressWarnings("unchecked")
    public static void register() {
        ROLLING_TYPE = registerType("rolling");
        ROLLING = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, CreateAddition.asResource("rolling"),
                new ProcessingRecipeSerializer<>(RollingRecipe::new, ProcessingCodecBuilder.getDefaultBuilder()));
        CHARGING_TYPE = registerType("charging");
        CHARGING = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, CreateAddition.asResource("charging"),
                new ProcessingRecipeSerializer<>(ChargingRecipe::new, ProcessingCodecBuilder.getDefaultBuilder()));
        LIQUID_BURNING_TYPE = registerType("liquid_burning");
        LIQUID_BURNING = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, CreateAddition.asResource("liquid_burning"),
                new ProcessingRecipeSerializer<>(LiquidBurningRecipe::new, ProcessingCodecBuilder.getDefaultBuilder()));
    }
}
