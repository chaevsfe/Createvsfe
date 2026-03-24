package com.simibubi.create.api.registry;

import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.filter.attribute.ItemAttributeType;
import com.simibubi.create.content.logistics.packagePort.PackagePortTargetType;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

/**
 * Resource keys for Create's custom registries.
 * Fabric equivalent of NeoForge's CreateRegistries.
 */
public class CreateRegistries {
	public static final ResourceKey<Registry<ItemAttributeType>> ITEM_ATTRIBUTE_TYPE =
		ResourceKey.createRegistryKey(Create.asResource("item_attribute_type"));

	public static final ResourceKey<Registry<PackagePortTargetType>> PACKAGE_PORT_TARGET_TYPE =
		ResourceKey.createRegistryKey(Create.asResource("package_port_target_type"));
}
