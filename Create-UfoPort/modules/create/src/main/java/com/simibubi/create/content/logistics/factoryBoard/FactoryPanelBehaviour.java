package com.simibubi.create.content.logistics.factoryBoard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock.PanelSlot;
import com.simibubi.create.content.logistics.packagePort.frogport.FrogportBlockEntity;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.content.logistics.packager.PackagingRequest;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour.RequestType;
import com.simibubi.create.content.logistics.packagerLink.LogisticsManager;
import com.simibubi.create.content.logistics.packagerLink.RequestPromise;
import com.simibubi.create.content.logistics.packagerLink.RequestPromiseQueue;
import com.simibubi.create.content.logistics.stockTicker.PackageOrder;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.infrastructure.config.AllConfigs;

import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import com.simibubi.create.foundation.utility.animation.LerpedFloat.Chaser;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class FactoryPanelBehaviour extends BlockEntityBehaviour {

	public static final BehaviourType<FactoryPanelBehaviour> TYPE = new BehaviourType<>();

	/** Hash strategy matching items by type and components (Fabric equivalent of NeoForge's ItemStackLinkedSet.TYPE_AND_TAG). */
	private static final Hash.Strategy<ItemStack> ITEM_STACK_HASH = new Hash.Strategy<>() {
		@Override
		public int hashCode(ItemStack stack) {
			return ItemStack.hashItemAndComponents(stack);
		}

		@Override
		public boolean equals(ItemStack a, ItemStack b) {
			return a == b || (a != null && b != null && ItemStack.isSameItemSameComponents(a, b));
		}
	};

	public PanelSlot slot;
	public ItemStack filter;
	public int count;
	public boolean upTo;
	public UUID networkId;

	// Fields needed for renderer, screens, and connection handler
	public Map<FactoryPanelPosition, FactoryPanelConnection> targetedBy;
	public Map<BlockPos, FactoryPanelConnection> targetedByLinks;
	public Set<FactoryPanelPosition> targeting;
	public List<ItemStack> activeCraftingArrangement;

	public boolean satisfied;
	public boolean promisedSatisfied;
	public boolean waitingForNetwork;
	public String recipeAddress;
	public int recipeOutput;
	public LerpedFloat bulb;
	public int promiseClearingInterval;
	public boolean forceClearPromises;
	public boolean redstonePowered;
	public boolean active;
	public RequestPromiseQueue restockerPromises;

	private int lastReportedUnloadedLinks;
	private int lastReportedLevelInStorage;
	private int lastReportedPromises;
	private int timer;
	private boolean promisePrimedForMarkDirty;

	private FactoryPanelSlotPositioning slotPositioning;

	public FactoryPanelBehaviour(SmartBlockEntity be, PanelSlot slot) {
		super(be);
		this.slot = slot;
		this.filter = ItemStack.EMPTY;
		this.count = 0;
		this.upTo = false;
		this.targetedBy = new HashMap<>();
		this.targetedByLinks = new HashMap<>();
		this.targeting = new HashSet<>();
		this.activeCraftingArrangement = List.of();
		this.satisfied = false;
		this.promisedSatisfied = false;
		this.waitingForNetwork = false;
		this.recipeAddress = "";
		this.recipeOutput = 1;
		this.active = false;
		this.forceClearPromises = false;
		this.redstonePowered = false;
		this.promiseClearingInterval = -1;
		this.bulb = LerpedFloat.linear()
			.startWithValue(0)
			.chase(0, 0.175, Chaser.EXP);
		this.restockerPromises = new RequestPromiseQueue(be::setChanged);
		this.promisePrimedForMarkDirty = true;
		this.networkId = UUID.randomUUID();
		setLazyTickRate(40);
	}

	public boolean isActive() {
		return active;
	}

	public void enable() {
		active = true;
		blockEntity.notifyUpdate();
	}

	public void disable() {
		disconnectAll();
		active = false;
		targetedBy = new HashMap<>();
		targeting = new HashSet<>();
		count = 0;
		satisfied = false;
		promisedSatisfied = false;
		recipeAddress = "";
		recipeOutput = 1;
		setFilter(ItemStack.EMPTY);
		blockEntity.notifyUpdate();
	}

	public void setNetwork(UUID network) {
		this.networkId = network;
	}

	public ItemStack getFilter() {
		return filter;
	}

	public boolean setFilter(ItemStack stack) {
		ItemStack f = stack.copy();
		if (!stack.isEmpty())
			f.setCount(1);
		this.filter = f;
		blockEntity.setChanged();
		blockEntity.sendData();
		return true;
	}

	public int getAmount() {
		return count;
	}

	// --- Inventory monitoring: getLevelInStorage, getUnloadedLinks, getRelevantSummary ---

	public int getUnloadedLinks() {
		if (getWorld().isClientSide())
			return lastReportedUnloadedLinks;
		if (panelBE().restocker)
			return panelBE().getRestockedPackager() == null ? 1 : 0;
		return Create.LOGISTICS.getUnloadedLinkCount(networkId);
	}

	public int getLevelInStorage() {
		if (blockEntity.isVirtual())
			return 1;
		if (getWorld().isClientSide())
			return lastReportedLevelInStorage;
		if (getFilter().isEmpty())
			return 0;

		InventorySummary summary = getRelevantSummary();
		return summary.getCountOf(getFilter());
	}

	private InventorySummary getRelevantSummary() {
		FactoryPanelBlockEntity panelBE = panelBE();
		if (!panelBE.restocker)
			return LogisticsManager.getSummaryOfNetwork(networkId, false);
		PackagerBlockEntity packager = panelBE.getRestockedPackager();
		if (packager == null)
			return InventorySummary.EMPTY;
		return packager.getAvailableItems();
	}

	public int getPromised() {
		if (getWorld().isClientSide())
			return lastReportedPromises;
		ItemStack item = getFilter();
		if (item.isEmpty())
			return 0;

		if (panelBE().restocker) {
			if (forceClearPromises) {
				restockerPromises.forceClear(item);
				resetTimerSlightly();
			}
			forceClearPromises = false;
			return restockerPromises.getTotalPromisedAndRemoveExpired(item, getPromiseExpiryTimeInTicks());
		}

		RequestPromiseQueue promises = Create.LOGISTICS.getQueuedPromises(networkId);
		if (promises == null)
			return 0;

		if (forceClearPromises) {
			promises.forceClear(item);
			resetTimerSlightly();
		}
		forceClearPromises = false;

		return promises.getTotalPromisedAndRemoveExpired(item, getPromiseExpiryTimeInTicks());
	}

	private int getPromiseExpiryTimeInTicks() {
		if (promiseClearingInterval == -1)
			return -1;
		if (promiseClearingInterval == 0)
			return 20 * 30;
		return promiseClearingInterval * 20 * 60;
	}

	// --- Timer management ---

	public void resetTimer() {
		timer = getConfigRequestIntervalInTicks();
	}

	public void resetTimerSlightly() {
		timer = getConfigRequestIntervalInTicks() / 2;
	}

	private int getConfigRequestIntervalInTicks() {
		return AllConfigs.server().logistics.factoryGaugeTimer.get();
	}

	// --- Storage monitoring tick ---

	private void tickStorageMonitor() {
		ItemStack f = getFilter();
		int unloadedLinkCount = getUnloadedLinks();
		FactoryPanelBlockEntity panelBE = panelBE();
		if (!panelBE.restocker && unloadedLinkCount == 0 && lastReportedUnloadedLinks != 0) {
			// All links have been loaded, invalidate cache so we can get an accurate summary
			LogisticsManager.SUMMARIES.invalidate(networkId);
		}
		int inStorage = getLevelInStorage();
		int promised = getPromised();
		int maxStackSize = f.isEmpty() ? 64 : f.getMaxStackSize();
		int demand = getAmount() * (upTo ? 1 : maxStackSize);
		boolean shouldSatisfy = f.isEmpty() || inStorage >= demand;
		boolean shouldPromiseSatisfy = f.isEmpty() || inStorage + promised >= demand;
		boolean shouldWait = unloadedLinkCount > 0;

		if (lastReportedLevelInStorage == inStorage && lastReportedPromises == promised
			&& lastReportedUnloadedLinks == unloadedLinkCount && satisfied == shouldSatisfy
			&& promisedSatisfied == shouldPromiseSatisfy && waitingForNetwork == shouldWait)
			return;

		if (!satisfied && shouldSatisfy && demand > 0) {
			AllSoundEvents.CONFIRM.playOnServer(getWorld(), getPos(), 0.075f, 1f);
			AllSoundEvents.CONFIRM_2.playOnServer(getWorld(), getPos(), 0.125f, 0.575f);
		}

		boolean notifyOutputs = satisfied != shouldSatisfy;
		lastReportedLevelInStorage = inStorage;
		satisfied = shouldSatisfy;
		lastReportedPromises = promised;
		promisedSatisfied = shouldPromiseSatisfy;
		lastReportedUnloadedLinks = unloadedLinkCount;
		waitingForNetwork = shouldWait;
		if (!getWorld().isClientSide)
			blockEntity.sendData();
		if (notifyOutputs)
			notifyRedstoneOutputs();
	}

	// --- Request tick: the core auto-request logic ---

	public static class ItemStackConnections extends ArrayList<FactoryPanelConnection> {
		public ItemStack item;
		public int totalAmount;

		public ItemStackConnections(ItemStack item) {
			this.item = item;
		}
	}

	private void tickRequests() {
		FactoryPanelBlockEntity panelBE = panelBE();
		if (targetedBy.isEmpty() && !panelBE.restocker)
			return;
		if (panelBE.restocker)
			restockerPromises.tick();
		if (satisfied || promisedSatisfied || waitingForNetwork || redstonePowered)
			return;
		if (timer > 0) {
			timer = Math.min(timer, getConfigRequestIntervalInTicks());
			timer--;
			return;
		}

		resetTimer();

		if (recipeAddress.isBlank())
			return;

		if (panelBE.restocker) {
			tryRestock();
			return;
		}

		boolean failed = false;

		Map<UUID, Map<ItemStack, ItemStackConnections>> consolidated = new HashMap<>();

		for (FactoryPanelConnection connection : targetedBy.values()) {
			FactoryPanelBehaviour source = at(getWorld(), connection);
			if (source == null)
				return;

			ItemStack item = source.getFilter();

			Map<ItemStack, ItemStackConnections> networkItemCounts =
				consolidated.computeIfAbsent(source.networkId, $ -> new Object2ObjectOpenCustomHashMap<>(ITEM_STACK_HASH));
			networkItemCounts.computeIfAbsent(item, $ -> new ItemStackConnections(item));
			ItemStackConnections existingConnections = networkItemCounts.get(item);
			existingConnections.add(connection);
			existingConnections.totalAmount += connection.amount;
		}

		Multimap<UUID, BigItemStack> toRequest = HashMultimap.create();

		for (Entry<UUID, Map<ItemStack, ItemStackConnections>> entry : consolidated.entrySet()) {
			UUID network = entry.getKey();
			InventorySummary summary = LogisticsManager.getSummaryOfNetwork(network, true);

			for (ItemStackConnections connections : entry.getValue().values()) {
				if (connections.totalAmount == 0 || connections.item.isEmpty() || summary.getCountOf(connections.item) < connections.totalAmount) {
					for (FactoryPanelConnection connection : connections)
						sendEffect(connection.from, false);
					failed = true;
					continue;
				}

				BigItemStack stack = new BigItemStack(connections.item, connections.totalAmount);
				toRequest.put(network, stack);
				for (FactoryPanelConnection connection : connections)
					sendEffect(connection.from, true);
			}
		}

		if (failed)
			return;

		// Input items may come from differing networks
		Map<UUID, Collection<BigItemStack>> asMap = toRequest.asMap();
		PackageOrderWithCrafts craftingContext = PackageOrderWithCrafts.empty();
		List<Multimap<PackagerBlockEntity, PackagingRequest>> requests = new ArrayList<>();

		// Panel may enforce item arrangement
		if (!activeCraftingArrangement.isEmpty())
			craftingContext = PackageOrderWithCrafts.singleRecipe(activeCraftingArrangement.stream()
				.map(stack -> new BigItemStack(stack.copyWithCount(1)))
				.toList());

		// Collect request distributions
		for (Entry<UUID, Collection<BigItemStack>> entry : asMap.entrySet()) {
			PackageOrderWithCrafts order =
				new PackageOrderWithCrafts(new PackageOrder(new ArrayList<>(entry.getValue())), craftingContext.orderedCrafts());
			Multimap<PackagerBlockEntity, PackagingRequest> request =
				LogisticsManager.findPackagersForRequest(entry.getKey(), order, null, recipeAddress);
			requests.add(request);
		}

		// Check if any packager is busy - cancel all
		for (Multimap<PackagerBlockEntity, PackagingRequest> entry : requests)
			for (PackagerBlockEntity packager : entry.keySet())
				if (packager.isTooBusyFor(RequestType.RESTOCK))
					return;

		// Send it
		for (Multimap<PackagerBlockEntity, PackagingRequest> entry : requests)
			LogisticsManager.performPackageRequests(entry);

		// Keep the output promise
		RequestPromiseQueue promises = Create.LOGISTICS.getQueuedPromises(networkId);
		if (promises != null)
			promises.add(new RequestPromise(new BigItemStack(getFilter(), recipeOutput)));
	}

	private void tryRestock() {
		ItemStack item = getFilter();
		if (item.isEmpty())
			return;

		FactoryPanelBlockEntity panelBE = panelBE();
		PackagerBlockEntity packager = panelBE.getRestockedPackager();
		if (packager == null || !packager.targetInventory.hasInventory())
			return;

		int availableOnNetwork = LogisticsManager.getStockOf(networkId, item, packager.targetInventory.getIdentifiedInventory());
		if (availableOnNetwork == 0) {
			sendEffect(getPanelPosition(), false);
			return;
		}

		int inStorage = getLevelInStorage();
		int promised = getPromised();
		int maxStackSize = item.getMaxStackSize();
		int demand = getAmount() * (upTo ? 1 : maxStackSize);
		int amountToOrder = Math.clamp(demand - promised - inStorage, 0, maxStackSize * 9);

		BigItemStack orderedItem = new BigItemStack(item, Math.min(amountToOrder, availableOnNetwork));
		PackageOrderWithCrafts order = PackageOrderWithCrafts.simple(List.of(orderedItem));

		sendEffect(getPanelPosition(), true);

		if (!LogisticsManager.broadcastPackageRequest(networkId, RequestType.RESTOCK, order,
			packager.targetInventory.getIdentifiedInventory(), recipeAddress))
			return;

		restockerPromises.add(new RequestPromise(orderedItem));
	}

	private void sendEffect(FactoryPanelPosition fromPos, boolean success) {
		if (getWorld() instanceof ServerLevel) {
			com.simibubi.create.AllPackets.sendToNear(getWorld(), getPos(), 64,
				new FactoryPanelEffectPacket(fromPos, getPanelPosition(), success));
		}
	}

	// --- Redstone output notifications ---

	private void notifyRedstoneOutputs() {
		for (FactoryPanelConnection connection : targetedByLinks.values()) {
			if (!getWorld().isLoaded(connection.from.pos()))
				return;
			FactoryPanelSupportBehaviour linkAt = linkAt(getWorld(), connection);
			if (linkAt == null || linkAt.isOutput())
				return;
			linkAt.notifyLink();
		}
	}

	// --- Connection management ---

	public String getFrogAddress() {
		FactoryPanelBlockEntity fpbe = panelBE();
		if (fpbe == null)
			return "";
		PackagerBlockEntity packager = fpbe.getRestockedPackager();
		if (packager == null)
			return "";
		if (packager.getLevel().getBlockEntity(packager.getBlockPos().above()) instanceof FrogportBlockEntity frogport)
			if (frogport.addressFilter != null && !frogport.addressFilter.isBlank())
				return frogport.addressFilter;
		return "";
	}

	public boolean isMissingAddress() {
		return (!targetedBy.isEmpty() || panelBE().restocker) && count != 0 && recipeAddress.isBlank();
	}

	public int getIngredientStatusColor() {
		if (count == 0 || isMissingAddress() || redstonePowered)
			return 0x888898;
		if (waitingForNetwork)
			return 0x5B3B3B;
		if (satisfied)
			return 0x9EFF7F;
		if (promisedSatisfied)
			return 0x22AFAF;
		return 0x3D6EBD;
	}

	public FactoryPanelPosition getPanelPosition() {
		return new FactoryPanelPosition(getPos(), slot);
	}

	public FactoryPanelBlockEntity panelBE() {
		return (FactoryPanelBlockEntity) blockEntity;
	}

	public void addConnection(FactoryPanelPosition fromPos) {
		FactoryPanelSupportBehaviour link = linkAt(getWorld(), new FactoryPanelConnection(fromPos, 1));
		if (link != null) {
			targetedByLinks.put(fromPos.pos(), new FactoryPanelConnection(fromPos, 1));
			link.connect(this);
			blockEntity.notifyUpdate();
			return;
		}

		if (panelBE().restocker)
			return;
		if (targetedBy.size() >= 9)
			return;

		FactoryPanelBehaviour source = at(getWorld(), fromPos);
		if (source == null)
			return;

		source.targeting.add(getPanelPosition());
		targetedBy.put(fromPos, new FactoryPanelConnection(fromPos, 1));
		blockEntity.notifyUpdate();
	}

	public void moveTo(FactoryPanelPosition newPos, net.minecraft.server.level.ServerPlayer player) {
		Level level = getWorld();
		BlockState existingState = level.getBlockState(newPos.pos());

		if (FactoryPanelBehaviour.at(level, newPos) != null)
			return;
		boolean isAddedToOtherGauge = AllBlocks.FACTORY_GAUGE.has(existingState);
		if (!existingState.isAir() && !isAddedToOtherGauge)
			return;
		if (isAddedToOtherGauge && existingState != blockEntity.getBlockState())
			return;
		if (!isAddedToOtherGauge)
			level.setBlock(newPos.pos(), blockEntity.getBlockState(), Block.UPDATE_ALL);

		for (BlockPos blockPos : targetedByLinks.keySet())
			if (!blockPos.closerThan(newPos.pos(), 24))
				return;
		for (FactoryPanelPosition blockPos : targetedBy.keySet())
			if (!blockPos.pos().closerThan(newPos.pos(), 24))
				return;
		for (FactoryPanelPosition blockPos : targeting)
			if (!blockPos.pos().closerThan(newPos.pos(), 24))
				return;

		// Disconnect links
		for (BlockPos pos : targetedByLinks.keySet()) {
			FactoryPanelSupportBehaviour at = linkAt(level, new FactoryPanelConnection(new FactoryPanelPosition(pos, slot), 1));
			if (at != null)
				at.disconnect(this);
		}

		SmartBlockEntity oldBE = blockEntity;
		FactoryPanelPosition oldPos = getPanelPosition();
		moveToSlot(newPos.slot());

		// Add to new BE
		if (level.getBlockEntity(newPos.pos()) instanceof FactoryPanelBlockEntity fpbe) {
			fpbe.attachPanel(this);
			fpbe.panels.put(slot, this);
			fpbe.redraw = true;
			fpbe.lastShape = null;
			fpbe.notifyUpdate();
		}

		// Remove from old BE
		if (oldBE instanceof FactoryPanelBlockEntity fpbe) {
			FactoryPanelBehaviour newBehaviour = new FactoryPanelBehaviour(fpbe, oldPos.slot());
			fpbe.attachPanel(newBehaviour);
			fpbe.panels.put(oldPos.slot(), newBehaviour);
			fpbe.redraw = true;
			fpbe.lastShape = null;
			fpbe.notifyUpdate();
		}

		// Rewire connections
		for (FactoryPanelPosition position : targeting) {
			FactoryPanelBehaviour at = at(level, position);
			if (at != null) {
				FactoryPanelConnection connection = at.targetedBy.remove(oldPos);
				if (connection != null) {
					connection.from = newPos;
					at.targetedBy.put(newPos, connection);
				}
				at.blockEntity.sendData();
			}
		}

		for (FactoryPanelPosition position : targetedBy.keySet()) {
			FactoryPanelBehaviour at = at(level, position);
			if (at != null) {
				at.targeting.remove(oldPos);
				at.targeting.add(newPos);
			}
		}

		// Reconnect links
		for (BlockPos pos : targetedByLinks.keySet()) {
			FactoryPanelSupportBehaviour at = linkAt(level, new FactoryPanelConnection(new FactoryPanelPosition(pos, slot), 1));
			if (at != null)
				at.connect(this);
		}
	}

	private void moveToSlot(PanelSlot slot) {
		this.slot = slot;
		if (this.getSlotPositioning() != null)
			this.getSlotPositioning().slot = slot;
	}

	public void disconnectAll() {
		FactoryPanelPosition panelPosition = getPanelPosition();
		disconnectAllLinks();
		for (FactoryPanelConnection connection : targetedBy.values()) {
			FactoryPanelBehaviour source = at(getWorld(), connection);
			if (source != null) {
				source.targeting.remove(panelPosition);
				source.blockEntity.sendData();
			}
		}
		for (FactoryPanelPosition position : targeting) {
			FactoryPanelBehaviour target = at(getWorld(), position);
			if (target != null) {
				target.targetedBy.remove(panelPosition);
				target.blockEntity.sendData();
			}
		}
		targetedBy.clear();
		targeting.clear();
	}

	public void disconnectAllLinks() {
		for (FactoryPanelConnection connection : targetedByLinks.values()) {
			FactoryPanelSupportBehaviour source = linkAt(getWorld(), connection);
			if (source != null)
				source.disconnect(this);
		}
		targetedByLinks.clear();
	}

	public void checkForRedstoneInput() {
		if (!active)
			return;

		boolean shouldPower = false;
		for (FactoryPanelConnection connection : targetedByLinks.values()) {
			if (!getWorld().isLoaded(connection.from.pos()))
				return;
			FactoryPanelSupportBehaviour linkAt = linkAt(getWorld(), connection);
			if (linkAt == null)
				return;
			shouldPower |= linkAt.shouldPanelBePowered();
		}

		if (shouldPower == redstonePowered)
			return;

		redstonePowered = shouldPower;
		blockEntity.notifyUpdate();
		timer = 1;
	}

	// --- Lifecycle ---

	@Override
	public void lazyTick() {
		super.lazyTick();
		if (getWorld().isClientSide())
			return;
		checkForRedstoneInput();
	}

	@Override
	public void tick() {
		super.tick();
		if (getWorld().isClientSide()) {
			if (blockEntity.isVirtual())
				tickStorageMonitor();
			bulb.updateChaseTarget(redstonePowered || satisfied ? 1 : 0);
			bulb.tickChaser();
			return;
		}

		if (!promisePrimedForMarkDirty) {
			restockerPromises.setOnChanged(blockEntity::setChanged);
			promisePrimedForMarkDirty = true;
		}

		tickStorageMonitor();
		tickRequests();
	}

	@Override
	public BehaviourType<?> getType() {
		return TYPE;
	}

	// --- NBT persistence ---
	// NeoForge uses per-slot keys inside a shared parent tag, driven by FilteringBehaviour.
	// UfoPort doesn't extend FilteringBehaviour, so we use a simpler flat NBT layout
	// with keys prefixed by slot name for disambiguation when 4 panels share one block entity.

	@Override
	public void read(CompoundTag nbt, boolean clientPacket) {
		super.read(nbt, clientPacket);
		readPanel(nbt);
	}

	@Override
	public void write(CompoundTag nbt, boolean clientPacket) {
		super.write(nbt, clientPacket);
		writePanel(nbt, clientPacket);
	}

	public void readPanel(CompoundTag nbt) {
		active = nbt.getBoolean("Active");
		if (!active)
			return;

		count = nbt.getInt("Count");
		upTo = nbt.getBoolean("UpTo");
		timer = nbt.getInt("Timer");
		lastReportedLevelInStorage = nbt.getInt("LastLevel");
		lastReportedPromises = nbt.getInt("LastPromised");
		lastReportedUnloadedLinks = nbt.getInt("LastUnloadedLinks");
		satisfied = nbt.getBoolean("Satisfied");
		promisedSatisfied = nbt.getBoolean("PromisedSatisfied");
		waitingForNetwork = nbt.getBoolean("Waiting");
		redstonePowered = nbt.getBoolean("RedstonePowered");
		recipeOutput = nbt.contains("RecipeOutput") ? nbt.getInt("RecipeOutput") : 1;
		recipeAddress = nbt.getString("RecipeAddress");
		promiseClearingInterval = nbt.contains("PromiseInterval") ? nbt.getInt("PromiseInterval") : -1;

		if (nbt.contains("NetworkId"))
			networkId = nbt.getUUID("NetworkId");
		if (nbt.contains("Filter"))
			filter = ItemStack.parseOptional(blockEntity.getLevel() != null
				? blockEntity.getLevel().registryAccess()
				: RegistryAccess.EMPTY, nbt.getCompound("Filter"));

		// Read targeting set
		targeting.clear();
		if (nbt.contains("Targeting")) {
			CompoundTag targetingTag = nbt.getCompound("Targeting");
			int size = targetingTag.getInt("Size");
			for (int i = 0; i < size; i++) {
				if (targetingTag.contains("Pos" + i)) {
					CompoundTag posTag = targetingTag.getCompound("Pos" + i);
					BlockPos pos = new BlockPos(posTag.getInt("X"), posTag.getInt("Y"), posTag.getInt("Z"));
					PanelSlot s = PanelSlot.values()[posTag.getInt("Slot")];
					targeting.add(new FactoryPanelPosition(pos, s));
				}
			}
		}

		// Read targetedBy connections
		targetedBy.clear();
		if (nbt.contains("TargetedBy")) {
			CompoundTag tbTag = nbt.getCompound("TargetedBy");
			int size = tbTag.getInt("Size");
			for (int i = 0; i < size; i++) {
				if (tbTag.contains("Con" + i)) {
					CompoundTag conTag = tbTag.getCompound("Con" + i);
					BlockPos pos = new BlockPos(conTag.getInt("X"), conTag.getInt("Y"), conTag.getInt("Z"));
					PanelSlot s = PanelSlot.values()[conTag.getInt("Slot")];
					int amount = conTag.getInt("Amount");
					int bendMode = conTag.getInt("BendMode");
					FactoryPanelPosition from = new FactoryPanelPosition(pos, s);
					targetedBy.put(from, new FactoryPanelConnection(from, amount, bendMode));
				}
			}
		}

		// Read targetedByLinks
		targetedByLinks.clear();
		if (nbt.contains("TargetedByLinks")) {
			CompoundTag tblTag = nbt.getCompound("TargetedByLinks");
			int size = tblTag.getInt("Size");
			for (int i = 0; i < size; i++) {
				if (tblTag.contains("Con" + i)) {
					CompoundTag conTag = tblTag.getCompound("Con" + i);
					BlockPos pos = new BlockPos(conTag.getInt("X"), conTag.getInt("Y"), conTag.getInt("Z"));
					PanelSlot s = PanelSlot.values()[conTag.getInt("Slot")];
					int amount = conTag.getInt("Amount");
					int bendMode = conTag.getInt("BendMode");
					FactoryPanelPosition from = new FactoryPanelPosition(pos, s);
					targetedByLinks.put(pos, new FactoryPanelConnection(from, amount, bendMode));
				}
			}
		}

		// Read crafting arrangement
		activeCraftingArrangement = new ArrayList<>();
		if (nbt.contains("Craft")) {
			CompoundTag craftTag = nbt.getCompound("Craft");
			int size = craftTag.getInt("Size");
			RegistryAccess registries = blockEntity.getLevel() != null
				? blockEntity.getLevel().registryAccess()
				: RegistryAccess.EMPTY;
			for (int i = 0; i < size; i++) {
				if (craftTag.contains("Item" + i))
					activeCraftingArrangement.add(ItemStack.parseOptional(registries, craftTag.getCompound("Item" + i)));
			}
		}

		if (nbt.contains("Promises")) {
			RegistryAccess registries = blockEntity.getLevel() != null
				? blockEntity.getLevel().registryAccess()
				: RegistryAccess.EMPTY;
			restockerPromises = RequestPromiseQueue.read(nbt.getCompound("Promises"), registries, () -> {});
			promisePrimedForMarkDirty = false;
		}
	}

	public void writePanel(CompoundTag nbt, boolean clientPacket) {
		nbt.putBoolean("Active", active);
		if (!active)
			return;

		nbt.putInt("Count", count);
		nbt.putBoolean("UpTo", upTo);
		nbt.putInt("Timer", timer);
		nbt.putInt("LastLevel", lastReportedLevelInStorage);
		nbt.putInt("LastPromised", lastReportedPromises);
		nbt.putInt("LastUnloadedLinks", lastReportedUnloadedLinks);
		nbt.putBoolean("Satisfied", satisfied);
		nbt.putBoolean("PromisedSatisfied", promisedSatisfied);
		nbt.putBoolean("Waiting", waitingForNetwork);
		nbt.putBoolean("RedstonePowered", redstonePowered);
		nbt.putInt("RecipeOutput", recipeOutput);
		nbt.putString("RecipeAddress", recipeAddress);
		nbt.putInt("PromiseInterval", promiseClearingInterval);

		if (networkId != null)
			nbt.putUUID("NetworkId", networkId);

		if (!filter.isEmpty()) {
			CompoundTag filterTag = new CompoundTag();
			if (blockEntity.getLevel() != null)
				filterTag = (CompoundTag) filter.save(blockEntity.getLevel().registryAccess(), filterTag);
			nbt.put("Filter", filterTag);
		}

		// Write targeting set
		CompoundTag targetingTag = new CompoundTag();
		targetingTag.putInt("Size", targeting.size());
		int idx = 0;
		for (FactoryPanelPosition pos : targeting) {
			CompoundTag posTag = new CompoundTag();
			posTag.putInt("X", pos.pos().getX());
			posTag.putInt("Y", pos.pos().getY());
			posTag.putInt("Z", pos.pos().getZ());
			posTag.putInt("Slot", pos.slot().ordinal());
			targetingTag.put("Pos" + idx++, posTag);
		}
		nbt.put("Targeting", targetingTag);

		// Write targetedBy connections
		CompoundTag tbTag = new CompoundTag();
		tbTag.putInt("Size", targetedBy.size());
		idx = 0;
		for (FactoryPanelConnection con : targetedBy.values()) {
			CompoundTag conTag = new CompoundTag();
			conTag.putInt("X", con.from.pos().getX());
			conTag.putInt("Y", con.from.pos().getY());
			conTag.putInt("Z", con.from.pos().getZ());
			conTag.putInt("Slot", con.from.slot().ordinal());
			conTag.putInt("Amount", con.amount);
			conTag.putInt("BendMode", con.arrowBendMode);
			tbTag.put("Con" + idx++, conTag);
		}
		nbt.put("TargetedBy", tbTag);

		// Write targetedByLinks
		CompoundTag tblTag = new CompoundTag();
		tblTag.putInt("Size", targetedByLinks.size());
		idx = 0;
		for (FactoryPanelConnection con : targetedByLinks.values()) {
			CompoundTag conTag = new CompoundTag();
			conTag.putInt("X", con.from.pos().getX());
			conTag.putInt("Y", con.from.pos().getY());
			conTag.putInt("Z", con.from.pos().getZ());
			conTag.putInt("Slot", con.from.slot().ordinal());
			conTag.putInt("Amount", con.amount);
			conTag.putInt("BendMode", con.arrowBendMode);
			tblTag.put("Con" + idx++, conTag);
		}
		nbt.put("TargetedByLinks", tblTag);

		// Write crafting arrangement
		if (!activeCraftingArrangement.isEmpty() && blockEntity.getLevel() != null) {
			CompoundTag craftTag = new CompoundTag();
			craftTag.putInt("Size", activeCraftingArrangement.size());
			RegistryAccess registries = blockEntity.getLevel().registryAccess();
			for (int i = 0; i < activeCraftingArrangement.size(); i++) {
				CompoundTag itemTag = new CompoundTag();
				activeCraftingArrangement.get(i).save(registries, itemTag);
				craftTag.put("Item" + i, itemTag);
			}
			nbt.put("Craft", craftTag);
		}

		if (panelBE().restocker && !clientPacket && !restockerPromises.isEmpty() && blockEntity.getLevel() != null)
			nbt.put("Promises", restockerPromises.write(blockEntity.getLevel().registryAccess()));
	}

	public FactoryPanelSlotPositioning getSlotPositioning() {
		if (slotPositioning == null)
			slotPositioning = new FactoryPanelSlotPositioning(slot);
		return slotPositioning;
	}

	// --- Static lookup helpers ---

	@Nullable
	public static FactoryPanelBehaviour at(BlockAndTintGetter world, FactoryPanelConnection connection) {
		return at(world, connection.from);
	}

	@Nullable
	public static FactoryPanelBehaviour at(BlockAndTintGetter world, FactoryPanelPosition pos) {
		if (world instanceof Level l && !l.isLoaded(pos.pos()))
			return null;
		if (!(world.getBlockEntity(pos.pos()) instanceof FactoryPanelBlockEntity fpbe))
			return null;
		FactoryPanelBehaviour behaviour = fpbe.panels.get(pos.slot());
		if (behaviour == null || !behaviour.active)
			return null;
		return behaviour;
	}

	@Nullable
	public static FactoryPanelBehaviour at(Level level, FactoryPanelPosition pos) {
		return at((BlockAndTintGetter) level, pos);
	}

	@Nullable
	public static FactoryPanelSupportBehaviour linkAt(BlockAndTintGetter world, FactoryPanelConnection connection) {
		if (world instanceof Level l && !l.isLoaded(connection.from.pos()))
			return null;
		return BlockEntityBehaviour.get(world, connection.from.pos(), FactoryPanelSupportBehaviour.TYPE);
	}
}
