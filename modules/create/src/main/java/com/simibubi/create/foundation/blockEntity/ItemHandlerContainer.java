package com.simibubi.create.foundation.blockEntity;

import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * A Container wrapping a MountedItemStorage.
 * Fabric port of NeoForge's ItemHandlerContainer.
 */
public class ItemHandlerContainer implements Container {
	protected final MountedItemStorage inv;

	public ItemHandlerContainer(MountedItemStorage inv) {
		this.inv = inv;
	}

	@Override
	public int getContainerSize() {
		return inv.getSlotCount();
	}

	@Override
	public ItemStack getItem(int slot) {
		return inv.getStackInSlot(slot);
	}

	@Override
	public ItemStack removeItem(int slot, int count) {
		ItemStack stack = inv.getStackInSlot(slot);
		return stack.isEmpty() ? ItemStack.EMPTY : stack.split(count);
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		inv.setStackInSlot(slot, stack);
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		ItemStack s = getItem(index);
		if (s.isEmpty())
			return ItemStack.EMPTY;

		setItem(index, ItemStack.EMPTY);
		return s;
	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < inv.getSlotCount(); i++) {
			if (!inv.getStackInSlot(i).isEmpty())
				return false;
		}
		return true;
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return inv.isItemValid(slot, stack);
	}

	@Override
	public void clearContent() {
		for (int i = 0; i < inv.getSlotCount(); i++)
			inv.setStackInSlot(i, ItemStack.EMPTY);
	}

	@Override
	public int getMaxStackSize() {
		return 0;
	}

	@Override
	public void setChanged() {
	}

	@Override
	public boolean stillValid(Player player) {
		return false;
	}

	@Override
	public void startOpen(Player player) {
	}

	@Override
	public void stopOpen(Player player) {
	}
}
