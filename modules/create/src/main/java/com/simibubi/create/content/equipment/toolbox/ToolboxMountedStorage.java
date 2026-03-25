package com.simibubi.create.content.equipment.toolbox;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllMountedStorageTypes;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.api.contraption.storage.item.WrapperMountedItemStorage;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.foundation.codec.CreateCodecs;

import io.github.fabricators_of_create.porting_lib_ufo.transfer.item.ItemStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public class ToolboxMountedStorage extends WrapperMountedItemStorage<ToolboxInventory> {
	// ToolboxInventory doesn't have its own codec, so we serialize it as an ItemStackHandler.
	// This captures all 32 item slots. Filters are not included (not needed on contraptions).
	public static final MapCodec<ToolboxMountedStorage> CODEC = CreateCodecs.ITEM_STACK_HANDLER.xmap(
		handler -> {
			ToolboxInventory inv = new ToolboxInventory(null);
			int slots = Math.min(handler.getSlotCount(), inv.getSlotCount());
			for (int i = 0; i < slots; i++) {
				inv.setStackInSlot(i, handler.getStackInSlot(i));
			}
			return new ToolboxMountedStorage(inv);
		},
		storage -> {
			ItemStackHandler handler = new ItemStackHandler(storage.wrapped.getSlotCount());
			for (int i = 0; i < storage.wrapped.getSlotCount(); i++) {
				handler.setStackInSlot(i, storage.wrapped.getStackInSlot(i));
			}
			return handler;
		}
	).fieldOf("value");

	protected ToolboxMountedStorage(MountedItemStorageType<?> type, ToolboxInventory wrapped) {
		super(type, wrapped);
	}

	protected ToolboxMountedStorage(ToolboxInventory wrapped) {
		this(AllMountedStorageTypes.TOOLBOX.get(), wrapped);
	}

	@Override
	public void unmount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
		if (be instanceof ToolboxBlockEntity toolbox) {
			// Copy all slots from this storage back to the toolbox inventory
			int slots = Math.min(this.wrapped.getSlotCount(), toolbox.inventory.getSlotCount());
			for (int i = 0; i < slots; i++) {
				toolbox.inventory.setStackInSlot(i, this.wrapped.getStackInSlot(i));
			}
		}
	}

	@Override
	public boolean handleInteraction(ServerPlayer player, Contraption contraption, StructureBlockInfo info) {
		// The default impl will fail anyway, might as well cancel trying
		return false;
	}

	public static ToolboxMountedStorage fromToolbox(ToolboxBlockEntity toolbox) {
		// The inventory will send updates to the block entity, make an isolated copy to avoid that
		ToolboxInventory copy = new ToolboxInventory(null);
		int slots = Math.min(toolbox.inventory.getSlotCount(), copy.getSlotCount());
		for (int i = 0; i < slots; i++) {
			copy.setStackInSlot(i, toolbox.inventory.getStackInSlot(i).copy());
		}

		copy.filters.clear();
		for (ItemStack stack : toolbox.inventory.filters)
			copy.filters.add(stack.copy());

		return new ToolboxMountedStorage(copy);
	}

	public static ToolboxMountedStorage fromLegacy(CompoundTag nbt) {
		ToolboxInventory inv = new ToolboxInventory(null);
		inv.deserializeNBT(nbt);
		return new ToolboxMountedStorage(inv);
	}
}
