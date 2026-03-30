package com.simibubi.create.content.logistics.stockTicker;

import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class StockKeeperCategoryRefundPacket extends BlockEntityConfigurationPacket<StockTickerBlockEntity> {

	private ItemStack filter;

	public StockKeeperCategoryRefundPacket(BlockPos pos, ItemStack filter) {
		super(pos);
		this.filter = filter;
	}

	public StockKeeperCategoryRefundPacket(RegistryFriendlyByteBuf buffer) {
		super(buffer);
	}

	@Override
	protected void writeSettings(RegistryFriendlyByteBuf buffer) {
		ItemStack.STREAM_CODEC.encode(buffer, filter);
	}

	@Override
	protected void readSettings(RegistryFriendlyByteBuf buffer) {
		filter = ItemStack.STREAM_CODEC.decode(buffer);
	}

	@Override
	protected void applySettings(ServerPlayer player, StockTickerBlockEntity be) {
		if (!filter.isEmpty() && filter.getItem() instanceof FilterItem)
			player.getInventory()
				.placeItemBackInInventory(filter);
	}

	@Override
	protected void applySettings(StockTickerBlockEntity be) {}
}
