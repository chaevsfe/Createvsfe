package com.simibubi.create.content.logistics.factoryBoard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock.PanelSlot;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock.PanelState;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock.PanelType;
import com.simibubi.create.content.logistics.packagerLink.RequestPromise;
import com.simibubi.create.content.logistics.packagerLink.RequestPromiseQueue;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import com.simibubi.create.foundation.utility.animation.LerpedFloat.Chaser;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;

public class FactoryPanelBehaviour extends BlockEntityBehaviour {

	public static final BehaviourType<FactoryPanelBehaviour> TYPE = new BehaviourType<>();

	public PanelSlot slot;
	public PanelType type;
	public PanelState panelState;
	public ItemStack filter;
	public int count;
	public String address;
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
	private int lastReportedPromises;

	private FactoryPanelSlotPositioning slotPositioning;

	public FactoryPanelBehaviour(SmartBlockEntity be, PanelSlot slot) {
		super(be);
		this.slot = slot;
		this.type = PanelType.NETWORK;
		this.panelState = PanelState.PASSIVE;
		this.filter = ItemStack.EMPTY;
		this.count = 0;
		this.address = "";
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
		this.lastReportedPromises = 0;
	}

	public boolean isActive() {
		return active;
	}

	public void enable() {
		active = true;
	}

	public void disable() {
		active = false;
		filter = ItemStack.EMPTY;
		count = 0;
		disconnectAll();
	}

	public void setNetwork(UUID network) {
		this.networkId = network;
	}

	public ItemStack getFilter() {
		return filter;
	}

	public boolean setFilter(ItemStack stack) {
		this.filter = stack.copy();
		if (!stack.isEmpty())
			this.filter.setCount(1);
		blockEntity.notifyUpdate();
		return true;
	}

	public int getAmount() {
		return count;
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
			}
			forceClearPromises = false;
			return restockerPromises.getTotalPromisedAndRemoveExpired(item, getPromiseExpiryTimeInTicks());
		}

		RequestPromiseQueue promises = Create.LOGISTICS.getQueuedPromises(networkId);
		if (promises == null)
			return 0;

		if (forceClearPromises) {
			promises.forceClear(item);
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

	public String getFrogAddress() {
		FactoryPanelBlockEntity fpbe = panelBE();
		if (fpbe == null)
			return "";
		com.simibubi.create.content.logistics.packager.PackagerBlockEntity packager = fpbe.getRestockedPackager();
		if (packager == null)
			return "";
		if (packager.getLevel().getBlockEntity(packager.getBlockPos().above()) instanceof
				com.simibubi.create.content.logistics.packagePort.frogport.FrogportBlockEntity frogport)
			if (frogport.addressFilter != null && !frogport.addressFilter.isBlank())
				return frogport.addressFilter;
		return "";
	}

	public boolean isMissingAddress() {
		return (!targetedBy.isEmpty() || panelBE().restocker) && count != 0 && recipeAddress.isBlank();
	}

	public int getIngredientStatusColor() {
		if (redstonePowered)
			return 0x580101;
		if (satisfied)
			return 0x3C9852;
		return 0x888898;
	}

	public FactoryPanelPosition getPanelPosition() {
		return new FactoryPanelPosition(getPos(), slot);
	}

	public FactoryPanelBlockEntity panelBE() {
		return (FactoryPanelBlockEntity) blockEntity;
	}

	public void addConnection(FactoryPanelPosition from) {
		if (targetedBy.containsKey(from))
			return;
		targetedBy.put(from, new FactoryPanelConnection(from, 1));
		FactoryPanelBehaviour source = FactoryPanelBehaviour.at(getWorld(), from);
		if (source != null) {
			source.targeting.add(getPanelPosition());
			source.blockEntity.sendData();
		}
		blockEntity.notifyUpdate();
	}

	public void moveTo(FactoryPanelPosition newPos, net.minecraft.world.entity.player.Player player) {
		Level level = getWorld();
		net.minecraft.world.level.block.state.BlockState existingState = level.getBlockState(newPos.pos());

		if (FactoryPanelBehaviour.at(level, newPos) != null)
			return;
		boolean isAddedToOtherGauge = AllBlocks.FACTORY_GAUGE.has(existingState);
		if (!existingState.isAir() && !isAddedToOtherGauge)
			return;
		if (isAddedToOtherGauge && existingState != blockEntity.getBlockState())
			return;
		if (!isAddedToOtherGauge)
			level.setBlock(newPos.pos(), blockEntity.getBlockState(), net.minecraft.world.level.block.Block.UPDATE_ALL);

		for (BlockPos blockPos : targetedByLinks.keySet())
			if (!blockPos.closerThan(newPos.pos(), 24))
				return;
		for (FactoryPanelPosition blockPos : targetedBy.keySet())
			if (!blockPos.pos().closerThan(newPos.pos(), 24))
				return;
		for (FactoryPanelPosition blockPos : targeting)
			if (!blockPos.pos().closerThan(newPos.pos(), 24))
				return;

		SmartBlockEntity oldBE = blockEntity;
		FactoryPanelPosition oldPos = getPanelPosition();
		PanelSlot newSlot = newPos.slot();
		this.slot = newSlot;
		FactoryPanelSlotPositioning fpsp = this.getSlotPositioning();
		if (fpsp != null)
			fpsp.slot = newSlot;

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
	}

	public void disconnectAll() {
		for (FactoryPanelPosition pos : new ArrayList<>(targetedBy.keySet())) {
			FactoryPanelBehaviour source = FactoryPanelBehaviour.at(getWorld(), pos);
			if (source != null) {
				source.targeting.remove(getPanelPosition());
				source.blockEntity.sendData();
			}
		}
		targetedBy.clear();

		for (FactoryPanelPosition targetPos : new ArrayList<>(targeting)) {
			FactoryPanelBehaviour target = FactoryPanelBehaviour.at(getWorld(), targetPos);
			if (target != null) {
				target.targetedBy.remove(getPanelPosition());
				target.blockEntity.sendData();
			}
		}
		targeting.clear();

		disconnectAllLinks();
	}

	public void disconnectAllLinks() {
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
	}

	@Override
	public void lazyTick() {
		super.lazyTick();
		if (getWorld().isClientSide())
			return;
		checkForRedstoneInput();
	}

	@Override
	public BehaviourType<?> getType() {
		return TYPE;
	}

	@Override
	public void read(CompoundTag nbt, boolean clientPacket) {
		super.read(nbt, clientPacket);
		readPanel(nbt);
	}

	@Override
	public void write(CompoundTag nbt, boolean clientPacket) {
		super.write(nbt, clientPacket);
		writePanel(nbt);
	}

	public void readPanel(CompoundTag nbt) {
		type = nbt.contains("Type") ? PanelType.values()[nbt.getInt("Type")] : PanelType.NETWORK;
		panelState = nbt.contains("State") ? PanelState.values()[nbt.getInt("State")] : PanelState.PASSIVE;
		count = nbt.getInt("Count");
		address = nbt.getString("Address");
		active = nbt.getBoolean("Active");
		recipeOutput = nbt.contains("RecipeOutput") ? nbt.getInt("RecipeOutput") : 1;
		recipeAddress = nbt.getString("RecipeAddress");
		promiseClearingInterval = nbt.contains("PromiseInterval") ? nbt.getInt("PromiseInterval") : -1;
		satisfied = nbt.getBoolean("Satisfied");
		redstonePowered = nbt.getBoolean("RedstonePowered");
		if (nbt.contains("NetworkId"))
			networkId = nbt.getUUID("NetworkId");
		if (nbt.contains("Filter"))
			filter = ItemStack.parseOptional(blockEntity.getLevel() != null
				? blockEntity.getLevel().registryAccess()
				: net.minecraft.core.RegistryAccess.EMPTY, nbt.getCompound("Filter"));
	}

	public void writePanel(CompoundTag nbt) {
		nbt.putInt("Type", type.ordinal());
		nbt.putInt("State", panelState.ordinal());
		nbt.putInt("Count", count);
		nbt.putString("Address", address);
		nbt.putBoolean("Active", active);
		nbt.putInt("RecipeOutput", recipeOutput);
		nbt.putString("RecipeAddress", recipeAddress);
		nbt.putInt("PromiseInterval", promiseClearingInterval);
		nbt.putBoolean("Satisfied", satisfied);
		nbt.putBoolean("RedstonePowered", redstonePowered);
		if (networkId != null)
			nbt.putUUID("NetworkId", networkId);
		if (!filter.isEmpty()) {
			CompoundTag filterTag = new CompoundTag();
			if (blockEntity.getLevel() != null)
				filterTag = (CompoundTag) filter.save(blockEntity.getLevel().registryAccess(), filterTag);
			nbt.put("Filter", filterTag);
		}
	}

	public FactoryPanelSlotPositioning getSlotPositioning() {
		if (slotPositioning == null)
			slotPositioning = new FactoryPanelSlotPositioning(slot);
		return slotPositioning;
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

	@Override
	public void tick() {
		super.tick();
		if (getWorld().isClientSide()) {
			bulb.updateChaseTarget(redstonePowered || satisfied ? 1 : 0);
			bulb.tickChaser();
		}
	}
}
