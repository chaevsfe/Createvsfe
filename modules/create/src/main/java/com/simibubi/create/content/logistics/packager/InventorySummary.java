package com.simibubi.create.content.logistics.packager;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import com.mojang.serialization.Codec;
import com.simibubi.create.content.logistics.BigItemStack;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Tracks inventory contents as a summary of BigItemStacks, keyed by Item type.
 * Minimal stub for Phase 3 foundation — will be expanded when Packager/Stock systems are ported.
 */
public class InventorySummary {
	public static Codec<InventorySummary> CODEC = Codec.list(BigItemStack.CODEC)
		.xmap(i -> {
				InventorySummary summary = new InventorySummary();
				i.forEach(summary::add);
				return summary;
			},
			i -> {
				List<BigItemStack> all = new ArrayList<>();
				i.items.forEach((key, list) -> all.addAll(list));
				return all;
			});

	public static final InventorySummary EMPTY = new InventorySummary();

	private Map<Item, List<BigItemStack>> items = new IdentityHashMap<>();
	private List<BigItemStack> stacksByCount;
	private int totalCount;

	public int contributingLinks;

	public void add(BigItemStack bigStack) {
		add(bigStack.stack, bigStack.count);
	}

	public void add(ItemStack stack) {
		add(stack, stack.getCount());
	}

	public void add(ItemStack stack, int count) {
		if (stack.isEmpty())
			return;
		Item item = stack.getItem();
		List<BigItemStack> list = items.computeIfAbsent(item, k -> new ArrayList<>());
		for (BigItemStack existing : list) {
			if (ItemStack.isSameItemSameComponents(existing.stack, stack)) {
				existing.count += count;
				totalCount += count;
				stacksByCount = null;
				return;
			}
		}
		list.add(new BigItemStack(stack.copyWithCount(1), count));
		totalCount += count;
		stacksByCount = null;
	}

	public List<BigItemStack> getStacks() {
		if (stacksByCount == null) {
			stacksByCount = new ArrayList<>();
			items.forEach((item, list) -> stacksByCount.addAll(list));
			stacksByCount.sort(BigItemStack.comparator());
		}
		return stacksByCount;
	}

	public int getCountOf(ItemStack stack) {
		List<BigItemStack> list = items.get(stack.getItem());
		if (list == null)
			return 0;
		for (BigItemStack entry : list) {
			if (ItemStack.isSameItemSameComponents(entry.stack, stack))
				return entry.count;
		}
		return 0;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public boolean isEmpty() {
		return items.isEmpty();
	}
}
