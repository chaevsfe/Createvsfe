package com.simibubi.create.content.logistics.crate;

import java.util.Iterator;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllMountedStorageTypes;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;

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
 * Mounted storage for creative crates — provides an endless supply of items.
 */
public class CreativeCrateMountedStorage extends MountedItemStorage {
	public static final MapCodec<CreativeCrateMountedStorage> CODEC = ItemStack.OPTIONAL_CODEC.xmap(
		CreativeCrateMountedStorage::new, storage -> storage.suppliedStack
	).fieldOf("value");

	private final ItemStack suppliedStack;
	private final ItemStack cachedStackInSlot;

	protected CreativeCrateMountedStorage(MountedItemStorageType<?> type, ItemStack suppliedStack) {
		super(type);
		this.suppliedStack = suppliedStack;
		this.cachedStackInSlot = suppliedStack.copyWithCount(suppliedStack.getMaxStackSize());
	}

	public CreativeCrateMountedStorage(ItemStack suppliedStack) {
		this(AllMountedStorageTypes.CREATIVE_CRATE.get(), suppliedStack);
	}

	@Override
	public void unmount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
		// no need to do anything here, the supplied item can't change while mounted
	}

	@Override
	public int getSlotCount() {
		return 2; // 0 holds the supplied stack endlessly, 1 is always empty to accept
	}

	@Override
	@NotNull
	public ItemStack getStackInSlot(int slot) {
		return slot == 0 ? this.cachedStackInSlot : ItemStack.EMPTY;
	}

	@Override
	public void setStackInSlot(int slot, @NotNull ItemStack stack) {
	}

	@Override
	@NotNull
	public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		return ItemStack.EMPTY; // no remainder, accept any input
	}

	@Override
	@NotNull
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (slot == 0 && !this.suppliedStack.isEmpty()) {
			int count = Math.min(amount, this.suppliedStack.getMaxStackSize());
			return this.suppliedStack.copyWithCount(count);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public int getSlotLimit(int slot) {
		return 64;
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return true;
	}

	// Fabric Transfer API

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		return maxAmount; // accept everything
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		if (suppliedStack.isEmpty() || !resource.matches(suppliedStack)) return 0;
		long count = Math.min(maxAmount, suppliedStack.getMaxStackSize());
		return count;
	}

	@Override
	public Iterator<StorageView<ItemVariant>> iterator() {
		return getSlots().stream().map(s -> (StorageView<ItemVariant>) s).iterator();
	}

	@Override
	public List<SingleSlotStorage<ItemVariant>> getSlots() {
		return List.of(new SupplySlot(), new SinkSlot());
	}

	@Override
	public SingleSlotStorage<ItemVariant> getSlot(int slot) {
		if (slot == 0) return new SupplySlot();
		return new SinkSlot();
	}

	private class SupplySlot implements SingleSlotStorage<ItemVariant> {
		@Override
		public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
			return 0; // don't accept into supply slot
		}

		@Override
		public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
			if (suppliedStack.isEmpty() || !resource.matches(suppliedStack)) return 0;
			return Math.min(maxAmount, suppliedStack.getMaxStackSize());
		}

		@Override
		public boolean isResourceBlank() {
			return suppliedStack.isEmpty();
		}

		@Override
		public ItemVariant getResource() {
			return ItemVariant.of(suppliedStack);
		}

		@Override
		public long getAmount() {
			return suppliedStack.isEmpty() ? 0 : suppliedStack.getMaxStackSize();
		}

		@Override
		public long getCapacity() {
			return 64;
		}
	}

	private static class SinkSlot implements SingleSlotStorage<ItemVariant> {
		@Override
		public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
			return maxAmount; // accept all
		}

		@Override
		public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
			return 0;
		}

		@Override
		public boolean isResourceBlank() {
			return true;
		}

		@Override
		public ItemVariant getResource() {
			return ItemVariant.blank();
		}

		@Override
		public long getAmount() {
			return 0;
		}

		@Override
		public long getCapacity() {
			return 64;
		}
	}
}
