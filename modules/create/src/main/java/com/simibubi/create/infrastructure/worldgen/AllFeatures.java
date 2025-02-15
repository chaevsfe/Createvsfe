package com.simibubi.create.infrastructure.worldgen;

import com.simibubi.create.Create;

import io.github.fabricators_of_create.porting_lib_ufo.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib_ufo.util.RegistryObject;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;

public class AllFeatures {
	private static final LazyRegistrar<Feature<?>> REGISTER = LazyRegistrar.create(BuiltInRegistries.FEATURE, Create.ID);

	public static final RegistryObject<LayeredOreFeature> LAYERED_ORE = REGISTER.register("layered_ore", () -> new LayeredOreFeature());

	public static void register() {
		REGISTER.register();
	}
}
