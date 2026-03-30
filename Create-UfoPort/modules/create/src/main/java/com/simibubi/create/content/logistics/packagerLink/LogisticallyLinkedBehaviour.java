package com.simibubi.create.content.logistics.packagerLink;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;

import com.simibubi.create.content.logistics.packager.IdentifiedInventory;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.content.logistics.packager.PackagingRequest;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import com.simibubi.create.foundation.utility.Pair;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import io.github.fabricators_of_create.porting_lib_ufo.transfer.item.ItemStackHandler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

/**
 * Behaviour that links a block entity to the logistics network via a frequency ID.
 * Manages link tracking, redstone power, and permission checks for logistics network members.
 */
public class LogisticallyLinkedBehaviour extends BlockEntityBehaviour {

	public static final BehaviourType<LogisticallyLinkedBehaviour> TYPE = new BehaviourType<>();

	// Simplified link tracking (replaces NeoForge's Guava Cache-based system)
	private static final Map<UUID, Map<Integer, WeakReference<LogisticallyLinkedBehaviour>>> LINKS = new ConcurrentHashMap<>();
	private static final Map<UUID, Map<Integer, WeakReference<LogisticallyLinkedBehaviour>>> CLIENT_LINKS = new ConcurrentHashMap<>();
	private static int LINK_ID_GENERATOR = 0;

	public int redstonePower;
	public UUID freqId;
	public int linkId;
	private boolean global;

	public static enum RequestType {
		RESTOCK, REDSTONE, PLAYER
	}

	public LogisticallyLinkedBehaviour(SmartBlockEntity be, boolean global) {
		super(be);
		this.global = global;
		this.linkId = LINK_ID_GENERATOR++;
		this.freqId = UUID.randomUUID();
	}

	public static Collection<LogisticallyLinkedBehaviour> getAllPresent(UUID freq, boolean sortByPriority) {
		return getAllPresent(freq, sortByPriority, false);
	}

	public static Collection<LogisticallyLinkedBehaviour> getAllPresent(UUID freq, boolean sortByPriority, boolean clientSide) {
		Map<Integer, WeakReference<LogisticallyLinkedBehaviour>> cache =
			(clientSide ? CLIENT_LINKS : LINKS).get(freq);
		if (cache == null)
			return Collections.emptyList();
		Stream<LogisticallyLinkedBehaviour> stream = cache.values().stream()
			.map(WeakReference::get)
			.filter(b -> b != null && !b.blockEntity.isRemoved());
		return stream.toList();
	}

	@Override
	public void initialize() {
		super.initialize();
		Map<UUID, Map<Integer, WeakReference<LogisticallyLinkedBehaviour>>> map =
			blockEntity.getLevel().isClientSide ? CLIENT_LINKS : LINKS;
		map.computeIfAbsent(freqId, $ -> new ConcurrentHashMap<>())
			.put(linkId, new WeakReference<>(this));
	}

	@Override
	public void destroy() {
		super.destroy();
		Map<UUID, Map<Integer, WeakReference<LogisticallyLinkedBehaviour>>> map =
			blockEntity.getLevel().isClientSide ? CLIENT_LINKS : LINKS;
		Map<Integer, WeakReference<LogisticallyLinkedBehaviour>> cache = map.get(freqId);
		if (cache != null) {
			cache.remove(linkId);
			if (cache.isEmpty())
				map.remove(freqId);
		}
	}

	@Override
	public BehaviourType<?> getType() {
		return TYPE;
	}

	@Override
	public void read(CompoundTag nbt, boolean clientPacket) {
		super.read(nbt, clientPacket);
		if (nbt.contains("FreqId"))
			freqId = nbt.getUUID("FreqId");
		redstonePower = nbt.getInt("RedstonePower");
	}

	@Override
	public void write(CompoundTag nbt, boolean clientPacket) {
		super.write(nbt, clientPacket);
		if (freqId != null)
			nbt.putUUID("FreqId", freqId);
		nbt.putInt("RedstonePower", redstonePower);
	}

	/**
	 * Check if a player may administrate this logistics network (lock/unlock, configure).
	 * Stub — always returns true until full permissions system is ported.
	 */
	public boolean mayAdministrate(Player player) {
		return true;
	}

	/**
	 * Check if a player may interact with this logistics network (place orders).
	 * Stub — always returns true until full permissions system is ported.
	 */
	public boolean mayInteract(Player player) {
		return true;
	}

	/**
	 * Check interaction permission and send status message if denied.
	 * Stub — always returns true until full permissions system is ported.
	 */
	public boolean mayInteractMessage(Player player) {
		return true;
	}

	/**
	 * Called when redstone power level changes for this link.
	 */
	public void redstonePowerChanged(int power) {
		this.redstonePower = power;
	}

	public @Nullable Pair<PackagerBlockEntity, PackagingRequest> processRequest(net.minecraft.world.item.ItemStack stack,
		int amount, String address, int linkIndex, MutableBoolean finalLink, int orderId,
		@Nullable PackageOrderWithCrafts context, @Nullable IdentifiedInventory ignoredHandler) {
		if (blockEntity instanceof PackagerLinkBlockEntity plbe)
			return plbe.processRequest(stack, amount, address, linkIndex, finalLink, orderId, context, ignoredHandler);
		return null;
	}

	/**
	 * Get the inventory summary for items available through this link.
	 * Delegates to PackagerLinkBlockEntity for packager links.
	 */
	public InventorySummary getSummary(IdentifiedInventory ignoredHandler) {
		if (blockEntity instanceof PackagerLinkBlockEntity plbe)
			return plbe.fetchSummaryFromPackager(ignoredHandler);
		return InventorySummary.EMPTY;
	}

	/**
	 * Deduct items in a delivered package from the accurate summary cache,
	 * so repeated queries reflect current state without waiting for next refresh.
	 */
	public void deductFromAccurateSummary(ItemStackHandler packageContents) {
		InventorySummary summary = LogisticsManager.ACCURATE_SUMMARIES.getIfPresent(freqId);
		if (summary == null)
			return;
		int slotCount = packageContents.getSlots().size();
		for (int i = 0; i < slotCount; i++) {
			net.minecraft.world.item.ItemStack orderedStack = packageContents.getStackInSlot(i);
			if (orderedStack.isEmpty())
				continue;
			int current = summary.getCountOf(orderedStack);
			summary.add(orderedStack, -Math.min(current, orderedStack.getCount()));
		}
	}
}
