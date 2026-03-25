package com.simibubi.create.api.registry;

import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageType;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
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
}
