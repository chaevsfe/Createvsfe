package com.simibubi.create.api.registry;

import com.simibubi.create.content.logistics.filter.attribute.ItemAttributeType;

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
}
