package com.simibubi.create.content.logistics.packager;

import com.simibubi.create.content.logistics.box.PackageItem;

import io.github.fabricators_of_create.porting_lib_ufo.transfer.item.ItemStackHandler;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.item.ItemStack;

/**
 * Custom single-slot item handler exposing the Packager's held box.
 * Accepts packages for unwrapping (insert) and provides the held box (extract).
 */
public class PackagerItemHandler extends ItemStackHandler {

	private PackagerBlockEntity blockEntity;

	public PackagerItemHandler(PackagerBlockEntity blockEntity) {
		super(1);
		this.blockEntity = blockEntity;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return blockEntity.heldBox;
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		if (slot != 0)
			return;
		blockEntity.heldBox = stack;
		blockEntity.notifyUpdate();
	}

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		if (maxAmount <= 0) return 0;
		ItemStack stack = resource.toStack((int) Math.min(maxAmount, 1));
		if (!blockEntity.heldBox.isEmpty() || !blockEntity.queuedExitingPackages.isEmpty())
			return 0;
		if (!PackageItem.isPackage(stack))
			return 0;
		if (!blockEntity.unwrapBox(stack, true))
			return 0;
		transaction.addOuterCloseCallback(result -> {
			if (result.wasCommitted()) {
				blockEntity.unwrapBox(stack, false);
				blockEntity.triggerStockCheck();
			}
		});
		return 1;
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		if (maxAmount <= 0) return 0;
		if (blockEntity.animationTicks != 0)
			return 0;
		ItemStack box = blockEntity.heldBox;
		if (box.isEmpty())
			return 0;
		if (!resource.isBlank() && !resource.matches(box))
			return 0;
		transaction.addOuterCloseCallback(result -> {
			if (result.wasCommitted()) {
				setStackInSlot(0, ItemStack.EMPTY);
			}
		});
		return 1;
	}

	@Override
	public int getSlotLimit(int slot) {
		return 1;
	}

}
