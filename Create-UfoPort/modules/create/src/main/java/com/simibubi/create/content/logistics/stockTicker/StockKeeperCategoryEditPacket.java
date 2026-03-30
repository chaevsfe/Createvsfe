package com.simibubi.create.content.logistics.stockTicker;

import java.util.ArrayList;
import java.util.List;

import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class StockKeeperCategoryEditPacket extends BlockEntityConfigurationPacket<StockTickerBlockEntity> {

	private List<ItemStack> schedule;

	public StockKeeperCategoryEditPacket(BlockPos pos, List<ItemStack> schedule) {
		super(pos);
		this.schedule = schedule;
	}

	public StockKeeperCategoryEditPacket(RegistryFriendlyByteBuf buffer) {
		super(buffer);
	}

	@Override
	protected void writeSettings(RegistryFriendlyByteBuf buffer) {
		buffer.writeVarInt(schedule.size());
		for (ItemStack stack : schedule)
			ItemStack.STREAM_CODEC.encode(buffer, stack);
	}

	@Override
	protected void readSettings(RegistryFriendlyByteBuf buffer) {
		int count = buffer.readVarInt();
		schedule = new ArrayList<>(count);
		for (int i = 0; i < count; i++)
			schedule.add(ItemStack.STREAM_CODEC.decode(buffer));
	}

	@Override
	protected void applySettings(ServerPlayer player, StockTickerBlockEntity be) {
		be.categories = schedule;
		be.notifyUpdate();
	}

	@Override
	protected void applySettings(StockTickerBlockEntity be) {}
}
