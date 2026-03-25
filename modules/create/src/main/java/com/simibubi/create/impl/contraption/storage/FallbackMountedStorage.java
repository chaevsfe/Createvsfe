package com.simibubi.create.impl.contraption.storage;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllMountedStorageTypes;
import com.simibubi.create.api.contraption.storage.item.simple.SimpleMountedStorage;

import io.github.fabricators_of_create.porting_lib_ufo.transfer.item.ItemStackHandler;

/**
 * A fallback mounted storage impl that will try to be used when no type is
 * registered for a block. This requires that the mounted block provide an item handler
 * whose class is exactly {@link ItemStackHandler}.
 */
public class FallbackMountedStorage extends SimpleMountedStorage {
	public static final MapCodec<FallbackMountedStorage> CODEC = SimpleMountedStorage.codec(FallbackMountedStorage::new);

	public FallbackMountedStorage(ItemStackHandler handler) {
		super(AllMountedStorageTypes.FALLBACK.get(), handler);
	}

	public static boolean isValid(Object handler) {
		return handler != null && handler.getClass() == ItemStackHandler.class;
	}
}
