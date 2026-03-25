package com.simibubi.create.impl.contraption.storage;

import com.simibubi.create.api.contraption.storage.item.simple.SimpleMountedStorage;
import com.simibubi.create.api.contraption.storage.item.simple.SimpleMountedStorageType;

import io.github.fabricators_of_create.porting_lib_ufo.transfer.item.ItemStackHandler;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class FallbackMountedStorageType extends SimpleMountedStorageType<FallbackMountedStorage> {
	public FallbackMountedStorageType() {
		super(FallbackMountedStorage.CODEC);
	}

	@Override
	protected ItemStackHandler getHandler(Level level, BlockEntity be) {
		ItemStackHandler handler = super.getHandler(level, be);
		return handler != null && FallbackMountedStorage.isValid(handler) ? handler : null;
	}

	@Override
	protected SimpleMountedStorage createStorage(ItemStackHandler handler) {
		return new FallbackMountedStorage(handler);
	}
}
