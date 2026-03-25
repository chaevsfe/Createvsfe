package com.simibubi.create.api.contraption.storage.item.chest;

import java.util.Iterator;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.simibubi.create.AllMountedStorageTypes;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A combined storage wrapping two chest halves, used for double chest GUI access.
 * This is a view-only combination used for the menu; it delegates to the two separate storages.
 */
class CombinedChestMountedStorage extends MountedItemStorage {
	private final MountedItemStorage left;
	private final MountedItemStorage right;
	private final int leftSlots;

	CombinedChestMountedStorage(MountedItemStorage left, MountedItemStorage right) {
		super(AllMountedStorageTypes.CHEST.get());
		this.left = left;
		this.right = right;
		this.leftSlots = left.getSlotCount();
	}

	@Override
	public int getSlotCount() {
		return left.getSlotCount() + right.getSlotCount();
	}

	@Override
	@NotNull
	public ItemStack getStackInSlot(int slot) {
		if (slot < leftSlots) return left.getStackInSlot(slot);
		return right.getStackInSlot(slot - leftSlots);
	}

	@Override
	public void setStackInSlot(int slot, @NotNull ItemStack stack) {
		if (slot < leftSlots) left.setStackInSlot(slot, stack);
		else right.setStackInSlot(slot - leftSlots, stack);
	}

	@Override
	@NotNull
	public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		if (slot < leftSlots) return left.insertItem(slot, stack, simulate);
		return right.insertItem(slot - leftSlots, stack, simulate);
	}

	@Override
	@NotNull
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (slot < leftSlots) return left.extractItem(slot, amount, simulate);
		return right.extractItem(slot - leftSlots, amount, simulate);
	}

	@Override
	public int getSlotLimit(int slot) {
		if (slot < leftSlots) return left.getSlotLimit(slot);
		return right.getSlotLimit(slot - leftSlots);
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		if (slot < leftSlots) return left.isItemValid(slot, stack);
		return right.isItemValid(slot - leftSlots, stack);
	}

	@Override
	public void unmount(Level level, BlockState state, BlockPos pos, BlockEntity be) {
		// Not used directly — individual halves handle unmounting
	}

	// Fabric Transfer API delegation

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		long inserted = left.insert(resource, maxAmount, transaction);
		if (inserted < maxAmount) inserted += right.insert(resource, maxAmount - inserted, transaction);
		return inserted;
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		long extracted = left.extract(resource, maxAmount, transaction);
		if (extracted < maxAmount) extracted += right.extract(resource, maxAmount - extracted, transaction);
		return extracted;
	}

	@Override
	public Iterator<StorageView<ItemVariant>> iterator() {
		return java.util.stream.Stream.concat(
			java.util.stream.StreamSupport.stream(java.util.Spliterators.spliteratorUnknownSize(left.iterator(), 0), false),
			java.util.stream.StreamSupport.stream(java.util.Spliterators.spliteratorUnknownSize(right.iterator(), 0), false)
		).iterator();
	}

	@Override
	public List<SingleSlotStorage<ItemVariant>> getSlots() {
		var combined = new java.util.ArrayList<>(left.getSlots());
		combined.addAll(right.getSlots());
		return combined;
	}

	@Override
	public SingleSlotStorage<ItemVariant> getSlot(int slot) {
		if (slot < leftSlots) return left.getSlot(slot);
		return right.getSlot(slot - leftSlots);
	}
}
