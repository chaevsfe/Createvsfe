package com.simibubi.create.compat.thresholdSwitch;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;

public class StorageDrawers implements ThresholdSwitchCompat {

	@Override
	public boolean isFromThisMod(BlockEntity blockEntity) {
		if (blockEntity == null)
			return false;
		String namespace = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType()).getNamespace();
		return "storagedrawers".equals(namespace);
	}

	@Override
	public long getSpaceInSlot(StorageView<ItemVariant> view) {
		// Storage Drawers: first slot is the "locked" slot used internally (slot 0 = skip)
		// For Fabric Transfer API we can't easily check slot index, so return capacity as-is.
		// The NeoForge version returns 0 for slot 0 and capacity for others.
		// Since StorageDrawers Fabric exposes slots individually, each StorageView is one drawer slot.
		return view.getCapacity();
	}

}
