package com.simibubi.create.content.logistics.stockTicker;

import java.util.ArrayList;
import java.util.List;

import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class StockKeeperCategoryHidingPacket extends BlockEntityConfigurationPacket<StockTickerBlockEntity> {

	private List<Integer> indices;

	public StockKeeperCategoryHidingPacket(BlockPos pos, List<Integer> indices) {
		super(pos);
		this.indices = indices;
	}

	public StockKeeperCategoryHidingPacket(RegistryFriendlyByteBuf buffer) {
		super(buffer);
	}

	@Override
	protected void writeSettings(RegistryFriendlyByteBuf buffer) {
		buffer.writeVarInt(indices.size());
		for (int idx : indices)
			buffer.writeVarInt(idx);
	}

	@Override
	protected void readSettings(RegistryFriendlyByteBuf buffer) {
		int count = buffer.readVarInt();
		indices = new ArrayList<>(count);
		for (int i = 0; i < count; i++)
			indices.add(buffer.readVarInt());
	}

	@Override
	protected void applySettings(ServerPlayer player, StockTickerBlockEntity be) {
		if (indices.isEmpty()) {
			be.hiddenCategoriesByPlayer.remove(player.getUUID());
		} else {
			be.hiddenCategoriesByPlayer.put(player.getUUID(), indices);
			be.notifyUpdate();
		}
	}

	@Override
	protected void applySettings(StockTickerBlockEntity be) {}
}
