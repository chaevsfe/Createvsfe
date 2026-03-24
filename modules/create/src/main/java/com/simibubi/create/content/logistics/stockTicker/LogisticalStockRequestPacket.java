package com.simibubi.create.content.logistics.stockTicker;

import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class LogisticalStockRequestPacket extends BlockEntityConfigurationPacket<StockCheckingBlockEntity> {

	public LogisticalStockRequestPacket(BlockPos pos) {
		super(pos);
	}

	public LogisticalStockRequestPacket(RegistryFriendlyByteBuf buffer) {
		super(buffer);
	}

	@Override
	protected void writeSettings(RegistryFriendlyByteBuf buffer) {
		// No additional data — just the block position (written by super)
	}

	@Override
	protected void readSettings(RegistryFriendlyByteBuf buffer) {
		// No additional data — just the block position (read by super)
	}

	@Override
	protected void applySettings(ServerPlayer player, StockCheckingBlockEntity be) {
		be.getRecentSummary()
			.divideAndSendTo(player, pos);
	}

	@Override
	protected void applySettings(StockCheckingBlockEntity be) {
		// Not used — player-dependent version is called instead
	}

	@Override
	protected int maxRange() {
		return 4096;
	}
}
