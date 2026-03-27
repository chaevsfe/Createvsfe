package com.simibubi.create.content.logistics.redstoneRequester;

import java.util.List;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.compat.computercraft.ComputerCraftProxy;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour.RequestType;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import com.simibubi.create.content.logistics.stockTicker.StockCheckingBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import io.github.fabricators_of_create.porting_lib_ufo.util.NetworkHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class RedstoneRequesterBlockEntity extends StockCheckingBlockEntity implements MenuProvider {
	public AbstractComputerBehaviour computerBehaviour;

	public boolean allowPartialRequests;
	public PackageOrderWithCrafts encodedRequest = PackageOrderWithCrafts.empty();
	public String encodedTargetAdress = "";

	public boolean lastRequestSucceeded;

	protected boolean redstonePowered;

	public RedstoneRequesterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		allowPartialRequests = false;
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);
		behaviours.add(computerBehaviour = ComputerCraftProxy.behaviour(this));
	}

	@Override
	public void invalidate() {
		super.invalidate();
		computerBehaviour.removePeripheral();
	}

	protected void onRedstonePowerChanged() {
		boolean hasNeighborSignal = level.hasNeighborSignal(worldPosition);
		if (redstonePowered == hasNeighborSignal)
			return;

		lastRequestSucceeded = false;
		if (hasNeighborSignal)
			triggerRequest();

		redstonePowered = hasNeighborSignal;
		notifyUpdate();
	}

	public void triggerRequest() {
		if (encodedRequest.isEmpty())
			return;

		InventorySummary summaryOfOrder = new InventorySummary();
		encodedRequest.stacks().forEach(summaryOfOrder::add);

		InventorySummary summary = getAccurateSummary();
		boolean anySucceeded = true;
		for (BigItemStack entry : summaryOfOrder.getStacks()) {
			if (summary.getCountOf(entry.stack) >= entry.count)
				continue;
			if (!allowPartialRequests) {
				// Send failure effect
				playEffect(false);
				return;
			}
			anySucceeded = false;
		}

		broadcastPackageRequest(RequestType.REDSTONE, encodedRequest, null, encodedTargetAdress);
		playEffect(true);
		lastRequestSucceeded = true;
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		redstonePowered = tag.getBoolean("Powered");
		lastRequestSucceeded = tag.getBoolean("Success");
		allowPartialRequests = tag.getBoolean("AllowPartial");
		if (tag.contains("EncodedRequest") && registries != null) {
			RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, registries);
			encodedRequest = PackageOrderWithCrafts.CODEC.parse(ops, tag.get("EncodedRequest"))
				.resultOrPartial(err -> {}).orElse(PackageOrderWithCrafts.empty());
		}
		encodedTargetAdress = tag.getString("EncodedAddress");
	}

	@Override
	protected void write(CompoundTag tag, boolean clientPacket) {
		super.write(tag, clientPacket);
		tag.putBoolean("Powered", redstonePowered);
		tag.putBoolean("Success", lastRequestSucceeded);
		tag.putBoolean("AllowPartial", allowPartialRequests);
		tag.putString("EncodedAddress", encodedTargetAdress);
		HolderLookup.Provider registries = level != null ? level.registryAccess() : null;
		if (registries != null) {
			RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, registries);
			PackageOrderWithCrafts.CODEC.encodeStart(ops, encodedRequest)
				.resultOrPartial(err -> {})
				.ifPresent(encoded -> tag.put("EncodedRequest", encoded));
		}
	}

	public InteractionResult use(Player player) {
		if (player == null || player.isCrouching())
			return InteractionResult.PASS;
		if (level.isClientSide)
			return InteractionResult.SUCCESS;
		if (!behaviour.mayInteractMessage(player))
			return InteractionResult.SUCCESS;

		if (player instanceof ServerPlayer sp)
			NetworkHooks.openScreen(sp, this, worldPosition);
		return InteractionResult.SUCCESS;
	}

	@Override
	public Component getDisplayName() {
		return Component.empty();
	}

	@Override
	public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
		return RedstoneRequesterMenu.create(pContainerId, pPlayerInventory, this);
	}

	public void playEffect(boolean success) {
		Vec3 vec3 = Vec3.atCenterOf(worldPosition);
		if (success) {
			AllSoundEvents.CONFIRM.playAt(level, worldPosition, 0.5f, 1.5f, false);
			AllSoundEvents.STOCK_LINK.playAt(level, worldPosition, 1.0f, 1.0f, false);
			// WiFiParticle deferred
		} else {
			AllSoundEvents.DENY.playAt(level, worldPosition, 0.5f, 1, false);
			level.addParticle(ParticleTypes.ENCHANTED_HIT, vec3.x, vec3.y + 1, vec3.z, 0, 0, 0);
		}
	}
}
