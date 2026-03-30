package com.simibubi.create.compat.thresholdSwitch;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SophisticatedStorage implements ThresholdSwitchCompat {

	@Override
	public boolean isFromThisMod(BlockEntity be) {
		if (be == null)
			return false;

		String namespace = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(be.getType()).getNamespace();
		return "sophisticatedstorage".equals(namespace) || "sophisticatedbackpacks".equals(namespace);
	}

	@Override
	public long getSpaceInSlot(StorageView<ItemVariant> view) {
		// Sophisticated Storage uses non-standard stack sizes; capacity is pre-scaled
		ItemVariant resource = view.getResource();
		long capacity = view.getCapacity();
		long amount = view.getAmount();
		if (resource.isBlank())
			return capacity;
		int maxStack = resource.toStack().getMaxStackSize();
		// Mirrors NeoForge: (slotLimit * maxStackSize) / 64
		return (capacity * maxStack) / 64;
	}

}
