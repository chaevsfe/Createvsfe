package com.simibubi.create.content.logistics.stockTicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.stockTicker.LogisticalStockRequestPacket;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.utility.NBTHelper;
import io.github.fabricators_of_create.porting_lib_ufo.transfer.item.ItemStackHandler;

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
	@Environment(EnvType.CLIENT)
	private int ticksSinceLastUpdate = Integer.MAX_VALUE;

	// Category configuration for the Stock Keeper UI
	public String previouslyUsedAddress = "";
	public List<ItemStack> categories = new ArrayList<>();
	public Map<UUID, List<Integer>> hiddenCategoriesByPlayer = new HashMap<>();

	// Received payments inventory (27 slots) — for CC peripheral and future shop integration
	protected SmartInventory receivedPayments;

	public StockTickerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		receivedPayments = new SmartInventory(27, this, 64, false);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);
	}

	@Override
	public void lazyTick() {
		super.lazyTick();
		if (level != null && level.isClientSide && ticksSinceLastUpdate < Integer.MAX_VALUE - 1)
			ticksSinceLastUpdate++;
	}

	public ItemStackHandler getReceivedPaymentsHandler() {
		return receivedPayments;
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
		if (lastPacket)
			ticksSinceLastUpdate = 0;
	}

	@Environment(EnvType.CLIENT)
	public int getTicksSinceLastUpdate() {
		return ticksSinceLastUpdate;
	}

	@Environment(EnvType.CLIENT)
	public void refreshClientStockSnapshot() {
		ticksSinceLastUpdate = 0;
		AllPackets.getChannel().sendToServer(new LogisticalStockRequestPacket(worldPosition));
	}

	@Environment(EnvType.CLIENT)
	public InventorySummary getLastClientsideStockSnapshotAsSummary() {
		if (clientStockItems == null || !clientStockComplete)
			return null;
		InventorySummary summary = new InventorySummary();
		for (BigItemStack entry : clientStockItems)
			summary.add(entry.stack, entry.count);
		return summary;
	}

	public InventorySummary getRecentSummary() {
		// Server-side summary — full LogisticsNetwork not yet ported; returns null
		return null;
	}

	@Override
	protected void write(CompoundTag compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		compound.putString("PreviousAddress", previouslyUsedAddress);
		compound.put("ReceivedPayments", receivedPayments.serializeNBT());
		HolderLookup.Provider registries = level != null ? level.registryAccess() : net.minecraft.core.RegistryAccess.EMPTY;
		if (!categories.isEmpty()) {
			ListTag list = new ListTag();
			for (ItemStack stack : categories)
				list.add(stack.saveOptional(registries));
			compound.put("Categories", list);
		}
		if (!hiddenCategoriesByPlayer.isEmpty()) {
			compound.put("HiddenCategories", NBTHelper.writeCompoundList(hiddenCategoriesByPlayer.entrySet(), e -> {
				CompoundTag c = new CompoundTag();
				c.putUUID("Id", e.getKey());
				c.putIntArray("Indices", e.getValue());
				return c;
			}));
		}
	}

	@Override
	protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(compound, registries, clientPacket);
		previouslyUsedAddress = compound.getString("PreviousAddress");
		receivedPayments.deserializeNBT(compound.getCompound("ReceivedPayments"));
		categories.clear();
		if (compound.contains("Categories", Tag.TAG_LIST)) {
			ListTag list = compound.getList("Categories", Tag.TAG_COMPOUND);
			for (int i = 0; i < list.size(); i++)
				categories.add(ItemStack.parseOptional(registries, list.getCompound(i)));
		}
		hiddenCategoriesByPlayer.clear();
		if (compound.contains("HiddenCategories", Tag.TAG_LIST)) {
			NBTHelper.readCompoundList(compound.getList("HiddenCategories", Tag.TAG_COMPOUND),
				c -> hiddenCategoriesByPlayer.put(c.getUUID("Id"),
					IntStream.of(c.getIntArray("Indices")).boxed().collect(java.util.stream.Collectors.toList())));
		}
	}

	public void playEffect() {
		com.simibubi.create.AllSoundEvents.STOCK_LINK.playAt(level, worldPosition, 1.0f, 1.0f, false);
		// WiFiParticle effect deferred until particle system is ported
	}
}
