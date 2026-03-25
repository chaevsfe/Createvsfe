package com.simibubi.create.api.contraption.storage.fluid;

import java.util.List;

import com.google.common.collect.ImmutableMap;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;

/**
 * Wrapper around many MountedFluidStorages, providing access to all of them as one storage.
 * They can still be accessed individually through the map.
 */
public class MountedFluidStorageWrapper extends CombinedTankWrapper {
	public final ImmutableMap<BlockPos, MountedFluidStorage> storages;

	@SuppressWarnings("unchecked")
	public MountedFluidStorageWrapper(ImmutableMap<BlockPos, MountedFluidStorage> storages) {
		super(storages.values().stream()
			.map(MountedFluidStorage::getStorage)
			.toArray(Storage[]::new));
		this.storages = storages;
	}
}
