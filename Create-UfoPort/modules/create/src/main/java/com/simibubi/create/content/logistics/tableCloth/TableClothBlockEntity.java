package com.simibubi.create.content.logistics.tableCloth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.compat.computercraft.ComputerCraftProxy;
import com.simibubi.create.content.logistics.redstoneRequester.AutoRequestData;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.simibubi.create.content.logistics.tableCloth.ShoppingListItem.ShoppingList;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.utility.IntAttached;
import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class TableClothBlockEntity extends SmartBlockEntity {
	public AbstractComputerBehaviour computerBehaviour;

	public AutoRequestData requestData;
	public List<ItemStack> manuallyAddedItems;
	public UUID owner;
	public FilteringBehaviour priceTag;

	public Direction facing;
	public boolean sideOccluded;

	public TableClothBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		manuallyAddedItems = new ArrayList<>();
		requestData = new AutoRequestData();
		owner = null;
		facing = Direction.SOUTH;
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		behaviours.add(priceTag = new TableClothFilteringBehaviour(this));
		behaviours.add(computerBehaviour = ComputerCraftProxy.behaviour(this));
	}

	@Override
	public void lazyTick() {
		super.lazyTick();
		BlockPos relativePos = worldPosition.relative(facing);
		sideOccluded = com.simibubi.create.AllTags.AllBlockTags.TABLE_CLOTHS.matches(level.getBlockState(relativePos))
			|| net.minecraft.world.level.block.Block.isFaceFull(level.getBlockState(relativePos.below())
			.getOcclusionShape(level, relativePos.below()), facing.getOpposite());
	}

	@Override
	public void invalidate() {
		super.invalidate();
		computerBehaviour.removePeripheral();
	}

	public boolean isShop() {
		return requestData != null && requestData.isValid();
	}

	public ItemStack getPaymentItem() {
		return priceTag != null ? priceTag.getFilter() : ItemStack.EMPTY;
	}

	public int getPaymentAmount() {
		return priceTag != null && !priceTag.getFilter().isEmpty() ? priceTag.count : 1;
	}

	public boolean targetsPriceTag(Player player, BlockHitResult ray) {
		return priceTag != null && ((TableClothFilteringBehaviour) priceTag).mayInteract(player)
			&& priceTag.getSlotPositioning().testHit(getBlockState(), ray.getLocation().subtract(net.minecraft.world.phys.Vec3.atLowerCornerOf(worldPosition)));
	}

	public ItemInteractionResult use(Player player, BlockHitResult hitResult) {
		if (player == null)
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

		if (isShop())
			return useShop(player);

		ItemStack heldItem = player.getMainHandItem();
		boolean shiftClick = player.isShiftKeyDown();

		if (shiftClick && heldItem.isEmpty()) {
			// Remove last manually added item
			if (!manuallyAddedItems.isEmpty()) {
				ItemStack removed = manuallyAddedItems.remove(manuallyAddedItems.size() - 1);
				if (!level.isClientSide()) {
					player.getInventory().placeItemBackInInventory(removed);
					level.playSound(null, worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS);
					notifyUpdate();
				}
				return ItemInteractionResult.SUCCESS;
			}
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}

		if (!heldItem.isEmpty() && !shiftClick) {
			// Add item to display
			if (manuallyAddedItems.size() < 6) {
				if (!level.isClientSide()) {
					ItemStack toAdd = heldItem.copyWithCount(1);
					manuallyAddedItems.add(toAdd);
					level.playSound(null, worldPosition, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS);
					notifyUpdate();
				}
				return ItemInteractionResult.SUCCESS;
			}
		}

		return ItemInteractionResult.SUCCESS;
	}

	public ItemInteractionResult useShop(Player player) {
		ItemStack itemInHand = player.getItemInHand(InteractionHand.MAIN_HAND);
		ItemStack prevListItem = ItemStack.EMPTY;
		boolean addOntoList = false;

		// Collect any existing shopping list from hotbar
		for (int i = 0; i < 9; i++) {
			ItemStack item = player.getInventory().getItem(i);
			if (!AllItems.SHOPPING_LIST.isIn(item))
				continue;
			prevListItem = item;
			addOntoList = true;
			player.getInventory().setItem(i, ItemStack.EMPTY);
		}

		// Add onto existing list if held in hand
		if (AllItems.SHOPPING_LIST.isIn(itemInHand)) {
			prevListItem = itemInHand;
			addOntoList = true;
		}

		if (!itemInHand.isEmpty() && !addOntoList) {
			Lang.translate("stock_keeper.shopping_list_empty_hand")
				.sendStatus(player);
			AllSoundEvents.DENY.playOnServer(level, worldPosition, 0.5f, 1);
			return ItemInteractionResult.SUCCESS;
		}

		if (getPaymentItem().isEmpty()) {
			Lang.translate("stock_keeper.no_price_set")
				.sendStatus(player);
			AllSoundEvents.DENY.playOnServer(level, worldPosition, 0.5f, 1);
			return ItemInteractionResult.SUCCESS;
		}

		UUID tickerID = null;
		BlockPos tickerPos = requestData.targetOffset().offset(worldPosition);
		if (level.getBlockEntity(tickerPos) instanceof StockTickerBlockEntity stbe && stbe.isKeeperPresent())
			tickerID = stbe.behaviour.freqId;

		int stockLevel = getStockLevelForTrade(ShoppingListItem.getList(prevListItem));

		if (tickerID == null) {
			Lang.translate("stock_keeper.keeper_missing")
				.style(ChatFormatting.RED)
				.sendStatus(player);
			AllSoundEvents.DENY.playOnServer(level, worldPosition, 0.5f, 1);
			return ItemInteractionResult.SUCCESS;
		}

		if (stockLevel == 0) {
			Lang.translate("stock_keeper.out_of_stock")
				.style(ChatFormatting.RED)
				.sendStatus(player);
			AllSoundEvents.DENY.playOnServer(level, worldPosition, 0.5f, 1);
			if (!prevListItem.isEmpty()) {
				if (player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty())
					player.setItemInHand(InteractionHand.MAIN_HAND, prevListItem);
				else
					player.getInventory().placeItemBackInInventory(prevListItem);
			}
			return ItemInteractionResult.SUCCESS;
		}

		ShoppingList list = new ShoppingList(new ArrayList<>(), owner, tickerID);

		if (addOntoList) {
			ShoppingList prevList = ShoppingListItem.getList(prevListItem).duplicate();
			if (owner.equals(prevList.shopOwner()) && tickerID.equals(prevList.shopNetwork()))
				list = prevList;
			else
				addOntoList = false;
		}

		if (list.getPurchases(worldPosition) >= stockLevel) {
			for (IntAttached<BlockPos> entry : list.purchases())
				if (worldPosition.equals(entry.getValue()))
					entry.setFirst(Math.min(stockLevel, entry.getFirst()));

			Lang.translate("stock_keeper.limited_stock")
				.style(ChatFormatting.RED)
				.sendStatus(player);
		} else {
			AllSoundEvents.CONFIRM.playOnServer(level, worldPosition, 0.5f, 1.0f);

			ShoppingList.Mutable mutable = new ShoppingList.Mutable(list);
			mutable.addPurchases(worldPosition, 1);
			list = mutable.toImmutable();

			if (!addOntoList)
				Lang.translate("stock_keeper.use_list_to_add_purchases")
					.color(0xeeeeee)
					.sendStatus(player);
			if (!addOntoList)
				level.playSound(null, worldPosition, SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1, 1.5f);
		}

		ItemStack newListItem =
			ShoppingListItem.saveList(AllItems.SHOPPING_LIST.asStack(), list, requestData.encodedTargetAddress());

		if (player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty())
			player.setItemInHand(InteractionHand.MAIN_HAND, newListItem);
		else
			player.getInventory().placeItemBackInInventory(newListItem);

		return ItemInteractionResult.SUCCESS;
	}

	@Override
	protected void write(CompoundTag tag, boolean clientPacket) {
		super.write(tag, clientPacket);
		tag.putString("Facing", facing.getName());
		tag.putBoolean("SideOccluded", sideOccluded);
		if (owner != null)
			tag.putUUID("Owner", owner);

		if (!manuallyAddedItems.isEmpty()) {
			ListTag items = new ListTag();
			for (ItemStack stack : manuallyAddedItems)
				items.add(stack.saveOptional(level != null ? level.registryAccess() : com.simibubi.create.Create.getRegistryAccess()));
			tag.put("ManualItems", items);
		}

		if (requestData != null && requestData.isValid()) {
			tag.put("RequestData", AutoRequestData.CODEC.encodeStart(
				net.minecraft.nbt.NbtOps.INSTANCE, requestData).getOrThrow());
		}
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		facing = Direction.byName(tag.getString("Facing"));
		if (facing == null) facing = Direction.SOUTH;
		sideOccluded = tag.getBoolean("SideOccluded");
		owner = tag.contains("Owner") ? tag.getUUID("Owner") : null;

		manuallyAddedItems.clear();
		if (tag.contains("ManualItems", Tag.TAG_LIST)) {
			ListTag items = tag.getList("ManualItems", Tag.TAG_COMPOUND);
			for (int i = 0; i < items.size(); i++)
				manuallyAddedItems.add(ItemStack.parseOptional(registries, items.getCompound(i)));
		}

		if (tag.contains("RequestData")) {
			requestData = AutoRequestData.CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE, tag.get("RequestData"))
				.resultOrPartial(err -> {}).orElse(new AutoRequestData());
		}
	}

	public int getStockLevelForTrade(@Nullable ShoppingListItem.ShoppingList otherPurchases) {
		if (requestData == null || requestData.encodedRequest().isEmpty())
			return 0;
		com.simibubi.create.content.logistics.packager.InventorySummary recentSummary = null;
		net.minecraft.core.BlockPos tickerPos = requestData.targetOffset().offset(worldPosition);
		if (!(level.getBlockEntity(tickerPos) instanceof com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity stbe))
			return 0;

		if (level.isClientSide()) {
			if (stbe.getTicksSinceLastUpdate() > 15)
				stbe.refreshClientStockSnapshot();
			recentSummary = stbe.getLastClientsideStockSnapshotAsSummary();
		} else
			recentSummary = stbe.getRecentSummary();

		if (recentSummary == null)
			return 0;

		com.simibubi.create.content.logistics.packager.InventorySummary modifierSummary = new com.simibubi.create.content.logistics.packager.InventorySummary();
		if (otherPurchases != null)
			modifierSummary = otherPurchases.bakeEntries(level, worldPosition).getFirst();

		int smallestQuotient = Integer.MAX_VALUE;
		for (com.simibubi.create.content.logistics.BigItemStack entry : requestData.encodedRequest().stacks())
			if (entry.count > 0)
				smallestQuotient = Math.min(smallestQuotient,
					(recentSummary.getCountOf(entry.stack) - modifierSummary.getCountOf(entry.stack)) / entry.count);

		return smallestQuotient == Integer.MAX_VALUE ? 0 : smallestQuotient;
	}

	/** Stub for CC compat — notifies connected clients of shop state changes. */
	public void notifyShopUpdate() {
		notifyUpdate();
	}

	@Override
	public void destroy() {
		super.destroy();
		for (ItemStack item : manuallyAddedItems)
			Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), item);
	}
}
