package com.simibubi.create.content.logistics.stockTicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
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

	// Category configuration for the Stock Keeper UI
	public List<ItemStack> categories = new ArrayList<>();
	public Map<UUID, List<Integer>> hiddenCategoriesByPlayer = new HashMap<>();

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

	@Override
	protected void write(CompoundTag compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		if (!categories.isEmpty()) {
			ListTag list = new ListTag();
			HolderLookup.Provider registries = level != null ? level.registryAccess() : net.minecraft.core.RegistryAccess.EMPTY;
			for (ItemStack stack : categories)
				list.add(stack.saveOptional(registries));
			compound.put("Categories", list);
		}
	}

	@Override
	protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(compound, registries, clientPacket);
		categories.clear();
		if (compound.contains("Categories", Tag.TAG_LIST)) {
			ListTag list = compound.getList("Categories", Tag.TAG_COMPOUND);
			for (int i = 0; i < list.size(); i++)
				categories.add(ItemStack.parseOptional(registries, list.getCompound(i)));
		}
	}
}
