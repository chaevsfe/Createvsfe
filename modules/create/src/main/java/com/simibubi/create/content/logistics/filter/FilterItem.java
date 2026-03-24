package com.simibubi.create.content.logistics.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllKeys;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;

import io.github.fabricators_of_create.porting_lib_ufo.transfer.item.ItemHandlerHelper;
import io.github.fabricators_of_create.porting_lib_ufo.transfer.item.ItemStackHandler;
import io.github.fabricators_of_create.porting_lib_ufo.util.NetworkHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class FilterItem extends Item implements MenuProvider {

	private FilterType type;

	private enum FilterType {
		REGULAR, ATTRIBUTE, PACKAGE;
	}

	public static FilterItem regular(Properties properties) {
		return new FilterItem(FilterType.REGULAR, properties);
	}

	public static FilterItem attribute(Properties properties) {
		return new FilterItem(FilterType.ATTRIBUTE, properties);
	}

	public static FilterItem address(Properties properties) {
		return new FilterItem(FilterType.PACKAGE, properties);
	}

	private FilterItem(FilterType type, Properties properties) {
		super(properties);
		this.type = type;
	}

	@Nonnull
	@Override
	public InteractionResult useOn(UseOnContext context) {
		if (context.getPlayer() == null)
			return InteractionResult.PASS;
		return use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip,
			TooltipFlag tooltipFlag) {
		if (!AllKeys.shiftDown()) {
			List<Component> makeSummary = makeSummary(stack);
			if (makeSummary.isEmpty())
				return;
			tooltip.add(Components.literal(" "));
			tooltip.addAll(makeSummary);
		}
	}

	private List<Component> makeSummary(ItemStack filter) {
		List<Component> list = new ArrayList<>();

		if (type == FilterType.REGULAR) {
			if (!filter.has(AllDataComponents.FILTER_DATA))
				return list;

			ItemStackHandler filterItems = getFilterItems(filter);
			boolean blacklist = filter.get(AllDataComponents.FILTER_DATA)
				.getBoolean("Blacklist");

			list.add((blacklist ? Lang.translateDirect("gui.filter.deny_list")
				: Lang.translateDirect("gui.filter.allow_list")).withStyle(ChatFormatting.GOLD));
			int count = 0;
			for (int i = 0; i < filterItems.getSlotCount(); i++) {
				if (count > 3) {
					list.add(Components.literal("- ...")
						.withStyle(ChatFormatting.DARK_GRAY));
					break;
				}

				ItemStack filterStack = filterItems.getStackInSlot(i);
				if (filterStack.isEmpty())
					continue;
				list.add(Components.literal("- ")
					.append(filterStack.getHoverName())
					.withStyle(ChatFormatting.GRAY));
				count++;
			}

			if (count == 0)
				return Collections.emptyList();
		}

		if (type == FilterType.PACKAGE) {
			String address = com.simibubi.create.content.logistics.box.PackageItem.getAddress(filter);
			if (!address.isBlank()) {
				list.add(Components.literal("-> ")
					.withStyle(ChatFormatting.GRAY)
					.append(Components.literal(address)
						.withStyle(ChatFormatting.GOLD)));
			}
			return list;
		}

		if (type == FilterType.ATTRIBUTE) {
			AttributeFilterWhitelistMode whitelistMode = filter.getOrDefault(
				AllDataComponents.ATTRIBUTE_FILTER_WHITELIST_MODE, AttributeFilterWhitelistMode.WHITELIST_DISJ);
			list.add((whitelistMode == AttributeFilterWhitelistMode.WHITELIST_CONJ
				? Lang.translateDirect("gui.attribute_filter.allow_list_conjunctive")
				: whitelistMode == AttributeFilterWhitelistMode.WHITELIST_DISJ
					? Lang.translateDirect("gui.attribute_filter.allow_list_disjunctive")
					: Lang.translateDirect("gui.attribute_filter.deny_list")).withStyle(ChatFormatting.GOLD));

			int count = 0;
			List<ItemAttribute.ItemAttributeEntry> attributes = filter.getOrDefault(
				AllDataComponents.ATTRIBUTE_FILTER_MATCHED_ATTRIBUTES, List.of());
			for (ItemAttribute.ItemAttributeEntry entry : attributes) {
				if (entry == null || entry.attribute() == null)
					continue;
				if (count > 3) {
					list.add(Components.literal("- ...")
						.withStyle(ChatFormatting.DARK_GRAY));
					break;
				}
				list.add(Components.literal("- ")
					.append(entry.attribute().format(entry.inverted())));
				count++;
			}

			if (count == 0)
				return Collections.emptyList();
		}

		return list;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack heldItem = player.getItemInHand(hand);

		if (!player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND) {
			if (!world.isClientSide && player instanceof ServerPlayer)
				NetworkHooks.openScreen((ServerPlayer) player, this, buf -> {
					ItemStack.STREAM_CODEC.encode(buf, heldItem);
				});
			return InteractionResultHolder.success(heldItem);
		}
		return InteractionResultHolder.pass(heldItem);
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
		ItemStack heldItem = player.getMainHandItem();
		if (type == FilterType.REGULAR)
			return FilterMenu.create(id, inv, heldItem);
		if (type == FilterType.ATTRIBUTE)
			return AttributeFilterMenu.create(id, inv, heldItem);
		if (type == FilterType.PACKAGE)
			return PackageFilterMenu.create(id, inv, heldItem);
		return null;
	}

	@Override
	public Component getDisplayName() {
		return getDescription();
	}

	public static ItemStackHandler getFilterItems(ItemStack stack) {
		ItemStackHandler newInv = new ItemStackHandler(18);
		if (AllItems.FILTER.get() != stack.getItem())
			throw new IllegalArgumentException("Cannot get filter items from non-filter: " + stack);
		if (!stack.has(AllDataComponents.FILTER_DATA))
			return newInv;
		CompoundTag invNBT = stack.get(AllDataComponents.FILTER_DATA).getCompound("Items");
		if (!invNBT.isEmpty())
			newInv.deserializeNBT(invNBT);
		return newInv;
	}

	public static boolean testDirect(ItemStack filter, ItemStack stack, boolean matchNBT) {
		if (matchNBT) {
			return ItemHandlerHelper.canItemStacksStack(filter, stack);
		} else {
			return ItemHelper.sameItem(filter, stack);
		}
	}

}
