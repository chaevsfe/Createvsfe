package com.simibubi.create.foundation.recipe;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;

public class DummyCraftingContainer extends CraftingInput {

	private final NonNullList<ItemStack> inv;

	public DummyCraftingContainer(NonNullList<ItemStack> stacks) {
		super(0, 0, stacks);
		this.inv = stacks;
	}

//	@Override
	public int getContainerSize() {
		return this.inv.size();
	}

	@Override
	public boolean isEmpty() {
		for (int slot = 0; slot < this.getContainerSize(); slot++) {
			if (!this.getItem(slot).isEmpty())
				return false;
		}

		return true;
	}

	@Override
	public @NotNull ItemStack getItem(int slot) {
		return slot >= this.getContainerSize() ? ItemStack.EMPTY : this.inv.get(slot);
	}

//	@Override
//	public @NotNull ItemStack removeItemNoUpdate(int slot) {
//		return ItemStack.EMPTY;
//	}
//
//	@Override
//	public @NotNull ItemStack removeItem(int slot, int count) {
//		return ItemStack.EMPTY;
//	}
//
//	@Override
//	public void setItem(int slot, @NotNull ItemStack stack) {}
//
//	@Override
//	public void clearContent() {}
//
//	@Override
//	public void fillStackedContents(@NotNull StackedContents helper) {}

	@Override
	public int size() {
		return this.inv.size();
	}
}
