package com.simibubi.create.content.logistics.packager.repackager;

import java.util.List;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.crate.BottomlessItemHandler;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.content.logistics.packager.PackagerItemHandler;
import com.simibubi.create.content.logistics.packager.PackagingRequest;

import io.github.fabricators_of_create.porting_lib_ufo.transfer.item.SlottedStackStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RepackagerBlockEntity extends PackagerBlockEntity {

	public PackageRepackageHelper repackageHelper;

	public RepackagerBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
		super(typeIn, pos, state);
		repackageHelper = new PackageRepackageHelper();
	}

	public static void registerItemStorage() {
		ItemStorage.SIDED.registerForBlockEntity(
			(be, dir) -> be.inventory,
			AllBlockEntityTypes.REPACKAGER.get()
		);
	}

	@Override
	public boolean unwrapBox(ItemStack box, boolean simulate) {
		if (animationTicks > 0)
			return false;

		Storage<ItemVariant> targetInv = targetInventory.getInventory();
		if (targetInv == null || targetInv instanceof PackagerItemHandler)
			return false;

		boolean targetIsCreativeCrate = targetInv instanceof BottomlessItemHandler;

		if (!(targetInv instanceof SlottedStackStorage slotted))
			return false;

		boolean anySpace = false;

		for (int slot = 0; slot < slotted.getSlotCount(); slot++) {
			// Check if there's space in this slot
			ItemStack inSlot = slotted.getStackInSlot(slot);
			if (inSlot.isEmpty()) {
				anySpace = true;
				if (!simulate) {
					// Insert the box
					try (Transaction tx = Transaction.openOuter()) {
						slotted.insertSlot(slot, ItemVariant.of(box), 1, tx);
						tx.commit();
					}
				}
				break;
			} else if (ItemStack.isSameItemSameComponents(inSlot, box)
				&& inSlot.getCount() < inSlot.getMaxStackSize()) {
				anySpace = true;
				if (!simulate) {
					try (Transaction tx = Transaction.openOuter()) {
						slotted.insertSlot(slot, ItemVariant.of(box), 1, tx);
						tx.commit();
					}
				}
				break;
			}
		}

		if (!targetIsCreativeCrate && !anySpace)
			return false;
		if (simulate)
			return true;

		computerBehaviour.prepareComputerEvent(new com.simibubi.create.compat.computercraft.events.PackageEvent(box, "package_received"));
		previouslyUnwrapped = box;
		animationInward = true;
		animationTicks = CYCLE;
		notifyUpdate();
		return true;
	}

	@Override
	public void recheckIfLinksPresent() {
	}

	@Override
	public boolean redstoneModeActive() {
		return true;
	}

	@Override
	public void attemptToSend(List<PackagingRequest> queuedRequests) {
		if (!heldBox.isEmpty() || animationTicks != 0 || buttonCooldown > 0)
			return;
		if (!queuedExitingPackages.isEmpty())
			return;

		Storage<ItemVariant> targetInv = targetInventory.getInventory();
		if (targetInv == null || targetInv instanceof PackagerItemHandler)
			return;

		if (!(targetInv instanceof SlottedStackStorage slotted))
			return;

		attemptToRepackage(slotted);
		if (heldBox.isEmpty())
			return;

		updateSignAddress();
		if (!signBasedAddress.isBlank())
			PackageItem.addAddress(heldBox, signBasedAddress);
	}

	protected void attemptToRepackage(SlottedStackStorage targetInv) {
		repackageHelper.clear();
		int completedOrderId = -1;

		for (int slot = 0; slot < targetInv.getSlotCount(); slot++) {
			ItemStack inSlot = targetInv.getStackInSlot(slot);
			if (inSlot.isEmpty() || !PackageItem.isPackage(inSlot))
				continue;

			// Simulate extracting 1 item
			ItemStack extracted = inSlot.copyWithCount(1);

			if (!repackageHelper.isFragmented(extracted)) {
				// Actually extract
				try (Transaction tx = Transaction.openOuter()) {
					targetInv.extractSlot(slot, ItemVariant.of(inSlot), 1, tx);
					tx.commit();
				}
				heldBox = extracted.copy();
				animationInward = false;
				animationTicks = CYCLE;
				notifyUpdate();
				return;
			}

			completedOrderId = repackageHelper.addPackageFragment(extracted);
			if (completedOrderId != -1)
				break;
		}

		if (completedOrderId == -1)
			return;

		List<BigItemStack> boxesToExport = repackageHelper.repack(completedOrderId, level.getRandom());

		for (int slot = 0; slot < targetInv.getSlotCount(); slot++) {
			ItemStack inSlot = targetInv.getStackInSlot(slot);
			if (inSlot.isEmpty() || !PackageItem.isPackage(inSlot))
				continue;
			if (PackageItem.getOrderId(inSlot) != completedOrderId)
				continue;
			try (Transaction tx = Transaction.openOuter()) {
				targetInv.extractSlot(slot, ItemVariant.of(inSlot), 1, tx);
				tx.commit();
			}
		}

		if (boxesToExport.isEmpty())
			return;

		if (computerBehaviour.hasAttachedComputer()) {
			for (BigItemStack box : boxesToExport) {
				computerBehaviour.prepareComputerEvent(new com.simibubi.create.compat.computercraft.events.RepackageEvent(box.stack, box.count));
			}
		}

		queuedExitingPackages.addAll(boxesToExport);
		notifyUpdate();
	}

}
