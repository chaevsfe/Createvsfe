package com.simibubi.create.content.logistics.packagerLink;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.content.logistics.packager.IdentifiedInventory;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour.RequestType;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;

import net.minecraft.world.level.Level;

/**
 * Manages logistics network operations — inventory summaries and package requests.
 * Uses a simple tick-based cache to avoid recomputing network state every query.
 */
public class LogisticsManager {

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
		Set<Object> processedInventories = new HashSet<>();
		Collection<LogisticallyLinkedBehaviour> links = LogisticallyLinkedBehaviour.getAllPresent(freqId, false);
		for (LogisticallyLinkedBehaviour link : links) {
			InventorySummary summary = link.getSummary(null);
			if (summary != InventorySummary.EMPTY) {
				summaryOfLinks.contributingLinks++;
				summaryOfLinks.add(summary);
			}
		}
		return summaryOfLinks;
	}

	public static boolean broadcastPackageRequest(UUID freqId, RequestType type, PackageOrderWithCrafts order,
			@Nullable IdentifiedInventory ignoredHandler, String address) {
		// Full implementation deferred until PackagingRequest and PackagerLinkBlockEntity.processRequest are ported
		return false;
	}
}
