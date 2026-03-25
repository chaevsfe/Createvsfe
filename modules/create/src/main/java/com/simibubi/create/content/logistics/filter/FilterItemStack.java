package com.simibubi.create.content.logistics.filter;

import java.util.ArrayList;
import java.util.List;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.logistics.box.PackageItem;
import io.github.fabricators_of_create.porting_lib_ufo.fluids.FluidStack;
import io.github.fabricators_of_create.porting_lib_ufo.transfer.item.ItemStackHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FilterItemStack {

	private ItemStack filterItemStack;
	private boolean fluidExtracted;
	private FluidStack filterFluidStack;

	public static FilterItemStack of(ItemStack filter) {
		if (AllItems.PACKAGE_FILTER.isIn(filter))
			return new PackageFilterItemStack(filter);
		if (AllItems.ATTRIBUTE_FILTER.isIn(filter)
			&& (filter.has(AllDataComponents.ATTRIBUTE_FILTER_MATCHED_ATTRIBUTES)
				|| filter.has(AllDataComponents.ATTRIBUTE_FILTER_WHITELIST_MODE)))
			return new AttributeFilterItemStack(filter);
		if (filter.has(AllDataComponents.FILTER_DATA) && AllItems.FILTER.isIn(filter))
			return new ListFilterItemStack(filter);

		return new FilterItemStack(filter);
	}

	public static FilterItemStack of(CompoundTag tag) {
		return of(ItemStack.parseOptional(Create.getRegistryAccess(), tag));
	}

	public static FilterItemStack empty() {
		return of(ItemStack.EMPTY);
	}

	public boolean isEmpty() {
		return filterItemStack.isEmpty();
	}

	public CompoundTag serializeNBT() {
		CompoundTag ret = (CompoundTag)filterItemStack.saveOptional(Create.getRegistryAccess());
		return ret;
	}

	public ItemStack item() {
		return filterItemStack;
	}

	public FluidStack fluid(Level level) {
		resolveFluid(level);
		return filterFluidStack;
	}

	public boolean isFilterItem() {
		return filterItemStack.getItem() instanceof FilterItem;
	}

	//

	public boolean test(Level world, ItemStack stack) {
		return test(world, stack, false);
	}

	public boolean test(Level world, FluidStack stack) {
		return test(world, stack, true);
	}

	public boolean test(Level world, ItemStack stack, boolean matchNBT) {
		if (isEmpty())
			return true;
		return FilterItem.testDirect(filterItemStack, stack, matchNBT);
	}

	public boolean test(Level world, FluidStack stack, boolean matchNBT) {
		if (isEmpty())
			return true;
		if (stack.isEmpty())
			return false;

		resolveFluid(world);

		if (filterFluidStack.isEmpty())
			return false;
		if (!matchNBT)
			return filterFluidStack.getFluid()
				.isSame(stack.getFluid());
		return filterFluidStack.isFluidEqual(stack);
	}

	//

	private void resolveFluid(Level world) {
		if (!fluidExtracted) {
			fluidExtracted = true;
			if (GenericItemEmptying.canItemBeEmptied(world, filterItemStack))
				filterFluidStack = GenericItemEmptying.emptyItem(world, filterItemStack, true)
				.getFirst();
		}
	}

	protected FilterItemStack(ItemStack filter) {
		filterItemStack = filter;
		filterFluidStack = FluidStack.EMPTY;
		fluidExtracted = false;
	}

	public static class ListFilterItemStack extends FilterItemStack {

		public List<FilterItemStack> containedItems;
		public boolean shouldRespectNBT;
		public boolean isBlacklist;

		protected ListFilterItemStack(ItemStack filter) {
			super(filter);
			boolean defaults = !filter.has(AllDataComponents.FILTER_DATA);

			containedItems = new ArrayList<>();
			ItemStackHandler items = FilterItem.getFilterItems(filter);
			for (int i = 0; i < items.getSlots().size(); i++) {
				ItemStack stackInSlot = items.getStackInSlot(i);
				if (!stackInSlot.isEmpty())
					containedItems.add(FilterItemStack.of(stackInSlot));
			}

			shouldRespectNBT = !defaults ? false
				: filter.get(AllDataComponents.FILTER_DATA)
					.getBoolean("RespectNBT");
			isBlacklist = defaults ? false
				: filter.get(AllDataComponents.FILTER_DATA)
					.getBoolean("Blacklist");
		}

		@Override
		public boolean test(Level world, ItemStack stack, boolean matchNBT) {
			if (containedItems.isEmpty())
				return super.test(world, stack, matchNBT);
			for (FilterItemStack filterItemStack : containedItems)
				if (filterItemStack.test(world, stack, shouldRespectNBT))
					return !isBlacklist;
			return isBlacklist;
		}

		@Override
		public boolean test(Level world, FluidStack stack, boolean matchNBT) {
			for (FilterItemStack filterItemStack : containedItems)
				if (filterItemStack.test(world, stack, shouldRespectNBT))
					return !isBlacklist;
			return isBlacklist;
		}

	}

	public static class AttributeFilterItemStack extends FilterItemStack {

		public AttributeFilterWhitelistMode whitelistMode;
		public List<ItemAttribute.ItemAttributeEntry> attributeTests;

		protected AttributeFilterItemStack(ItemStack filter) {
			super(filter);
			attributeTests = new ArrayList<>();
			whitelistMode = filter.getOrDefault(AllDataComponents.ATTRIBUTE_FILTER_WHITELIST_MODE,
				AttributeFilterWhitelistMode.WHITELIST_DISJ);
			List<ItemAttribute.ItemAttributeEntry> entries = filter.getOrDefault(
				AllDataComponents.ATTRIBUTE_FILTER_MATCHED_ATTRIBUTES, List.of());
			attributeTests.addAll(entries);
		}

		@Override
		public boolean test(Level world, FluidStack stack, boolean matchNBT) {
			return false;
		}

		@Override
		public boolean test(Level world, ItemStack stack, boolean matchNBT) {
			if (attributeTests.isEmpty())
				return super.test(world, stack, matchNBT);
			for (ItemAttribute.ItemAttributeEntry entry : attributeTests) {
				ItemAttribute attribute = entry.attribute();
				boolean inverted = entry.inverted();
				boolean matches = attribute.appliesTo(stack, world) != inverted;

				if (matches) {
					switch (whitelistMode) {
					case BLACKLIST:
						return false;
					case WHITELIST_CONJ:
						continue;
					case WHITELIST_DISJ:
						return true;
					}
				} else {
					switch (whitelistMode) {
					case BLACKLIST:
						continue;
					case WHITELIST_CONJ:
						return false;
					case WHITELIST_DISJ:
						continue;
					}
				}
			}

			switch (whitelistMode) {
			case BLACKLIST:
				return true;
			case WHITELIST_CONJ:
				return true;
			case WHITELIST_DISJ:
				return false;
			}

			return false;
		}

	}

	public static class PackageFilterItemStack extends FilterItemStack {

		public String filterString;

		public PackageFilterItemStack(ItemStack filter) {
			super(filter);
			filterString = PackageItem.getAddress(filter);
		}

		@Override
		public boolean test(Level world, ItemStack stack, boolean matchNBT) {
			return (filterString.isBlank() && super.test(world, stack, matchNBT))
				|| PackageItem.isPackage(stack) && PackageItem.matchAddress(stack, filterString);
		}

		@Override
		public boolean test(Level world, FluidStack stack, boolean matchNBT) {
			return false;
		}

	}

}
