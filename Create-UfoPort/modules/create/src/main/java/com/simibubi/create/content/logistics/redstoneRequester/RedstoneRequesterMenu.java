package com.simibubi.create.content.logistics.redstoneRequester;

import java.util.ArrayList;
import java.util.List;

import com.simibubi.create.AllMenuTypes;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.stockTicker.PackageOrder;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import com.simibubi.create.foundation.gui.menu.GhostItemMenu;

import io.github.fabricators_of_create.porting_lib_ufo.transfer.item.ItemStackHandler;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;

public class RedstoneRequesterMenu extends GhostItemMenu<RedstoneRequesterBlockEntity> {

	public RedstoneRequesterMenu(MenuType<?> type, int id, Inventory inv, RedstoneRequesterBlockEntity contentHolder) {
		super(type, id, inv, contentHolder);
	}

	public RedstoneRequesterMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
		super(type, id, inv, extraData);
	}

	public static RedstoneRequesterMenu create(int id, Inventory inv, RedstoneRequesterBlockEntity be) {
		return new RedstoneRequesterMenu(AllMenuTypes.REDSTONE_REQUESTER.get(), id, inv, be);
	}

	@Override
	protected ItemStackHandler createGhostInventory() {
		return new ItemStackHandler(9);
	}

	@Override
	protected boolean allowRepeats() {
		return true;
	}

	@Override
	protected RedstoneRequesterBlockEntity createOnClient(RegistryFriendlyByteBuf extraData) {
		BlockPos pos = extraData.readBlockPos();
		BlockEntity be = Minecraft.getInstance().level.getBlockEntity(pos);
		if (be instanceof RedstoneRequesterBlockEntity rrbe)
			return rrbe;
		return null;
	}

	@Override
	protected void addSlots() {
		addPlayerSlots(5, 142);
		for (int i = 0; i < 9; i++)
			addSlot(new io.github.fabricators_of_create.porting_lib_ufo.transfer.item.SlotItemHandler(
				ghostInventory, i, 27 + 20 * i, 28));
	}

	@Override
	protected void saveData(RedstoneRequesterBlockEntity contentHolder) {
		List<BigItemStack> stacks = new ArrayList<>();
		for (int i = 0; i < ghostInventory.getSlotCount(); i++) {
			ItemStack stack = ghostInventory.getStackInSlot(i);
			if (!stack.isEmpty())
				stacks.add(new BigItemStack(stack.copyWithCount(1), Math.max(1, stack.getCount())));
		}
		contentHolder.encodedRequest = stacks.isEmpty()
			? PackageOrderWithCrafts.empty()
			: PackageOrderWithCrafts.simple(stacks);
		contentHolder.notifyUpdate();
	}
}
