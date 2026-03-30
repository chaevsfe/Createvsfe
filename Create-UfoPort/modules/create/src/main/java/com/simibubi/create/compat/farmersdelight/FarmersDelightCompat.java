package com.simibubi.create.compat.farmersdelight;

import com.simibubi.create.compat.Mods;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class FarmersDelightCompat {

	private static Block richSoilBlock;
	private static boolean initialized = false;

	private static void initialize() {
		if (initialized)
			return;
		initialized = true;
		if (Mods.FARMERSDELIGHT.isLoaded()) {
			richSoilBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("farmersdelight", "rich_soil"));
		}
	}

	public static boolean shouldHarvestMushroom(Level world, BlockPos pos, BlockState state) {
		initialize();
		if (richSoilBlock == null)
			return true;
		return !world.getBlockState(pos.below()).is(richSoilBlock);
	}
}
