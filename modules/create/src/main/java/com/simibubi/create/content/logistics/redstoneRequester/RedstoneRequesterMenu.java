package com.simibubi.create.content.logistics.redstoneRequester;

import com.simibubi.create.AllMenuTypes;
import com.simibubi.create.foundation.gui.menu.GhostItemMenu;

import io.github.fabricators_of_create.porting_lib_ufo.transfer.item.ItemStackHandler;
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
		// Ghost inventory slots for requested items
		for (int row = 0; row < 3; row++)
			for (int col = 0; col < 3; col++)
				addSlot(new io.github.fabricators_of_create.porting_lib_ufo.transfer.item.SlotItemHandler(
					ghostInventory, row * 3 + col, 19 + col * 18, 16 + row * 18));

		addPlayerSlots(12, 90);
	}

	@Override
	protected void saveData(RedstoneRequesterBlockEntity contentHolder) {
		// TODO: save ghost items to request data
	}
}
