package com.simibubi.create.content.logistics.stockTicker;

import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class StockKeeperLockPacket extends BlockEntityConfigurationPacket<StockTickerBlockEntity> {

	private boolean lock;

	public StockKeeperLockPacket(BlockPos pos, boolean lock) {
		super(pos);
		this.lock = lock;
	}

	public StockKeeperLockPacket(RegistryFriendlyByteBuf buffer) {
		super(buffer);
	}

	@Override
	protected void writeSettings(RegistryFriendlyByteBuf buffer) {
		buffer.writeBoolean(lock);
	}

	@Override
	protected void readSettings(RegistryFriendlyByteBuf buffer) {
		lock = buffer.readBoolean();
	}

	@Override
	protected void applySettings(ServerPlayer player, StockTickerBlockEntity be) {
		if (!be.behaviour.mayAdministrate(player))
			return;
		// Stub: LogisticsNetwork lock/unlock deferred until full network system
		// In full implementation: Create.LOGISTICS.logisticsNetworks.get(be.behaviour.freqId).locked = lock
	}

	@Override
	protected void applySettings(StockTickerBlockEntity be) {}
}
