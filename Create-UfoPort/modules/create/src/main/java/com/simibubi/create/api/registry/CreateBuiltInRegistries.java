package com.simibubi.create.api.registry;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageType;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.api.equipment.potatoCannon.PotatoProjectileBlockHitAction;
import com.simibubi.create.api.equipment.potatoCannon.PotatoProjectileEntityHitAction;
import com.simibubi.create.api.equipment.potatoCannon.PotatoProjectileRenderMode;
import com.simibubi.create.content.logistics.filter.attribute.ItemAttributeType;
import com.simibubi.create.content.logistics.packagePort.PackagePortTargetType;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;

/**
 * Create's custom built-in registries.
 * Fabric equivalent of NeoForge's CreateBuiltInRegistries, using FabricRegistryBuilder.
 */
public class CreateBuiltInRegistries {
	public static final Registry<ItemAttributeType> ITEM_ATTRIBUTE_TYPE =
		FabricRegistryBuilder.createSimple(CreateRegistries.ITEM_ATTRIBUTE_TYPE)
			.buildAndRegister();

	public static final Registry<PackagePortTargetType> PACKAGE_PORT_TARGET_TYPE =
		FabricRegistryBuilder.createSimple(CreateRegistries.PACKAGE_PORT_TARGET_TYPE)
			.buildAndRegister();

	public static final Registry<MountedItemStorageType<?>> MOUNTED_ITEM_STORAGE_TYPE =
		FabricRegistryBuilder.createSimple(CreateRegistries.MOUNTED_ITEM_STORAGE_TYPE)
			.buildAndRegister();

	public static final Registry<MountedFluidStorageType<?>> MOUNTED_FLUID_STORAGE_TYPE =
		FabricRegistryBuilder.createSimple(CreateRegistries.MOUNTED_FLUID_STORAGE_TYPE)
			.buildAndRegister();

	public static final Registry<MapCodec<? extends PotatoProjectileRenderMode>> POTATO_PROJECTILE_RENDER_MODE =
		FabricRegistryBuilder.createSimple(CreateRegistries.POTATO_PROJECTILE_RENDER_MODE)
			.buildAndRegister();

	public static final Registry<MapCodec<? extends PotatoProjectileEntityHitAction>> POTATO_PROJECTILE_ENTITY_HIT_ACTION =
		FabricRegistryBuilder.createSimple(CreateRegistries.POTATO_PROJECTILE_ENTITY_HIT_ACTION)
			.buildAndRegister();

	public static final Registry<MapCodec<? extends PotatoProjectileBlockHitAction>> POTATO_PROJECTILE_BLOCK_HIT_ACTION =
		FabricRegistryBuilder.createSimple(CreateRegistries.POTATO_PROJECTILE_BLOCK_HIT_ACTION)
			.buildAndRegister();

	// Display source/target registries are intentionally not created as Fabric registries.
	// On Fabric/UfoPort, display source/target registration is handled by
	// AllDisplayBehaviours.registerDefaults() using AttachedRegistry, not the Fabric registry system.
	// Creating empty Fabric registries causes "Registry was empty after loading" errors.
}
