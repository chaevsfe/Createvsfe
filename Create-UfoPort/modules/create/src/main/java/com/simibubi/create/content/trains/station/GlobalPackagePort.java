package com.simibubi.create.content.trains.station;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.item.SmartInventory;

import io.github.fabricators_of_create.porting_lib_ufo.transfer.item.ItemStackHandler;
import net.minecraft.world.item.ItemStack;

public class GlobalPackagePort {
	public String address = "";
	public ItemStackHandler offlineBuffer = new ItemStackHandler(18);
	public boolean primed = false;
	private boolean restoring = false;

	public void restoreOfflineBuffer(SmartInventory inventory) {
		if (!primed) return;

		restoring = true;

		for (int slot = 0; slot < offlineBuffer.getSlotCount(); slot++) {
			inventory.setStackInSlot(slot, offlineBuffer.getStackInSlot(slot));
		}

		restoring = false;
		primed = false;
	}

	public void saveOfflineBuffer(SmartInventory inventory) {
		if (restoring) return;

		for (int slot = 0; slot < inventory.getSlotCount(); slot++) {
			offlineBuffer.setStackInSlot(slot, inventory.getStackInSlot(slot));
		}

		Create.RAILWAYS.markTracksDirty();
	}
}
