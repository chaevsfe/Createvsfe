package com.simibubi.create.impl.registry;

import org.jetbrains.annotations.ApiStatus;

/**
 * Fabric stub for CreateDataMapsImpl.
 * On NeoForge, this registers DataMapType entries via RegisterDataMapTypesEvent.
 * On Fabric, BlazeBurnerFuel data is loaded via ResourceReloadListener instead of data maps.
 */
@ApiStatus.Internal
public class CreateDataMapsImpl {
	// No-op on Fabric — BlazeBurnerFuel is loaded via ResourceManagerReloadListener
}
