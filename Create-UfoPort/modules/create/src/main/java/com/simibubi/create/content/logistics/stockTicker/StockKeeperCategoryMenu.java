package com.simibubi.create.content.logistics.stockTicker;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllMenuTypes;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.foundation.gui.menu.MenuBase;

import io.github.fabricators_of_create.porting_lib_ufo.transfer.item.ItemStackHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class StockKeeperCategoryMenu extends MenuBase<StockTickerBlockEntity> {

	public boolean slotsActive = true;
	public ItemStackHandler proxyInventory;

	public StockKeeperCategoryMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
		super(type, id, inv, extraData);
	}

	public static AbstractContainerMenu create(int pContainerId, Inventory pPlayerInventory,
		StockTickerBlockEntity stockTickerBlockEntity) {
		return new StockKeeperCategoryMenu(AllMenuTypes.STOCK_KEEPER_CATEGORY.get(), pContainerId, pPlayerInventory,
			stockTickerBlockEntity);
	}

	public StockKeeperCategoryMenu(MenuType<?> type, int id, Inventory inv, StockTickerBlockEntity contentHolder) {
		super(type, id, inv, contentHolder);
	}

	@Override
	protected void initAndReadInventory(StockTickerBlockEntity contentHolder) {
		proxyInventory = new ItemStackHandler(1);
	}

	@Override
	protected StockTickerBlockEntity createOnClient(RegistryFriendlyByteBuf extraData) {
		BlockPos blockPos = extraData.readBlockPos();
		return AllBlocks.STOCK_TICKER.get()
			.getBlockEntity(Minecraft.getInstance().level, blockPos);
	}

	@Override
	protected void addSlots() {
		// Proxy slot for filter insertion — uses InactiveSlot wrapper
		addSlot(new InactiveFilterSlot(proxyInventory, 0, 16, 24));
		addPlayerSlots(18, 106);
	}

	// Note: NeoForge uses createPlayerSlot() override — not available in UfoPort's MenuBase.
	// Player slots use standard Slot class; slotsActive controls category screen slot visibility.

	@Override
	protected void saveData(StockTickerBlockEntity contentHolder) {}

	@Override
	public boolean stillValid(Player player) {
		return !contentHolder.isRemoved() && player.position()
			.closerThan(Vec3.atCenterOf(contentHolder.getBlockPos()),
				player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE) + 4);
	}

	class InactiveSlot extends Slot {
		public InactiveSlot(Container pContainer, int pIndex, int pX, int pY) {
			super(pContainer, pIndex, pX, pY);
		}

		@Override
		public boolean isActive() {
			return slotsActive;
		}
	}

	/**
	 * Filter-only slot backed by ItemStackHandler.
	 * Fabric adaptation: uses ItemStackHandler directly instead of NeoForge SlotItemHandler.
	 */
	class InactiveFilterSlot extends Slot {
		private final ItemStackHandler handler;
		private final int handlerIndex;

		public InactiveFilterSlot(ItemStackHandler handler, int index, int x, int y) {
			super(new SimpleSlotContainer(handler, index), 0, x, y);
			this.handler = handler;
			this.handlerIndex = index;
		}

		@Override
		public boolean mayPlace(ItemStack stack) {
			return stack.isEmpty() || stack.getItem() instanceof FilterItem;
		}

		@Override
		public boolean isActive() {
			return slotsActive;
		}
	}

	/**
	 * Simple Container adapter for a single slot in an ItemStackHandler.
	 */
	private static class SimpleSlotContainer implements Container {
		private final ItemStackHandler handler;
		private final int slot;

		SimpleSlotContainer(ItemStackHandler handler, int slot) {
			this.handler = handler;
			this.slot = slot;
		}

		@Override public int getContainerSize() { return 1; }
		@Override public boolean isEmpty() { return handler.getStackInSlot(slot).isEmpty(); }
		@Override public ItemStack getItem(int index) { return handler.getStackInSlot(slot); }
		@Override public ItemStack removeItem(int index, int count) {
			ItemStack stack = handler.getStackInSlot(slot);
			if (stack.isEmpty()) return ItemStack.EMPTY;
			ItemStack result = stack.split(count);
			if (stack.isEmpty()) handler.setStackInSlot(slot, ItemStack.EMPTY);
			setChanged();
			return result;
		}
		@Override public ItemStack removeItemNoUpdate(int index) {
			ItemStack stack = handler.getStackInSlot(slot);
			handler.setStackInSlot(slot, ItemStack.EMPTY);
			return stack;
		}
		@Override public void setItem(int index, ItemStack stack) { handler.setStackInSlot(slot, stack); setChanged(); }
		@Override public void setChanged() {}
		@Override public boolean stillValid(Player player) { return true; }
		@Override public void clearContent() { handler.setStackInSlot(slot, ItemStack.EMPTY); }
	}

	@Override
	public ItemStack quickMoveStack(Player pPlayer, int index) {
		Slot clickedSlot = getSlot(index);
		if (!clickedSlot.hasItem())
			return ItemStack.EMPTY;

		ItemStack stack = clickedSlot.getItem();
		int size = 1;
		boolean success = false;
		if (index < size) {
			success = !moveItemStackTo(stack, size, slots.size(), true);
		} else
			success = !moveItemStackTo(stack, 0, size, false);

		return success ? ItemStack.EMPTY : stack;
	}

}
