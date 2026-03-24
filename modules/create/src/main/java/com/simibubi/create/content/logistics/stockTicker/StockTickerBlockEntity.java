package com.simibubi.create.content.logistics.stockTicker;

import java.util.List;

import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Stock Ticker block entity — the central hub of the High Logistics network.
 * Tracks item availability across connected packagers and allows players to
 * request packages via the Stock Keeper UI.
 *
 * Minimal stub for Phase 3 foundation — full implementation will be expanded
 * when the Stock Keeper GUI, Packager integration, and inventory summary
 * systems are fully ported.
 */
public class StockTickerBlockEntity extends StockCheckingBlockEntity {

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
}
