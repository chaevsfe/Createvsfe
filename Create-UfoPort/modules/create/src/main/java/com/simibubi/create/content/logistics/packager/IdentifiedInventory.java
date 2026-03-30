package com.simibubi.create.content.logistics.packager;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.api.packager.InventoryIdentifier;

import io.github.fabricators_of_create.porting_lib_ufo.transfer.item.ItemStackHandler;

/**
 * An item inventory, possibly with an associated identifier.
 * Fabric adaptation: uses ItemStackHandler (porting_lib_ufo) instead of NeoForge IItemHandler.
 */
public record IdentifiedInventory(@Nullable InventoryIdentifier identifier, ItemStackHandler handler) {
	public static IdentifiedInventory of(ItemStackHandler handler) {
		return new IdentifiedInventory(null, handler);
	}

	public static IdentifiedInventory of(InventoryIdentifier id, ItemStackHandler handler) {
		return new IdentifiedInventory(id, handler);
	}
}
