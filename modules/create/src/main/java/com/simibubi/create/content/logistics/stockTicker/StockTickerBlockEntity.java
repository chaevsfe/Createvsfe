package com.simibubi.create.content.logistics.stockTicker;

import java.util.ArrayList;
import java.util.List;

import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Stock Ticker block entity — the central hub of the High Logistics network.
 * Tracks item availability across connected packagers and allows players to
 * request packages via the Stock Keeper UI.
 */
public class StockTickerBlockEntity extends StockCheckingBlockEntity {

	// Client-side stock data received from server
	@Environment(EnvType.CLIENT)
	public List<BigItemStack> clientStockItems;
	@Environment(EnvType.CLIENT)
	public boolean clientStockComplete;

	public StockTickerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);
	}

	public boolean isKeeperPresent() {
		// Stub — keeper NPCs not yet ported
		return false;
	}

	/**
	 * Called on the client when a stock response packet arrives.
	 * Packets may arrive in chunks — lastPacket indicates the final chunk.
	 */
	@Environment(EnvType.CLIENT)
	public void receiveStockPacket(List<BigItemStack> items, boolean lastPacket) {
		if (clientStockItems == null || clientStockComplete)
			clientStockItems = new ArrayList<>();
		clientStockItems.addAll(items);
		clientStockComplete = lastPacket;
	}
}
