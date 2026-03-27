package com.simibubi.create.content.logistics.packagePort.frogport;

import java.util.List;

import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.compat.computercraft.ComputerCraftProxy;
import com.simibubi.create.content.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.box.PackageStyles;
import com.simibubi.create.content.logistics.packagePort.PackagePortBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.NBTHelper;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import com.simibubi.create.foundation.utility.animation.LerpedFloat.Chaser;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class FrogportBlockEntity extends PackagePortBlockEntity implements IHaveHoveringInformation {
	public AbstractComputerBehaviour computerBehaviour;

	public ItemStack animatedPackage;
	public LerpedFloat manualOpenAnimationProgress;
	public LerpedFloat animationProgress;
	public LerpedFloat anticipationProgress;
	public boolean currentlyDepositing;
	public boolean goggles;

	public boolean sendAnticipate;

	public float passiveYaw;

	private boolean failedLastExport;

	private ItemStack deferAnimationStart;
	private boolean deferAnimationInward;

	public FrogportBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		animationProgress = LerpedFloat.linear();
		anticipationProgress = LerpedFloat.linear();
		manualOpenAnimationProgress = LerpedFloat.linear()
			.startWithValue(0)
			.chase(0, 0.35, Chaser.LINEAR);
		goggles = false;
	}

	private AdvancementBehaviour advancements;

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);
		behaviours.add(advancements = new AdvancementBehaviour(this, AllAdvancements.FROGPORT));
		behaviours.add(computerBehaviour = ComputerCraftProxy.behaviour(this));
	}

	@Override
	public void invalidate() {
		super.invalidate();
		computerBehaviour.removePeripheral();
	}

	public boolean isAnimationInProgress() {
		return animationProgress.getChaseTarget() == 1;
	}

	@Override
	public AABB getRenderBoundingBox() {
		AABB bb = super.getRenderBoundingBox().expandTowards(0, 1, 0);
		if (target != null)
			bb = bb.minmax(new AABB(BlockPos.containing(target.getExactTargetLocation(this, level, worldPosition))))
				.inflate(0.5);
		return bb;
	}

	@Override
	public void lazyTick() {
		super.lazyTick();
		if (level.isClientSide() || isAnimationInProgress())
			return;

		boolean prevFail = failedLastExport;
		tryPullingFromOwnInventory();

		if (failedLastExport != prevFail)
			sendData();
	}

	public void sendAnticipate() {
		if (isAnimationInProgress())
			return;
		for (int i = 0; i < inventory.getSlotCount(); i++)
			if (inventory.getStackInSlot(i).isEmpty()) {
				sendAnticipate = true;
				sendData();
				return;
			}
	}

	public void anticipate() {
		anticipationProgress.chase(1, 0.1, Chaser.LINEAR);
	}

	@Override
	public void tick() {
		super.tick();

		if (deferAnimationStart != null) {
			startAnimation(deferAnimationStart, deferAnimationInward);
			deferAnimationStart = null;
		}

		if (anticipationProgress.getValue() == 1)
			anticipationProgress.startWithValue(0);

		manualOpenAnimationProgress.updateChaseTarget(openTracker.openCount > 0 ? 1 : 0);

		anticipationProgress.tickChaser();
		manualOpenAnimationProgress.tickChaser();

		if (!isAnimationInProgress())
			return;

		animationProgress.tickChaser();

		float value = animationProgress.getValue();
		if (currentlyDepositing) {
			if (!level.isClientSide() || isVirtual()) {
				if (value > 0.5 && animatedPackage != null) {
					if (target == null
						|| !target.depositImmediately() && !target.export(level, worldPosition, animatedPackage, false))
						drop(animatedPackage);
					else
						computerBehaviour.prepareComputerEvent(new com.simibubi.create.compat.computercraft.events.PackageEvent(animatedPackage, "package_sent"));
					animatedPackage = null;
				}
			} else {
				if (value > 0.7 && animatedPackage != null)
					animatedPackage = null;
			}
		}

		if (value < 1)
			return;

		anticipationProgress.startWithValue(0);
		animationProgress.startWithValue(0);
		if (level.isClientSide()) {
			animatedPackage = null;
			return;
		}

		if (!currentlyDepositing) {
			boolean inserted = false;
			for (int i = 0; i < inventory.getSlotCount(); i++) {
				if (inventory.getStackInSlot(i).isEmpty()) {
					inventory.setStackInSlot(i, animatedPackage.copy());
					inserted = true;
					break;
				}
			}
			if (!inserted)
				drop(animatedPackage);
			else
				computerBehaviour.prepareComputerEvent(new com.simibubi.create.compat.computercraft.events.PackageEvent(animatedPackage, "package_received"));
		}

		animatedPackage = null;
	}

	public void startAnimation(ItemStack box, boolean deposit) {
		if (!PackageItem.isPackage(box))
			return;

		if (deposit && (target == null
			|| target.depositImmediately() && !target.export(level, worldPosition, box.copy(), false)))
			return;

		animationProgress.startWithValue(0);
		animationProgress.chase(1, 0.1, Chaser.LINEAR);
		animatedPackage = box;
		currentlyDepositing = deposit;

		if (level != null && !level.isClientSide()) {
			level.blockEntityChanged(worldPosition);
			sendData();
		}
	}

	public void tryPullingFromOwnAndAdjacentInventories() {
		if (isAnimationInProgress())
			return;
		if (target == null || !target.export(level, worldPosition, PackageStyles.getDefaultBox(), true))
			return;
		if (tryPullingFromOwnInventoryInternal())
			return;
		// Only check below (DOWN) — matching NeoForge behaviour
		Storage<ItemVariant> storage = getAdjacentStorage(Direction.DOWN);
		if (storage != null)
			tryPullingFromStorage(storage);
	}

	protected void tryPullingFromOwnInventory() {
		failedLastExport = false;
		if (isAnimationInProgress())
			return;
		if (target == null || !target.export(level, worldPosition, PackageStyles.getDefaultBox(), true))
			return;
		tryPullingFromOwnInventoryInternal();
	}

	private boolean tryPullingFromOwnInventoryInternal() {
		failedLastExport = false;
		for (int i = 0; i < inventory.getSlotCount(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack.isEmpty() || !PackageItem.isPackage(stack))
				continue;
			String filterString = getFilterString();
			if (filterString != null && PackageItem.matchAddress(stack, filterString))
				continue;
			ItemStack extracted = stack.copy();
			extracted.setCount(1);
			stack.shrink(1);
			if (stack.isEmpty())
				inventory.setStackInSlot(i, ItemStack.EMPTY);
			startAnimation(extracted, true);
			return true;
		}
		return false;
	}

	private boolean tryPullingFromStorage(Storage<ItemVariant> storage) {
		String filterString = getFilterString();
		try (Transaction tx = Transaction.openOuter()) {
			for (StorageView<ItemVariant> view : storage) {
				if (view.isResourceBlank())
					continue;
				ItemStack candidate = view.getResource().toStack(1);
				if (!PackageItem.isPackage(candidate))
					continue;
				if (filterString != null && PackageItem.matchAddress(candidate, filterString))
					continue;
				long extracted = view.extract(view.getResource(), 1, tx);
				if (extracted > 0) {
					tx.commit();
					startAnimation(view.getResource().toStack(1), true);
					return true;
				}
			}
		}
		return false;
	}

	private Storage<ItemVariant> getAdjacentStorage(Direction side) {
		BlockPos adjacentPos = worldPosition.relative(side);
		BlockEntity be = level.getBlockEntity(adjacentPos);
		if (be == null || be instanceof FrogportBlockEntity)
			return null;
		return ItemStorage.SIDED.find(level, adjacentPos, level.getBlockState(adjacentPos), be, side.getOpposite());
	}

	@Override
	protected void onOpenChange(boolean open) {
	}

	@Override
	protected void write(CompoundTag tag, boolean clientPacket) {
		super.write(tag, clientPacket);
		tag.putFloat("PlacedYaw", passiveYaw);
		if (animatedPackage != null && isAnimationInProgress()) {
			tag.put("AnimatedPackage", animatedPackage.saveOptional(level.registryAccess()));
			tag.putBoolean("Deposit", currentlyDepositing);
		}
		if (sendAnticipate) {
			sendAnticipate = false;
			tag.putBoolean("Anticipate", true);
		}
		if (failedLastExport)
			NBTHelper.putMarker(tag, "FailedLastExport");
		if (goggles)
			NBTHelper.putMarker(tag, "Goggles");
	}

	@Override
	protected void read(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		passiveYaw = tag.getFloat("PlacedYaw");
		failedLastExport = tag.getBoolean("FailedLastExport");
		goggles = tag.getBoolean("Goggles");
		if (!clientPacket)
			animatedPackage = null;
		if (tag.contains("AnimatedPackage")) {
			deferAnimationInward = tag.getBoolean("Deposit");
			deferAnimationStart = ItemStack.parseOptional(registries, tag.getCompound("AnimatedPackage"));
		}
		if (clientPacket && tag.contains("Anticipate"))
			anticipate();
	}

	public float getYaw() {
		if (target == null)
			return passiveYaw;
		Vec3 diff = target.getExactTargetLocation(this, level, worldPosition)
			.subtract(Vec3.atCenterOf(worldPosition));
		return (float) (Mth.atan2(diff.x, diff.z) * Mth.RAD_TO_DEG) + 180;
	}

	@Override
	public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		boolean superTip = IHaveHoveringInformation.super.addToTooltip(tooltip, isPlayerSneaking);
		if (!failedLastExport)
			return superTip;
		TooltipHelper.addHint(tooltip, "hint.blocked_frogport");
		return true;
	}
}
