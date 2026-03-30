package com.simibubi.create.compat.thresholdSwitch;

import com.simibubi.create.compat.Mods;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;

public class FunctionalStorage implements ThresholdSwitchCompat {

	@Override
	public boolean isFromThisMod(BlockEntity blockEntity) {
		if (blockEntity == null)
			return false;
		String namespace = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType()).getNamespace();
		return "functionalstorage".equals(namespace);
	}

	@Override
	public long getSpaceInSlot(StorageView<ItemVariant> view) {
		// FunctionalStorage uses standard slot limits
		return view.getCapacity();
	}
}
