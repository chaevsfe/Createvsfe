package com.simibubi.create.compat.thresholdSwitch;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Fabric adaptation of NeoForge ThresholdSwitchCompat.
 * Allows mods to customize how ThresholdSwitch reads slot capacity for their inventories.
 */
public interface ThresholdSwitchCompat {

	boolean isFromThisMod(BlockEntity blockEntity);

	/**
	 * Returns the effective storage space for a given slot view.
	 * This allows mods with non-standard slot limits (like Storage Drawers) to report
	 * their true capacity to the threshold switch.
	 */
	long getSpaceInSlot(StorageView<ItemVariant> view);

}
