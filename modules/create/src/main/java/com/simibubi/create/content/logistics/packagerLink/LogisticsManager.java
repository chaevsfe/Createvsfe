package com.simibubi.create.content.logistics.packagerLink;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.simibubi.create.api.packager.InventoryIdentifier;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.packager.IdentifiedInventory;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.content.logistics.packager.PackagingRequest;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour.RequestType;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import com.simibubi.create.foundation.utility.Pair;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Manages logistics network operations — inventory summaries and package requests.
 * Uses a simple tick-based cache to avoid recomputing network state every query.
 */
public class LogisticsManager {

	private static Random r = new Random();

	/** Simple tick-based cache entry for inventory summaries. */
	private static class CacheEntry {
		final InventorySummary summary;
		final long computedAtTick;

		CacheEntry(InventorySummary summary, long tick) {
			this.summary = summary;
			this.computedAtTick = tick;
		}
	}

	// Cache accurate summaries for 1 tick, regular summaries for 20 ticks
	private static final Map<UUID, CacheEntry> ACCURATE_CACHE = new ConcurrentHashMap<>();
	private static final Map<UUID, CacheEntry> CACHE = new ConcurrentHashMap<>();

	/** Public accessor for deductFromAccurateSummary calls. */
	public static @Nullable InventorySummary ACCURATE_SUMMARIES_GET(UUID freqId) {
		CacheEntry entry = ACCURATE_CACHE.get(freqId);
		return entry != null ? entry.summary : null;
	}

	/** Alias used by LogisticallyLinkedBehaviour.deductFromAccurateSummary. */
	public static final LogisticsManager.SummaryCache ACCURATE_SUMMARIES = new SummaryCache(ACCURATE_CACHE, 1);
	public static final LogisticsManager.SummaryCache SUMMARIES = new SummaryCache(CACHE, 20);

	/** Thin cache wrapper for Guava Cache-compatible API surface. */
	public static class SummaryCache {
		private final Map<UUID, CacheEntry> map;
		private final int ttlTicks;

		SummaryCache(Map<UUID, CacheEntry> map, int ttlTicks) {
			this.map = map;
			this.ttlTicks = ttlTicks;
		}

		public @Nullable InventorySummary getIfPresent(UUID key) {
			CacheEntry entry = map.get(key);
			if (entry == null)
				return null;
			return entry.summary;
		}
	}

	private static long currentTick = 0;

	public static void tick(Level level) {
		if (!level.isClientSide)
			currentTick = level.getGameTime();
	}

	public static InventorySummary getSummaryOfNetwork(UUID freqId, boolean accurate) {
		Map<UUID, CacheEntry> cache = accurate ? ACCURATE_CACHE : CACHE;
		int ttl = accurate ? 1 : 20;
		CacheEntry existing = cache.get(freqId);
		if (existing != null && (currentTick - existing.computedAtTick) < ttl)
			return existing.summary;
		InventorySummary fresh = createSummaryOfNetwork(freqId);
		cache.put(freqId, new CacheEntry(fresh, currentTick));
		return fresh;
	}

	private static InventorySummary createSummaryOfNetwork(UUID freqId) {
		InventorySummary summaryOfLinks = new InventorySummary();
		Set<InventoryIdentifier> processedInventories = new HashSet<>();
		for (LogisticallyLinkedBehaviour link : LogisticallyLinkedBehaviour.getAllPresent(freqId, false)) {

			// Skip inventories already presented by other links
			InventoryIdentifier currentInventoryId = getInventoryIdentifierFromLink(link);
			if (currentInventoryId != null && !processedInventories.add(currentInventoryId))
				continue;

			InventorySummary summary = link.getSummary(null);
			if (summary != InventorySummary.EMPTY) {
				summaryOfLinks.contributingLinks++;
				summaryOfLinks.add(summary);
			}
		}

		return summaryOfLinks;
	}

	public static int getStockOf(UUID freqId, ItemStack stack, @Nullable IdentifiedInventory ignoredHandler) {
		int sum = 0;
		for (LogisticallyLinkedBehaviour link : LogisticallyLinkedBehaviour.getAllPresent(freqId, false))
			sum += link.getSummary(ignoredHandler)
				.getCountOf(stack);
		return sum;
	}

	public static boolean broadcastPackageRequest(UUID freqId, RequestType type, PackageOrderWithCrafts order,
			@Nullable IdentifiedInventory ignoredHandler, String address) {
		if (order.isEmpty())
			return false;

		Multimap<PackagerBlockEntity, PackagingRequest> requests = findPackagersForRequest(freqId, order,
			ignoredHandler, address);

		// Check if packagers have accumulated too many packages already
		for (PackagerBlockEntity packager : requests.keySet())
			if (packager.isTooBusyFor(type))
				return false;

		// Actually perform package creation
		performPackageRequests(requests);
		return true;
	}

	public static Multimap<PackagerBlockEntity, PackagingRequest> findPackagersForRequest(UUID freqId,
			PackageOrderWithCrafts order, @Nullable IdentifiedInventory ignoredHandler, String address) {
		List<BigItemStack> stacks = new ArrayList<>();

		for (BigItemStack stack : order.stacks())
			if (!stack.stack.isEmpty() && stack.count > 0)
				stacks.add(stack);

		Multimap<PackagerBlockEntity, PackagingRequest> requests = HashMultimap.create();

		// Packages need to track their index and successors for successful defrag
		Iterable<LogisticallyLinkedBehaviour> allAvailableLinks = LogisticallyLinkedBehaviour.getAllPresent(freqId,
			true);

		// Group links by InventoryIdentifier and randomly select one from each group
		Map<InventoryIdentifier, List<LogisticallyLinkedBehaviour>> linksByInventory = new HashMap<>();
		List<LogisticallyLinkedBehaviour> availableLinks = new ArrayList<>();

		// Group links by their inventory identifier
		for (LogisticallyLinkedBehaviour link : allAvailableLinks) {
			InventoryIdentifier inventoryId = getInventoryIdentifierFromLink(link);
			if (inventoryId != null) {
				linksByInventory.computeIfAbsent(inventoryId, k -> new ArrayList<>()).add(link);
			} else {
				// Links without inventory identifier are added directly
				availableLinks.add(link);
			}
		}

		// Randomly select one link from each inventory group
		for (List<LogisticallyLinkedBehaviour> linkGroup : linksByInventory.values()) {
			if (!linkGroup.isEmpty()) {
				LogisticallyLinkedBehaviour selectedLink = linkGroup.get(r.nextInt(linkGroup.size()));
				availableLinks.add(selectedLink);
			}
		}

		List<LogisticallyLinkedBehaviour> usedLinks = new ArrayList<>();
		MutableBoolean finalLinkTracker = new MutableBoolean(false);

		// First box needs to carry the order specifics for successful defrag
		PackageOrderWithCrafts context = order;

		// Packages from future orders should not be merged in the packager queue
		int orderId = r.nextInt();

		for (int i = 0; i < stacks.size(); i++) {
			BigItemStack entry = stacks.get(i);
			int remainingCount = entry.count;
			boolean finalEntry = i == stacks.size() - 1;
			ItemStack requestedItem = entry.stack;

			for (LogisticallyLinkedBehaviour link : availableLinks) {
				int usedIndex = usedLinks.indexOf(link);
				int linkIndex = usedIndex == -1 ? usedLinks.size() : usedIndex;
				MutableBoolean isFinalLink = new MutableBoolean(false);
				if (linkIndex == usedLinks.size() - 1)
					isFinalLink = finalLinkTracker;

				// Only send context and craftingContext with first package
				Pair<PackagerBlockEntity, PackagingRequest> request = link.processRequest(requestedItem, remainingCount,
					address, linkIndex, isFinalLink, orderId, context, ignoredHandler);
				if (request == null)
					continue;

				requests.put(request.getFirst(), request.getSecond());

				int processedCount = request.getSecond()
					.getCount();
				if (processedCount > 0 && usedIndex == -1) {
					context = null;
					usedLinks.add(link);
					finalLinkTracker = isFinalLink;
				}

				remainingCount -= processedCount;
				if (remainingCount > 0)
					continue;
				if (finalEntry)
					finalLinkTracker.setTrue();
				break;
			}
		}
		return requests;
	}

	@Nullable
	private static InventoryIdentifier getInventoryIdentifierFromLink(LogisticallyLinkedBehaviour link) {
		if (!(link.blockEntity instanceof PackagerLinkBlockEntity plbe))
			return null;

		PackagerBlockEntity packager = plbe.getPackager();
		if (packager == null || !packager.targetInventory.hasInventory())
			return null;

		IdentifiedInventory identifiedInventory = packager.targetInventory.getIdentifiedInventory();
		InventoryIdentifier result = identifiedInventory != null ? identifiedInventory.identifier() : null;
		return result;
	}

	public static void performPackageRequests(Multimap<PackagerBlockEntity, PackagingRequest> requests) {
		Map<PackagerBlockEntity, Collection<PackagingRequest>> asMap = requests.asMap();
		for (Map.Entry<PackagerBlockEntity, Collection<PackagingRequest>> entry : asMap.entrySet()) {
			ArrayList<PackagingRequest> queuedRequests = new ArrayList<>(entry.getValue());
			PackagerBlockEntity packager = entry.getKey();

			if (!queuedRequests.isEmpty())
				packager.flashLink();
			for (int i = 0; i < 100; i++) {
				if (queuedRequests.isEmpty())
					break;
				packager.attemptToSend(queuedRequests);
			}

			packager.triggerStockCheck();
			packager.notifyUpdate();
		}
	}

}
