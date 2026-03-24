package com.simibubi.create.content.logistics.packager;

import org.jetbrains.annotations.Nullable;

import io.github.fabricators_of_create.porting_lib_ufo.transfer.item.ItemStackHandler;

/**
 * An item inventory, possibly with an associated identifier.
 * Fabric adaptation: uses ItemStackHandler instead of NeoForge IItemHandler.
 * The identifier is a simple string key (simplified from NeoForge's InventoryIdentifier registry).
 */
public record IdentifiedInventory(@Nullable String identifier, ItemStackHandler handler) {
	public static IdentifiedInventory of(ItemStackHandler handler) {
		return new IdentifiedInventory(null, handler);
	}

	public static IdentifiedInventory of(String id, ItemStackHandler handler) {
		return new IdentifiedInventory(id, handler);
	}
}
