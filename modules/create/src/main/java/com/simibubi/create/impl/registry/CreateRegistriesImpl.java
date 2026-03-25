package com.simibubi.create.impl.registry;

import org.jetbrains.annotations.ApiStatus;

/**
 * Fabric stub for CreateRegistriesImpl.
 * On NeoForge, this registers datapack registries via DataPackRegistryEvent.
 * On Fabric, custom registries are built via FabricRegistryBuilder in CreateBuiltInRegistries.
 */
@ApiStatus.Internal
public class CreateRegistriesImpl {
	// No-op on Fabric — registry setup happens in CreateBuiltInRegistries via FabricRegistryBuilder
}
