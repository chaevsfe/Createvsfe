package com.simibubi.create.api.contraption.storage.item;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableMap;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;

/**
 * Wrapper around many MountedItemStorages, providing access to all of them as one storage.
 * They can still be accessed individually through the map.
 *
 * Uses O(1) lookup arrays instead of O(n) linear scan.
 */
public class MountedItemStorageWrapper implements SlottedStorage<ItemVariant> {
	public final ImmutableMap<BlockPos, MountedItemStorage> storages;
	private final List<MountedItemStorage> storageList;

	// Lookup arrays
	private final int[] slotToStorage;   // Maps each slot to its storage index
	private final int[] slotOffsets;     // Starting slot for each storage
	private final int totalSlots;

	public MountedItemStorageWrapper(ImmutableMap<BlockPos, MountedItemStorage> storages) {
		this.storages = storages;
		this.storageList = List.copyOf(storages.values());

		// Compute total slots
		int slots = 0;
		for (MountedItemStorage s : storageList) {
			slots += s.getSlotCount();
		}
		this.totalSlots = slots;

		// Build lookup arrays
		this.slotToStorage = new int[totalSlots];
		this.slotOffsets = new int[storageList.size()];

		int currentSlot = 0;
		for (int storageIdx = 0; storageIdx < storageList.size(); storageIdx++) {
			slotOffsets[storageIdx] = currentSlot;
			int slotsInStorage = storageList.get(storageIdx).getSlotCount();

			for (int i = 0; i < slotsInStorage; i++) {
				slotToStorage[currentSlot + i] = storageIdx;
			}

			currentSlot += slotsInStorage;
		}
	}

	@Override
	public int getSlotCount() {
		return totalSlots;
	}

	@Override
	public SingleSlotStorage<ItemVariant> getSlot(int slot) {
		if (slot < 0 || slot >= totalSlots) {
			throw new IndexOutOfBoundsException("Slot " + slot + " is out of bounds for size " + totalSlots);
		}
		int storageIdx = slotToStorage[slot];
		int localSlot = slot - slotOffsets[storageIdx];
		MountedItemStorage storage = storageList.get(storageIdx);
		return storage.getSlot(localSlot);
	}

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notBlankNotNegative(resource, maxAmount);
		long inserted = 0;
		for (MountedItemStorage storage : storageList) {
			inserted += storage.insert(resource, maxAmount - inserted, transaction);
			if (inserted >= maxAmount) break;
		}
		return inserted;
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notBlankNotNegative(resource, maxAmount);
		long extracted = 0;
		for (MountedItemStorage storage : storageList) {
			extracted += storage.extract(resource, maxAmount - extracted, transaction);
			if (extracted >= maxAmount) break;
		}
		return extracted;
	}

	@Override
	public Iterator<StorageView<ItemVariant>> iterator() {
		return storageList.stream()
			.<StorageView<ItemVariant>>flatMap(s -> {
				Iterator<StorageView<ItemVariant>> it = s.iterator();
				return java.util.stream.StreamSupport.stream(
					java.util.Spliterators.spliteratorUnknownSize(it, 0), false);
			})
			.iterator();
	}
}
