package com.simibubi.create.impl.unpacking;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.api.packager.unpacking.UnpackingHandler;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;

import io.github.fabricators_of_create.porting_lib_ufo.transfer.item.ItemStackHandler;
import io.github.fabricators_of_create.porting_lib_ufo.transfer.item.SlottedStackStorage;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Default unpacking handler for Fabric.
 * Uses Fabric Transfer API (ItemStorage.SIDED) instead of NeoForge IItemHandler capabilities.
 */
public enum DefaultUnpackingHandler implements UnpackingHandler {
	INSTANCE;

	@Override
	public boolean unpack(Level level, BlockPos pos, BlockState state, Direction side, List<ItemStack> items, @Nullable PackageOrderWithCrafts orderContext, boolean simulate) {
		Storage<ItemVariant> storage = ItemStorage.SIDED.find(level, pos, state, level.getBlockEntity(pos), side);
		if (storage == null)
			return false;

		if (!simulate) {
			// Insert all items. Some mods may not handle simulation correctly,
			// but the simulate pass should have verified space exists.
			try (Transaction tx = Transaction.openOuter()) {
				for (ItemStack itemStack : items) {
					if (!itemStack.isEmpty()) {
						ItemVariant variant = ItemVariant.of(itemStack);
						storage.insert(variant, itemStack.getCount(), tx);
					}
				}
				tx.commit();
			}
			return true;
		}

		// Simulation pass: try to insert each item and track remaining
		try (Transaction tx = Transaction.openOuter()) {
			for (int i = 0; i < items.size(); i++) {
				ItemStack toInsert = items.get(i);
				if (toInsert.isEmpty())
					continue;

				ItemVariant variant = ItemVariant.of(toInsert);
				long inserted = storage.insert(variant, toInsert.getCount(), tx);
				int remaining = toInsert.getCount() - (int) inserted;
				if (remaining > 0) {
					items.set(i, toInsert.copyWithCount(remaining));
				} else {
					items.set(i, ItemStack.EMPTY);
				}
			}
			// Don't commit - this is a simulation
			tx.abort();
		}

		for (ItemStack stack : items) {
			if (!stack.isEmpty()) {
				return false;
			}
		}

		return true;
	}
}
