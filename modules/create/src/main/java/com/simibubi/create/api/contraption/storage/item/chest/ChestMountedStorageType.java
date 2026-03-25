package com.simibubi.create.api.contraption.storage.item.chest;

import com.simibubi.create.api.contraption.storage.item.simple.SimpleMountedStorage;
import com.simibubi.create.api.contraption.storage.item.simple.SimpleMountedStorageType;

import io.github.fabricators_of_create.porting_lib_ufo.transfer.item.ItemStackHandler;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ChestMountedStorageType extends SimpleMountedStorageType<ChestMountedStorage> {
	public ChestMountedStorageType() {
		super(ChestMountedStorage.CODEC);
	}

	@Override
	protected ItemStackHandler getHandler(Level level, BlockEntity be) {
		if (be instanceof Container container) {
			// Wrap the container as an ItemStackHandler
			int size = container.getContainerSize();
			ItemStackHandler handler = new ItemStackHandler(size);
			for (int i = 0; i < size; i++) {
				handler.setStackInSlot(i, container.getItem(i).copy());
			}
			return handler;
		}
		return null;
	}

	@Override
	protected SimpleMountedStorage createStorage(ItemStackHandler handler) {
		return new ChestMountedStorage(handler);
	}
}
