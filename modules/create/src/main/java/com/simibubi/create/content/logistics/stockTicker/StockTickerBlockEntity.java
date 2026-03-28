package com.simibubi.create.content.logistics.stockTicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

import com.simibubi.create.AllPackets;
import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.compat.computercraft.ComputerCraftProxy;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.packagerLink.LogisticsManager;
import com.simibubi.create.content.logistics.stockTicker.LogisticalStockRequestPacket;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.utility.NBTHelper;
import io.github.fabricators_of_create.porting_lib_ufo.transfer.item.ItemStackHandler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Stock Ticker block entity — the central hub of the High Logistics network.
 * Tracks item availability across connected packagers and allows players to
 * request packages via the Stock Keeper UI.
 */
public class StockTickerBlockEntity extends StockCheckingBlockEntity {
	public AbstractComputerBehaviour computerBehaviour;

	// Client-side stock data received from server
	@Environment(EnvType.CLIENT)
	public List<BigItemStack> clientStockItems;
	@Environment(EnvType.CLIENT)
	public boolean clientStockComplete;
	@Environment(EnvType.CLIENT)
	public int ticksSinceLastUpdate = Integer.MAX_VALUE;

	// Categorized stock snapshot used by the request screen
	@Environment(EnvType.CLIENT)
	public List<List<BigItemStack>> lastClientsideStockSnapshot;
	@Environment(EnvType.CLIENT)
	public InventorySummary lastClientsideStockSnapshotAsSummary;
	@Environment(EnvType.CLIENT)
	public int activeLinks;

	// Category configuration for the Stock Keeper UI
	public String previouslyUsedAddress = "";
	public List<ItemStack> categories = new ArrayList<>();
	public Map<UUID, List<Integer>> hiddenCategoriesByPlayer = new HashMap<>();

	// Received payments inventory (27 slots) — for CC peripheral and future shop integration
	protected SmartInventory receivedPayments;

	public StockTickerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		receivedPayments = new SmartInventory(27, this, 64, false);
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

	@Override
	public void tick() {
		super.tick();
		if (level.isClientSide()) {
			if (ticksSinceLastUpdate < 100)
				ticksSinceLastUpdate += 1;
			return;
		}
	}

	@Override
	public InventorySummary getRecentSummary() {
		InventorySummary recentSummary = super.getRecentSummary();
		int contributingLinks = recentSummary.contributingLinks;
		if (activeLinks != contributingLinks && !isRemoved()) {
			activeLinks = contributingLinks;
			sendData();
		}
		return recentSummary;
	}

	public ItemStackHandler getReceivedPaymentsHandler() {
		return receivedPayments;
	}

	public boolean isKeeperPresent() {
		for (int yOffset : com.simibubi.create.foundation.utility.Iterate.zeroAndOne) {
			for (Direction side : com.simibubi.create.foundation.utility.Iterate.horizontalDirections) {
				BlockPos seatPos = worldPosition.below(yOffset).relative(side);
				for (com.simibubi.create.content.contraptions.actors.seat.SeatEntity seatEntity :
					level.getEntitiesOfClass(com.simibubi.create.content.contraptions.actors.seat.SeatEntity.class,
						new net.minecraft.world.phys.AABB(seatPos)))
					if (seatEntity.isVehicle())
						return true;
				if (yOffset == 0 && com.simibubi.create.AllBlockEntityTypes.HEATER.is(level.getBlockEntity(seatPos)))
					return true;
			}
		}
		return false;
	}

	/**
	 * Called on the client when a stock response packet arrives.
	 * Packets may arrive in chunks -- endOfTransmission indicates the final chunk.
	 */
	@Environment(EnvType.CLIENT)
	public void receiveStockPacket(List<BigItemStack> stacks, boolean endOfTransmission) {
		if (clientStockItems == null)
			clientStockItems = new ArrayList<>();
		clientStockItems.addAll(stacks);

		if (!endOfTransmission)
			return;

		ticksSinceLastUpdate = 0;

		lastClientsideStockSnapshotAsSummary = new InventorySummary();
		lastClientsideStockSnapshot = new ArrayList<>();

		for (BigItemStack bigStack : clientStockItems)
			lastClientsideStockSnapshotAsSummary.add(bigStack);

		for (ItemStack filter : categories) {
			List<BigItemStack> inCategory = new ArrayList<>();
			if (!filter.isEmpty()) {
				com.simibubi.create.content.logistics.filter.FilterItemStack filterItemStack =
					com.simibubi.create.content.logistics.filter.FilterItemStack.of(filter);
				java.util.Iterator<BigItemStack> iterator = clientStockItems.iterator();
				while (iterator.hasNext()) {
					BigItemStack bigStack = iterator.next();
					if (!filterItemStack.test(level, bigStack.stack))
						continue;
					inCategory.add(bigStack);
					iterator.remove();
				}
			}
			lastClientsideStockSnapshot.add(inCategory);
		}

		List<BigItemStack> unsorted = new ArrayList<>(clientStockItems);
		lastClientsideStockSnapshot.add(unsorted);
		clientStockItems = null;
		clientStockComplete = true;
	}

	@Environment(EnvType.CLIENT)
	public int getTicksSinceLastUpdate() {
		return ticksSinceLastUpdate;
	}

	@Environment(EnvType.CLIENT)
	public void refreshClientStockSnapshot() {
		ticksSinceLastUpdate = 0;
		AllPackets.getChannel().sendToServer(new LogisticalStockRequestPacket(worldPosition));
	}

	@Environment(EnvType.CLIENT)
	public List<List<BigItemStack>> getClientStockSnapshot() {
		return lastClientsideStockSnapshot;
	}

	@Environment(EnvType.CLIENT)
	public InventorySummary getLastClientsideStockSnapshotAsSummary() {
		return lastClientsideStockSnapshotAsSummary;
	}

	@Override
	protected void write(CompoundTag compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		compound.putString("PreviousAddress", previouslyUsedAddress);
		compound.put("ReceivedPayments", receivedPayments.serializeNBT());
		HolderLookup.Provider registries = level != null ? level.registryAccess() : net.minecraft.core.RegistryAccess.EMPTY;
		if (!categories.isEmpty()) {
			ListTag list = new ListTag();
			for (ItemStack stack : categories)
				list.add(stack.saveOptional(registries));
			compound.put("Categories", list);
		}
		if (!hiddenCategoriesByPlayer.isEmpty()) {
			compound.put("HiddenCategories", NBTHelper.writeCompoundList(hiddenCategoriesByPlayer.entrySet(), e -> {
				CompoundTag c = new CompoundTag();
				c.putUUID("Id", e.getKey());
				c.putIntArray("Indices", e.getValue());
				return c;
			}));
		}
		if (clientPacket)
			compound.putInt("ActiveLinks", activeLinks);
	}

	@Override
	protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(compound, registries, clientPacket);
		previouslyUsedAddress = compound.getString("PreviousAddress");
		receivedPayments.deserializeNBT(compound.getCompound("ReceivedPayments"));
		categories.clear();
		if (compound.contains("Categories", Tag.TAG_LIST)) {
			ListTag list = compound.getList("Categories", Tag.TAG_COMPOUND);
			for (int i = 0; i < list.size(); i++)
				categories.add(ItemStack.parseOptional(registries, list.getCompound(i)));
		}
		categories.removeIf(stack -> !stack.isEmpty() && !(stack.getItem() instanceof com.simibubi.create.content.logistics.filter.FilterItem));
		hiddenCategoriesByPlayer.clear();
		if (compound.contains("HiddenCategories", Tag.TAG_LIST)) {
			NBTHelper.readCompoundList(compound.getList("HiddenCategories", Tag.TAG_COMPOUND),
				c -> hiddenCategoriesByPlayer.put(c.getUUID("Id"),
					IntStream.of(c.getIntArray("Indices")).boxed().collect(java.util.stream.Collectors.toList())));
		}
		if (clientPacket)
			activeLinks = compound.getInt("ActiveLinks");
	}

	public void clearContent() {
		categories.clear();
		for (int i = 0; i < receivedPayments.getSlotCount(); i++)
			receivedPayments.setStackInSlot(i, ItemStack.EMPTY);
	}

	@Override
	public void destroy() {
		com.simibubi.create.foundation.item.ItemHelper.dropContents(level, worldPosition, receivedPayments);
		for (ItemStack filter : categories)
			if (!filter.isEmpty() && filter.getItem() instanceof com.simibubi.create.content.logistics.filter.FilterItem)
				net.minecraft.world.Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
					filter);
		super.destroy();
	}

	public void playEffect() {
		com.simibubi.create.AllSoundEvents.STOCK_LINK.playAt(level, worldPosition, 1.0f, 1.0f, false);
		net.minecraft.world.phys.Vec3 vec3 = net.minecraft.world.phys.Vec3.atCenterOf(worldPosition);
		level.addParticle(new com.simibubi.create.content.logistics.packagerLink.WiFiParticle.Data(),
			vec3.x, vec3.y, vec3.z, 1, 1, 1);
	}

	public record RequestMenuData(boolean showLockOption, boolean isLocked, BlockPos targetPos) {}

	public class RequestMenuProvider implements ExtendedScreenHandlerFactory<RequestMenuData> {
		private final boolean showLockOption;
		private final boolean isLocked;
		private final BlockPos targetPos;

		public RequestMenuProvider(boolean showLockOption, boolean isLocked, BlockPos targetPos) {
			this.showLockOption = showLockOption;
			this.isLocked = isLocked;
			this.targetPos = targetPos;
		}

		@Override
		public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
			return StockKeeperRequestMenu.create(pContainerId, pPlayerInventory, StockTickerBlockEntity.this);
		}

		@Override
		public Component getDisplayName() {
			return Component.empty();
		}

		@Override
		public RequestMenuData getScreenOpeningData(ServerPlayer player) {
			return new RequestMenuData(showLockOption, isLocked, targetPos);
		}
	}
}
