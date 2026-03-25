package com.simibubi.create.content.contraptions.behaviour.dispenser.storage;

import com.simibubi.create.api.contraption.storage.item.simple.SimpleMountedStorage;
import com.simibubi.create.api.contraption.storage.item.simple.SimpleMountedStorageType;

import io.github.fabricators_of_create.porting_lib_ufo.transfer.item.ItemStackHandler;

public class DispenserMountedStorageType extends SimpleMountedStorageType<DispenserMountedStorage> {
	public DispenserMountedStorageType() {
		super(DispenserMountedStorage.CODEC);
	}

	@Override
	protected SimpleMountedStorage createStorage(ItemStackHandler handler) {
		return new DispenserMountedStorage(handler);
	}
}
