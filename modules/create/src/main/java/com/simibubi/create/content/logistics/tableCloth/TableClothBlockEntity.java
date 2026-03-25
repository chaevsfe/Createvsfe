package com.simibubi.create.content.logistics.tableCloth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.redstoneRequester.AutoRequestData;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.NBTHelper;

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

	/** Simple shop price filter — stub matching NeoForge's FilteringBehaviour API surface used by CC compat. */
	public static class PriceTag {
		private ItemStack filter = ItemStack.EMPTY;
		public int count = 1;

		public ItemStack getFilter() {
			return filter;
		}

		public void setFilter(ItemStack filter) {
			this.filter = filter == null ? ItemStack.EMPTY : filter;
		}
	}

	public AutoRequestData requestData;
	public List<ItemStack> manuallyAddedItems;
	public UUID owner;
	public PriceTag priceTag = new PriceTag();

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
	}

	public boolean isShop() {
		return requestData != null && requestData.isValid();
	}

	public ItemStack getPaymentItem() {
		// TODO: implement when FilteringBehaviour is connected
		return ItemStack.EMPTY;
	}

	public int getPaymentAmount() {
		return 1;
	}

	public ItemInteractionResult use(Player player, BlockHitResult hitResult) {
		if (player == null)
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

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
