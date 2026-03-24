package com.simibubi.create.content.logistics.packager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;
import com.simibubi.create.api.packager.unpacking.UnpackingHandler;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceBlockEntity;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.crate.BottomlessItemHandler;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.logistics.packagePort.frogport.FrogportBlockEntity;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour.RequestType;
import com.simibubi.create.content.logistics.packagerLink.PackagerLinkBlock;
import com.simibubi.create.content.logistics.packagerLink.PackagerLinkBlockEntity;
import com.simibubi.create.content.logistics.packagerLink.RequestPromiseQueue;
import com.simibubi.create.content.logistics.packagerLink.WiFiEffectPacket;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.CapManipulationBehaviourBase.InterfaceProvider;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.VersionedInventoryTrackerBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.BlockFace;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.NBTHelper;

import io.github.fabricators_of_create.porting_lib_ufo.transfer.item.ItemStackHandler;
import io.github.fabricators_of_create.porting_lib_ufo.transfer.item.SlottedStackStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;

public class PackagerBlockEntity extends SmartBlockEntity implements Clearable {
	public boolean redstonePowered;
	public int buttonCooldown;
	public String signBasedAddress;

	public InvManipulationBehaviour targetInventory;
	public ItemStack heldBox;
	public ItemStack previouslyUnwrapped;

	public List<BigItemStack> queuedExitingPackages;

	public final PackagerItemHandler inventory;

	public static final int CYCLE = 20;
	public int animationTicks;
	public boolean animationInward;

	// ComputerCraft not ported — stubs
	public Boolean hasCustomComputerAddress;
	public String customComputerAddress;

	private InventorySummary availableItems;
	private VersionedInventoryTrackerBehaviour invVersionTracker;

	private AdvancementBehaviour advancements;

	//

	public PackagerBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
		super(typeIn, pos, state);
		redstonePowered = state.getOptionalValue(PackagerBlock.POWERED)
			.orElse(false);
		heldBox = ItemStack.EMPTY;
		previouslyUnwrapped = ItemStack.EMPTY;
		inventory = new PackagerItemHandler(this);
		animationTicks = 0;
		animationInward = true;
		queuedExitingPackages = new LinkedList<>();
		signBasedAddress = "";
		customComputerAddress = "";
		hasCustomComputerAddress = false;
		buttonCooldown = 0;
	}

	public static void registerItemStorage() {
		ItemStorage.SIDED.registerForBlockEntity(
			(be, dir) -> be.inventory,
			AllBlockEntityTypes.PACKAGER.get()
		);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		behaviours.add(targetInventory = new InvManipulationBehaviour(this, InterfaceProvider.oppositeOfBlockFacing()));
		behaviours.add(invVersionTracker = new VersionedInventoryTrackerBehaviour(this));
		behaviours.add(advancements = new AdvancementBehaviour(this, AllAdvancements.PACKAGER));
	}

	@Override
	public void initialize() {
		super.initialize();
		recheckIfLinksPresent();
	}

	@Override
	public void invalidate() {
		super.invalidate();
	}

	@Override
	public void tick() {
		super.tick();

		if (buttonCooldown > 0)
			buttonCooldown--;

		if (animationTicks == 0) {
			previouslyUnwrapped = ItemStack.EMPTY;

			if (!level.isClientSide() && !queuedExitingPackages.isEmpty() && heldBox.isEmpty()) {
				BigItemStack entry = queuedExitingPackages.get(0);
				heldBox = entry.stack.copy();

				entry.count--;
				if (entry.count <= 0)
					queuedExitingPackages.remove(0);

				animationInward = false;
				animationTicks = CYCLE;
				notifyUpdate();
			}

			return;
		}

		if (level.isClientSide) {
			if (animationTicks == CYCLE - (animationInward ? 5 : 1))
				AllSoundEvents.DEPOT_PLOP.playAt(level, worldPosition, 1, 1, true);
			if (animationTicks == (animationInward ? 1 : 5))
				level.playLocalSound(worldPosition, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 0.25f, 0.75f,
					true);
		}

		animationTicks--;

		if (animationTicks == 0 && !level.isClientSide()) {
			wakeTheFrogs();
			setChanged();
		}
	}

	public void triggerStockCheck() {
		getAvailableItems();
	}

	public InventorySummary getAvailableItems() {
		if (availableItems != null && invVersionTracker.stillWaiting(targetInventory))
			return availableItems;

		InventorySummary availableItems = new InventorySummary();

		Storage<ItemVariant> targetInv = targetInventory.getInventory();
		if (targetInv == null || targetInv instanceof PackagerItemHandler) {
			this.availableItems = availableItems;
			return availableItems;
		}

		if (targetInv instanceof BottomlessItemHandler bih) {
			availableItems.add(bih.getStackInSlot(0), BigItemStack.INF);
			this.availableItems = availableItems;
			return availableItems;
		}

		if (targetInv instanceof SlottedStackStorage slotted) {
			for (int slot = 0; slot < slotted.getSlotCount(); slot++) {
				availableItems.add(slotted.getStackInSlot(slot));
			}
		} else {
			for (StorageView<ItemVariant> view : targetInv.nonEmptyViews()) {
				ItemStack stack = view.getResource().toStack((int) view.getAmount());
				availableItems.add(stack);
			}
		}

		invVersionTracker.awaitNewVersion(targetInventory);
		submitNewArrivals(this.availableItems, availableItems);
		this.availableItems = availableItems;
		return availableItems;
	}

	private void submitNewArrivals(InventorySummary before, InventorySummary after) {
		if (before == null || after.isEmpty())
			return;

		Set<RequestPromiseQueue> promiseQueues = new HashSet<>();

		for (Direction d : Iterate.directions) {
			if (!level.isLoaded(worldPosition.relative(d)))
				continue;

			BlockState adjacentState = level.getBlockState(worldPosition.relative(d));
			// Factory Panel restocking — deferred until FactoryPanelBehaviour.isActive()/restockerPromises ported

			if (AllBlocks.STOCK_LINK.has(adjacentState)) {
				if (PackagerLinkBlock.getConnectedDirection(adjacentState) != d)
					continue;
				if (!(level.getBlockEntity(worldPosition.relative(d)) instanceof PackagerLinkBlockEntity plbe))
					continue;
				UUID freqId = plbe.behaviour.freqId;
				if (!Create.LOGISTICS.hasQueuedPromises(freqId))
					continue;
				promiseQueues.add(Create.LOGISTICS.getQueuedPromises(freqId));
			}
		}

		if (promiseQueues.isEmpty())
			return;

		for (BigItemStack entry : after.getStacks())
			before.add(entry.stack, -entry.count);
		for (RequestPromiseQueue queue : promiseQueues)
			for (BigItemStack entry : before.getStacks())
				if (entry.count < 0)
					queue.itemEnteredSystem(entry.stack, -entry.count);
	}

	@Override
	public void lazyTick() {
		super.lazyTick();
		if (level.isClientSide())
			return;
		recheckIfLinksPresent();
		if (!redstonePowered)
			return;
		redstonePowered = getBlockState().getOptionalValue(PackagerBlock.POWERED)
			.orElse(false);
		if (!redstoneModeActive())
			return;
		updateSignAddress();
		attemptToSend(null);
	}

	public void recheckIfLinksPresent() {
		if (level.isClientSide())
			return;
		BlockState blockState = getBlockState();
		if (!blockState.hasProperty(PackagerBlock.LINKED))
			return;
		boolean shouldBeLinked = getLinkPos() != null;
		boolean isLinked = blockState.getValue(PackagerBlock.LINKED);
		if (shouldBeLinked == isLinked)
			return;
		level.setBlockAndUpdate(worldPosition, blockState.cycle(PackagerBlock.LINKED));
	}

	public boolean redstoneModeActive() {
		return !getBlockState().getOptionalValue(PackagerBlock.LINKED)
			.orElse(false);
	}

	private BlockPos getLinkPos() {
		for (Direction d : Iterate.directions) {
			BlockState adjacentState = level.getBlockState(worldPosition.relative(d));
			if (!AllBlocks.STOCK_LINK.has(adjacentState))
				continue;
			if (PackagerLinkBlock.getConnectedDirection(adjacentState) != d)
				continue;
			return worldPosition.relative(d);
		}
		return null;
	}

	public void flashLink() {
		for (Direction d : Iterate.directions) {
			BlockState adjacentState = level.getBlockState(worldPosition.relative(d));
			if (!AllBlocks.STOCK_LINK.has(adjacentState))
				continue;
			if (PackagerLinkBlock.getConnectedDirection(adjacentState) != d)
				continue;
			WiFiEffectPacket.send(level, worldPosition.relative(d));
			return;
		}
	}

	public boolean isTooBusyFor(RequestType type) {
		int queue = queuedExitingPackages.size();
		return queue >= switch (type) {
			case PLAYER -> 50;
			case REDSTONE -> 20;
			case RESTOCK -> 10;
		};
	}

	public void activate() {
		redstonePowered = true;
		setChanged();

		recheckIfLinksPresent();
		if (!redstoneModeActive())
			return;

		updateSignAddress();
		attemptToSend(null);

		// dont send multiple packages when a button signal length is received
		if (buttonCooldown <= 0) { // still on button cooldown, don't prolong it
			buttonCooldown = 40;
		}
	}

	public boolean unwrapBox(ItemStack box, boolean simulate) {
		if (animationTicks > 0)
			return false;

		Objects.requireNonNull(this.level);

		ItemStackHandler contents = PackageItem.getContents(box);
		List<ItemStack> items = getNonEmptyStacks(contents);
		if (items.isEmpty())
			return true;

		PackageOrderWithCrafts orderContext = PackageItem.getOrderContext(box);
		Direction facing = getBlockState().getOptionalValue(PackagerBlock.FACING).orElse(Direction.UP);
		BlockPos target = worldPosition.relative(facing.getOpposite());
		BlockState targetState = level.getBlockState(target);

		UnpackingHandler handler = UnpackingHandler.REGISTRY.get(targetState);
		UnpackingHandler toUse = handler != null ? handler : UnpackingHandler.DEFAULT;
		// note: handler may modify the passed items
		boolean unpacked = toUse.unpack(level, target, targetState, facing, items, orderContext, simulate);

		if (unpacked && !simulate) {
			previouslyUnwrapped = box;
			animationInward = true;
			animationTicks = CYCLE;
			notifyUpdate();
		}

		return unpacked;
	}

	public void attemptToSend(List<PackagingRequest> queuedRequests) {
		if (queuedRequests == null && (!heldBox.isEmpty() || animationTicks != 0 || buttonCooldown > 0))
			return;

		Storage<ItemVariant> targetInv = targetInventory.getInventory();
		if (targetInv == null || targetInv instanceof PackagerItemHandler)
			return;

		// We need slot-level access for extraction. Use SlottedStackStorage if available.
		if (!(targetInv instanceof SlottedStackStorage slotted))
			return;

		boolean anyItemPresent = false;
		ItemStackHandler extractedItems = new ItemStackHandler(PackageItem.SLOTS);
		ItemStack extractedPackageItem = ItemStack.EMPTY;
		PackagingRequest nextRequest = null;
		String fixedAddress = null;
		int fixedOrderId = 0;

		// Data written to packages for defrags
		int linkIndexInOrder = 0;
		boolean finalLinkInOrder = false;
		int packageIndexAtLink = 0;
		boolean finalPackageAtLink = false;
		PackageOrderWithCrafts orderContext = null;
		boolean requestQueue = queuedRequests != null;

		if (requestQueue && !queuedRequests.isEmpty()) {
			nextRequest = queuedRequests.get(0);
			fixedAddress = nextRequest.address();
			fixedOrderId = nextRequest.orderId();
			linkIndexInOrder = nextRequest.linkIndex();
			finalLinkInOrder = nextRequest.finalLink()
				.booleanValue();
			packageIndexAtLink = nextRequest.packageCounter()
				.getAndIncrement();
			orderContext = nextRequest.context();
		}

		Outer:
		for (int i = 0; i < PackageItem.SLOTS; i++) {
			boolean continuePacking = true;

			while (continuePacking) {
				continuePacking = false;

				for (int slot = 0; slot < slotted.getSlotCount(); slot++) {
					int initialCount = requestQueue ? Math.min(64, nextRequest.getCount()) : 64;

					// Simulate extract from this slot
					ItemStack inSlot = slotted.getStackInSlot(slot);
					if (inSlot.isEmpty())
						continue;
					int canExtract = Math.min(initialCount, inSlot.getCount());
					ItemStack extracted = inSlot.copyWithCount(canExtract);

					if (requestQueue && !ItemStack.isSameItemSameComponents(extracted, nextRequest.item()))
						continue;

					boolean bulky = !extracted.getItem()
						.canFitInsideContainerItems();
					if (bulky && anyItemPresent)
						continue;

					anyItemPresent = true;
					// Insert into extractedItems (stacked, like ItemHandlerHelper.insertItemStacked)
					ItemStack remaining = insertItemStacked(extractedItems, extracted.copy());
					int leftovers = remaining.getCount();
					int transferred = extracted.getCount() - leftovers;

					// Actually extract the transferred amount using Transaction API
					if (transferred > 0) {
						try (Transaction tx = Transaction.openOuter()) {
							slotted.extractSlot(slot, ItemVariant.of(inSlot), transferred, tx);
							tx.commit();
						}
					}

					if (extracted.getItem() instanceof PackageItem)
						extractedPackageItem = extracted;

					if (!requestQueue) {
						if (bulky)
							break Outer;
						continue;
					}

					nextRequest.subtract(transferred);

					if (!nextRequest.isEmpty()) {
						if (bulky)
							break Outer;
						continue;
					}

					finalPackageAtLink = true;
					queuedRequests.remove(0);
					if (queuedRequests.isEmpty())
						break Outer;
					int previousCount = nextRequest.packageCounter()
						.intValue();
					nextRequest = queuedRequests.get(0);
					if (!fixedAddress.equals(nextRequest.address()))
						break Outer;
					if (fixedOrderId != nextRequest.orderId())
						break Outer;

					nextRequest.packageCounter()
						.setValue(previousCount);
					finalPackageAtLink = false;
					continuePacking = true;
					if (nextRequest.context() != null)
						orderContext = nextRequest.context();

					if (bulky)
						break Outer;
					break;
				}
			}
		}

		if (!anyItemPresent) {
			if (nextRequest != null)
				queuedRequests.remove(0);
			return;
		}

		ItemStack createdBox =
			extractedPackageItem.isEmpty() ? PackageItem.containing(extractedItems) : extractedPackageItem.copy();
		PackageItem.clearAddress(createdBox);

		if (fixedAddress != null)
			PackageItem.addAddress(createdBox, fixedAddress);
		if (requestQueue)
			PackageItem.setOrder(createdBox, fixedOrderId, linkIndexInOrder, finalLinkInOrder, packageIndexAtLink,
				finalPackageAtLink, orderContext);
		if (!requestQueue && !signBasedAddress.isBlank())
			PackageItem.addAddress(createdBox, signBasedAddress);

		// Deduct from link's accurate summary — deferred until LogisticallyLinkedBehaviour.deductFromAccurateSummary ported

		if (!heldBox.isEmpty() || animationTicks != 0) {
			queuedExitingPackages.add(new BigItemStack(createdBox, 1));
			return;
		}

		heldBox = createdBox;
		animationInward = false;
		animationTicks = CYCLE;

		advancements.awardPlayer(AllAdvancements.PACKAGER);
		triggerStockCheck();
		notifyUpdate();
	}

	/**
	 * Inserts an ItemStack into an ItemStackHandler, distributing across slots (stacking).
	 * Replacement for NeoForge's ItemHandlerHelper.insertItemStacked().
	 */
	private static ItemStack insertItemStacked(ItemStackHandler handler, ItemStack stack) {
		ItemStack remaining = stack.copy();

		// First pass: try to merge into existing stacks
		for (int slot = 0; slot < handler.getSlotCount(); slot++) {
			ItemStack inSlot = handler.getStackInSlot(slot);
			if (inSlot.isEmpty() || !ItemStack.isSameItemSameComponents(inSlot, remaining))
				continue;
			int space = Math.min(handler.getSlotLimit(slot), remaining.getMaxStackSize()) - inSlot.getCount();
			if (space <= 0)
				continue;
			int toInsert = Math.min(space, remaining.getCount());
			handler.setStackInSlot(slot, inSlot.copyWithCount(inSlot.getCount() + toInsert));
			remaining.shrink(toInsert);
			if (remaining.isEmpty())
				return ItemStack.EMPTY;
		}

		// Second pass: fill empty slots
		for (int slot = 0; slot < handler.getSlotCount(); slot++) {
			if (!handler.getStackInSlot(slot).isEmpty())
				continue;
			int toInsert = Math.min(remaining.getCount(), remaining.getMaxStackSize());
			handler.setStackInSlot(slot, remaining.copyWithCount(toInsert));
			remaining.shrink(toInsert);
			if (remaining.isEmpty())
				return ItemStack.EMPTY;
		}

		return remaining;
	}

	/**
	 * Get all non-empty stacks from an ItemStackHandler.
	 * Replacement for NeoForge's ItemHelper.getNonEmptyStacks().
	 */
	static List<ItemStack> getNonEmptyStacks(ItemStackHandler handler) {
		List<ItemStack> stacks = new ArrayList<>();
		for (int i = 0; i < handler.getSlotCount(); i++) {
			ItemStack stack = handler.getStackInSlot(i);
			if (!stack.isEmpty())
				stacks.add(stack);
		}
		return stacks;
	}

	public void updateSignAddress() {
		signBasedAddress = "";
		for (Direction side : Iterate.directions) {
			String address = getSign(side);
			if (address == null || address.isBlank())
				continue;
			signBasedAddress = address;
		}
		// ComputerCraft not ported
		hasCustomComputerAddress = false;
	}

	protected String getSign(Direction side) {
		BlockEntity blockEntity = level.getBlockEntity(worldPosition.relative(side));
		if (!(blockEntity instanceof SignBlockEntity sign))
			return null;
		for (boolean front : Iterate.trueAndFalse) {
			SignText text = sign.getText(front);
			String address = "";
			for (Component component : text.getMessages(false)) {
				String string = component.getString();
				if (!string.isBlank())
					address += string.trim() + " ";
			}
			if (!address.isBlank())
				return address.trim();
		}
		return null;
	}

	protected void wakeTheFrogs() {
		// Frogport pulling — deferred until FrogportBlockEntity.tryPullingFromOwnAndAdjacentInventories() ported
	}

	@Override
	protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(compound, registries, clientPacket);
		redstonePowered = compound.getBoolean("Active");
		animationInward = compound.getBoolean("AnimationInward");
		animationTicks = compound.getInt("AnimationTicks");
		signBasedAddress = compound.getString("SignAddress");
		customComputerAddress = compound.getString("ComputerAddress");
		hasCustomComputerAddress = compound.getBoolean("HasComputerAddress");
		heldBox = ItemStack.parseOptional(registries, compound.getCompound("HeldBox"));
		previouslyUnwrapped = ItemStack.parseOptional(registries, compound.getCompound("InsertedBox"));
		if (clientPacket)
			return;
		RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, registries);
		queuedExitingPackages = new LinkedList<>(NBTHelper.readCompoundList(
			compound.getList("QueuedExitingPackages", Tag.TAG_COMPOUND),
			c -> BigItemStack.CODEC.parse(ops, c)
				.result()
				.orElse(new BigItemStack(ItemStack.EMPTY, 0))));
		if (compound.contains("LastSummary"))
			availableItems = InventorySummary.CODEC.parse(ops, compound.getCompound("LastSummary"))
				.result()
				.orElse(null);
	}

	@Override
	protected void write(CompoundTag compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		HolderLookup.Provider registries = Create.getRegistryAccess();
		compound.putBoolean("Active", redstonePowered);
		compound.putBoolean("AnimationInward", animationInward);
		compound.putInt("AnimationTicks", animationTicks);
		compound.putString("SignAddress", signBasedAddress);
		compound.putString("ComputerAddress", customComputerAddress);
		compound.putBoolean("HasComputerAddress", hasCustomComputerAddress);
		compound.put("HeldBox", heldBox.saveOptional(registries));
		compound.put("InsertedBox", previouslyUnwrapped.saveOptional(registries));
		if (clientPacket)
			return;
		RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, registries);
		compound.put("QueuedExitingPackages", NBTHelper.writeCompoundList(queuedExitingPackages, bis -> {
			var result = BigItemStack.CODEC.encodeStart(ops, bis).result();
			if (result.isPresent() && result.get() instanceof CompoundTag ct)
				return ct;
			return new CompoundTag();
		}));
		if (availableItems != null) {
			InventorySummary.CODEC.encodeStart(ops, availableItems)
				.result()
				.ifPresent(encoded -> compound.put("LastSummary", encoded));
		}
	}

	@Override
	public void clearContent() {
		inventory.setStackInSlot(0, ItemStack.EMPTY);
		queuedExitingPackages.clear();
	}

	@Override
	public void destroy() {
		super.destroy();
		ItemHelper.dropContents(level, worldPosition, inventory);
		queuedExitingPackages.forEach(bigStack -> {
			for (int i = 0; i < bigStack.count; i++)
				Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
					bigStack.stack.copy());
		});
		queuedExitingPackages.clear();
	}

	public float getTrayOffset(float partialTicks) {
		float tickCycle = animationInward ? animationTicks - partialTicks : animationTicks - 5 - partialTicks;
		float progress = Mth.clamp(tickCycle / (CYCLE - 5) * 2 - 1, -1, 1);
		progress = 1 - progress * progress;
		return progress * progress;
	}

	public ItemStack getRenderedBox() {
		if (animationInward)
			return animationTicks <= CYCLE / 2 ? ItemStack.EMPTY : previouslyUnwrapped;
		return animationTicks >= CYCLE / 2 ? ItemStack.EMPTY : heldBox;
	}

	public boolean isTargetingSameInventory(@Nullable IdentifiedInventory inventory) {
		if (inventory == null)
			return false;

		Storage<ItemVariant> targetInv = this.targetInventory.getInventory();
		if (targetInv == null)
			return false;

		if (inventory.identifier() != null) {
			// Compute target BlockFace from facing direction
			Direction facing = getBlockState().getOptionalValue(PackagerBlock.FACING).orElse(Direction.UP);
			BlockFace face = new BlockFace(worldPosition.relative(facing.getOpposite()), facing);
			return inventory.identifier().contains(face);
		} else {
			return isSameInventoryFallback(targetInv, inventory.handler());
		}
	}

	private static boolean isSameInventoryFallback(Storage<ItemVariant> first, ItemStackHandler second) {
		if (first == second)
			return true;

		// If a contained ItemStack instance is the same, we can be pretty sure these
		// inventories are the same (works for compound inventories)
		if (first instanceof SlottedStackStorage firstSlotted) {
			for (int i = 0; i < second.getSlotCount(); i++) {
				ItemStack stackInSlot = second.getStackInSlot(i);
				if (stackInSlot.isEmpty())
					continue;
				for (int j = 0; j < firstSlotted.getSlotCount(); j++)
					if (stackInSlot == firstSlotted.getStackInSlot(j))
						return true;
				break;
			}
		}

		return false;
	}
}
