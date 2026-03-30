package com.simibubi.create.content.logistics.packagePort;

import java.util.List;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.animatedContainer.AnimatedContainerBehaviour;
import com.simibubi.create.foundation.item.SmartInventory;

import io.github.fabricators_of_create.porting_lib_ufo.util.NetworkHooks;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Clearable;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class PackagePortBlockEntity extends SmartBlockEntity implements MenuProvider, Clearable {

	public static void registerItemStorage() {
		ItemStorage.SIDED.registerForBlockEntity(
			(be, dir) -> new PackagePortAutomationInventoryWrapper(be.inventory, be),
			AllBlockEntityTypes.PACKAGE_FROGPORT.get()
		);
		ItemStorage.SIDED.registerForBlockEntity(
			(be, dir) -> new PackagePortAutomationInventoryWrapper(be.inventory, be),
			AllBlockEntityTypes.PACKAGE_POSTBOX.get()
		);
	}

	public boolean acceptsPackages;
	public String addressFilter;
	public PackagePortTarget target;
	public SmartInventory inventory;

	protected AnimatedContainerBehaviour<PackagePortMenu> openTracker;

	public PackagePortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		addressFilter = "";
		acceptsPackages = true;
		inventory = new SmartInventory(18, this);
	}

	public boolean isBackedUp() {
		for (int i = 0; i < inventory.getSlotCount(); i++)
			if (inventory.getStackInSlot(i).isEmpty())
				return false;
		return true;
	}

	public void filterChanged() {
		if (target != null) {
			target.deregister(this, level, worldPosition);
			target.register(this, level, worldPosition);
		}
	}

	@Override
	public void lazyTick() {
		super.lazyTick();
		if (target != null)
			target.register(this, level, worldPosition);
	}

	public String getFilterString() {
		return acceptsPackages ? addressFilter : null;
	}

	@Override
	protected void write(CompoundTag tag, boolean clientPacket) {
		super.write(tag, clientPacket);
		if (target != null) {
			HolderLookup.Provider registries = level != null ? level.registryAccess() : null;
			if (registries != null) {
				RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, registries);
				PackagePortTarget.CODEC.encodeStart(ops, target)
					.resultOrPartial(err -> {})
					.ifPresent(encoded -> tag.put("Target", encoded));
			}
		}
		tag.putString("AddressFilter", addressFilter);
		tag.putBoolean("AcceptsPackages", acceptsPackages);
		tag.put("Inventory", inventory.serializeNBT());
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		inventory.deserializeNBT(tag.getCompound("Inventory"));
		PackagePortTarget prevTarget = target;
		if (tag.contains("Target") && registries != null) {
			RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, registries);
			target = PackagePortTarget.CODEC.parse(ops, tag.get("Target"))
				.resultOrPartial(err -> {})
				.orElse(null);
		} else {
			target = null;
		}
		addressFilter = tag.getString("AddressFilter");
		acceptsPackages = tag.getBoolean("AcceptsPackages");
		if (clientPacket && prevTarget != target)
			invalidateRenderBoundingBox();
	}

	@Override
	public void clearContent() {
		for (int i = 0; i < inventory.getSlotCount(); i++)
			inventory.setStackInSlot(i, ItemStack.EMPTY);
	}

	@Override
	public void destroy() {
		if (target != null)
			target.deregister(this, level, worldPosition);
		super.destroy();
		for (int i = 0; i < inventory.getSlotCount(); i++)
			drop(inventory.getStackInSlot(i));
	}

	public void drop(ItemStack box) {
		if (box.isEmpty())
			return;
		Block.popResource(level, worldPosition, box);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		behaviours.add(openTracker = new AnimatedContainerBehaviour<>(this, PackagePortMenu.class));
		openTracker.onOpenChanged(this::onOpenChange);
	}

	protected abstract void onOpenChange(boolean open);

	public ItemInteractionResult use(Player player) {
		if (player == null || player.isCrouching())
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		ItemStack mainHandItem = player.getMainHandItem();
		boolean clipboard = AllBlocks.CLIPBOARD.isIn(mainHandItem);

		if (level.isClientSide) {
			if (!clipboard)
				onOpenedManually();
			return ItemInteractionResult.SUCCESS;
		}

		if (player instanceof ServerPlayer sp) {
			NetworkHooks.openScreen(sp, this, worldPosition);
		}
		return ItemInteractionResult.SUCCESS;
	}

	protected void onOpenedManually() {
	}

	@Override
	public Component getDisplayName() {
		return Component.empty();
	}

	@Override
	public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
		return PackagePortMenu.create(pContainerId, pPlayerInventory, this);
	}

	public int getComparatorOutput() {
		int filled = 0;
		for (int i = 0; i < inventory.getSlotCount(); i++)
			if (!inventory.getStackInSlot(i).isEmpty())
				filled++;
		return filled * 15 / inventory.getSlotCount();
	}
}
