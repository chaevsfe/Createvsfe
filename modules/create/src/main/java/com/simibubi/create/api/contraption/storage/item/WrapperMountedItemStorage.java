package com.simibubi.create.api.contraption.storage.item;

import java.util.Iterator;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import io.github.fabricators_of_create.porting_lib_ufo.transfer.item.ItemStackHandler;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;

/**
 * Partial implementation of a MountedItemStorage that wraps an ItemStackHandler.
 * On Fabric, uses porting_lib_ufo's ItemStackHandler which implements Storage<ItemVariant>.
 */
public abstract class WrapperMountedItemStorage<T extends ItemStackHandler> extends MountedItemStorage {
	protected final T wrapped;

	protected WrapperMountedItemStorage(MountedItemStorageType<?> type, T wrapped) {
		super(type);
		this.wrapped = wrapped;
	}

	@Override
	public int getSlotCount() {
		return this.wrapped.getSlotCount();
	}

	@Override
	@NotNull
	public ItemStack getStackInSlot(int slot) {
		return this.wrapped.getStackInSlot(slot);
	}

	@Override
	public void setStackInSlot(int slot, @NotNull ItemStack stack) {
		this.wrapped.setStackInSlot(slot, stack);
	}

	@Override
	@NotNull
	public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		// Simple insert: try to put the stack in the given slot
		if (stack.isEmpty() || !isItemValid(slot, stack)) return stack;
		ItemStack existing = getStackInSlot(slot);
		int limit = getSlotLimit(slot);
		if (!existing.isEmpty()) {
			if (!ItemStack.isSameItemSameComponents(stack, existing)) return stack;
			limit -= existing.getCount();
		}
		if (limit <= 0) return stack;
		int toInsert = Math.min(stack.getCount(), limit);
		if (!simulate) {
			ItemStack newStack = existing.isEmpty() ? stack.copyWithCount(toInsert) : existing.copyWithCount(existing.getCount() + toInsert);
			setStackInSlot(slot, newStack);
		}
		if (toInsert >= stack.getCount()) return ItemStack.EMPTY;
		ItemStack remainder = stack.copy();
		remainder.shrink(toInsert);
		return remainder;
	}

	@Override
	@NotNull
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		ItemStack existing = getStackInSlot(slot);
		if (existing.isEmpty()) return ItemStack.EMPTY;
		int toExtract = Math.min(amount, existing.getCount());
		ItemStack result = existing.copyWithCount(toExtract);
		if (!simulate) {
			ItemStack newStack = existing.copyWithCount(existing.getCount() - toExtract);
			setStackInSlot(slot, newStack.isEmpty() ? ItemStack.EMPTY : newStack);
		}
		return result;
	}

	@Override
	public int getSlotLimit(int slot) {
		return this.wrapped.getSlotLimit(slot);
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return true;
	}

	// Delegate Fabric Transfer API to wrapped handler

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		return this.wrapped.insert(resource, maxAmount, transaction);
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		return this.wrapped.extract(resource, maxAmount, transaction);
	}

	@Override
	public Iterator<StorageView<ItemVariant>> iterator() {
		return this.wrapped.iterator();
	}

	@Override
	public List<SingleSlotStorage<ItemVariant>> getSlots() {
		return this.wrapped.getSlots();
	}

	@Override
	public SingleSlotStorage<ItemVariant> getSlot(int slot) {
		return this.wrapped.getSlot(slot);
	}

	public static ItemStackHandler copyToItemStackHandler(ItemStackHandler handler) {
		ItemStackHandler copy = new ItemStackHandler(handler.getSlotCount());
		for (int i = 0; i < handler.getSlotCount(); i++) {
			copy.setStackInSlot(i, handler.getStackInSlot(i).copy());
		}
		return copy;
	}
}
